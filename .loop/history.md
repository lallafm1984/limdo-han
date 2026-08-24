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

## Loop 002 — Iteration 1

### Hypothesis

A normalized two-segment `ㄱ` geometry model can drive a size-responsive non-interactive Compose Canvas without adding dependencies or input behavior.

### Change

- Added a pure Kotlin geometry model that centers a padded two-segment `ㄱ` path within any positive canvas size.
- Added a dedicated non-interactive Compose `WritingCanvas` with practice guides, a preview path, and a start marker.
- Replaced placeholder artwork while preserving the existing 30/70 learning-area layout.
- Updated visible and accessibility copy to identify the surface as a preview.
- Added unit coverage for path shape, bounds, and invalid empty dimensions.

### Verification

- `./scripts/verify.sh`: passed all four stages
- Automation contract: Loop 002 `IN_PROGRESS`, iteration 1, active Loop 002
- Unit tests: passed with the new geometry tests compiled and executed
- Android lint: passed
- Debug APK assembly: passed

### Result

PASSED. The code-level hypothesis is confirmed with no new dependency or input behavior.

### Failure / Improvement

Visual criteria still require a fresh emulator install, launch, exact-bounds check, and screenshot inspection.

### Next Action

Install the new APK on `alarmquest-qa` and verify the 2340 × 1080 landscape rendering.

## Loop 002 — Completion Verification

### Verification

- Installed `app-debug.apk` successfully on `alarmquest-qa`
- Cold launch succeeded for `com.example.limdo/.MainActivity` in 1541 ms
- Confirmed focused activity `com.example.limdo/.MainActivity`
- Confirmed fullscreen app bounds `Rect(0, 0 - 2340, 1080)` and `ROTATION_90`
- Captured and inspected `captures/loop002-iteration1-2340x1080.png` at exactly 2340 × 1080
- Confirmed no clipping, overlap, illegible guide path, or surrounding-shell regression
- Confirmed accessibility content description `손가락 입력 전 단계인 기역 쓰기 길 미리보기` in the UI hierarchy
- Confirmed 4 unit tests, 0 failures, 0 errors
- Inspected the 8.7 MB APK with SHA-256 `f585651a1406759fc51f63e653624aa5b847115be4e6689176169dd5d6655e10`
- Confirmed package `com.example.limdo`, version `0.1.0`, min SDK 26, and target SDK 36

### Result

PASSED. All Loop 002 success criteria are satisfied with fresh automated and emulator evidence.

### Remaining Risk

- Android 16 may ignore fixed orientation on some large-screen devices; the exact reference emulator remains verified.
- The Windows PowerShell automation check was not run on this Mac.
- The Canvas is intentionally non-interactive; touch behavior remains out of scope.

### Queue Transition

Marked Loop 002 `COMPLETE`, set `Active Loop: NONE`, and stopped at `HUMAN_REVIEW_AFTER_LOOP_002`.

## Child Usability QA Policy — 2026-08-24

### User Priority

QA must prioritize whether a five-year-old child can understand and use the screen, and whether the handwriting surface is large enough for comfortable input.

### Change

- Added separate `AUTOMATED`, `EMULATOR`, `CHILD_PROXY`, and `OBSERVED_CHILD` evidence levels.
- Added a five-year-old comprehension checklist and a privacy-preserving observed-child protocol.
- Set the interactive drawable-area minimum to 1170 × 378 px at the 2340 × 1080 reference viewport.
- Set child control targets to at least 64 × 64 dp with 12 dp spacing.
- Added the QA contract to automation resume instructions and self-check scripts.

### Current-screen Audit

- Loop 002 preview Canvas bounds from the verified UI hierarchy: `[820,447][2214,632]`
- Measured active preview size: 1394 × 185 px
- Width: 59.6% of app width, passes the future input width minimum
- Height: 17.1% of app height, fails the future input height minimum of 35%
- `CHILD_PROXY`: the start point and unavailable controls are visually identifiable, but the drawable height is not comfortable enough for future handwriting input
- `OBSERVED_CHILD: NOT RUN`

### Result

The completed non-interactive preview remains valid. Any next loop that enables input must enlarge the drawable interior before touch capture can be considered complete.

## Child Usability QA Policy — Verification

- `bash -n scripts/check-automation.sh`: passed
- `./scripts/check-automation.sh`: passed with the new child-QA contract required
- `./scripts/verify.sh`: passed automation contract, 4 unit tests, Android lint, and debug assembly
- `git diff --check`: passed
- Application code and APK contents were unchanged by this policy update

## Loop 003 — Goal Activation

### Objective

Provide a child-ready writing surface large enough for comfortable input, capture one finger stroke, and make Clear safely usable without introducing correctness validation or progression.

### Baseline

- Previous Canvas: 1394 × 185 px at 2340 × 1080
- Required minimum: 1170 × 378 px
- Current input behavior: none
- `CHILD_PROXY`: writing height fails
- `OBSERVED_CHILD: NOT RUN`

### Queue Transition

Activated Loop 003 and set the stopping condition to `HUMAN_REVIEW_AFTER_LOOP_003`.

### Next Action

Validate the new loop contract, create a local goal-definition checkpoint, and start Codex App Goal mode.

## Loop 003 — Iteration 1

### Hypothesis

Removing vertical copy from inside the writing card will expand the Canvas above 1170 × 378 px while preserving the 30/70 hierarchy; a pure bounded stroke model can support one-finger input and Clear without dependencies.

### Change

- Expanded the Canvas to fill the writing-card interior.
- Added bounded pure stroke state, one-finger pointer capture, immediate stroke rendering, and an active Clear control.
- Increased all child action surfaces to a 64 dp minimum height.
- Moved concise task guidance to the header and guide character.
- Added unit coverage for safe-inset clamping and Clear.

### Verification

- `./scripts/verify.sh`: failed during `compileDebugKotlin` in unit, lint, and assembly stages
- Exact error: unresolved `semantics` and `contentDescription` references in `MainActivity.kt`

### Result

FAILED. The Clear accessibility modifier is missing two imports.

### Next Action

Add only the missing Compose semantics imports and rerun the complete verification suite.

## Loop 003 — Iteration 2

### Hypothesis

Adding only the missing semantics imports will make the focused input implementation compile and allow all automated verification stages to run.

### Change

Added the missing `semantics` and `contentDescription` imports in `MainActivity.kt`.

### Verification

- `./scripts/verify.sh`: passed all four stages
- Automation contract: Loop 003 `IN_PROGRESS`, iteration 2, active Loop 003
- Unit tests: passed, including safe-inset clamping and Clear behavior
- Android lint: passed
- Debug APK assembly: passed

### Result

PASSED. The implementation compiles and the pure input-state contract is verified.

### Failure / Improvement

Drawable size, live pointer input, Clear behavior, and child-proxy comprehension still require fresh emulator evidence.

### Next Action

Install the APK, measure UI bounds, draw a stroke, activate Clear, and inspect exact 2340 × 1080 screenshots.

## Loop 003 — Iteration 2 Emulator Review

### Verification

- APK install and cold launch: passed in 1687 ms
- App focus and bounds: `2340 × 1080`, `ROTATION_90`
- Canvas bounds: `[789,321][2245,759]`, measuring 1456 × 438 px
- Canvas share: 62.2% of app width and 40.6% of app height; both minimums pass
- Clear bounds: `[811,849][1470,1017]`, measuring 659 × 168 px, approximately 251 × 64 dp
- Minimum action gap: 37 px, approximately 14.1 dp; passes 12 dp
- ADB swipe created a visible blue stroke within the Canvas
- Clear tap removed only the child stroke and preserved the `ㄱ` guide
- Exact 2340 × 1080 before, stroke, and cleared screenshots inspected

### Result

PARTIAL PASS. Size, input, Clear, clipping, and visual-layout criteria pass.

### Child Proxy Failure

The unavailable `다음` action still uses a green emphasized background. A five-year-old who cannot rely on the `준비 중` text could mistake it for the active action.

### Next Action

Remove emphasis from the unavailable Next placeholder without changing layout or behavior, then rerun full and emulator verification.

## Loop 003 — Iteration 3

### Hypothesis

Removing only the unavailable Next emphasis will make Clear the sole visually active control while preserving all passing size and input behavior.

### Change

Removed the green emphasis from the unavailable Next placeholder and aligned both unavailable actions to the same neutral presentation.

### Automated Verification

- `./scripts/verify.sh`: passed all four stages
- Unit tests: 6 passed, 0 failures, 0 errors
- Android lint: passed
- Debug assembly: passed
- APK SHA-256: `5cde9a6492cda7a724c6cc8e6c44ff50b37b5518f2b7d2012425836abca883f2`

### Emulator Verification

- Final APK install and cold launch: passed in 1568 ms
- Focused activity: `com.example.limdo/.MainActivity`
- App bounds: exact 2340 × 1080, `ROTATION_90`
- Canvas bounds: `[789,321][2245,759]` = 1456 × 438 px
- Canvas share: 62.2% width and 40.6% height; minimum 50% × 35% passes
- Clear bounds: `[811,849][1470,1017]` = 659 × 168 px, approximately 251 × 64 dp
- Smallest child-action spacing: approximately 14.1 dp; minimum 12 dp passes
- ADB swipe produced a visible blue stroke without navigation
- Clear removed the child stroke and preserved the guide
- Final before and cleared screenshots have identical SHA-256 `45a609ac963de8d4f2650124215e29555c2c1fd9666d05bee31435eb034a644f`
- Fresh 2340 × 1080 before, stroke, and cleared screenshots inspected without clipping or overlap

### Child Proxy

1. What should the child do now? Draw `ㄱ`, stated once in the large header and demonstrated by the guide.
2. Where should the child write? In the largest white-and-green region occupying most of the learning area.
3. Where should the child start? At the distinct orange start marker.
4. Which controls are unavailable? Replay and Next share a neutral appearance and `준비 중`; Clear is the only warm active control.

`CHILD_PROXY: PASSED`

`OBSERVED_CHILD: NOT RUN`

### Result

PASSED. All twelve Loop 003 success criteria are satisfied within the declared automated, emulator, and child-proxy boundaries.

### Remaining Risk

Actual five-year-old comprehension and physical comfort remain unproven until the supervised observed-child protocol is run.

### Queue Transition

Marked Loop 003 `COMPLETE`, set `Active Loop: NONE`, and stopped at `HUMAN_REVIEW_AFTER_LOOP_003`.

## Loop 004 — Goal Activation

### Objective

Complete one full, child-friendly `ㄱ` tracing cycle: evaluate a continuous stroke for a forgiving start, horizontal-then-vertical order and direction, guide proximity, turn, and finish, then show an immediate success or gentle retry response.

### Baseline

- Canvas: 1456 × 438 px at exact 2340 × 1080, passing the child-size gate
- Input: bounded single-finger stroke and Clear are implemented
- Validation: none
- Result feedback: none
- `CHILD_PROXY`: input discovery passes; attempt-result comprehension is unavailable
- `OBSERVED_CHILD: NOT RUN`

### Queue Transition

User direction on 2026-08-24 authorized continued Goal execution without intermediate review. Activated Loop 004 and moved the next review gate to `HUMAN_REVIEW_AFTER_LOOP_004`.

### Hypothesis

A pure normalized evaluator and a single pointer-release callback can add forgiving `ㄱ` validation and clear child-facing feedback without changing the passing Canvas or action-shelf geometry.

### Next Action

Run the automation preflight, create a local goal-definition checkpoint, then implement and test the evaluator before connecting it to Compose.
