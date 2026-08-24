#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
lock_pid_file="$runtime_dir/supervisor.lock/pid"

if [[ -r "$lock_pid_file" ]]; then
    running_pid="$(sed -n '1p' "$lock_pid_file")"
    if [[ "$running_pid" =~ ^[0-9]+$ ]] && kill -0 "$running_pid" 2>/dev/null; then
        echo "CLI LOOP ALREADY RUNNING: pid=$running_pid"
        exit 0
    fi
fi

mkdir -p "$runtime_dir"
nohup "$repo_root/scripts/run-cli-loop.sh" \
    > "$runtime_dir/supervisor.log" \
    2>&1 \
    < /dev/null &
launched_pid=$!
printf '%s\n' "$launched_pid" > "$runtime_dir/launch.pid"

sleep 1
if kill -0 "$launched_pid" 2>/dev/null; then
    echo "CLI LOOP STARTED: pid=$launched_pid"
    echo "Status: $repo_root/scripts/cli-loop-status.sh"
    exit 0
fi

echo "CLI LOOP FAILED TO START" >&2
tail -n 40 "$runtime_dir/supervisor.log" >&2 || true
exit 1
