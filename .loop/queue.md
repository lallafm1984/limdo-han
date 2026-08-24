# Codex App Loop Queue

Execution Stage: CODEX_APP

Active Loop: 003

Review Gate: HUMAN_REVIEW_AFTER_LOOP_003

| Loop | Status | Goal source | Automatic transition |
| --- | --- | --- | --- |
| 002 | COMPLETE | `.loop/history.md` | Stopped at human review after verified completion |
| 003 | ACTIVE | `LOOP_GOAL.md` | Stop for human review after verified completion |

Queue rules:

- Exactly one loop may be `ACTIVE`.
- Only an explicitly defined item marked `READY` may be promoted next.
- No next item is currently authorized; completing Loop 003 sets `Active Loop: NONE`.
- Queue transitions must be recorded in `.loop/history.md`.
