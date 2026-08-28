# 현재 시각 루프 증거

루프: 215
상태: 완료 — 반복 1 `너` 조립·쓰기·성공·`다` 다음·홈 통과
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop214/iteration1/after/assembly-selection.png
변경 전 hierarchy: captures/loop214/iteration1/after/assembly-selection.xml
변경 후 PNG: captures/loop215/iteration1/after/target-selection.png
변경 후 hierarchy: captures/loop215/iteration1/after/target-selection.xml
성공 PNG: captures/loop215/iteration1/after/neo-success-samepoint.png
성공 hierarchy: captures/loop215/iteration1/after/neo-success-samepoint.txt
package: com.limdo.hangul
focus: 통과 — `captures/loop215/iteration1/after/neo-success-samepoint-focus.txt`의 `mCurrentFocus`·`mFocusedApp`이 LimDo
APK SHA-256: 4b6bc8ff3a83d6078a1931dc0f843d5b82680b09fd1bb26f0373ac0ef0b366c6
Git commit: 현재 HEAD 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — production `NEO` geometry와 기존 조립 token·성공·조작 atlas 재사용
자산 자동 검사: 불필요 — 새 raster 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 루프 212 반복 1 중간 근거

- 상태: 진행 중 — `그` 조립·쓰기 진입 통과, 정방향 성공·다음·홈 미판정
- 변경 전: `captures/loop211/iteration1/after/target-selection.png`, 동일 경로 hierarchy
- 변경 후: `captures/loop212/iteration1/after/target-selection-final.png`, `geu-start.png`, `geu-wrong-order.png`, `geu-complete.png`, `geu-writing.png`과 각 hierarchy·focus
- package: `com.limdo.hangul`; 화면: 2340 × 1080; 물리: 1080 × 2340; `user_rotation=1`; cold boot uptime `8.78`초
- APK SHA-256: `5f077aa66b434eff834e8f6340532cf0e8599c1ae026bb41cf70cb1c353aeca8`
- 자산 필요 판정: 불필요 — production `GEU` geometry·기존 token 재사용, 새 raster 0건
- 자동 그래픽 디자인 역할: 조립 배치 통과, 루프 최종 미판정
- 자동 QA 역할: verify·diff·오순서·진입 통과, 성공·다음·홈 미판정
- 아이 대리 QA: 조립 배치 통과, 성공 결과·다음 행동 미판정. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0(초기 카드 압축 1건은 같은 반복에서 교정)

## 루프 212 반복 2 중간 근거

- 상태: 진행 중 — `GeuAssemblyFlowTest` 1/1로 2획·성공 callback·`GI` 다음·홈 통과, 성공 동시점 화면 미판정
- 새 실행 화면: `captures/loop212/iteration2/after/geu-success.png` 2340 × 1080. 해당 프레임은 성공 overlay가 아니라 두 획 사이 진행 상태이므로 완료 근거로 쓰지 않음
- package: `com.limdo.hangul`; 화면: 2340 × 1080; 물리: 1080 × 2340; `user_rotation=1`; cold boot uptime `22.51`→`364.77`초
- APK SHA-256: `5f077aa66b434eff834e8f6340532cf0e8599c1ae026bb41cf70cb1c353aeca8`
- 자산 필요 판정: 불필요 — production `GEU` geometry·기존 성공·조작 atlas 재사용, 새 raster 0건
- 자동 그래픽 디자인 역할: 미판정; 자동 QA: callback 통과·성공 화면 미판정; 아이 대리 QA: 미판정
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 1(성공 PNG·hierarchy·focus 동시점 근거 누락)

## 현재 통과 근거

- 일곱 선택 카드는 각각 252 × 252 px이고 `규` 선택·`ㄱ`→`ㅠ`·오순서 거부·완성 상태에 잘림·겹침·왜곡이 없다.
- WritingCanvas는 `[189,63][2151,1017]`=1962 × 954 px이고 네 그림 조작은 각각 168 × 168 px이다.
- `attempt4/gyu-success.png`는 완성 `규`·큰 초록 체크와 도마뱀·네 조작을 동시에 보여 주고, 같은 노출 구간의 hierarchy는 정답 이미지·완성 Canvas·네 callback을 모두 노출한다.
- 수집 시 uptime은 `289.08`초로 7,200초 미만이고, 물리 1080 × 2340·`user_rotation=1`·LimDo 2340 × 1080 focus를 확인했다.

## 역할 QA 판정

- 자동 그래픽 디자인: `ㅠ`의 아래쪽 짧은 세로획 두 개와 완성 `규`가 선택·조립·쓰기·성공에서 일치하고 잘림·왜곡·조작 가림이 0건이어 통과한다.
- 자동 QA: `verify.sh`·diff·`GyuAssemblyFlowTest` 1/1·오순서 거부·정방향 4획·성공·`GEU` 다음·홈이 통과했다.
- 아이 대리 QA: 글을 읽지 않아도 큰 `ㄱ`·`ㅠ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판과 정답 체크를 순서대로 구분할 수 있어 통과한다. 실제 아이 관찰: 실행 안 함.

## 루프 212 반복 3 최종 근거

- 상태: 완료 — `그` 성공 동시점 PNG·Compose hierarchy·focus 통과
- 변경 전: `captures/loop211/iteration1/after/target-selection.png`와 hierarchy
- 변경 후: `captures/loop212/iteration1/after/target-selection-final.png`와 hierarchy
- 성공 동시점: `captures/loop212/iteration3/after/geu-success-samepoint.png`, `geu-success-samepoint-hierarchy.txt`, `geu-success-samepoint-focus.txt`
- 환경: 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo, uptime `436.66`초로 7,200초 미만
- APK SHA-256: `5f077aa66b434eff834e8f6340532cf0e8599c1ae026bb41cf70cb1c353aeca8`
- 자산 필요 판정: 불필요 — production `GEU` geometry와 기존 성공·조작 atlas 재사용, 새 raster 0건
- hierarchy: `정답이에요`, `그를 2획으로`, 홈·다시쓰기·이전·다음 callback을 같은 성공 고정 구간에서 노출
- 자동 그래픽 디자인 역할: 통과 — 완성 `그`가 가장 크고, 체크·도마뱀·별·색종이는 뒤에서 결과를 강조하며 글자와 네 조작을 가리지 않는다.
- 자동 QA 역할: 통과 — verify·diff·`GeuAssemblyFlowTest` 1/1, 2획 성공·`GI` 다음·홈, 1962 × 954 px WritingCanvas와 네 168 × 168 px 조작을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 초성 아래의 단일 평행 가로획, 완성 글자, 큰 체크와 기쁜 캐릭터, 네 그림 조작으로 과제·성공·다음 행동을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0

## 루프 213 반복 2 최종 근거

- 변경 전: `captures/loop212/iteration1/after/target-selection-final.png`와 hierarchy. 변경 후: `captures/loop213/iteration1/after/target-selection-final.png`와 hierarchy.
- 성공 동시점: `captures/loop213/iteration2/after/gi-success-samepoint.png`, `gi-success-samepoint-hierarchy.txt`, `gi-success-samepoint-focus.txt`.
- 환경: `alarmquest-qa` cold boot uptime `8.72`초에서 시작, 성공 focus 수집 uptime `237.09`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo.
- APK SHA-256: `dad728f0dee18926c5aa900af7844fd80513dca40dfb41a9233c2171ba4a2c44`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- hierarchy: `정답이에요`, `기를 2획으로`, 홈·다시쓰기·이전·다음 callback을 같은 성공 고정 구간에서 노출한다.
- 자산 필요 판정: 불필요 — production `GI` geometry와 기존 성공·조작 atlas 재사용. 새 raster 0건.
- 자동 그래픽 디자인 역할: 통과 — 완성 `기`가 가장 크고, 체크·도마뱀·별·색종이는 글자 뒤에서 결과를 강조하며 글자·네 조작 가림·잘림·왜곡·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·diff·시각 계약·`GiAssemblyFlowTest` 1/1, 2획 성공·`RA` 다음·홈, WritingCanvas 1962 × 954 px와 네 168 × 168 px 조작을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 왼쪽 `ㄱ`·오른쪽 단일 세로획, 완성 글자, 큰 체크와 기쁜 캐릭터, 네 그림 조작으로 과제·성공·다음 행동을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
