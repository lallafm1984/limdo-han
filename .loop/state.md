# 현재 루프 상태

루프: 122 — `가` 3획 보상 이동 일치

상태: 준비

반복: 0

마지막 검증: 루프 121 반복 1에서 production `ga` 현재 획과 입력의 최근 진행률로 입력 중 앞쪽 화살표를 파생했다. 정확한 2340 × 1080 연속 프레임에서 1획 오른쪽→아래쪽, 2획 아래쪽, 3획 오른쪽과 최종 `SUCCESS`를 확인했고 `./scripts/verify.sh`가 통과했다.

완료한 조건: 루프 121 성공 조건 1~7 전체.

현재 실패: production 화면은 `가` 3획을 성공 판정하지만 보상은 `strokeCount = 1`인 `GieokLesson`을 사용해 경찰차가 126 px 한 칸만 이동한다.

현재 가설: production `가` lesson을 `strokeCount = 3`으로 연결하고 보상 이동을 칸별로 순차 실행하면 정답 1회에만 세 칸을 분명하게 이동하고 중복 callback은 추가 이동을 만들지 않을 것이다.

다음 작업: `GieokLesson`의 production 사용처와 `LessonRewardState` 이동 애니메이션을 조사하고 `가` lesson data·칸별 이동 테스트부터 추가한다.

남은 조건: 루프 122 성공 조건 1~8 전체.
