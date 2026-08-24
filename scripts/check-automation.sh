#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "AUTOMATION CONTRACT FAILED: $1" >&2
    exit 1
}

required_files=(
    "AGENTS.md"
    "APP_AUTOMATION.md"
    "LOOP_GOAL.md"
    ".loop/queue.md"
    ".loop/state.md"
    ".loop/history.md"
)

for required_file in "${required_files[@]}"; do
    [[ -s "$required_file" ]] || fail "missing or empty $required_file"
done

grep -q '^Stage: `CODEX_APP`$' APP_AUTOMATION.md || fail "Codex App stage is not selected"
grep -q '^Execution Stage: CODEX_APP$' .loop/queue.md || fail "queue stage does not match"
grep -q '2340 × 1080' AGENTS.md || fail "reference viewport missing from AGENTS.md"
grep -q '2340 × 1080' LOOP_GOAL.md || fail "reference viewport missing from LOOP_GOAL.md"

goal_loop="$(sed -n 's/^# Loop Goal \([0-9][0-9][0-9]\).*/\1/p' LOOP_GOAL.md)"
state_loop="$(sed -n 's/^Loop: \([0-9][0-9][0-9]\).*/\1/p' .loop/state.md)"
active_loop="$(sed -n 's/^Active Loop: //p' .loop/queue.md)"
status="$(sed -n 's/^Status: //p' .loop/state.md)"
iteration="$(sed -n 's/^Iteration: //p' .loop/state.md)"

[[ -n "$goal_loop" ]] || fail "cannot read goal loop id"
[[ "$state_loop" == "$goal_loop" ]] || fail "state loop $state_loop does not match goal loop $goal_loop"

case "$status" in
    READY|IN_PROGRESS|COMPLETE|BLOCKED) ;;
    *) fail "invalid loop status: $status" ;;
esac

[[ "$iteration" =~ ^[0-9]+$ ]] || fail "iteration is not numeric"
(( iteration <= 15 )) || fail "iteration exceeds the 15-iteration stop condition"

if [[ "$active_loop" == "NONE" ]]; then
    [[ "$status" == "COMPLETE" || "$status" == "BLOCKED" ]] || fail "no active loop but state is $status"
else
    [[ "$active_loop" == "$goal_loop" ]] || fail "active loop $active_loop does not match goal loop $goal_loop"
fi

echo "AUTOMATION CONTRACT PASSED"
echo "Stage: CODEX_APP"
echo "Loop: $goal_loop"
echo "Status: $status"
echo "Iteration: $iteration"
echo "Active Loop: $active_loop"
