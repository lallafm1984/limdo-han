# 현재 루프 상태

루프: 120 — `가` 3획 입력 판정 연결

상태: 준비

반복: 0

마지막 검증: 루프 119 반복 1에서 `gaDemonstrationGuide`가 production `ga(width, height)`의 선분 길이로 현재 획·선분·방향을 파생하고, 시범과 정적 화살표를 연결했다. `./scripts/verify.sh`, `./scripts/check-automation.sh`, `git diff --check`가 통과했다.

완료한 조건: 루프 119 성공 조건 1~7 전체. 정확한 2340 × 1080의 16개 연속 프레임에서 `ㄱ` 가로→세로, `ㅏ` 세로, `ㅏ` 가로 안내와 화살표촉을 확인했다.

현재 실패: 화면에는 `가` 3획이 보이고 동적 안내도 세 획을 따르지만, pointer 입력과 결과는 여전히 `GieokTraceEvaluator`의 단일 `ㄱ` 경로로 판정된다.

현재 가설: production `ga`의 세 획을 순차적으로 받는 입력 상태와 evaluator를 추가하면 표시·안내·판정을 같은 geometry로 일치시킬 수 있다.

다음 작업: `GieokTraceEvaluator`와 `TraceAttempt`의 단일 획 가정을 조사하고 production `ga` 3획의 정상·잘못된 획순·방향·이탈 테스트를 먼저 추가한다.

남은 조건: 루프 120 성공 조건 1~7 전체.
