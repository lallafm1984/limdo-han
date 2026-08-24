# Codex CLI Loop Queue

Execution Stage: CLI

Active Loop: 005

Review Gate: HUMAN_REVIEW_AFTER_LOOP_005

| Loop | Status | Goal source | Automatic transition |
| --- | --- | --- | --- |
| 002 | COMPLETE | `.loop/history.md` | Stopped after verified completion |
| 003 | COMPLETE | `.loop/history.md` | Stopped after verified completion |
| 004 | COMPLETE | `.loop/history.md` | Stopped after verified completion |
| 005 | ACTIVE | `LOOP_GOAL.md` | Fresh CLI sessions continue one iteration at a time until completion or stop |

Queue rules:

- Exactly one loop may be `ACTIVE`.
- Only an explicitly defined item marked `READY` may be promoted next.
- A worker may complete the active loop but may not invent its successor.
- Queue transitions must be recorded in `.loop/history.md`.
