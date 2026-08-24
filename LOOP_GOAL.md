# Loop Goal 001 — Landscape Application Shell

## Objective

Create the child-friendly landscape shell that will host the future handwriting engine.

This loop defines layout and visual hierarchy only. Do not implement touch tracking, stroke paths, tracing validation, scoring, audio, persistence, or rewards.

## Success Criteria

1. The main activity is locked to landscape orientation.
2. The screen has a calm top progress area, a central learning area, and a bottom action area.
3. The shell renders without clipping or overlap at the 2340 × 1080 landscape reference resolution.
4. The central area reserves approximately 70% of its width for the future writing canvas.
5. The remaining central space provides a visually separate guide-character area.
6. Placeholder copy clearly indicates that handwriting behavior is not implemented yet.
7. The layout uses child-readable spacing, contrast, and touch-target-sized action placeholders.
8. The application declares a launcher icon.
9. No handwriting-engine, server, login, ads, analytics, or unnecessary dependency is added.
10. `./scripts/verify.sh` passes unit tests, Android lint, and debug assembly.

## Verification

- Run `./scripts/verify.sh`.
- Inspect the debug APK artifact.
- If an Android emulator is available, install and launch the debug APK to verify landscape orientation and the visible shell.

## Completion Definition

This loop is complete only when every automated verification step passes and the shell is ready to receive a standalone writing canvas in Loop 002.
