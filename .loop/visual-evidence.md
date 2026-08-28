# 현재 시각 루프 증거

루프: 208
상태: 완료 — 반복 1 제품·자동·실화면·역할 QA 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop208/iteration1/before/target-selection.png
변경 전 hierarchy: captures/loop208/iteration1/before/target-selection.xml
변경 후 PNG: captures/loop208/iteration1/after/target-selection.png
변경 후 hierarchy: captures/loop208/iteration1/after/target-selection.xml
package: com.limdo.hangul
focus: 통과 — `captures/loop208/iteration1/after/focus.txt`의 `mCurrentFocus`·`mFocusedApp`이 `com.limdo.hangul/.MainActivity`
APK SHA-256: aa5068e585fbe2fb6c7780b5ec7b00f3781b082581036a10f67090e53482aef1
Git commit: 현재 HEAD 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production `GO` geometry·기존 카드 token·그림 조작 atlas가 위·아래 조립과 쓰기 연결을 직접 보여 준다.
자산 자동 검사: 불필요 — 새 raster 0건, 기존 production atlas 소비 경로 변경 없음
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 변경 전·후 판정

- 변경 전은 `가`·`거`·`겨` 578 × 578 px 선택 카드 세 개였다. 변경 후는 같은 시각 언어로 `가`·`거`·`겨`·`고` 394 × 394 px 카드 네 개와 168 × 168 px 홈 조작을 겹침·잘림 없이 보여 준다.
- `go-start.png`, `go-wrong-order.png`, `go-complete.png`, `go-writing.png`, `go-success.png`, `home-return.png`는 모두 2340 × 1080이다. 조각·칸·완성 글자·쓰기판·조작의 잘림·겹침·장식 가림은 0건이다.
- `go-start.xml`의 `기역 조각`·`오 모음 조각`은 각각 341 × 368 px, `위쪽 기역 칸`·`아래쪽 오 모음 칸`은 각각 284 × 284 px이다. 빈 상태에서 완성 카드는 비활성이고 `ㄱ`→`ㅗ` 순서 뒤에만 `완성한 고 쓰기 시작`으로 활성된다.
- 모음을 먼저 누르면 완성되지 않고 다시 안내되며, 초성 위·가로 모음 아래의 위치가 좌우형 `가·거·겨`와 분명히 다르다.
- 조립 조각·완성 카드·쓰기 화면은 모두 `LessonId.GO`의 정사각형 em·균일 배율 production template을 사용한다.

## 쓰기·전환 근거

- `go-writing.xml`의 WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이고 홈·지우기·이전·다음 네 그림 조작은 각각 168 × 168 px이다.
- production 중심선의 `ㄱ` 꺾임 1획, `ㅗ` 가로 1획, 아래에서 위로 향하는 세로 1획을 입력해 `go-success.png`의 완성한 파란 `고`와 성공 배경을 확인했다.
- 성공 최소 노출 뒤 `go-success.xml`은 `교 0/4획 완료`로 한 번 자동 이동했고, 홈 그림은 `home-return.png`의 세 메뉴 홈으로 복귀했다.

## 역할 QA 판정

- 자동 그래픽 디자인 역할: 네 선택 글자와 위·아래 조립 칸이 기존 색·모서리·선 굵기를 유지하며, 가로 모음의 위치 관계를 새 장식 없이 가장 강한 과제로 보여 통과한다.
- 자동 QA 역할: unit·Android lint·debug build·diff, 오순서 거부, 완성 전 비활성, `GO` callback, 1962 × 954 px WritingCanvas, 정방향 3획·성공·`GYO` 다음·홈을 통과한다.
- 아이 대리 QA: 글을 읽지 않아도 `ㄱ`과 `ㅗ` 조각, 위·아래 빈 칸, 오순서 재강조, 노란 완성 카드, 큰 쓰기판·초록 시작점·점선·네 그림 조작으로 과제·시작·방향·성공·초기화·이동을 구분할 수 있어 통과한다.
- 실제 아이 관찰: 실행 안 함.
