# LimDo Child Usability QA

## Purpose

Every visual or interactive loop must prove that the screen is understandable and physically comfortable for a five-year-old child. A successful build or an adult visual review is not proof of child usability.

## Required QA Boundaries

Report these levels separately:

1. `AUTOMATED`: unit tests, lint, build, and measurable layout rules pass.
2. `EMULATOR`: exact 2340 × 1080 landscape rendering, focus, bounds, and interaction behavior are verified.
3. `CHILD_PROXY`: the screen passes the child-focused heuristic checklist below, inspected from a fresh screenshot and UI hierarchy.
4. `OBSERVED_CHILD`: a five-year-old child completes the short observation protocol with a supervising adult.

Never claim `OBSERVED_CHILD` from code, screenshots, an emulator, an adult tester, or Codex judgment.

## Five-year-old Comprehension Gate

For every child-facing screen:

- Present one obvious learning task at a time.
- Make the place to look or act the largest and strongest visual element.
- Do not require fluent reading to discover the primary action; use shape, position, color, motion, or a character demonstration.
- Use short, concrete Korean phrasing. Avoid abstract instructions and long multi-clause sentences.
- Show the start location and intended direction visually when handwriting is involved.
- Keep unavailable actions visually secondary and label their unavailable state consistently.
- Avoid dense decoration, competing highlights, and controls that look active when they are not.
- Maintain strong foreground/background contrast and verify that important meaning is not conveyed by color alone.

The `CHILD_PROXY` gate fails if a reviewer cannot answer these questions from the first frame:

1. What should the child do now?
2. Where should the child touch or write?
3. Where should the child start?
4. Which visible controls are unavailable?

## Writing-area Size Gate

For every loop that adds or enables handwriting input, measure the active drawable interior rather than the surrounding card.

At the 2340 × 1080 reference viewport:

- Active drawable width must be at least 50% of app width: 1170 px.
- Active drawable height must be at least 35% of app height: 378 px.
- The drawable area must be the largest single child-interaction region on the screen.
- At least 24 dp of safe inset must remain between the accepted stroke region and clipping edges.
- A child must be able to draw the target without starting on labels, system bars, or action controls.
- The full target path and the child's stroke must remain visible under the finger as much as practical.

If a lesson layout cannot meet these minimums, reduce surrounding copy and chrome before shrinking the drawable area. Do not satisfy the gate by measuring a non-drawable container.

## Child Touch Gate

- Primary child controls must have a minimum 64 × 64 dp touch target.
- Adjacent child controls must have at least 12 dp separation.
- Essential actions must not depend on long press, double tap, precise edge gestures, or small icons.
- Touch input must tolerate a young child's imprecise contact without accidental navigation or destructive actions.
- Reset or retry must be obvious, recoverable, and free of punishment language.

## Observed-child Protocol

Run only with a supervising adult and a debug/local build. Do not collect identifying data, audio, video, or analytics.

1. Show the screen without explaining the controls.
2. Ask, `어디에 써야 할까?`
3. Record only pass/fail: the child identifies the writing area within 5 seconds.
4. Ask, `어디서 시작하면 될까?`
5. Record only pass/fail: the child identifies the start point within 5 seconds.
6. Let the child make three attempts and observe whether the writing area permits a comfortable natural stroke without repeated edge collisions.
7. After one demonstration, record only pass/fail: the child can find retry/reset without additional coaching.

Any failure becomes an explicit next-loop condition. Do not store the child's name, exact age, voice, image, or free-form behavioral notes.

## Evidence Required in Loop History

- Exact screenshot pixel dimensions.
- Active drawable bounds and width/height percentages.
- Primary touch-target dimensions and spacing for interactive screens.
- `CHILD_PROXY` answers with concrete visual evidence.
- `OBSERVED_CHILD: NOT RUN`, `PASSED`, or `FAILED`; never imply it was run when it was not.
