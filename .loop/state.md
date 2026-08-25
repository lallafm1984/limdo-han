# 현재 루프 상태

루프: 121 — `가` 입력 중 다음 방향 안내 일치

상태: 준비

반복: 0

마지막 검증: 루프 120 반복 1에서 production `ga`의 세 획을 순차 판정하는 `GaTraceEvaluator`와 다중 획 `TraceAttempt`를 연결했다. 정확한 2340 × 1080에서 잘못된 첫 획 재시도·지우기·정상 3획·`★  ✓`·경찰차 126 px 한 칸 이동을 확인했고 `./scripts/verify.sh`가 통과했다.

완료한 조건: 루프 120 성공 조건 1~7 전체.

현재 실패: `WritingCanvas`의 입력 중 앞쪽 화살표는 여전히 `gieokInputDirectionGuide`를 사용해 `가` 2·3획의 현재 위치·방향과 일치하지 않는다.

현재 가설: production `ga` 획과 `completedStrokes.size`를 입력 중 안내에 연결하면 각 획에서 손가락 앞의 올바른 위치·방향을 보여 줄 수 있다.

다음 작업: `gieokInputDirectionGuide`의 단일 획 가정을 production `ga` 획 index API로 교체하는 테스트부터 추가한다.

남은 조건: 루프 121 성공 조건 1~7 전체.
