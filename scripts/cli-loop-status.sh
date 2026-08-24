#!/usr/bin/env bash

set -u
set -o pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
lock_pid_file="$runtime_dir/supervisor.lock/pid"

echo "LimDo CLI Loop Status"
if [[ -r "$lock_pid_file" ]]; then
    running_pid="$(sed -n '1p' "$lock_pid_file")"
    if [[ "$running_pid" =~ ^[0-9]+$ ]] && kill -0 "$running_pid" 2>/dev/null; then
        echo "Supervisor: RUNNING (pid=$running_pid)"
    else
        echo "Supervisor: STALE LOCK"
    fi
else
    echo "Supervisor: STOPPED"
fi

sed -n '/^Execution Stage:/p;/^Active Loop:/p;/^Review Gate:/p' "$repo_root/.loop/queue.md"
sed -n '/^Loop:/p;/^Status:/p;/^Iteration:/p;/^Last Verification:/p;/^Current Failure:/p;/^Next Action:/p' "$repo_root/.loop/state.md"

if [[ -r "$runtime_dir/current-session" ]]; then
    echo "Current session: $(sed -n '1p' "$runtime_dir/current-session")"
fi
if [[ -s "$runtime_dir/sessions.tsv" ]]; then
    echo "Recent fresh sessions (run, thread, exit, durable progress):"
    tail -n 5 "$runtime_dir/sessions.tsv"
fi
