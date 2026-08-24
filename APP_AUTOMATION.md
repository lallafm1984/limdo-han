# LimDo Codex App Handoff

## Execution Stage

Stage: `CLI`

The initial Codex App Goal stage ended after verified Loop 004 completion. Fresh-session CLI automation is now authoritative; read `CLI_AUTOMATION.md` for the active contract.

Codex App may inspect status or make an explicitly requested configuration change, but it must not run a second supervisor while `.loop/runtime/supervisor.lock` is live.

The CLI stage remains local-only. Pushes, releases, deployments, external messages, destructive operations, and unqueued product scope still require explicit user authorization.
