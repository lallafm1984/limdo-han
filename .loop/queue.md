# Codex App Loop Queue

Execution Stage: CODEX_APP

Active Loop: NONE

Review Gate: HUMAN_REVIEW_AFTER_LOOP_004

| Loop | Status | Goal source | Automatic transition |
| --- | --- | --- | --- |
| 002 | COMPLETE | `.loop/history.md` | Stopped at human review after verified completion |
| 003 | COMPLETE | `.loop/history.md` | Stopped at human review after verified completion |
| 004 | COMPLETE | `LOOP_GOAL.md` | Stopped at human review after verified completion |

Queue rules:

- Exactly one loop may be `ACTIVE`.
- Only an explicitly defined item marked `READY` may be promoted next.
- No next item is currently authorized; Loop 004 completion sets `Active Loop: NONE`.
- Queue transitions must be recorded in `.loop/history.md`.
