# Loop Goal 003 — Child-ready Stroke Input Foundation

## Objective

Turn the non-interactive `ㄱ` preview into a large, child-ready writing surface that captures and displays a single finger stroke and can be cleared safely.

This loop implements input capture only. Do not implement stroke-order or direction validation, correctness scoring, audio, persistence, rewards, lesson navigation, multi-character lessons, or remote services.

## Success Criteria

1. At the exact 2340 × 1080 landscape viewport, the active drawable interior is at least 1170 × 378 px and is the largest child-interaction region on screen.
2. The surrounding lesson chrome and copy are reduced as needed before shrinking the drawable surface.
3. The fixed `ㄱ` preview, center guides, start point, and at least 24 dp of drawable safe inset remain visible and unclipped.
4. A single-finger down-and-drag gesture produces an immediate visible stroke that follows the pointer within the Canvas.
5. Captured points are clipped or constrained to drawable bounds and do not trigger navigation or destructive behavior.
6. A new active Clear control removes the child stroke without removing the preview guide.
7. Clear has at least a 64 × 64 dp touch target; unavailable actions remain visibly secondary and cannot be mistaken for active controls.
8. Visible Korean copy and accessibility semantics make the current task, writing location, start point, and Clear action identifiable without implying validation or scoring.
9. Pure input-state behavior and geometry have focused unit tests; no new dependency is added.
10. `CHILD_PROXY` can answer what to do, where to write, where to start, and which controls are unavailable from the first frame. Record `OBSERVED_CHILD: NOT RUN` unless a supervised child test actually occurs.
11. `./scripts/verify.sh` passes the automation contract, unit tests, Android lint, and debug assembly.
12. The debug APK installs and cold-launches on `alarmquest-qa`; fresh UI hierarchy and screenshot evidence confirm exact bounds, active drawable dimensions, focus, and no clipping or overlap.

## Verification

- Run `./scripts/check-automation.sh` before implementation.
- Run `./scripts/verify.sh` after every code iteration.
- Install and cold-launch the debug APK on `alarmquest-qa`.
- Draw a stroke through ADB input, confirm it is visible, activate Clear, and confirm only the child stroke is removed.
- Capture and inspect a fresh exact 2340 × 1080 screenshot before and after Clear.
- Measure the Canvas and child-control bounds from the UI hierarchy.
- Complete the `CHILD_PROXY` report in `.loop/history.md` and state `OBSERVED_CHILD` accurately.

## Completion Definition

This loop is complete only when all twelve criteria pass with fresh evidence. Then set `.loop/queue.md` to `Active Loop: NONE`, create one local checkpoint commit, do not push, and stop at `HUMAN_REVIEW_AFTER_LOOP_003`.
