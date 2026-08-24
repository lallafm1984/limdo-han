#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
mkdir -p "$runtime_dir"
touch "$runtime_dir/stop"
echo "CLI LOOP STOP REQUESTED: the supervisor will exit after the current fresh session"
