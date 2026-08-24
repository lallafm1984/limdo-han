# LimDo Development Instructions

## Project Purpose

LimDo is a personal, local-first Android learning application for a five-year-old child. Its long-term goal is to teach Korean Hangul progressively, especially handwriting, stroke order, stroke direction, and child-friendly interaction.

Do not implement the full application unless `LOOP_GOAL.md` explicitly requests it.

## Technology

- Android
- Kotlin
- Jetpack Compose
- Custom Canvas for the future writing engine
- Minimum SDK 26
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

## Verification Rules

Never claim completion from code inspection alone. Android changes normally require fresh unit-test, lint, and debug-build results through `./scripts/verify.sh` on macOS/Linux or `scripts/verify.ps1` on Windows.

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
- required Android tooling is unavailable.

## Completion Report

Report the iteration count, changed files, verification commands and results, remaining risks, and suggested next loop.

