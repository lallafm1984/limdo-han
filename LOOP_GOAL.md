# Loop Goal 004 — Guided ㄱ Tracing Lesson

## Objective

Complete the first child-friendly `ㄱ` tracing cycle. Preserve the large writing surface from Loop 003, evaluate one continuous finger stroke locally for start position, horizontal-then-vertical order, direction, guide proximity, and completion, then provide an immediate success or gentle retry response a five-year-old can understand.

This loop does not add scores, penalties, handwriting grades, audio, persistence, rewards, lesson navigation, multi-character lessons, networking, accounts, ads, analytics, or sensitive permissions.

## Success Criteria

1. At the exact 2340 × 1080 landscape viewport, the active drawable interior remains at least 1170 × 378 px and remains the largest child-interaction region.
2. A pure, deterministic evaluator accepts Canvas dimensions plus one bounded stroke and produces named outcomes for success and the actionable retry reasons required by the lesson.
3. A successful stroke starts near the orange marker, progresses primarily left-to-right before top-to-bottom, stays within a forgiving child-sized guide corridor, reaches the turn, and finishes near the guide end.
4. Empty, wrong-start, wrong-order or direction, substantially off-guide, and incomplete strokes do not produce success; natural jitter and sparse sampling on an otherwise valid child stroke are tolerated.
5. Evaluation occurs only when the finger is released. No partial stroke can flash a final success or retry result during drawing.
6. The result is communicated immediately with short Korean copy, a non-color cue, and accessibility semantics. Retry language is encouraging and does not show a numeric score, grade, punishment, or technical failure reason.
7. A successful trace remains visible until Clear. Clear resets both the child stroke and feedback while preserving the guide; the next gesture replaces the previous attempt predictably.
8. Clear remains the only active shelf control and retains at least a 64 × 64 dp target. Replay and Next remain unmistakably unavailable.
9. The guide, orange start marker, at least 24 dp drawable safe inset, child stroke, and result feedback remain visible without clipping or overlap.
10. Focused unit tests cover every evaluator outcome, boundary handling, a jittery valid stroke, stroke replacement, and Clear reset without adding a dependency.
11. `CHILD_PROXY` can identify what to do, where to start, which direction to move, whether the attempt succeeded or should be retried, and how to reset from the first relevant frame. Record `OBSERVED_CHILD: NOT RUN` unless a supervised child test actually occurs.
12. `./scripts/verify.sh` passes the automation contract, unit tests, Android lint, and debug assembly after every code iteration.
13. The debug APK installs and cold-launches on `alarmquest-qa`; fresh exact 2340 × 1080 screenshots and UI hierarchy evidence verify bounds, focus, valid success, invalid retry, and Clear restoration.

## Verification

- Run `./scripts/check-automation.sh` before implementation.
- Run `./scripts/verify.sh` after every code iteration.
- Install and cold-launch the debug APK on `alarmquest-qa` at exact 2340 × 1080 landscape.
- Use one continuous injected pointer gesture for a valid `ㄱ` and at least one invalid attempt; confirm distinct final feedback only after release.
- Activate Clear and confirm both the child stroke and result reset while the guide remains.
- Capture and inspect fresh exact 2340 × 1080 screenshots for initial, success, retry, and cleared states.
- Measure the Canvas, feedback, and active-control bounds from a fresh UI hierarchy.
- Complete the `CHILD_PROXY` report in `.loop/history.md` and state `OBSERVED_CHILD` accurately.

## Completion Definition

This loop is complete only when all thirteen criteria pass with fresh evidence. Then set `.loop/queue.md` to `Active Loop: NONE`, create one local checkpoint commit, do not push, and stop at `HUMAN_REVIEW_AFTER_LOOP_004`.
