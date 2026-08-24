# LimDo Development Instructions

## Project Purpose

LimDo is a personal, local-first Android learning application for a five-year-old child. Its long-term goal is to teach Korean Hangul progressively, especially handwriting, stroke order, stroke direction, and child-friendly interaction.

Do not implement the full application unless `LOOP_GOAL.md` explicitly requests it.

## Target Child Profile

- The target child can listen to spoken Korean but cannot yet read Korean UI text.
- Never use visible words as the sole way to discover an instruction, direction, result, reset, or unavailable action.
- Preserve short Korean copy for supervising adults and accessibility services, but prove child comprehension from non-reading cues such as demonstration, shape, position, icon, motion, or verified audio.
- Audio is helpful only after its playback and fallback behavior are implemented and verified; do not assume that visible text has been heard aloud.

## Technology

- Android
- Kotlin
- Jetpack Compose
- Custom Canvas for the future writing engine
- Minimum SDK 26
- Reference emulator viewport: 2340 × 1080 in landscape orientation
- Local-first architecture
- No server, login, ads, analytics, or sensitive permissions unless explicitly requested

## Required Loop

For every iteration:

1. Read `LOOP_GOAL.md`.
2. Read `.loop/state.md`.
3. Read recent entries in `.loop/history.md`.
4. Inspect the current implementation and verification evidence.
5. Identify the single highest-priority unmet condition.
6. Form one concrete hypothesis.
7. Make the smallest reasonable change.
8. Run fresh verification.
9. Compare the result with the previous iteration.
10. Append the outcome to `.loop/history.md`.
11. Update `.loop/state.md`.
12. Continue until the loop succeeds or a stop condition is met.

## Codex App Automation

During the initial development stage, automation runs in Codex App Goal mode. Read `APP_AUTOMATION.md` and `.loop/queue.md` before starting or resuming work.

- Work only on the single active loop named in `.loop/queue.md`.
- Resume from `.loop/state.md`; never restart a partially completed loop from memory.
- Keep `.loop/history.md` append-only and record every fresh verification result.
- When a loop completes, update the queue and state, create one local checkpoint commit, and stop at the declared review gate.
- Do not start a nested Codex CLI process while the execution stage is `CODEX_APP`.
- Do not push, publish, deploy, send external messages, or change remote state without explicit user authorization.
- Do not invent a next loop when the queue has no `READY` item.

## Verification Rules

Never claim completion from code inspection alone. Android changes normally require fresh unit-test, lint, and debug-build results through `./scripts/verify.sh` on macOS/Linux or `scripts/verify.ps1` on Windows.

For visual Android loops, use the `alarmquest-qa` emulator at 1080 × 2340 device resolution and verify the application in its required 2340 × 1080 landscape orientation.

Every visual or input loop must also read and satisfy `QA_CHECKLIST.md`. Report automated, emulator, child-proxy, and observed-child evidence separately. Never describe adult or emulator inspection as proof that a five-year-old child can use the screen.

For handwriting input, measure the active drawable interior, not its surrounding card. At 2340 × 1080 it must be at least 1170 × 378 px and remain the largest child-interaction region on screen.

## Change Discipline

- Prefer one focused change per iteration.
- Preserve unrelated user changes.
- Do not add dependencies without a concrete need.
- Do not change Gradle or SDK versions without evidence.
- Do not disable tests or lint merely to pass verification.
- Do not use destructive Git commands, including `git reset --hard`.

## Stop Conditions

Stop and report when:

- every success criterion passes;
- the same root cause fails three consecutive iterations;
- 15 iterations have been reached;
- a destructive operation is required;
- a major architectural choice cannot be inferred from the goal; or
- required Android tooling is unavailable;
- the active queue reaches its human-review gate; or
- continuation would require remote mutation or permission beyond local development.

## Completion Report

Report the iteration count, changed files, verification commands and results, remaining risks, and suggested next loop.
