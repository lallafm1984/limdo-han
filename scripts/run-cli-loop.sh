#!/usr/bin/env bash

set -u
set -o pipefail

usage() {
    echo "사용법: $0 [--once]"
}

once=0
case "${1:-}" in
    "") ;;
    --once) once=1 ;;
    -h|--help)
        usage
        exit 0
        ;;
    *)
        usage >&2
        exit 2
        ;;
esac

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
sessions_dir="$runtime_dir/sessions"
lock_dir="$runtime_dir/supervisor.lock"
stop_file="$runtime_dir/stop"
prompt_file="$repo_root/.loop/cli-worker-prompt.md"
session_log="$runtime_dir/sessions.tsv"
max_process_failures=3
pause_seconds="${CODEX_LOOP_PAUSE_SECONDS:-2}"
sandbox_mode="${CODEX_LOOP_SANDBOX:-danger-full-access}"

mkdir -p "$sessions_dir"

acquire_lock() {
    if mkdir "$lock_dir" 2>/dev/null; then
        printf '%s\n' "$$" > "$lock_dir/pid"
        return 0
    fi

    local existing_pid=""
    if [[ -r "$lock_dir/pid" ]]; then
        existing_pid="$(sed -n '1p' "$lock_dir/pid")"
    fi
    if [[ "$existing_pid" =~ ^[0-9]+$ ]] && kill -0 "$existing_pid" 2>/dev/null; then
        echo "CLI 루프가 이미 실행 중: pid=$existing_pid" >&2
        return 1
    fi

    rm -f "$lock_dir/pid"
    rmdir "$lock_dir" 2>/dev/null || {
        echo "CLI 루프 잠금을 복구할 수 없음: $lock_dir" >&2
        return 1
    }
    mkdir "$lock_dir" || return 1
    printf '%s\n' "$$" > "$lock_dir/pid"
}

release_lock() {
    local owner=""
    if [[ -r "$lock_dir/pid" ]]; then
        owner="$(sed -n '1p' "$lock_dir/pid")"
    fi
    if [[ "$owner" == "$$" ]]; then
        rm -f "$lock_dir/pid"
        rmdir "$lock_dir" 2>/dev/null || true
    fi
}

has_work() {
    local active_loop
    active_loop="$(sed -n 's/^활성 루프: //p' "$repo_root/.loop/queue.md")"
    [[ -n "$active_loop" && "$active_loop" != "없음" ]] && return 0
    grep -Eq '^\| [0-9]{3} \| 준비 \|' "$repo_root/.loop/queue.md"
}

durable_fingerprint() {
    shasum "$repo_root/.loop/state.md" "$repo_root/.loop/history.md" | shasum | awk '{print $1}'
}

acquire_lock || exit 2
trap release_lock EXIT INT TERM
rm -f "$stop_file"

cd "$repo_root" || exit 2
if ! ./scripts/check-automation.sh; then
    echo "CLI 루프 중지: 자동화 계약 실패" >&2
    exit 2
fi

codex_bin="${CODEX_BIN:-}"
if [[ -z "$codex_bin" ]]; then
    codex_bin="$(command -v codex 2>/dev/null || true)"
fi
if [[ -z "$codex_bin" || ! -x "$codex_bin" ]]; then
    echo "CLI 루프 중지: codex 실행 파일을 찾을 수 없음" >&2
    exit 2
fi
if [[ ! -s "$prompt_file" ]]; then
    echo "CLI 루프 중지: 작업자 지시문이 없음" >&2
    exit 2
fi

consecutive_failures=0
session_number=0

while has_work; do
    if [[ -e "$stop_file" ]]; then
        echo "CLI 루프 중지: 중지 신호를 받음"
        exit 0
    fi

    session_number=$((session_number + 1))
    run_id="$(date '+%Y%m%d-%H%M%S')-$(printf '%03d' "$session_number")"
    run_dir="$sessions_dir/$run_id"
    mkdir -p "$run_dir"

    before_fingerprint="$(durable_fingerprint)"
    printf '%s\n' "$run_id" > "$runtime_dir/current-session"
    printf 'CLI 루프 세션 시작 run=%s 순번=%s\n' "$run_id" "$session_number"

    codex_args=(
        exec
        --ignore-user-config
        --ephemeral
        --json
        --color never
        --sandbox "$sandbox_mode"
        -C "$repo_root"
        -o "$run_dir/final.txt"
    )

    [[ -d "$HOME/.gradle" ]] && codex_args+=(--add-dir "$HOME/.gradle")
    [[ -d "$HOME/.android" ]] && codex_args+=(--add-dir "$HOME/.android")
    [[ -d "$HOME/Library/Android/sdk" ]] && codex_args+=(--add-dir "$HOME/Library/Android/sdk")
    [[ -n "${CODEX_LOOP_MODEL:-}" ]] && codex_args+=(--model "$CODEX_LOOP_MODEL")

    set +e
    env -u CODEX_SESSION_ID -u CODEX_THREAD_ID -u CODEX_INTERNAL_ORIGINATOR_OVERRIDE \
        "$codex_bin" "${codex_args[@]}" - \
        < "$prompt_file" \
        > "$run_dir/events.jsonl" \
        2> "$run_dir/stderr.log"
    cli_status=$?
    set -e

    thread_id="$(sed -n 's/.*"type":"thread.started","thread_id":"\([^"]*\)".*/\1/p' "$run_dir/events.jsonl" | head -n 1)"
    after_fingerprint="$(durable_fingerprint)"
    progress="yes"
    if [[ "$before_fingerprint" == "$after_fingerprint" ]]; then
        progress="no"
    fi

    printf '%s\t%s\t%s\t%s\n' "$run_id" "${thread_id:-UNKNOWN}" "$cli_status" "$progress" >> "$session_log"
    printf 'CLI 루프 세션 종료 run=%s thread=%s 종료=%s 지속기록=%s\n' \
        "$run_id" "${thread_id:-UNKNOWN}" "$cli_status" "$progress"

    if [[ "$cli_status" -eq 0 && "$progress" == "yes" ]]; then
        consecutive_failures=0
    else
        consecutive_failures=$((consecutive_failures + 1))
        echo "CLI 루프 프로세스 실패: 연속=$consecutive_failures" >&2
    fi

    if (( consecutive_failures >= max_process_failures )); then
        echo "CLI 루프 중지: 세션 실패 또는 지속 기록 누락이 세 번 연속 발생" >&2
        exit 1
    fi

    iteration="$(sed -n 's/^반복: //p' .loop/state.md)"
    status="$(sed -n 's/^상태: //p' .loop/state.md)"
    if [[ "$iteration" =~ ^[0-9]+$ ]] && (( iteration >= 15 )) && [[ "$status" != "완료" && "$status" != "차단" ]]; then
        echo "CLI 루프 중지: 종료 상태 없이 반복 제한에 도달" >&2
        exit 1
    fi

    if (( once == 1 )); then
        echo "CLI 루프 중지: 한 세션 모드 완료"
        exit 0
    fi

    sleep "$pause_seconds"
done

echo "CLI 루프 완료: 활성 또는 준비 작업이 없음"
