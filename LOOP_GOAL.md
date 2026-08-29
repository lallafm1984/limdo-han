# 루프 목표 238 — 프리미엄 디자인 D1 홈·공통 scene shell

## 작업 가치 관문

분류: 사용자 지시
사용자 가치: 글을 읽지 못하는 아이가 첫 화면에서 세 학습 과제를 더 풍족하고 일관된 모양·색·공간으로 구분하고, 큰 카드와 보호자 진입을 잃지 않은 채 프리미엄 학습 세계에 들어갈 수 있다.
새로운 근거: 루프 237 D0에서 production 홈의 단색 면·반복 카드가 브랜드와 공간감을 주지 못한다고 확인했고, 사용자는 preview 세 방향 중 A `햇살 정원 글자 공방`을 production 방향으로 선택했다.
중복 방지: 이번 루프는 홈과 이후 화면이 공유할 scene shell, 세 메뉴의 기본·눌림·선택 상태, 짧은 홈 진입 연출만 구현한다. 선택 화면 내부·쓰기·성공·보호자 화면군과 D2 이후 작업은 시작하지 않는다.

## 목표

`docs/전-씬-프리미엄-디자인-루프-작업지시.md`의 D1을 수행한다. 선택된 A 방향을 full-screen preview 통째 붙이기가 아닌 측정된 슬롯별 production 자산과 실제 Compose semantics·callback으로 분해해 홈·공통 scene shell에 적용한다. 정확한 2340 × 1080에서 변경 전·후와 기본·눌림·선택·진입 시작·중간·끝을 검증한다.

시각 변경: 예
자산 필요 판정: 필요 — 정원 배경 깊이와 메뉴별 일관된 clay 그림을 기기 간 같은 화풍으로 보여 주려면 선택안 A를 기준으로 슬롯별 RGBA bitmap을 생성·검사해 production `res`와 소비 코드에 연결해야 한다.

## 성공 조건

1. D1 범위 자동 검사와 `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`, `scripts/check-automation.sh`가 통과한다.
2. 홈의 세 메뉴 touch bounds 약 688 × 702 px와 왼쪽 위 보호자 진입 callback을 유지하고 겹침·잘림이 없다.
3. A 방향의 warm cream 정원, matte clay surface, 좌상단 광원과 blue·orange·green 메뉴 식별을 공통 scene shell과 세 메뉴에 일관되게 적용한다.
4. 자음·모음·가나다를 글을 읽지 않아도 서로 다른 실제 그림·색·짧은 시범으로 구분하며 bitmap에 한글·숫자·가짜 UI·차량·워터마크가 없다.
5. 세 메뉴의 기본·눌림·선택과 홈 진입 연출 시작·중간·끝이 입력을 가로채지 않고 animator scale 0에서 같은 의미의 정적 상태를 보존한다.
6. 생성 자산의 RGBA·alpha bbox·여백·균일 배율·APK 포함·production 소비와 현재 APK 대비 decode·PSS·framestats 회귀를 검사한다.
7. WritingCanvas 1962 × 954 px, 네 조작 168 × 168 px, 교육 geometry, 보호자 비스크롤 계약과 승인된 38개 음성 기능에 회귀가 없다.
8. 자동 아트 디렉션·UI/UX·그래픽 자산·모션·성능·접근성·아이 대리 QA가 통과하고 새 P0·P1·진행 방해 P2가 0건이다.

## 완료 정의

D1 홈·공통 shell만 새 production APK와 2340 × 1080 화면으로 통과하면 D2 세 선택 화면을 다음 단일 루프로 준비한다. 같은 작업자는 D2를 구현하지 않는다. 완료 체크포인트만 `git push origin HEAD`로 일반 push한다.
