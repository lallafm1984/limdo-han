# 현재 시각 루프 증거

루프: 206
상태: 완료 — 반복 3 최종 증거 연결·역할 QA 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop205/iteration3/emulator/assembly-initial.png
변경 전 hierarchy: captures/loop205/iteration3/emulator/assembly-initial.xml
변경 후 PNG: captures/loop206/iteration2/emulator/target-selection.png
변경 후 hierarchy: captures/loop206/iteration2/emulator/target-selection.xml
package: com.limdo.hangul
focus: 통과 — 반복 2 입력·캡처 전후 `mCurrentFocus`·`mFocusedApp` `com.limdo.hangul/.MainActivity`
APK SHA-256: 115c25823c8e3625aabdf789346bd3c1d373a7f80fe410ee6d3056fcc55c2f48
Git commit: 5c085d28406911d4aabc5bf53680a0ff1f6fdf54 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production 글자 geometry·색·카드·모서리·그림자 token과 기존 조작 atlas로 조립 위치·순서·완성·쓰기가 분명하다.
자산 자동 검사: 불필요 — 새 raster 0건, 기존 production atlas 소비 경로 변경 없음
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 변경 전·후 판정

- 변경 전의 루프 205 조립 초기 상태는 `ㄱ + ㅏ → 가` 한 목표로 바로 진입했다. 변경 후에는 같은 production 배경·카드·굵기·색을 유지하면서 `가`·`거` 578 × 578 px 선택 카드 두 개와 168 × 168 px 홈 조작을 겹침 없이 보여 모양으로 목표를 구분한다.
- `target-selection.png`, `geo-start.png`, `geo-complete.png`, `geo-writing-fixed2.png`, `geo-success-fixed.png`, `geo-next.png`, `home-return.png`는 모두 2340 × 1080이며 각 PNG에 대응하는 hierarchy가 보존돼 있다. 잘림·겹침·장식 가림은 0건이다.
- `geo-start` hierarchy의 `기역 조각`·`어 모음 조각`은 각각 341 × 368 px, `왼쪽 기역 칸`·`오른쪽 어 모음 칸`은 각각 284 × 284 px이다. 빈 칸에서 완성 카드는 비활성이고 `ㄱ`→`ㅓ` 순서 후만 `완성한 거 쓰기 시작`으로 활성된다.
- 조립의 `거`와 쓰기 화면은 같은 `LessonId.GEO` production template을 정사각형 em에서 균일 배율로 사용한다. `ㅓ`의 짧은 획은 세로획 왼쪽에 있고 조립·완성·쓰기 형태가 일치한다.

## 쓰기·전환 근거

- `geo-writing-fixed2.xml`의 WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이며 홈·지우기·이전·다음 네 그림 조작은 각각 168 × 168 px이다.
- production 중심선 정방향 3획이 `0/3`→성공으로 진행했고 `geo-success-fixed.png`에서 완성 `거`·큰 초록 체크·네 조작이 서로 가리지 않았다.
- 성공 후 2초 뒤 `geo-next.xml`의 `겨 0/4획 완료`로 한 번 이동했고, 홈 그림은 `home-return.png`의 자음·모음·가나다 세 메뉴로 복귀했다.

## 역할 QA 판정

- 자동 그래픽 디자인 역할: `가`·`거` 선택, `ㄱ`·`ㅓ` 조각, 왼쪽·오른쪽 칸, 완성 활성, 큰 쓰기판이 기존 production 시각 언어를 유지하며 글자 길·시작점·조작·성공 표식 가림이 없어 통과한다.
- 자동 QA 역할: unit·Android lint·debug build·diff·시각 계약, 오순서 거부, 완성 전 비활성, `GEO` controller·진도 범위, 1962 × 954 px WritingCanvas, 정방향 3획·성공·`GYEO` 다음·홈을 통과한다.
- 아이 대리 QA: 글을 읽지 않아도 `가`·`거`의 짧은 획 방향, 두 조각과 두 칸의 위치, 비활·완성 상태, 큰 쓰기판·초록 시작점·점선·정답 연출·네 그림 조작으로 과제·시작·방향·성공·초기화·이동을 구분할 수 있어 통과한다.
- 실제 아이 관찰: 실행 안 함.
