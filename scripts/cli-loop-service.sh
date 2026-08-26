#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
label="com.limdo.cli-loop"
domain="gui/$(id -u)"
template_file="$repo_root/automation/$label.plist.template"
runtime_dir="$repo_root/.loop/runtime/launchd"
staged_plist="$runtime_dir/$label.plist"
installed_plist="$HOME/Library/LaunchAgents/$label.plist"
log_dir="$repo_root/logs/launchd"

usage() {
    echo "사용법: $0 prepare|enable|disable|status|uninstall"
}

escape_sed_replacement() {
    sed 's/[&|]/\\&/g'
}

prepare() {
    local codex_bin launch_path run_script_value repo_value path_value codex_value stdout_value stderr_value
    codex_bin="${CODEX_BIN:-$(command -v codex 2>/dev/null || true)}"
    if [[ -z "$codex_bin" || ! -x "$codex_bin" ]]; then
        echo "launchd 준비 실패: codex 실행 파일을 찾을 수 없음" >&2
        exit 2
    fi
    [[ -s "$template_file" ]] || {
        echo "launchd 준비 실패: 템플릿이 없음" >&2
        exit 2
    }

    mkdir -p "$runtime_dir" "$log_dir"
    launch_path="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:$HOME/.local/bin"
    run_script_value="$(printf '%s' "$repo_root/scripts/run-cli-loop.sh" | escape_sed_replacement)"
    repo_value="$(printf '%s' "$repo_root" | escape_sed_replacement)"
    path_value="$(printf '%s' "$launch_path" | escape_sed_replacement)"
    codex_value="$(printf '%s' "$codex_bin" | escape_sed_replacement)"
    stdout_value="$(printf '%s' "$log_dir/stdout.log" | escape_sed_replacement)"
    stderr_value="$(printf '%s' "$log_dir/stderr.log" | escape_sed_replacement)"

    sed \
        -e "s|__RUN_SCRIPT__|$run_script_value|g" \
        -e "s|__REPO_ROOT__|$repo_value|g" \
        -e "s|__PATH__|$path_value|g" \
        -e "s|__CODEX_BIN__|$codex_value|g" \
        -e "s|__STDOUT_LOG__|$stdout_value|g" \
        -e "s|__STDERR_LOG__|$stderr_value|g" \
        "$template_file" > "$staged_plist"
    plutil -lint "$staged_plist" >/dev/null
    echo "launchd 준비 완료(아직 비활성): $staged_plist"
}

enable_service() {
    prepare
    mkdir -p "$(dirname "$installed_plist")"
    cp "$staged_plist" "$installed_plist"
    launchctl enable "$domain/$label" 2>/dev/null || true
    launchctl bootout "$domain/$label" 2>/dev/null || true
    launchctl bootstrap "$domain" "$installed_plist"
    echo "로그인 자동 실행 켜짐: $label"
}

disable_service() {
    launchctl disable "$domain/$label" 2>/dev/null || true
    "$repo_root/scripts/stop-cli-loop.sh"
    echo "로그인 자동 실행 꺼짐: 현재 작업자 세션이 끝나면 정상 종료"
}

status_service() {
    "$repo_root/scripts/cli-loop-status.sh"
    if launchctl print "$domain/$label" >/dev/null 2>&1; then
        echo "launchd: 등록·로드됨"
    elif [[ -f "$installed_plist" ]]; then
        echo "launchd: plist는 있으나 로드되지 않음"
    elif [[ -f "$staged_plist" ]]; then
        echo "launchd: 준비만 됨(비활성)"
    else
        echo "launchd: 준비 안 됨"
    fi
}

uninstall_service() {
    launchctl disable "$domain/$label" 2>/dev/null || true
    launchctl bootout "$domain/$label" 2>/dev/null || true
    rm -f "$installed_plist"
    echo "launchd 등록 제거 완료: 준비된 템플릿과 프로젝트 파일은 유지"
}

case "${1:-}" in
    prepare) prepare ;;
    enable) enable_service ;;
    disable) disable_service ;;
    status) status_service ;;
    uninstall) uninstall_service ;;
    -h|--help) usage ;;
    *) usage >&2; exit 2 ;;
esac
