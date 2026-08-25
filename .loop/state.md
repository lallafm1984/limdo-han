# 현재 루프 상태

루프: 119 — `가` 3획 동적 안내 연결

상태: 준비

반복: 0

마지막 검증: 루프 118 반복 1에서 `WritingCanvasGeometry.visibleLessonGlyph`가 production `ga(width, height)`를 그대로 선택하고 `WritingCanvas`가 세 획을 모두 그리도록 연결했다. `./scripts/verify.sh`, `./scripts/check-automation.sh`, `git diff --check`가 통과했다.

완료한 조건: 루프 118 성공 조건 1~7 전체. 정확한 2340 × 1080에서 WritingCanvas `[189,63][2151,838]` = 1962 × 775 px와 정사각형 620 px em, guide 124 px, `가` 중심선 bounds 약 551 × 521 px를 확인했다.

현재 실패: `가`는 표시되지만 초록 시작점·정적 화살표·움직이는 시범과 입력 판정은 아직 이전 `ㄱ` 경로를 사용한다.

현재 가설: production `ga`의 세 획과 선분 길이에서 전체 시범 진행률과 방향을 파생하면 별도 좌표 없이 올바른 3획 동적 안내를 표시할 수 있다.

다음 작업: `가` 세 획의 진행률→현재 선분·방향 매핑 API를 추가하고 입력 전 동적 안내를 연결한다.

남은 조건: 루프 119 성공 조건 1~7 전체.
