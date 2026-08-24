# Loop Goal 005 — Local Spoken Guidance and Replay

## Objective

Add short Korean spoken guidance to the completed `ㄱ` tracing cycle and make Replay the obvious large control for hearing the current instruction again. The target five-year-old can listen but cannot read, so initial guidance, success, and gentle retry must be understandable without relying on visible Korean words.

Keep the lesson local-first. Use an on-device, offline-capable Android speech path with a safe non-audio fallback; do not add a server, account, analytics, ads, sensitive permission, paid service, or network requirement.

This loop does not add persistence, scores, rewards, lesson navigation, multi-character lessons, speech recognition, microphone access, or new remote dependencies.

## Success Criteria

1. At the exact 2340 × 1080 landscape viewport, the active drawable interior remains at least 1170 × 378 px and remains the largest child-interaction region.
2. A pure deterministic cue model maps initial, success, and every retry result to one short concrete Korean utterance plus stable replay identity; focused unit tests cover every mapping.
3. Initial guidance is requested after the local speech engine becomes ready, without blocking first paint or repeating on recomposition and rotation.
4. Success or retry guidance is requested only after pointer release, matching the visible final state; drawing and Clear do not produce stale or overlapping speech.
5. Replay becomes an active control with a reading-independent speaker symbol, accessibility semantics, a minimum 64 × 64 dp target, and at least 12 dp separation. It repeats the current cue rather than an outdated cue.
6. The speech path selects installed Korean speech that does not require a network connection when available, queues only the latest cue, exposes completion/error state for verification, and releases resources with the activity lifecycle.
7. If initialization, Korean availability, or playback fails, the app does not crash or hang. Existing visual start, direction, success, retry, and Clear cues remain usable, and Replay becomes unmistakably unavailable without relying on text alone.
8. No internet or microphone permission, server call, analytics event, account, ad, or new dependency is introduced.
9. Clear resets the stroke and feedback, restores the initial current cue, and allows Replay to request that initial cue; it does not unexpectedly auto-speak merely because Clear was tapped.
10. The guide, child stroke, result feedback, Replay, and Clear remain visible without clipping or overlap. Next remains unmistakably unavailable.
11. Without reading visible Korean, `CHILD_PROXY` can identify the writing task, start, direction, success/retry, Clear, and the control used to hear guidance again. Record `OBSERVED_CHILD: NOT RUN` unless a supervised child test actually occurs.
12. `./scripts/verify.sh` passes the automation contract, unit tests, Android lint, and debug assembly after every code iteration.
13. The debug APK installs and cold-launches on `alarmquest-qa`; fresh exact 2340 × 1080 evidence verifies focus, bounds, initial/replay/result/clear behavior, and either successful Korean speech callbacks or the complete safe-unavailable fallback.

## Verification

- Run `./scripts/check-automation.sh` before implementation.
- Run `./scripts/verify.sh` after every code iteration.
- Inspect the manifest and dependency diff to prove that no forbidden permission, service, or dependency was added.
- Install and cold-launch the debug APK on `alarmquest-qa` at exact 2340 × 1080 landscape.
- Exercise initial Replay, one invalid attempt, one valid `ㄱ`, result Replay, and Clear followed by Replay.
- Capture fresh screenshots and UI hierarchy evidence for active and unavailable speech states encountered.
- Use lifecycle/playback callbacks and local logs as technical playback evidence; do not claim that a human heard or understood audio unless it was actually observed.
- Complete the non-reading `CHILD_PROXY` report in `.loop/history.md` and state `OBSERVED_CHILD` accurately.

## Completion Definition

This loop is complete only when all thirteen criteria pass with fresh evidence. Then set `.loop/queue.md` to `Active Loop: NONE`, create one local checkpoint commit, do not push, and stop at `HUMAN_REVIEW_AFTER_LOOP_005` unless a later explicitly defined loop is already `READY`.
