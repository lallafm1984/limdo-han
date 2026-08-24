# LimDo Fresh-session CLI Automation Contract

## Execution Stage

Stage: `CLI`

`scripts/run-cli-loop.sh` is the only session supervisor. `scripts/start-cli-loop.sh` submits it to the current macOS launchd user domain so it survives the terminal or Codex App command that started it. Each worker is a brand-new, ephemeral `codex exec` session; no session is resumed or forked.

## Durable Objective

Complete only the active loop in `.loop/queue.md` according to `LOOP_GOAL.md`. Each fresh worker performs exactly one loop iteration with one focused change, persists its evidence to `.loop/history.md` and `.loop/state.md`, then exits so the supervisor can start the next fresh session.

## Session Protocol

1. The supervisor validates `./scripts/check-automation.sh` and acquires one atomic repository-local lock.
2. It starts `codex exec --ephemeral --json --sandbox workspace-write` with `.loop/cli-worker-prompt.md` on standard input.
3. The worker reads all durable project contracts and the current Git state instead of relying on conversation history.
4. The worker performs exactly one iteration and records success or failure before exiting.
5. The supervisor records the new thread ID and exit status under `.loop/runtime/sessions/`.
6. A new session starts only while an active loop or explicitly `READY` queue item remains.

## Allowed Autonomous Actions

- Read and edit files inside this repository.
- Run local Gradle, Android SDK, emulator, ADB, and inspection commands.
- Install and launch debug builds on the designated local emulator.
- Capture local screenshots and logs needed for verification.
- Create one focused local Git checkpoint commit after a loop is fully verified.

## Actions Requiring User Authorization

- Git push, force push, tag, release, pull request, deployment, or any other remote mutation.
- Destructive Git or filesystem operations.
- Server, login, analytics, advertising, sensitive permissions, paid services, or external messages.
- Dependency, Gradle, SDK, architecture, or product-scope changes not required by the active loop.
- Inventing or starting a loop that is not explicitly defined in `LOOP_GOAL.md` and marked active or `READY` in `.loop/queue.md`.

## Stop Conditions

The supervisor stops cleanly when there is no active or `READY` work, `.loop/runtime/stop` exists, or the one-session mode was requested. It stops defensively after three consecutive non-zero CLI exits.

The worker marks the loop `BLOCKED` and exits when any `AGENTS.md` stop condition is met. A completed loop stops at its review gate unless another already-defined queue item is explicitly `READY`.

## Operator Commands

```bash
./scripts/start-cli-loop.sh
./scripts/cli-loop-status.sh
./scripts/stop-cli-loop.sh
```

Use `./scripts/run-cli-loop.sh` for a foreground supervisor or `./scripts/run-cli-loop.sh --once` for one fresh worker session. Runtime logs, lock files, and stop signals are intentionally ignored by Git.
