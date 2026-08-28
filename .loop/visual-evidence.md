# 현재 시각 루프 증거

루프: 207
상태: 완료 — 반복 1 제품·자동·실화면·역할 QA 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop207/iteration1/before/target-selection.png
변경 전 hierarchy: captures/loop207/iteration1/before/target-selection.xml
변경 후 PNG: captures/loop207/iteration1/after/target-selection.png
변경 후 hierarchy: captures/loop207/iteration1/after/target-selection.xml
package: com.limdo.hangul
focus: 통과 — `focus.txt`의 입력·캡처 전후 `mCurrentFocus`·`mFocusedApp` `com.limdo.hangul/.MainActivity`
APK SHA-256: a6f06eb019cd0d1ee82391463ae4aaa591aaa93cdca628bf4878cd69027076c7
Git commit: 09b441e097984354c15f1d3fa591a77dcf5f692b 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production `GYEO` geometry·색·카드·모서리·그림자 token과 기존 조작 atlas로 `ㅕ` 위치·순서·쓰기 연결이 분명하다.
자산 자동 검사: 불필요 — 새 raster 0건, 기존 production atlas 소비 경로 변경 없음
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 변경 전·후 판정

- 변경 전은 `가`·`거` 578 × 578 px 선택 카드 두 개였다. 변경 후는 같은 production 시각 언어로 `가`·`거`·`겨` 578 × 578 px 카드 세 개와 168 × 168 px 홈 조작을 겹침·잘림 없이 보여 준다.
- `gyeo-start.png`, `gyeo-wrong-order.png`, `gyeo-complete.png`, `gyeo-writing.png`, `gyeo-success.png`, `gyeo-next.png`, `home-return.png`는 모두 2340 × 1080이다. 시작·오순서·완성·쓰기에 글자·칸·조작의 잘림·겹침·장식 가림은 0건이다.
- `gyeo-start.xml`의 `기역 조각`·`여 모음 조각`은 각각 341 × 368 px, `왼쪽 기역 칸`·`오른쪽 여 모음 칸`은 각각 284 × 284 px이다. 빈 칸에서 완성 카드는 비활성이고 `ㄱ`→`ㅕ` 순서 후만 `완성한 겨 쓰기 시작`으로 활성된다.
- 오순서 입력은 모음 조각과 왼쪽 칸 외곽을 주황색으로 재강조하고 빈 칸을 유지해 처벌 없이 다시 시도할 수 있다.
- 조립 `겨`와 쓰기 `겨`는 같은 `LessonId.GYEO` production template과 균일 배율을 사용한다. `ㅕ`의 짧은 획 두 개는 세로획 왼쪽에 위·아래로 나란히 보이고 조립·완성·쓰기 모양이 일치한다.

## 쓰기·전환 근거

- `gyeo-writing.xml`의 WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이고 홈·지우기·이전·다음 네 그림 조작은 각각 168 × 168 px이다.
- production 중심선 정방향 4획을 연속 pointer stream으로 입력했고 `gyeo-success.png`에서 완성한 파란 `겨`와 네 조작의 가림·잘림이 0건임을 확인했다.
- 성공 후 `gyeo-next.xml`의 `고 0/3획 완료`로 한 번 자동 이동했고 홈 그림은 `home-return.png`의 자음·모음·가나다 세 메뉴로 복귀했다.

## 역할 QA 판정

- 자동 그래픽 디자인 역할: `가`·`거`·`겨` 선택, `ㄱ`·`ㅕ` 조각, 왼쪽·오른쪽 칸, 완성 활성, 큰 쓰기판이 기존 production 시각 언어를 유지하고 `ㅓ`와 `ㅕ`의 획 수 차이를 분명하게 보여 통과한다.
- 자동 QA 역할: unit·Android lint·debug build·diff, 오순서 거부, 완성 전 비활성, `GYEO` callback, 1962 × 954 px WritingCanvas, 정방향 4획·성공·`GO` 다음·홈을 통과한다.
- 아이 대리 QA: 글을 읽지 않아도 세 글자의 짧은 획 방향·개수, 두 조각과 두 칸의 위치, 오순서 재강조, 완성 활성, 큰 쓰기판·초록 시작점·점선·네 그림 조작으로 과제·시작·방향·성공·초기화·이동을 구분할 수 있어 통과한다.
- 실제 아이 관찰: 실행 안 함.
