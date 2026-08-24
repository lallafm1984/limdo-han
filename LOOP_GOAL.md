# Loop Goal 002 — Non-interactive Writing Canvas

## Objective

Replace the writing-board placeholder with an isolated, non-interactive Compose Canvas foundation for future Hangul tracing.

This loop defines rendering and geometry only. Do not implement touch input, stroke capture, stroke order or direction validation, scoring, audio, persistence, rewards, or lesson navigation.

## Success Criteria

1. A dedicated `WritingCanvas` composable replaces the placeholder artwork inside the existing writing-board region.
2. The canvas renders a calm child-friendly practice surface, center guides, and a fixed preview path for the first consonant `ㄱ`.
3. Preview geometry is derived from the available canvas size and remains within explicit padded bounds.
4. Geometry and rendering responsibilities are separated so the normalized `ㄱ` path can be unit tested without Compose UI or Android instrumentation.
5. The existing guide-character area and approximately 70% writing-board width are preserved.
6. Visible copy and accessibility wording clearly identify the canvas as a preview; no control implies that handwriting input already works.
7. No pointer, gesture, touch-tracking, validation, server, login, ads, analytics, sensitive permission, or unnecessary dependency is added.
8. The final debug APK installs and launches on `alarmquest-qa` in landscape with exact 2340 × 1080 app bounds.
9. A fresh screenshot shows no clipping, overlap, illegible guide path, or regression in the surrounding shell.
10. `./scripts/verify.sh` passes the automation contract check, unit tests, Android lint, and debug assembly.

## Verification

- Run `./scripts/check-automation.sh` before implementation.
- Run `./scripts/verify.sh` after each application-code iteration.
- Inspect the debug APK artifact.
- Install and cold-launch the debug APK on `alarmquest-qa`.
- Confirm focused app bounds are 2340 × 1080 in landscape and inspect a fresh screenshot.

## Completion Definition

This loop is complete only when all ten criteria pass with fresh evidence. Then update `.loop/queue.md` to `Active Loop: NONE`, create one local checkpoint commit, do not push, and stop at `HUMAN_REVIEW_AFTER_LOOP_002`.
