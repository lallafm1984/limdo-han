#!/usr/bin/env bash

set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
mkdir -p "$runtime_dir"
touch "$runtime_dir/stop"
echo "CLI 루프 중지 요청됨: 현재 새 세션이 끝나면 감독자가 종료됨"
