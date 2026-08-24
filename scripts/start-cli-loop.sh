#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
lock_pid_file="$runtime_dir/supervisor.lock/pid"
service_label="com.limdo.cli-loop"
service_target="gui/$(id -u)/$service_label"

if [[ -r "$lock_pid_file" ]]; then
    running_pid="$(sed -n '1p' "$lock_pid_file")"
    if [[ "$running_pid" =~ ^[0-9]+$ ]] && kill -0 "$running_pid" 2>/dev/null; then
        echo "CLI LOOP ALREADY RUNNING: pid=$running_pid"
        exit 0
    fi
fi

mkdir -p "$runtime_dir"
if launchctl print "$service_target" >/dev/null 2>&1; then
    launchctl remove "$service_label" >/dev/null 2>&1 || true
fi

launchctl submit \
    -l "$service_label" \
    -o "$runtime_dir/supervisor.log" \
    -e "$runtime_dir/supervisor.log" \
    -- "$repo_root/scripts/run-cli-loop.sh"

sleep 1
if [[ -r "$lock_pid_file" ]]; then
    launched_pid="$(sed -n '1p' "$lock_pid_file")"
else
    launched_pid=""
fi
if [[ "$launched_pid" =~ ^[0-9]+$ ]] && kill -0 "$launched_pid" 2>/dev/null; then
    echo "CLI LOOP STARTED: launchd service=$service_label pid=$launched_pid"
    echo "Status: $repo_root/scripts/cli-loop-status.sh"
    exit 0
fi

echo "CLI LOOP FAILED TO START" >&2
tail -n 40 "$runtime_dir/supervisor.log" >&2 || true
exit 1
