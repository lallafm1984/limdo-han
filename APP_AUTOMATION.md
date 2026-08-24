# LimDo Codex App Automation Contract

## Execution Stage

Stage: `CODEX_APP`

Codex App Goal mode is the only autonomous executor in this stage. Do not launch Codex CLI recursively. CLI automation will be designed separately only after the user explicitly moves the project to the CLI stage.

## Durable Objective

Complete the single active loop in `.loop/queue.md` according to `LOOP_GOAL.md`, with fresh automated and emulator evidence, then stop at the declared human-review gate.

The active Goal must always name:

- the outcome in `LOOP_GOAL.md`;
- the local-only boundaries in this file and `AGENTS.md`;
- the child-usability and writing-area gates in `QA_CHECKLIST.md`;
- `./scripts/verify.sh` plus required emulator evidence as proof; and
- the queue review gate as the stopping condition.

## Resume Protocol

At the start of every Goal turn or after any interruption:

1. Read `AGENTS.md`, this file, `QA_CHECKLIST.md`, `LOOP_GOAL.md`, `.loop/queue.md`, `.loop/state.md`, and the most recent `.loop/history.md` entries.
2. Inspect `git status` and preserve unrelated changes.
3. Run `./scripts/check-automation.sh` before changing application code.
4. Continue the current hypothesis when evidence still supports it; otherwise record a new focused hypothesis.
5. Make one focused change, run fresh verification, compare it with the previous iteration, and persist the outcome before continuing.

## Allowed Autonomous Actions

- Read and edit files inside this repository.
- Run local Gradle, Android SDK, emulator, ADB, and inspection commands.
- Install and launch debug builds on the designated local emulator.
- Capture local screenshots and logs needed for verification.
- Create one local Git checkpoint commit after the active loop is fully verified.

## Actions Requiring User Authorization

- Git push, force push, tag, release, pull request, deployment, or any other remote mutation.
- Destructive Git or filesystem operations.
- Server, login, analytics, advertising, sensitive permissions, paid services, or external messages.
- Dependency, Gradle, SDK, architecture, or product-scope changes not required by the active loop.
- Starting another loop when `.loop/queue.md` has no explicit `READY` item.

## Completion Protocol

When every success criterion passes:

1. Append final code, test, lint, build, APK, and emulator evidence to `.loop/history.md`.
2. Mark `.loop/state.md` as `COMPLETE` with no remaining criteria.
3. Mark the active queue item `COMPLETE`, set `Active Loop: NONE`, and retain the human-review gate.
4. Run `./scripts/verify.sh` once more against the final files.
5. Create a focused local checkpoint commit. Do not push it.
6. End the Codex App Goal and report the exact verification boundary and remaining risks.

## Failure Recovery

- If verification fails, record the exact failure before the next attempt.
- If the same root cause fails three consecutive iterations, stop without hiding or bypassing it.
- If interrupted, the queue identifies the scope, state identifies the next action, and history supplies the evidence trail.
- Stale test output never counts as current proof.
