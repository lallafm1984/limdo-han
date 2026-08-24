#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "AUTOMATION CONTRACT FAILED: $1" >&2
    exit 1
}

required_files=(
    "AGENTS.md"
    "APP_AUTOMATION.md"
    "CLI_AUTOMATION.md"
    "QA_CHECKLIST.md"
    "LOOP_GOAL.md"
    ".loop/queue.md"
    ".loop/state.md"
    ".loop/history.md"
    ".loop/cli-worker-prompt.md"
    "scripts/run-cli-loop.sh"
    "scripts/start-cli-loop.sh"
    "scripts/cli-loop-status.sh"
    "scripts/stop-cli-loop.sh"
)

for required_file in "${required_files[@]}"; do
    [[ -s "$required_file" ]] || fail "missing or empty $required_file"
done

grep -q '^Stage: `CLI`$' APP_AUTOMATION.md || fail "Codex App handoff does not select CLI"
grep -q '^Stage: `CLI`$' CLI_AUTOMATION.md || fail "CLI stage is not selected"
grep -q '^Execution Stage: CLI$' .loop/queue.md || fail "queue stage does not match"
grep -q 'exactly one loop iteration' CLI_AUTOMATION.md || fail "single-iteration session contract missing"
grep -q 'codex exec --ephemeral --json --sandbox workspace-write' CLI_AUTOMATION.md || fail "fresh-session command contract missing"
grep -q 'Perform exactly ONE loop iteration' .loop/cli-worker-prompt.md || fail "worker iteration boundary missing"
grep -q 'Never invoke `codex exec`' .loop/cli-worker-prompt.md || fail "nested CLI prohibition missing"
grep -q -- '--ephemeral' scripts/run-cli-loop.sh || fail "ephemeral CLI flag missing"
grep -q -- '--sandbox workspace-write' scripts/run-cli-loop.sh || fail "workspace sandbox flag missing"
grep -q 'env -u CODEX_SESSION_ID' scripts/run-cli-loop.sh || fail "fresh-session environment isolation missing"
grep -q 'launchctl submit' scripts/start-cli-loop.sh || fail "detached macOS launch service missing"
grep -q '2340 × 1080' AGENTS.md || fail "reference viewport missing from AGENTS.md"
grep -q '2340 × 1080' LOOP_GOAL.md || fail "reference viewport missing from LOOP_GOAL.md"
grep -q '1170 px' QA_CHECKLIST.md || fail "minimum writing width missing from QA_CHECKLIST.md"
grep -q '378 px' QA_CHECKLIST.md || fail "minimum writing height missing from QA_CHECKLIST.md"
grep -q 'OBSERVED_CHILD' QA_CHECKLIST.md || fail "observed-child evidence boundary missing"

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
echo "Stage: CLI"
echo "Loop: $goal_loop"
echo "Status: $status"
echo "Iteration: $iteration"
echo "Active Loop: $active_loop"
