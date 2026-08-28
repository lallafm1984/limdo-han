# 현재 시각 루프 증거

루프: 209
상태: 완료 — 반복 2 성공 정지 화면·hierarchy·역할 QA 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop208/iteration1/after/target-selection.png
변경 전 hierarchy: captures/loop208/iteration1/after/target-selection.xml
변경 후 PNG: captures/loop209/iteration1/after/target-selection.png
변경 후 hierarchy: captures/loop209/iteration1/after/target-selection.xml
package: com.limdo.hangul
focus: 통과 — `captures/loop209/iteration2/after/success-visible-focus.txt`·`success-focus.txt`의 `mCurrentFocus`·`mFocusedApp`이 LimDo
APK SHA-256: 6600da0598dd0615a0c2fa11e474d81d55d16ecee7075e03444ce14b2be84508
Git commit: 현재 HEAD 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production `GYO` geometry와 기존 token·atlas 재사용
자산 자동 검사: 불필요 — 새 raster 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 현재 통과 근거

- 다섯 선택 카드는 각각 326 × 326 px, 홈은 168 × 168 px이며 잘림·겹침이 없다.
- `gyo-start.xml`의 두 조각은 각각 341 × 368 px, 위·아래 칸은 각각 284 × 284 px이다.
- 모음 먼저 입력은 완성되지 않고, `ㄱ`→`ㅛ` 뒤에만 `완성한 교 쓰기 시작`이 활성된다.
- `gyo-writing.xml`의 WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이고 네 그림 조작은 각각 168 × 168 px이다.
- production 중심선 정방향 4획 뒤 `구 0/3획 완료`로 자동 이동하고 홈 복귀가 동작했다. `gyo-success.mp4`는 입력부터 다음 이동까지 연속 기록이다.

## 성공 화면·역할 QA 판정

- `captures/loop209/iteration2/after/gyo-success-150ms.png`는 정확한 2340 × 1080에서 완성 `교`를 중앙에 가장 크게 보존하고, 성공 atlas의 초록 표식·웃는 도마뱀·별·색종이가 뒤에서 보이며 홈·지우기·이전·다음을 가리지 않는다. 잘림·검은 배경·halo·버튼 가림은 0건이다.
- 같은 성공 상태의 `gyo-success-video.xml`은 전체 화면 정답 overlay, WritingCanvas `[189,63][2151,1017]`, 네 168 × 168 px 조작과 LimDo focus를 보존했다. 이후 `gu-next.xml`에서 `구 0/3획 완료`로 한 번 자동 이동했다.
- 자동 그래픽 디자인: 완성 글자가 성공 자산보다 앞에 유지되고 조작·글자 길 비가림이 0건이어 통과. 자동 QA: verify·diff·성공·자동 다음 통과. 아이 대리 QA: 글을 읽지 않아도 완성한 파란 `교`, 성공 연출, 네 그림 조작을 동시에 구분할 수 있어 통과. 실제 아이 관찰: 실행 안 함.
