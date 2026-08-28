# 루프 목표 183 — 선택 화면 HOME 아이콘 디자인 통일

## 작업 가치 관문

분류: 결함 수정
사용자 가치: 아이가 홈·선택·쓰기 화면에서 같은 집 그림을 보고 글을 읽지 않아도 일관되게 홈 복귀 동작을 이해할 수 있다.
새로운 근거: 루프 182의 새 package 실화면 감사에서 선택 화면만 시스템 문자 `⌂`와 평면 흰 원을 사용하고 쓰기 화면은 자체 생성 production atlas의 입체 HOME 그림을 사용해 같은 행동의 그림체가 끊기는 P2 일관성 결함을 발견했다.
중복 방지: 선택 화면 HOME 표현 하나만 기존 atlas 셀로 통일한다. 홈 카드·lesson 카드·쓰기 버튼·배경·geometry·판정·navigation과 보호자 기능은 바꾸지 않는다. 기존 HOME 셀이 실제 화면에서 부적합할 때만 재생성을 검토한다.

## 목표

1. `LessonSelection`의 `Text("⌂")` 시스템 글리프를 제거하고 쓰기 화면과 같은 `limdo_action_button_atlas.png` HOME 기본·눌림 셀을 사용한다.
2. 선택 화면의 홈 터치 영역과 content description·callback을 유지한다.
3. 기존 atlas의 alpha·bbox·셀 경계를 검사하고 새 자산이 불필요하다는 판정을 실제 2340 × 1080 화면에서 확인한다.
4. 같은 선택 화면의 변경 전·후를 비교해 그림체 통일, 크기, 여백, 흐림, 잘림, alpha halo와 카드 시선 경쟁을 판정한다.

## 성공 조건

1. `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`가 통과한다.
2. production source에 `Text("⌂")`와 선택 화면용 시스템 HOME 글리프가 0건이다.
3. 선택 화면과 쓰기 화면 HOME이 같은 atlas HOME column과 기본·눌림 row를 사용한다.
4. 선택 화면 홈 touch bounds가 최소 216 × 216 px이고 `clickable=true`·`enabled=true`, 한 번 누르면 실제 홈으로 이동한다.
5. 기존 HOME 셀의 네 모서리 alpha 0, 유효 bbox의 셀 내부 안전 여백과 셀 간 번짐 0건을 자동 검사한다.
6. 정확한 2340 × 1080 같은 선택 상태의 변경 전·후 PNG·hierarchy를 비교해 왜곡·흐림·잘림·검은 배경·halo 0건이며 선택 카드보다 과도하게 강조되지 않는다.
7. `.loop/visual-evidence.md`에 focus·package·APK SHA, 자동 그래픽 디자인·자동 QA·아이 대리 QA 통과와 새 P0·P1·진행 방해 P2 0건을 기록한다.

## 완료 정의

선택 화면 HOME이 새 생성 없이 기존 production HOME atlas로 통일되고 실제 callback·화면·자산 관문을 통과하면 루프 183을 완료한다. 다음에는 2차 M1의 보호자 전용 진입 화면을 단일 제품·디자인 루프로 준비하고 완료 체크포인트를 `git push origin HEAD`로 일반 push한다.
