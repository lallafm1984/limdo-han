# Loop History

This append-only file records Loop Engineering iterations.

Each iteration contains a hypothesis, focused change, verification evidence, result, and next action.

## Iteration 1

### Hypothesis

The minimal Compose project and generated Gradle wrapper should pass the complete verification suite without additional production dependencies.

### Change

Created the Android project skeleton, Loop 000 control files, cross-platform verification scripts, and Gradle 8.13 wrapper.

### Verification

Ran `./scripts/verify.sh`.

### Result

FAILED. Unit tests, lint, and assembly all stopped at `compileDebugKotlin` because `MainActivity.kt` referenced `@Preview` without the optional Compose tooling-preview dependency.

### Failure / Improvement

The preview is unnecessary in an environment-only loop. Removing it is narrower than adding another production dependency.

### Next Action

Remove the preview import and composable, then rerun the complete verification suite.

## Iteration 2

### Hypothesis

Removing the unused preview code will resolve the shared Kotlin compilation failure without adding a dependency.

### Change

Removed the `@Preview` import and preview-only composable from `MainActivity.kt`.

### Verification

Ran `./scripts/verify.sh` again.

### Result

PASSED.

- `testDebugUnitTest`: passed
- `lintDebug`: passed
- `assembleDebug`: passed
- Complete verification script: exited successfully with `ALL VERIFICATIONS PASSED`

### Failure / Improvement

No remaining Loop 000 failure. Gradle emitted a non-fatal native-symbol stripping notice. Lint passed with five non-blocking warnings: four available dependency/tooling updates and one missing launcher icon, which is deferred until the application-shell loop.

### Next Action

Close Loop 000 and propose Loop 001 for the landscape application shell.

## Loop 001 — Iteration 0

### Hypothesis

A dependency-free Compose shell with a 30/70 central split can establish the child-facing visual hierarchy while keeping the future writing engine isolated.

### Change

Defined Loop 001 success criteria. No application code changed in this planning checkpoint.

### Verification

Loop 000 was already verified, committed as `a03869d`, and pushed to `origin/main`.

### Result

READY TO IMPLEMENT.

### Failure / Improvement

The current activity renders an intentionally empty surface and has no launcher icon or landscape lock.

### Next Action

Implement the landscape application shell as one focused iteration.

## Loop 001 — Iteration 1

### Hypothesis

The landscape shell can compile using only the existing Compose dependencies.

### Change

Added the landscape manifest contract, launcher icon, localized shell copy, 30/70 learning-area layout, action placeholders, and a unit-tested layout ratio.

### Verification

Ran `./scripts/verify.sh`.

### Result

FAILED. All three verification stages stopped at `compileDebugKotlin` because the file-level `weight` import resolved to an internal Compose symbol.

### Failure / Improvement

`Modifier.weight` is available as a `RowScope`/`ColumnScope` member extension in the existing Compose version and should not be imported directly.

### Next Action

Remove only the conflicting import and rerun the complete verification suite.

## Loop 001 — Iteration 2

### Hypothesis

Removing the direct `weight` import will resolve compilation while preserving the 30/70 layout contract.

### Change

Removed the single conflicting import.

### Verification

- `./scripts/verify.sh`: passed unit tests, lint, and debug assembly
- Unit tests: 2 passed, 0 failed
- Installed the debug APK on the `alarmquest-qa` emulator
- Cold-launched `com.example.limdo/.MainActivity`
- Confirmed app focus, `2400×1080` bounds, and `ROTATION_90`
- Inspected a fresh emulator screenshot

### Result

PASSED. The shell rendered with the intended header, guide area, writing-board placeholder, and action shelf.

### Failure / Improvement

Lint still identified two trivial resource-quality warnings: the app-name string was not referenced and numeric progress copy preferred a typographic fraction.

### Next Action

Reference the app-name resource from the manifest, replace the numeric placeholder with child-readable copy, and run final verification.

## Loop 001 — Iteration 3

### Hypothesis

The resource-only cleanup will preserve the verified shell and remove the two avoidable lint warnings.

### Change

Connected the manifest label to `R.string.app_name` and replaced numeric placeholder progress with `첫 단계`.

### Verification

- `./scripts/verify.sh`: passed all three stages
- Unit tests: 2 passed, 0 failed
- Lint: passed; only four version notices and the expected Android 16 fixed-orientation warning remain
- Debug APK: built successfully
- Final APK reinstalled on `alarmquest-qa`
- Final cold launch: successful with `com.example.limdo/.MainActivity` focused
- Final display: `2400×1080`, landscape, `ROTATION_90`
- Final screenshot: inspected with no visible clipping or overlap

### Result

PASSED. All Loop 001 success criteria are satisfied.

### Failure / Improvement

No blocking failure remains. Android 16 may ignore fixed orientation on some large-screen form factors; the responsive 30/70 shell remains useful there, but additional device classes should be tested later.

### Next Action

Close Loop 001 and define Loop 002 for an isolated, non-interactive Writing Canvas prototype.

## Loop 001 — Iteration 4

### Hypothesis

The existing responsive shell will retain its hierarchy at the user-selected 2340 × 1080 landscape reference resolution.

### Change

- Changed the `alarmquest-qa` AVD hardware resolution from 1080 × 2400 to 1080 × 2340.
- Recorded 2340 × 1080 landscape as the required visual-development viewport in `AGENTS.md` and this loop goal.
- No application behavior or layout value changed.

### Verification

- Restarted the emulator from its persistent AVD configuration
- Confirmed physical device size `1080×2340`
- Reinstalled the final debug APK
- Cold-launched `com.example.limdo/.MainActivity`
- Confirmed focused app bounds `2340×1080` and `ROTATION_90`
- Captured and inspected an exact `2340×1080` screenshot

### Result

PASSED. The shell has no visible clipping, overlap, or hierarchy regression at the new reference resolution. This evidence supersedes the earlier 2400 × 1080 emulator check.

### Failure / Improvement

No blocking issue remains.

### Next Action

Use 2340 × 1080 landscape for subsequent visual loops, beginning with Loop 002.

## Codex App Automation Bootstrap — 2026-08-24

### Objective

Prepare the repository and current Codex task for unattended Goal-mode execution before any CLI automation is introduced.

### Change

- Selected `CODEX_APP` as the explicit execution stage.
- Added a durable local-only automation contract and a single-active-loop queue.
- Defined Loop 002 as a bounded non-interactive Writing Canvas goal with a human-review gate.
- Added cross-platform automation-contract checks and connected them to the normal verification scripts.
- Reset the durable loop state to Loop 002 iteration 0 so an interrupted Goal can resume deterministically.

### Verification

- `bash -n scripts/check-automation.sh`: passed
- `./scripts/check-automation.sh`: passed with `CODEX_APP`, Loop 002, `READY`, iteration 0, and active Loop 002 aligned
- `./scripts/verify.sh`: passed automation contract, unit tests, Android lint, and debug assembly
- `git diff --check`: passed
- Windows automation check: not run because PowerShell is not installed on this Mac

### Result

PASSED. The repository has a deterministic resume path, explicit local-only boundaries, a bounded next loop, and a human-review stopping condition.

### Next Action

Create a local setup checkpoint and activate the Codex App Goal for verified completion of Loop 002.
