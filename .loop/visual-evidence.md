# 현재 시각 루프 증거

루프: 210
상태: 완료 — 반복 1 조립·쓰기·성공·전환 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop209/iteration1/after/target-selection.png
변경 전 hierarchy: captures/loop209/iteration1/after/target-selection.xml
변경 후 PNG: captures/loop210/iteration1/after/target-selection.png
변경 후 hierarchy: captures/loop210/iteration1/after/target-selection.xml
package: com.limdo.hangul
focus: 통과 — `captures/loop210/iteration1/after/focus.txt`의 `mCurrentFocus`·`mFocusedApp`이 LimDo
APK SHA-256: d8bde80e74ee472fab2a3f61050dad91e62f60b7b2c05ceb906d948c050c3e28
Git commit: 현재 HEAD 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production `GU` geometry와 기존 token·atlas 재사용
자산 자동 검사: 불필요 — 새 raster 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 현재 통과 근거

- 여섯 선택 카드는 각각 284 × 284 px, 홈은 168 × 168 px이며 잘림·겹침이 없다.
- `gu-start.xml`의 두 조각은 각각 341 × 368 px, 위·아래 칸은 각각 284 × 284 px이다.
- 모음 먼저 입력은 완성되지 않고, `ㄱ`→`ㅜ` 뒤에만 `완성한 구 쓰기 시작`이 활성된다.
- `gu-writing.xml`의 WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이고 네 그림 조작은 각각 168 × 168 px이다.
- production 중심선 정방향 3획 뒤 `gu-success-overlay-450ms.png`에서 완성 `구`·정답 atlas·네 조작이 동시에 보였고, `gu-success-overlay.xml`은 정답 이미지·완성 Canvas·네 callback을 보존했다. 이후 `gyu-next.xml`에서 `규 0/4획 완료`로 한 번 자동 이동했고 홈으로 복귀했다.

## 역할 QA 판정

- 자동 그래픽 디자인: `ㅜ`의 아래로 향하는 짧은 획, 초성 위·모음 아래 관계, 완성 `구`가 조립·쓰기·성공에서 일치하고 잘림·왜곡·가림이 0건이어 통과한다.
- 자동 QA: `verify.sh`·diff·오순서 거부·완성·쓰기·정방향 3획·성공·`GYU` 다음·홈을 통과했다.
- 아이 대리 QA: 글을 읽지 않아도 큰 `ㄱ`·`ㅜ` 조각, 위·아래 빈칸, 오순서 주황 재강조, 노란 완성 카드, 큰 쓰기판과 정답 연출을 순서대로 구분할 수 있어 통과한다. 실제 아이 관찰: 실행 안 함.
