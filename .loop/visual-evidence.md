# 현재 시각 루프 증거

루프: 211
상태: 완료 — 반복 2 `규` 성공 동시점 근거 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop210/iteration1/after/target-selection.png
변경 전 hierarchy: captures/loop210/iteration1/after/target-selection.xml
변경 후 PNG: captures/loop211/iteration1/after/target-selection.png
변경 후 hierarchy: captures/loop211/iteration1/after/target-selection.xml
성공 PNG: captures/loop211/iteration2/attempt4/gyu-success.png
성공 hierarchy: captures/loop211/iteration2/attempt4/gyu-success.xml
package: com.limdo.hangul
focus: 통과 — `captures/loop211/iteration2/attempt4/gyu-success-focus.txt`의 `mCurrentFocus`·`mFocusedApp`이 LimDo
APK SHA-256: e7c8fad3f185f615f3b6d70d57b3a09784493a900419b4af87b93c9903b6479b
Git commit: 현재 HEAD 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production `GYU` geometry와 기존 token·성공·조작 atlas 재사용
자산 자동 검사: 불필요 — 새 raster 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 현재 통과 근거

- 일곱 선택 카드는 각각 252 × 252 px이고 `규` 선택·`ㄱ`→`ㅠ`·오순서 거부·완성 상태에 잘림·겹침·왜곡이 없다.
- WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이고 네 그림 조작은 각각 168 × 168 px이다.
- `attempt4/gyu-success.png`는 완성 `규`·큰 초록 체크와 도마뱀·네 조작을 동시에 보여 주고, 같은 노출 구간의 hierarchy는 정답 이미지·완성 Canvas·네 callback을 모두 노출한다.
- 수집 시 uptime은 `289.08`초로 7,200초 미만이고, 물리 1080 × 2340·`user_rotation=1`·LimDo 2340 × 1080 focus를 확인했다.

## 역할 QA 판정

- 자동 그래픽 디자인: `ㅠ`의 아래쪽 짧은 세로획 두 개와 완성 `규`가 선택·조립·쓰기·성공에서 일치하고 잘림·왜곡·조작 가림이 0건이어 통과한다.
- 자동 QA: `verify.sh`·diff·`GyuAssemblyFlowTest` 1/1·오순서 거부·정방향 4획·성공·`GEU` 다음·홈이 통과했다.
- 아이 대리 QA: 글을 읽지 않아도 큰 `ㄱ`·`ㅠ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판과 정답 체크를 순서대로 구분할 수 있어 통과한다. 실제 아이 관찰: 실행 안 함.
