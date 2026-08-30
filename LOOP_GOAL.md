# 루프 목표 240 — D1 성능 측정 재현성·홈 렌더 구조 정상화

## 작업 가치 관문

분류: 사용자 지시
사용자 가치: 자동 루프가 흔들리는 성능 표본이나 같은 미세 가설을 반복하지 않고, 아이가 홈 카드에서 글자 쓰기로 이동할 때 실제로 확인된 멈칫을 재현 가능하게 줄이며 개발 결과를 정상적으로 체크포인트·push할 수 있게 한다.
새로운 근거: 루프 239의 같은 이름 대표 흐름에서 total frames가 22~1001까지 크게 흔들렸고, 유효 후보도 176·189·177프레임으로 달랐다. 15개의 단일 미세 가설은 deadline miss 3% 미만을 만들지 못했으며, push 자체는 원격 조회가 가능하지만 완료 관문 미통과 때문에 의도적으로 보류된 상태였다.
중복 방지: 루프 239는 반복 15 차단 이력으로 보존하고 반복 16으로 늘리지 않는다. 먼저 입력·focus·도착 화면·프레임 유효 범위를 고정하는 단일 측정 도구를 만든 뒤, 그 근거로 기존과 다른 렌더 구조 변경 하나만 검증한다. 같은 명령의 표본 수만 늘리거나 3% 기준을 낮추지 않으며, 새 루프도 같은 근본 원인 3회 연속 실패 또는 15회 상한에서 차단한다.

## 목표

루프 238·239에서 검증된 A `햇살 정원 글자 공방` 홈·공통 scene shell, semantics, callback과 production 자산을 보존한다. 정확한 2340 × 1080에서 대표 흐름의 측정 시작·입력·도착 상태를 기계적으로 검증하는 재현 가능한 도구를 먼저 만들고, 그 결과에 따라 홈 화면의 composition·layout·draw 경계를 하나의 더 큰 렌더 구조 단위로 재설계해 D1 성능 관문을 통과한다.

시각 변경: 예
자산 필요 판정: 불필요 — 기존 정원 배경·세 clay 메뉴·action atlas는 APK 포함·production 소비·시각 관문을 통과했다. 새 bitmap을 만들지 않고 측정 도구와 Compose 렌더 구조만 다룬다.

## 고정 측정 계약

1. 첫 반복은 `cold launch → 홈 안정 → 자음 카드 입력 → 선택 화면 확인 → ㄱ 입력 → 쓰기 화면 3초 안정`을 한 명령으로 실행하는 전용 측정 도구를 만든다. `emulator-5554`, package·focus, 입력 좌표, 대기 시간, hierarchy의 홈·선택·쓰기 도착 상태를 모두 검사하고 하나라도 다르면 표본을 무효로 종료한다.
2. 측정 시작 전에 `gfxinfo reset`을 하고 종료 뒤 total frames·deadline miss·frozen frame·slow UI thread·slow draw command·bitmap upload·PSS를 같은 형식으로 저장한다. total frames가 고정 흐름의 예상 범위를 벗어나거나 hierarchy·focus가 다르면 성능 합격 표본으로 사용하지 않는다.
3. 기준 표본은 사전에 고정한 최대 3회의 유효 실행으로 제한한다. 횟수를 늘려 유리한 값만 고르지 않으며, 변경 전·후에 같은 APK 유형·cold launch·입력·대기·판독 규칙을 사용한다.
4. 도구가 재현성을 확보한 뒤 trace 근거로 composition·layout·draw의 가장 큰 경계 하나를 선택한다. 단일 modifier·대기 조정 반복이 아니라, 정적 장면과 상호작용 계층의 invalidation 범위를 분리하는 검증 가능한 렌더 구조 변경 하나를 적용한다.
5. 애니메이션 삭제, 정원·clay 제거, 표본 수 증가, 대기 연장, 기준 완화로 통과시키지 않는다. 기존 60 Hz 모션, 카드 기본·눌림·선택·진입 표현과 callback을 보존한다.

## 성공 조건

1. `./scripts/check-work-value.sh LOOP_GOAL.md`, `./scripts/check-automation.sh`, `./scripts/check-visual-loop.sh`, `./scripts/verify.sh`, `git diff --check`가 통과한다.
2. 전용 측정 도구가 잘못된 focus·도착 화면·프레임 범위를 실패로 판정하고, 유효한 동일 흐름에서 성능·메모리 값을 일관된 파일 구조로 남긴다. 자동 테스트 또는 고정 fixture로 parser와 무효 표본 거부도 검증한다.
3. 변경 전 재현 표본과 trace에서 가장 큰 composition·layout·draw 경계 하나를 특정하고, 반증 가능한 가설과 기존 미세 가설과 다른 렌더 구조 변경 하나를 적용한다.
4. 변경 후 사전에 고정한 유효 표본 모두에서 전체 대표 흐름 deadline miss가 3% 미만이고 frozen frame이 0이며, 최초 전환 진단 miss가 1회 이하이다.
5. 쓰기 안정 PSS가 D0 95,559 KiB 이하이고, 홈 동시 bitmap decode 16 MiB 상한과 기존 자산의 APK 포함·production 소비가 유지된다.
6. 홈 카드 약 688 × 702 px, 보호자 진입, 정원·clay 화풍, 기본·눌림·선택·진입 연출, animator scale 0, 홈→자음→ㄱ 쓰기 callback이 새 APK에서 통과한다.
7. WritingCanvas 1962 × 954 px, 네 조작 168 × 168 px, 교육 geometry, 보호자 비스크롤, 38개 음성, font scale 1.5·2.0과 TalkBack 의미·순서에 회귀가 없다.
8. 자동 Android 성능·아트 디렉션·UI/UX·접근성·아이 대리 QA가 새 APK 근거로 통과하고 새 P0·P1·진행 방해 P2가 0건이다. 실제 아이 관찰과 자동·에뮬레이터·아이 대리 QA를 구분한다.

## 완료 정의

위 조건을 모두 통과하면 루프 240을 완료하고 D1 성능 정상화 체크포인트를 `git push origin HEAD`로 일반 push한다. 다음 단일 루프로 D2 세 선택 화면을 준비만 하며 같은 작업자가 구현하지 않는다. force push·release·배포·실기기 조작은 하지 않는다.
