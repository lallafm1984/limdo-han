#!/usr/bin/env bash

set -u
set -o pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
lock_pid_file="$runtime_dir/supervisor.lock/pid"
screen_name="limdo_cli_loop"

echo "LimDo CLI 루프 상태"
if [[ -r "$lock_pid_file" ]]; then
    running_pid="$(sed -n '1p' "$lock_pid_file")"
    if [[ "$running_pid" =~ ^[0-9]+$ ]] && kill -0 "$running_pid" 2>/dev/null; then
        echo "감독자: 실행 중 (pid=$running_pid)"
    else
        echo "감독자: 오래된 잠금"
    fi
else
    echo "감독자: 중지"
fi
screen_list="$(screen -ls 2>/dev/null || true)"
if grep -E "[0-9]+\.${screen_name}[[:space:]]" <<< "$screen_list" >/dev/null; then
    echo "분리 CLI 세션: 실행 중 ($screen_name)"
else
    echo "분리 CLI 세션: 중지"
fi

sed -n '/^실행 단계:/p;/^활성 루프:/p;/^검토 관문:/p' "$repo_root/.loop/queue.md"
sed -n '/^루프:/p;/^상태:/p;/^반복:/p;/^마지막 검증:/p;/^현재 실패:/p;/^다음 작업:/p' "$repo_root/.loop/state.md"

if [[ -r "$runtime_dir/current-session" ]]; then
    echo "현재 세션: $(sed -n '1p' "$runtime_dir/current-session")"
fi
if [[ -s "$runtime_dir/sessions.tsv" ]]; then
    echo "최근 새 세션 (실행, thread, 종료, 지속 기록):"
    tail -n 5 "$runtime_dir/sessions.tsv"
fi
