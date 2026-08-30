# 현재 시각 루프 증거

## 루프 237 반복 7 저장 실패 복구 inventory

- 시각 변경: 아니오. production 배치·자산·색·callback은 유지하고 저장 실패 복구 상태를 새로 수집했다.
- 새 근거: `captures/loop237/iteration7/inventory/28-guardian-recording-save-failed-original-preserved.png`·hierarchy·focus. 2340 × 1080, LimDo focus, APK SHA-256 `c944e2b6be137f46156c1db8f7b93fde6fdac3a161445710321d0ebaf9d0b9a3`.
- 직접 판정: 기존 확정 녹음이 `녹음 완료`로 보존되고 듣기·다시 녹음·삭제가 각 389 × 194 px로 활성이다. 잘림·겹침·가림·상태 오판정 0건.
- 자동 그래픽 디자인 역할: 통과. 자동 QA 역할: 통과. 아이 대리 QA: 통과. 실제 아이 관찰: 실행 안 함. 새 P0·P1·진행 방해 P2: 0건.

루프: 237
상태: 진행 중 — 반복 1 보호자 녹음 삭제 확인·취소 보존 통과, D0 나머지 조건 대기
기준 화면: 2340 × 1080
변경 전 PNG: captures/loop194/iteration7/emulator/nieun-ready.png
변경 전 hierarchy: captures/loop194/iteration7/emulator/nieun-ready.xml
변경 후 PNG: captures/loop237/iteration1/after/delete-confirm.png
변경 후 hierarchy: captures/loop237/iteration1/after/delete-confirm.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 7f72cd4839e54736bb3658a1fec7520619037dfc5bc146f65a8a0dd13162c918
Git commit: 현재 HEAD 기준 미커밋 작업 트리
자산 필요 판정: 불필요 — 삭제 안전 modal은 기존 보호자 Compose token과 큰 버튼으로 충분하며 새 bitmap이 과제를 더 명확하게 하지 않음
production 자산 경로: 새 자산 없음
production 소비 검사: 통과
자산 자동 검사: 통과
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 루프 236 반복 1 중간 근거

- 환경: `alarmquest-qa` snapshot 없는 cold boot uptime `9.76`초에서 시작해 최종 화면 확인 `228.23`초, 물리 1080 × 2340, `user_rotation=1`, PNG 2340 × 1080, LimDo focus를 확인했다.
- 화면: `사용자 녹음 사용` touch bounds는 `[1568,80][2266,274]`=698 × 194 px이다. 켜짐은 초록 track·`사용 중`, 꺼짐은 회색 track·`기본 음성 사용`으로 구분되고 목록 버튼·제목·본문과 잘림·겹침이 없다. 강제 종료·재실행 뒤 꺼짐이 유지됐다.
- 음성: 네 M4A는 AAC mono 22,050 Hz, duration 0.374~0.420초이며 SHA-256이 모두 다르다. PCM16 decode peak는 `기역 16716·니은 15627·디귿 18041·리을 15969`로 무음이 아니며 APK `res/raw`에 네 파일이 포함됐다.
- 실제 재생: `DefaultGuardianVoicePlaybackTest` 1/1이 에뮬레이터와 SM-S931N 양쪽에서 네 자음의 재생 시작·완료 callback·수동 정지를 통과했다. 실기기의 기존 사용자 녹음은 재생 뒤 `READY`로 복귀해 보존됐다.
- 아이 대리 QA: 보호자 설정은 아이 쓰기판을 줄이거나 가리지 않고, 쓰기에서는 선택된 출처의 짧은 자음 이름만 들려준다. 실제 아이 관찰: 실행 안 함.
- 남은 관문: 로컬 Yuna 후보의 자연스러움·발음은 자동 판정으로 과장하지 않고 사용자의 실기기 청취 판정을 기다린다.
- 실기기 데이터 사고: SM-S931N의 connected test 정리 단계가 `com.limdo.hangul`을 제거했고 기존 `noBackupFilesDir` 녹음은 복구본을 찾지 못했다. 동일 SHA-256 APK를 즉시 재설치했으나 `firstInstallTime=2026-08-29 15:27:42`로 초기화가 확인된다. 이후 실기기 connected test 금지 규칙을 `QA_CHECKLIST.md`와 `docs/루프-엔지니어링-적용.md`에 고정했다.

## 루프 236 반복 2 중간 근거

- 음성 교체: 사용자가 기각한 Yuna 네 파일을 TTSFree 한국어 신경망 `InJoon`으로 직접 생성한 고정 자산으로 교체했다. 특정 인물·캐릭터 음성을 복제하지 않았고 앱의 runtime TTS·네트워크 호출은 추가하지 않았다.
- 최종 자산: `기역` 0.608초·SHA-256 `de26c1b8...5657e3`, `니은` 0.681초·`98c6b848...cbdadc`, `디귿` 0.629초·`85670bb9...5776a2`, `리을` 0.692초·`217500b3...1da54b`로 네 파일이 모두 다르다. 최종 AAC를 이어 붙여 큰 한국어 음성인식 모델로 다시 읽은 결과는 `기역·니은·디귿·리을`이다.
- 자동 검증: `./scripts/verify.sh`·`git diff --check`·`scripts/check-visual-loop.sh` 통과, APK `res/raw` 네 파일 포함과 APK SHA-256 `ad66ab9b9d812760c33e765ba7d7f96cb527c9b89ff09e5ec8b0f2ab3cb735a5`를 확인했다. 에뮬레이터 전용 `DefaultGuardianVoicePlaybackTest` 1/1에서 네 재생 완료·수동 정지가 통과했다.
- 에뮬레이터: 기존 uptime `25449.82`초가 7,200초 한도를 넘어 완전 종료 후 snapshot 없는 cold boot를 수행했다. 새 uptime `9.45`초, 물리 1080 × 2340, `user_rotation=1`, 앱 화면 2340 × 1080을 확인했다. 음성만 교체해 반복 1의 보호자 토글 layout은 변하지 않았다.
- 무선 전달: ADB 서버를 재시작하고 `adb devices -l`·`adb mdns services`·이전 주소 재연결을 확인했지만 SM-S931N이 검색되지 않았다. 이번 APK를 실기기에 설치했다고 표현하지 않으며, 실기기 connected test는 실행하지 않는다.
- 남은 관문: 사용자가 SM-S931N에서 네 후보를 직접 듣고 자연스러움·발음을 승인해야 한다. 실제 아이 관찰: 실행 안 함.

## 루프 236 반복 2 무선 설치 보강 근거

- SM-S931N에 `install -r --no-streaming`이 `Success`였고, 설치 전후 `no_backup` 파일 수는 11개로 유지됐다. 실기기 계측은 실행하지 않았다.
- 빌드 APK와 실기기 `base.apk` SHA-256은 모두 `ad66ab9b9d812760c33e765ba7d7f96cb527c9b89ff09e5ec8b0f2ab3cb735a5`로 일치한다.
- versionCode 1·versionName 0.1.0, cold launch 375 ms, `mCurrentFocus`·`mFocusedApp` LimDo, keyguard=false, 치명 오류 0건이다.
- `/tmp/limdo-loop236-ttsfree-device.png`은 정확한 2340 × 1080이며 hierarchy의 LimDo package node는 12개다. 화면 잘림·시스템 바 노출·다른 앱 가림은 보이지 않는다.
- 남은 관문은 사용자의 네 음성 자연스러움·발음 직접 청취뿐이다. 실제 아이 관찰: 실행 안 함.

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

## 루프 233 반복 1 최종 근거

- 변경 전: `captures/loop232/iteration1/after/loop232-iteration1-target-selection.png`와 hierarchy. 변경 후: `captures/loop233/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공·다음 PNG와 각 hierarchy·focus.
- 환경: 기존 serial이 없어 `alarmquest-qa`를 snapshot 없이 cold boot, uptime `9.87`초에서 시작해 최종 수집 `100.06`초, 물리 1080 × 2340, `user_rotation=1`, 모든 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `973559e0283c53679b8f66069dcbf1259d266aa9ded75f7873464b2adf11480d`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 기존 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px를 유지했다.
- 자산 필요 판정: 불필요 — production `RA`·`DEO` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `라`의 오른쪽 짧은 획과 `러`의 왼쪽 짧은 획이 선택·조립·쓰기에서 분명하고, 조립 카드·완성 글자·쓰기 길·성공 overlay의 잘림·왜곡·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`ReoAssemblyFlowTest` 1/1, 오순서 거부·정방향 5획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄹ·ㅓ` 조각, 좌·우 빈칸, 주황 재안내, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작의 semantics를 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 232 반복 1 최종 근거

- 변경 전: `captures/loop231/iteration1/after/loop231-iteration1-target-selection.png`. 변경 후: `captures/loop232/iteration1/after/`의 `라` 선택·시작·오순서·완성·쓰기·성공·다음 PNG·hierarchy·focus.
- 환경: `alarmquest-qa` cold boot, uptime `9.82`→`34.76`초, 물리 1080 × 2340, `user_rotation=1`, 모든 PNG 2340 × 1080, 7개 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `e5e7326f88a3bd422e687e592badb384aded1a53006ddeccf699cae699182d9c`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `RA` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다.
- 자동 그래픽 디자인 역할: 통과 — `디`와 3층 `ㄹ`의 `라`가 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`RaAssemblyFlowTest` 1/1, 오순서 거부·정방향 5획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과. 실제 아이 관찰: 실행 안 함. 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 226 반복 1 최종 근거

- 변경 전: `captures/loop225/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop226/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공·다음 PNG와 각 hierarchy·focus.
- 환경: serial이 없어 `alarmquest-qa`를 snapshot 없이 cold boot했고 uptime `9.77`초에서 시작해 최종 수집 `112.21`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 일곱 상태의 `mCurrentFocus`·`mFocusedApp` 모두 `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `59df617d509357516e0504ea806e9fbeff6e1cc308389b7417c99f7a495774ec`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 선택 카드 22개는 8열×3행이며 `도` 카드 `[1502,661][1702,861]`=200 × 200 px, 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px다.
- 자산 필요 판정: 불필요 — production `DO` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비는 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — 첫 7열×4행 후보에서 마지막 `도` 카드가 144 px 높이로 압축된 진행 방해 P2를 8열×3행으로 교정했다. 최종 화면에서 `뎌`의 오른쪽 세로 모음과 `도`의 아래 가로 모음, 조립·쓰기·성공의 `ㄷ·ㅗ`가 분명하고 잘림·겹침·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`의 159개 unit·lint·debug build, `git diff --check`, `DoAssemblyFlowTest` 1/1, 오순서 거부·정방향 4획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅗ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 초록 시작점·점선, 정답 체크와 네 그림 조작을 순서대로 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 223 반복 1 최종 근거

- 변경 전: `captures/loop223/iteration1/before/target-selection.png`와 hierarchy. 변경 후: `captures/loop223/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공·다음 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.86`초에서 시작해 최종 기록 7,200초 미만, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `3117083cbe14a4acc4c9f960c21e2ca2db053c542df938de8b5896feeb66e11f`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 선택 `다` 카드 `[1428,698][1680,950]`=252 × 252 px, 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px. 성공 overlay는 쓰기판 내부에 머물렀다.
- 자산 필요 판정: 불필요 — production `DA` geometry와 기존 조립 token·정답·조작 atlas 재사용, 새 raster 0건. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — 첫 6열 후보의 `다` 카드 126 px 높이 P2를 7열×3행으로 교정했고, `나`의 열린 `ㄴ`과 `다`의 닫힌 `ㄷ`, 조립·쓰기·성공에서 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DaAssemblyFlowTest` 1/1, 오순서 거부·production 4획·성공·`라` 다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 큰 `ㄷ·ㅏ` 조각, 좌·우 빈칸, 주황 재안내, 완성 카드, 큰 쓰기판, 큰 체크와 기쁜 캐릭터, 네 그림 조작을 순서대로 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 221 반복 1 최종 근거

- 변경 전: `captures/loop220/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop221/iteration1/after/` 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.73`초에서 시작해 최종 수집 `32.25`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 여섯 focus 모두 `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `f17f5c4febf24f0f315d8470ea3c86a65721c5622732b2acdccbde49c0e7289c`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `NEU` geometry와 기존 정답·조작 atlas 재사용, 새 raster 0건. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `뉴`의 아래쪽 짧은 획 두 개와 `느`의 단일 평행 가로획이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`NeuAssemblyFlowTest` 1/1, 오순서 거부·정방향 2획·성공·`다` 다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄴ·ㅡ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 성공 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 218 반복 1 최종 근거

- 변경 전: `captures/loop217/iteration2/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop218/iteration1/after/` 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` cold boot uptime `9.79`초, 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, 여섯 focus 모두 LimDo.
- APK SHA-256: `26d4b731da9a3c1558d070dead6884c7ea71a2b7a2dc419c9e83b88c98f025d5`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px. 조립 조각·칸은 검증된 341 × 368 px·284 × 284 px 배치를 재사용한다.
- 자산 필요 판정: 불필요 — production `NYO` geometry와 기존 정답·조작 atlas 재사용, 새 raster 0건. 자산 자동 검사 대상 없음.
- 자동 그래픽 디자인 역할: 통과 — `노`의 짧은 위쪽 획 하나와 `뇨`의 두 획이 선택·조립·쓰기·성공에서 구분되며 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`NyoAssemblyFlowTest` 1/1, 오순서 거부·정방향 4획·성공·`DA` 다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄴ`·`ㅛ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 큰 체크와 기쁜 캐릭터, 네 그림 조작을 순서대로 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 217 반복 2 최종 근거

- 변경 전: `captures/loop216/iteration2/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop217/iteration2/after/` 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` cold boot uptime `8.83`초에서 시작, 최종 수집 uptime `131.33`초, 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, 모든 focus LimDo.
- APK SHA-256: `af91161a00307e6bda12fa7eb4ba2b3f9412a30ed0fa6944617facba3c9c0849`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `NO` geometry와 기존 정답·조작 atlas 재사용, 새 raster 0건.
- 자산 자동 검사: 새 자산 대상 없음. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `녀`의 좌·우와 `노`의 위·아래 구조가 구분되고, 정착 프레임의 큰 체크·도마뱀·완성 `노`·네 조작 잘림·가림·halo·검은 배경 0건.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`NoAssemblyFlowTest` 1/1, 오순서 거부·정방향 3획·성공·`DA` 다음·홈 통과.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄴ`·`ㅗ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 큰 체크와 기쁜 캐릭터, 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 216 반복 2 최종 근거

- 변경 전: `captures/loop215/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop216/iteration2/after/target-selection.png`·`nyeo-start.png`·`nyeo-wrong-order.png`·`nyeo-complete.png`·`nyeo-writing.png`·`nyeo-success.png`와 각 Compose hierarchy·focus.
- 환경: cold boot uptime `9.40`→`70.75`초, 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, 모든 focus LimDo. APK SHA-256 `ace011229f13bee11a461d8aa41d643025c0075edbf17199c0ec121eedd72b66`.
- 실측: 선택 카드 12개 각 252 × 252 px, 조립 조각 341 × 368 px, 두 칸 284 × 284 px, WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `NYEO` geometry·기존 token·atlas 재사용, 새 raster 0건.
- 자동 그래픽 디자인 역할: 통과 — `녀`의 왼쪽 `ㄴ`·오른쪽 `ㅕ` 두 짧은 획이 전 상태에서 일치하며 잘림·왜곡·가림은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·diff·`NyeoAssemblyFlowTest` 1/1, 오순서 거부·정방향 4획·성공·`다` 다음·홈 통과.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `너`의 획 하나와 `녀`의 두 획, 조각·빈칸·재안내·완성·쓰기·성공·네 그림 조작을 순서대로 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 219 반복 2 최종 근거

- 변경 전: `captures/loop219/iteration1/after/nu-success-retry.png`와 hierarchy. 변경 후: `captures/loop219/iteration2/after/` 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` cold boot uptime `9.74`초에서 시작, 최종 수집 `35.90`초, 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, 모든 focus LimDo.
- APK SHA-256: `5ebbd4bc44cfcf73a8d41b37247a67ddb2303be366f493c408a7fa68ed520562`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px. 성공 overlay bounds `[189,0][2151,1080]`.
- 자산 필요 판정: 불필요 — production `NU` geometry와 기존 정답·조작 atlas 재사용, 새 raster 0건.
- 자산 자동 검사: 새 자산 대상 없음. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — 변경 전 전체 화면 atlas의 시각 침범을 제거했고, 변경 후 완성 `누`·큰 체크·도마뱀·네 그림 조작의 가림·잘림·왜곡·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`NuAssemblyFlowTest` 1/1, 오순서 거부·정방향 3획·성공·`DA` 다음·홈 통과.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄴ·ㅜ` 조각, 위·아래 빈칸, 재안내, 완성 카드, 큰 쓰기판, 성공 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 220 반복 1 최종 근거

- 변경 전: `captures/loop219/iteration2/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop220/iteration1/after/` 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.76`초에서 시작해 최종 수집 `73.77`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 여섯 focus 모두 `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `8c76da8f674964e70a4f905737698795b13b2330c99761ced1a5742bdff83b7c`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px. 성공 overlay는 `[189,0][2151,1080]`의 쓰기판 내부에만 머물렀다.
- 자산 필요 판정: 불필요 — production `NYU` geometry와 기존 정답·조작 atlas 재사용, 새 raster 0건. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `누`의 아래쪽 짧은 획 하나와 `뉴`의 두 획이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`NyuAssemblyFlowTest` 1/1, 오순서 거부·정방향 4획·성공·`DA` 다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄴ·ㅠ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 성공 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 222 반복 1 최종 근거

- 변경 전: `captures/loop221/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop222/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.82`초에서 시작해 최종 기록 `35.51`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 여섯 focus 모두 `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `ac581ad43d3dc9dbaff354fa495c72489e7ab8b0dd4db269298e3614351483f2`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px. 성공 overlay는 쓰기판 내부에 머물렀다.
- 자산 필요 판정: 불필요 — production `NI` geometry와 기존 조립 token·정답·조작 atlas 재사용, 새 raster 0건. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `느`의 아래쪽 가로 모음과 `니`의 오른쪽 세로 모음이 선택·조립·쓰기에서 분명히 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`NiAssemblyFlowTest` 1/1, 오순서 거부·정방향 2획·성공·`다` 다음·홈 통과.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄴ·ㅣ` 조각, 좌·우 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 큰 체크와 기쁜 캐릭터, 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 223 반복 1 최종 근거 최신 위치 정정

- 전체 근거가 과거 동일 문장 anchor 때문에 88행 부근에 먼저 덧붙여졌다. `루프 223 반복 1 최종 근거` 절과 이 절을 최신 유효 근거로 사용한다.
- 최종 파일은 `captures/loop223/iteration1/after/`, APK SHA-256 `3117083cbe14a4acc4c9f960c21e2ca2db053c542df938de8b5896feeb66e11f`, 자동 그래픽 디자인·자동 QA·아이 대리 QA 통과, 새 P0·P1·진행 방해 P2 0건이다.
## 루프 224 반복 1 최종 근거

- 변경 전: `captures/loop223/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop224/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `8.86`초에서 시작해 최종 기록 `47.20`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `545b582c81bd47a77defa16c1589f252208635ea016dec0d4eb15f1b8d7f11c7`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `DEO` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다.
- 자동 그래픽 디자인 역할: 통과 — `다`의 오른쪽 짧은 획과 `더`의 왼쪽 짧은 획이 선택·조립·쓰기에서 분명하고, 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DeoAssemblyFlowTest` 1/1, 오순서 거부·정방향 4획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅓ` 조각, 좌·우 빈칸, 주황 재안내, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 225 반복 1 최종 근거

- 변경 전: `captures/loop224/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop225/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.91`초에서 시작해 최종 기록 `37.87`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `e103b3dd0e516e50583d59017a4e902b756632d3df2480edf6579154e5f4a27e`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `DYEO` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다.
- 자동 그래픽 디자인 역할: 통과 — `더`의 왼쪽 짧은 획 하나와 `뎌`의 두 획이 선택·조립·쓰기에서 분명하고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DyeoAssemblyFlowTest` 1/1, 오순서 거부·정방향 5획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅕ` 조각, 좌·우 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 227 반복 1 최종 근거

- 변경 전: `captures/loop226/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop227/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `13.02`초에서 시작해 최종 수집 `68.54`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `8c58f95930ad891b95ab09d93fc8cd9ba37cd2ec9bd9280148c4b272b9689b12`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `DYO` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `도`의 짧은 위쪽 획 하나와 `됴`의 두 획이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DyoAssemblyFlowTest` 1/1, 오순서 거부·정방향 5획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅛ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 228 반복 1 최종 근거

- 변경 전: `captures/loop227/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop228/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.77`초에서 시작해 최종 수집 `35.93`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 상태 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `53101e99ce445823bc21bd4a1c84bfbdb90d66f9bada001642bbfa7318767d6e`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `DU` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `도·됴`의 위쪽 짧은 획과 `두`의 아래쪽 짧은 획이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DuAssemblyFlowTest` 1/1, 오순서 거부·정방향 4획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅜ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 229 반복 1 최종 근거

- 변경 전: `captures/loop228/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop229/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.50`초에서 시작해 최종 수집 `52.05`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `d65dc893edf25f6b97d0357322abe2ad1c1fda295e6213943b2446f0b4ce45cd`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 직전 루프와 동일한 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px를 유지했다.
- 자산 필요 판정: 불필요 — production `DYU` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `두`의 아래쪽 짧은 획 하나와 `듀`의 두 획이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DyuAssemblyFlowTest` 1/1, 오순서 거부·정방향 5획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅠ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 230 반복 1 최종 근거

- 변경 전: `captures/loop229/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop230/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공·다음 PNG와 각 hierarchy·focus.
- 환경: `alarmquest-qa` snapshot 없이 cold boot, uptime `9.93`초에서 시작해 최종 수집 `87.09`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, 모든 캡처 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `c27abe3746b9348478b8daeff676c77eb95b0b97afe430d255df4f2e16208152`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 위·아래 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `DEU` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `듀`의 아래쪽 짧은 세로획 두 개와 `드`의 단일 평행 가로획이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DeuAssemblyFlowTest` 1/1, 오순서 거부·정방향 3획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅡ` 조각, 위·아래 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 231 반복 1 최종 근거

- 변경 전: `captures/loop230/iteration1/after/target-selection.png`와 hierarchy. 변경 후: `captures/loop231/iteration1/after/`의 선택·시작·오순서·완성·쓰기·성공·다음 PNG와 각 hierarchy·focus.
- 환경: 기존 serial이 없어 `alarmquest-qa`를 snapshot 없이 cold boot, uptime `8.55`초에서 시작해 최종 수집 `55.36`초, 물리 1080 × 2340, `user_rotation=1`, 모든 PNG 2340 × 1080, 모든 focus `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `0148ca927f78e9cbbee777d4d401850c57bce8aca90a8e7dd782756b8b404f2a`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 실측: 조립 조각 341 × 368 px, 좌·우 칸 284 × 284 px, WritingCanvas `[189,63][2151,1017]`=1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 필요 판정: 불필요 — production `DI` geometry와 기존 조립 token·정답·조작 atlas를 재사용했고 새 raster는 0건이다. 기존 atlas의 APK production 소비를 성공 PNG·semantics로 재확인했다.
- 자동 그래픽 디자인 역할: 통과 — `드`의 아래쪽 가로 모음과 `디`의 오른쪽 세로 모음이 선택·조립·쓰기·성공에서 구분되고 잘림·왜곡·가림·halo·검은 배경은 0건이다.
- 자동 QA 역할: 통과 — `verify.sh`·`git diff --check`·`DiAssemblyFlowTest` 1/1, 오순서 거부·정방향 3획·성공·다음·홈을 통과했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `ㄷ·ㅣ` 조각, 좌·우 빈칸, 오순서 재강조, 완성 카드, 큰 쓰기판, 정답 체크와 네 그림 조작을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 235 반복 1 최종 근거

- 변경 전: `captures/loop234/iteration1/after/target-selection.png`와 hierarchy의 `가 조립 선택`. 변경 후: `captures/loop235/iteration1/after/ga-direct-writing.png`와 hierarchy·focus의 `가를 3획으로` production 쓰기판.
- 환경: `alarmquest-qa` cold boot, QA 시작 uptime `100.52`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo.
- APK SHA-256: `e4924c142a181b8cc58747e0d2bb7e3b6eac7b4d205851e15ed94acf360f2f14`. package `com.limdo.hangul`, version `0.1.0`/1, v2 서명 통과.
- 실측: WritingCanvas `[189,63][2151,1017]`=1962 × 954 px. 기존 네 조작 배치와 `가` 교육 geometry를 그대로 사용했다.
- 자산 필요 판정: 불필요 — 기존 `가` production geometry·시작점·조작 atlas를 재사용했고 새 raster는 0건이다.
- 자동 그래픽 디자인 역할: 통과 — 직접 진입한 화면에서 큰 `가`와 초록 시작점·점선 경로·네 조작의 잘림·겹침·가림·왜곡은 0건이다.
- 자동 QA 역할: 통과 — 격리 unit·lint·debug build와 `GanadaDirectWritingFlowTest` 1/1, `가 조립 선택` 0건, 설치 APK 동일 해시를 확인했다.
- 아이 대리 QA: 통과 — 글을 읽지 않아도 `가` 카드 뒤에 곧바로 큰 따라 쓰기 길이 나타나며 추가 조립 선택 없이 시작 행동을 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 236 반복 5 최종 근거

- 변경 전: 루프 236 이전 보호자 녹음 화면 근거와 `captures/loop236/iteration1/before/`. 변경 후: `captures/loop236/iteration5/after/home-stable.png`·`guardian-toggle.png`와 각 hierarchy.
- 환경: `alarmquest-qa` uptime `1053.64`초, 물리 1080 × 2340, `user_rotation=1`, 앱 PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `9def859fd0ba1581717e405262a9e672a7330d2a2f0b0da3a8a90aeee408d0e7`. Git commit: 현재 완료 체크포인트 생성 전 작업 트리.
- 자산: production M4A/AAC 38개, 서로 다른 SHA-256 38개, 길이 0.403~0.719초, RMS 0.095~0.255, APK raw 포함 38개. runtime TTS·네트워크 호출 0건이며 각 보호자 lesson에 매핑된다.
- 실측·시각 판정: 보호자 토글 `[1568,80][2266,274]`=698 × 194 px. 켜짐은 초록 surface뿐 아니라 오른쪽 knob·`사용 중` 문구로 구분되고, 목록·녹음 행동과 잘림·겹침·가림은 0건이다. 홈 세 카드도 안정 프레임에서 모두 보이며 2340 × 1080 경계를 벗어나지 않는다.
- 자동 그래픽 디자인 역할: 통과 — 기존 보호자 token과 일치하고 토글 상태 위계·대비·모서리·그림자가 명료하다.
- 자동 QA 역할: 통과 — `verify.sh`·diff·시각/자동화 계약과 에뮬레이터 계측 2/2, SM-S931N 데이터 보존 덮어쓰기·동일 APK·LimDo focus가 통과했다.
- 아이 대리 QA: 통과 — 설정은 보호자 화면에 격리되고 쓰기 진입 1회 음성·정답 후 무반복 흐름이 아이의 입력과 다음 전환을 방해하지 않는다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.
## 루프 237 반복 1 보호자 녹음 삭제 안전 근거

- 변경 전: `captures/loop194/iteration7/emulator/nieun-ready.png`와 hierarchy의 녹음 완료 상태에서는 삭제 버튼이 즉시 원본 파일 삭제를 실행했다. 변경 후: `captures/loop237/iteration1/after/recording-ready.png`·`delete-confirm.png`·`delete-cancel-preserved.png`·`delete-confirmed.png`와 각 hierarchy.
- 환경: `alarmquest-qa` QA 시작 uptime `1657.07`초, 종료 `1776.42`초로 7,200초 미만. 물리 1080 × 2340, `user_rotation=1`, 모든 PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` 모두 `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `7f72cd4839e54736bb3658a1fec7520619037dfc5bc146f65a8a0dd13162c918`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 동일 fixture: `ㄱ` 원본 M4A SHA-256 `23658728fd3014c2898fec3e78068fe0d2d3d72989de20267a018186bb805bd6`를 사용했다. 확인 화면을 연 뒤와 취소 뒤 해시가 그대로였고, 확인 화면의 영구 삭제 뒤 파일이 존재하지 않으며 상태가 `녹음 없음`으로 바뀌었다.
- 실측: 확인 panel `[814,115][1654,964]`=840 × 849 px. 영구 삭제와 취소는 각각 `[1202,481][1591,675]`, `[1202,707][1591,901]`=389 × 194 px이며 64 dp 최소 높이 192 px를 넘는다. 제목·결과 문구·두 버튼은 panel 안에서 잘림과 겹침이 없다.
- 자산 필요 판정: 불필요 — 이번 최소 변경은 보호자 데이터 안전을 위한 modal·문구·큰 버튼으로 충분하고, 새 bitmap은 삭제 대상·결과·취소를 더 명확하게 하지 않는다. D0 mock 3안용 전용 bitmap 판정은 다음 반복에서 유지한다.
- 자동 그래픽 디자인 역할: 통과 — 기존 보호자 palette·corner·elevation과 일치하고 배경 dim, 붉은 영구 삭제, 초록 취소가 정보 위계를 만든다. 흐림·왜곡·halo·검은 배경·잘림 0건.
- 자동 QA 역할: 통과 — 새 단위 회귀 검사, `./scripts/verify.sh`, `git diff --check`, 최신 APK 설치, 확인 전·취소 후 해시 보존, 명시적 확인 뒤 파일 삭제와 상태 전환을 통과했다.
- 아이 대리 QA: 통과 — 기능은 보호자 화면에 격리돼 아이의 쓰기·음성·성공 흐름을 바꾸지 않는다. 보호자는 대상 `ㄱ`, 원본 손실 결과, 취소와 영구 삭제를 모양·위치·문구로 구분한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0.

## 루프 237 반복 10 D0 mock 3안·현재 production 최종 근거

- 변경 전 production: `captures/design-audit-20260830/baseline/01-home.png`·`02-consonant-selection.png`·`03-writing.png`와 각 hierarchy. 반복 10 현재 APK 안정 홈: `captures/loop237/iteration10/emulator/home-current.png`·`home-current.xml`.
- preview 변경 후: `captures/loop237/iteration10/mocks/`의 A 햇살 정원, B 구름빛 놀이터, C 종이 공방 `*-2340x1080.png`. preview이므로 앱 hierarchy·focus나 production 소비를 주장하지 않는다.
- 환경: QA 시작 uptime `2159.11`초로 7,200초 미만, 물리 1080 × 2340, `user_rotation=1`, 현재 앱 PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` 모두 `com.limdo.hangul/.MainActivity`.
- APK SHA-256: `adf70bdf3de774173196a67ec9648990d829d3cfee7766ec7d715f08ad76197a`. 현재 production 홈 PNG SHA-256: `ac7238179845bf3f0722d4e2ecf617bebaeba7dcde2f59adcfffce3a2443ebc3`.
- 자산: Codex 내장 `imagegen`으로 정확히 3개 생성. 최종 세 PNG는 각각 2340 × 1080, 완전 불투명이고 SHA-256은 A `dd4f1cddd0a24f034d852a973c82772529f32e9231b8d58bb5bfd581f1647c60`, B `b2665c1829fc20bd8ea5557e7d27644c290c701de86344982b3ab26524c25def`, C `74977e551db0dd26d654d472563e300a7bc025a430fedccc38e932a8bccacdfb`다. production 소비·APK 포함은 0건이며 사용자 선택 뒤 D1에서 별도 적용한다.
- 자동 그래픽 디자인 역할: 통과 — 세 안은 정원 clay·하늘 felt·종이 craft로 명확히 구별되고 공통 blue·orange·green 카드와 부드러운 좌상단 광원을 유지한다. 임의 한글·숫자·가짜 UI·차량·워터마크·검은 배경·잘림은 0건이다.
- 자동 QA 역할: 통과 — `./scripts/verify.sh`, `git diff --check`, `check-visual-loop.sh`, `check-automation.sh`, `check-emulator-only.sh` 통과. 현재 production 홈은 새 APK에서도 세 카드·보호자 단서의 잘림·겹침·왜곡 0건이다.
- 아이 대리 QA: 통과 — 세 preview 모두 글을 읽지 않아도 큰 선택 영역 세 개와 왼쪽 위 보호자 단서를 구분하며 카드 중앙이 비어 production glyph를 가리지 않는다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0. D1 production 구현은 사용자 선택 전 금지한다.

자산 적용 범위: preview-only
preview 자산 경로: captures/loop237/iteration10/mocks/direction-a-sunny-garden-workshop-2340x1080.png
preview 자산 경로: captures/loop237/iteration10/mocks/direction-b-cloud-playground-2340x1080.png
preview 자산 경로: captures/loop237/iteration10/mocks/direction-c-paper-workshop-2340x1080.png
preview production 미소비 검사: 통과
preview 사용자 선택 관문: 통과
## 루프 238 반복 1 공통 정원 배경 중간 근거

- 변경 전: `captures/design-audit-20260830/baseline/01-home.png`·`.xml`. 변경 후: `captures/loop238/iteration1/after/home-stable.png`·`.xml`.
- 환경: QA 시작 uptime `3007.19`초, `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`, 변경 후 PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo.
- APK SHA-256: `8b1507506742eb97b50d4cd5eb1d5bcc5f95f7938dac672cce91b599947f40b8`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 자산: `limdo_sunny_garden_scene_background.png`, 2340 × 1080 불투명 PNG, SHA-256 `a7dd238985c9bec4f545eeb9b616521e2770002efb97a791f12035c4f2a09bcd`, APK 포함·production 소비 통과. background이므로 RGBA·alpha bbox 관문은 비대상이며 비율 왜곡·문자·가짜 UI·차량·워터마크 0건이다.
- 실측: 카드 688~689 × 702 px 세 개, 보호자 194 × 194 px. 잘림·겹침·callback 가로채기 0건.
- 자동 그래픽 디자인 중간 판정: 통과 — warm cream, matte clay, 좌상단 광원과 가장자리 정원 깊이가 생겼고 중앙 과제 위계가 유지됐다.
- 자동 QA 중간 판정: 통과 — `verify.sh`, `git diff --check`, APK 포함, 새 설치·focus·화면이 통과했다.
- 아이 대리 QA 중간 판정: 배경은 통과하나 메뉴별 실제 그림·짧은 시범이 아직 없어 D1 최종 판정은 보류한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0. 루프 상태: 진행 중.

## 루프 238 반복 6 배경 decode 축소·성능 재판정 근거

- 변경 전: `captures/loop238/iteration5/performance/writing.png`·`.xml`, 정원 배경 2340 × 1080·2.9 MiB, 쓰기 안정 PSS 104,202 KiB, deadline miss 7.66%. 변경 후: `captures/loop238/iteration6/performance/home.png`·`home.xml`·`writing-final.png`·`writing-final.xml`, 정원 배경 1170 × 540·815 KiB.
- 환경: QA 전 uptime `5491.11`초, `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`, PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo. APK SHA-256는 `captures/loop238/iteration6/performance/apk-sha256.txt`에 기록했다. Git commit은 현재 HEAD 기준 미커밋 작업 트리다.
- 실측·시각 판정: 배경 ARGB decode 상한 2.41 MiB, WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px. 원본 2340 × 1080 화면에서 정원 소품·햇살·표면과 세 카드·clay 그림에 흐림·왜곡·halo·알파 이상·잘림·겹침이 없다.
- 자동 그래픽 디자인 역할: 이번 단일 배경 최적화 범위 `통과`. 자동 QA 역할: unit·lint·debug build·diff·에뮬레이터 격리·화면·PSS는 통과했지만 전체 D1 성능은 `실패`. 아이 대리 QA: 과제·세 선택·보호자 진입·쓰기 영역 의미 보존 `통과`. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 성능 1건. PSS는 93,497 KiB로 회복했지만 deadline miss 6.69%(16/239)가 3% 기준을 넘으므로 루프 상태는 진행 중이다.

## 루프 238 반복 4 홈 상태·모션 유효 근거

- 변경 전: `captures/design-audit-20260830/baseline/01-home.png`·`.xml`. 변경 후: `captures/loop238/iteration4/after/` 홈 시작·안정·누림·자음 전환·animator scale 0 홈·모음 선택 PNG·hierarchy.
- 환경: uptime `4549.88`→`4636.11`초, 물리 1080 × 2340, `user_rotation=1`, PNG 모두 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo. APK SHA-256 `32cceedbe830441fb5feaa387b37a961ddc0f62bf63770df4373a71efc05cb2f`.
- 실측: 안정 홈 세 카드 688~689 × 702 px, 보호자 진입 194 × 194 px. 누림 중 세 카드 존속, 대상 카드 축소·흰 빛 테두리 구분, 선택 후 blue·orange shell을 확인했다.
- 자산 필요 판정: 불필요 — 기존 production 정원 배경·세 clay 그림·Compose 상태를 검증했으며 새 bitmap은 생성하지 않았다.
- 자동 그래픽 디자인·자동 QA·아이 대리 QA: 이번 상태·모션 범위 `통과`. 시작 순차, 누림 피드백, 선택 색 연결, animator scale 0 정적 완료가 의미를 보존했고 글자·버튼 가림·잘림·겹침은 0건이다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0. D1 전체 통합 회귀 관문은 남아 루프 상태는 진행 중이다.

## 루프 238 반복 3 홈 motion 미판정 근거

- 환경: `check-emulator-only.sh` 통과, 실기기 후보 0건, uptime `3894.96`→`4142.36`초, 물리 1080 × 2340, `user_rotation=1`, 최종 LimDo focus.
- APK SHA-256: `32cceedbe830441fb5feaa387b37a961ddc0f62bf63770df4373a71efc05cb2f`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- 유효한 중간 발견: `home-consonant-pressed.png`에서 누른 자음 카드의 축소·흰 빛 테두리는 보였으나 나머지 두 카드가 사라져 상태 소유 결함 후보를 발견했고 `key(menu)`로 수정했다.
- 완료 근거 제외: `home-entry-start.png`, `home-stable.png`, `home-reduced-motion*.png`, `home-final-*.png`는 launcher 노출 또는 서로 다른 복원 목적지가 섞여 파일명과 실제 상태가 일치하지 않으므로 자동 그래픽 디자인·자동 QA·아이 대리 QA 완료 근거로 사용하지 않는다.
- 판정: 자동 검증은 통과했으나 수정 후 동일 진입 순서의 2340 × 1080 기본·누림·선택·animator scale 0를 입증하지 못해 자동 그래픽 디자인·자동 QA·아이 대리 QA는 모두 `미판정`이다. 실제 아이 관찰: 실행 안 함.

## 루프 238 반복 2 세 메뉴 clay 교육 그림 중간 근거

- 변경 전: `captures/loop238/iteration1/after/home-stable.png`·`.xml`. 변경 후: `captures/loop238/iteration2/after/home-stable.png`·`.xml`·`focus.txt`.
- 환경: QA 시작 uptime `3432.87`초, `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`, 변경 후 PNG 2340 × 1080, `topResumedActivity`·`mCurrentFocus`·`mFocusedApp` LimDo.
- APK SHA-256: `1b030c3c4a122bd31b277cbdb182263a38b01903d64d21875b711bae3b678bc9`. Git commit: 현재 HEAD 기준 미커밋 작업 트리.
- production 자산: `limdo_home_consonant_clay.png` 840 × 519 RGBA `2a3fa451…b70b0f5`, `limdo_home_vowel_clay.png` 840 × 519 RGBA `cd429b39…8561415`, `limdo_home_syllable_clay.png` 840 × 520 RGBA `5864cd39…13cd7f5`. 모서리 alpha 0~1, APK 포함·Compose production 소비 통과.
- 실측: 세 그림 슬롯 약 420 × 258~260 px, 세 카드 688~689 × 702 px, 보호자 194 × 194 px. 홈 PSS `81,544 KiB`, 반복 1 대비 +5,042 KiB, 배경+세 자산 decode 상한 15,337,440 byte로 16 MiB 예산 이내.
- 자동 그래픽 디자인 중간 판정: 통과 — 동일 matte clay·좌상단 광원·blue/orange/green 화풍이며 자음 블록·모음 줄기·조각 결합 장면이 구분된다. 글자·배경·가림·잘림·왜곡·halo 0건.
- 자동 QA 중간 판정: 통과 — `verify.sh`·diff·자산 검사·APK 포함·새 설치·focus·화면·PSS가 통과했다.
- 아이 대리 QA 중간 판정: 통과 — 글을 읽지 않아도 세 카드의 색과 실루엣으로 각진 조각·줄기 조각·두 조각 합치기를 구분하고, 아래 큰 glyph·라벨이 보조한다. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 0. 루프 상태: 진행 중.
## 루프 238 반복 8 전환 bitmap 제거 성능 기각 근거

- 변경 전: 반복 7의 bitmap 포함 전환 `3/31=9.68%`. 후보 변경 후: `captures/loop238/iteration8/performance/home-fixed.png`·XML, `selection-fixed.png`·XML, `menu-transition-framestats-fixed.txt`, `focus-fixed.txt`; bitmap·overlay 제거 뒤에도 `3/31=9.68%`, frozen 0.
- 환경: QA 시작 uptime `6318.63`초, `emulator-5554` 단독, 물리 1080 × 2340, `user_rotation=1`, PNG 2340 × 1080, LimDo focus. 후보 APK SHA-256 `d5f77153cdc4eead784ef66aca7536ef27d47b11d7a07a82202b80afe58bb220`.
- 직접 판정: 홈·선택의 잘림·겹침·왜곡 0건, 홈 카드 `688~689 × 702 px`, 보호자 callback `194 × 194 px`. 자동 성능·아이 대리 QA 실패, 새 P0·P1 0건, 진행 방해 P2 1건. 실패 코드는 원복했으며 실제 아이 관찰은 실행하지 않았다.

## 루프 239 반복 2 action atlas 규격 복원 근거

- 변경 전: `captures/loop239/iteration1/after/writing.png`·`.xml`, atlas 836 × 836 px. 변경 후: `captures/loop239/iteration2/after/writing.png`·`.xml`, atlas 1254 × 1254 px. 두 화면은 같은 `ㄱ` 초기 쓰기 상태이며 앱 영역 2340 × 1080이다.
- 환경: QA 시작 uptime `139.52`초, `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`, 네 focus 지표 LimDo. APK SHA-256 `129c9f4e4871cfc4ded616222a82fdb62f1c2ae359ec4c4abd8c5245d04c2ed0`; Git commit은 현재 HEAD 기준 미커밋 작업 트리다.
- 자산 판정: 새 생성 없이 현재 3 × 3 RGBA atlas를 균일 확대했다. PNG·alpha·1254 × 1254·418 px 셀 계약이 일치하며 APK 포함·production 소비가 유지된다.
- 실측·직접 판정: WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px. 변경 전 인접 셀 노출은 사라졌고 각 버튼은 단일 house·eraser·왼쪽·오른쪽 아이콘만 표시한다. 검은 배경·halo·왜곡·잘림·글자 길 가림 0건이다.
- 자동 그래픽 디자인 역할: action atlas 범위 통과. 자동 QA 역할: 자동 검사·화면·bounds·PSS는 통과했으나 전체 성능 16/174=9.20%로 실패. 아이 대리 QA: 네 동작 구분은 통과했으나 전환 성능 P2 때문에 전체 실패. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 성능 1건. 루프 상태: 진행 중.

## 루프 239 반복 15 배경 RenderNode 격리 기각 근거

- 변경 전: `captures/loop239/iteration14/after/home.png`·`selection.png`·`writing.png`, 전체 흐름 13/189=`6.88%`. 변경 후 후보: `captures/loop239/iteration15/after/selection.png`·`writing.png`·hierarchy·focus, 전체 흐름 13/177=`7.34%`. `home.png`는 복원 중간 프레임으로 안정 화면 근거에서 제외했다.
- 환경: QA 시작 uptime `5726.56`초, `emulator-5554`/`alarmquest-qa`, 실기기 후보 0건, 물리 1080 × 2340, `user_rotation=1`, PNG 2340 × 1080, `mCurrentFocus`·`mFocusedApp` LimDo. APK SHA-256 `702a71724f87b9858d2a4a294a8bd482264cc2d2a1e20c7fa8e55865d62cb663`.
- 실측: 짧은 전환 15프레임·miss 1·slow draw 0·frozen 0, 전체 흐름 slow UI 11·slow draw 12·bitmap upload 0·frozen 0, 쓰기 안정 PSS 92,241 KiB. WritingCanvas 1962 × 954 px, 네 조작 각 168 × 168 px.
- 자산 판정: 새 생성 없이 기존 1170 × 540 정원 배경·세 clay·action atlas를 재사용했다. 선택·쓰기 화면에서 흐림·왜곡·halo·검은 면·잘림·겹침은 0건이다.
- 판정: 자동 그래픽 디자인 역할은 화면 보존 통과. 자동 QA·아이 대리 QA는 3% 미만 성능 관문 미충족으로 실패. 실제 아이 관찰: 실행 안 함. 새 P0 0건, 새 P1 0건, 진행 방해 P2 1건. 후보는 원복했고 루프는 15회 상한으로 차단했다.

## 루프 239 반복 3 controller 지연 생성 후보 기각 근거

- 변경 전: `captures/loop239/iteration2/after/home.png`·`writing.png`·hierarchy. 후보 변경 후: `captures/loop239/iteration3/after/home.png`·`selection.png`·`writing.png`·hierarchy. 모두 앱 영역 2340 × 1080이며 후보는 성능 기각 후 소스에서 원복했다.
- 환경: uptime `215.29`초, `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`, 네 focus 지표 LimDo. 후보 APK SHA-256 `46a015eef2d9fd0096b39a94ac6121a08b81e1a3c06bd47cb5aeb3ec7f3b8565`; Git commit은 현재 HEAD 기준 미커밋 작업 트리다.
- 자산 필요 판정: 불필요 — 기존 정원·clay·action atlas를 그대로 재사용했고 초기 CPU 구조 검증에 새 bitmap은 필요하지 않았다.
- 직접 판정: 세 홈 카드·보호자 진입·14 자음·WritingCanvas 1962 × 954 px·네 조작 각 168 × 168 px의 모양·잘림·겹침·callback 회귀 0건이다.
- 자동 그래픽 디자인 역할: 화면 보존 통과. 자동 QA 역할·아이 대리 QA: 전체 deadline miss `15/178=8.43%`로 실패. 실제 아이 관찰: 실행 안 함.
- 새 P0: 0; 새 P1: 0; 진행 방해 P2: 성능 1건. 루프 상태: 진행 중.

## 루프 239 반복 9 — 공통 scene shell 생명주기 후보

- 변경 전: `captures/loop239/iteration8/after/selection.png`·`writing.png`. 변경 후: `captures/loop239/iteration9/after/selection.png`·`selection.xml`, `writing.png`·`writing.xml`, `focus-writing.txt`, `performance/apk-sha256.txt`.
- 두 PNG는 2340 × 1080, 물리 1080 × 2340, `user_rotation=1`, package·focus `com.limdo.hangul`을 통과했다. WritingCanvas 1962 × 954 px, 네 조작 168 × 168 px, 가림·잘림·왜곡·흐림·블랙 배경 0건이다.
- 동일 흐름 19/295=`6.44%`, frozen 0, 짧은 전환 miss 1/22, PSS 91,190 KiB로 가설을 기각하고 후보를 원복했다.
- 자동 그래픽 디자인: 화면 보존 통과. 자동 QA·아이 대리 QA: 진행 방해 P2 1건으로 실패. 신규 P0 0, P1 0, 진행 방해 P2 1. 실제 아이 관찰: 실행 안 함.

## 루프 239 반복 10 — 선택 카드 외곽 node 통합 후보

- 변경 전: `captures/loop239/iteration9/after/selection.png`·`selection.xml`, 2340 × 1080.
- 변경 후: `captures/loop239/iteration10/after/selection.png`·`selection.xml`, `writing.png`·`writing.xml`, 각 2340 × 1080.
- focus: `captures/loop239/iteration10/after/focus-writing.txt`, package `com.limdo.hangul`, `mCurrentFocus`·`mFocusedApp` LimDo.
- APK SHA-256: `4f500b0ed87594ba08af24d012f5a9405537a1f72f71968f82a6bdd4053cbebd`.
- 자산: 새 생성 없음. 기존 정원·clay·action atlas production 소비를 보존했다.
- 자동 그래픽 디자인 역할: 통과. 선택 카드 14개의 파란 외곽·그림자·모서리·글자 배치와 쓰기 geometry·조작 자산에 가림·잘림·화풍 회귀 0건.
- 자동 QA 역할: 실패. 전체 deadline miss 19/297=`6.40%`로 3% 미만 관문 미충족.
- 아이 대리 QA: 실패. 글을 읽지 않아도 14개 큰 글자 카드·홈·쓰기 시작점·방향·네 조작은 구분하지만 전환 성능 P2 1건이 남음.
- 신규 P0 0건, P1 0건, 진행 방해 P2 1건. 실제 아이 관찰: 실행 안 함.

## 루프 239 반복 12 — 선택 glyph BasicText 후보

- 변경 전: `captures/loop239/iteration11/after/selection-stable.png`·XML. 후보 변경 후: `captures/loop239/iteration12/after/selection.png`·XML, `writing.png`·XML. 모두 2340 × 1080이며 후보 소스는 성능 기각 후 원복했다.
- 환경: uptime `4730.04`초, 물리 1080 × 2340, `user_rotation=1`, package `com.limdo.hangul`, `mCurrentFocus`·`mFocusedApp` LimDo. APK SHA-256 `752445cdb11d64c6f8a02af19482c9129763dcd0a0f8e50186530e8c86d03cbd`; Git commit은 현재 HEAD 기준 미커밋 작업 트리다.
- 자산 필요 판정: 불필요. 기존 정원·clay·action atlas production 소비를 보존했고 text 계층 비용 분리에 새 bitmap을 사용하지 않았다.
- 직접 판정: 14개 glyph의 굵기·중앙 정렬·잘림·겹침 0건, 카드 외곽·그림자·semantics·callback 보존. WritingCanvas 1962 × 954 px, 네 조작 168 × 168 px, 교육 geometry·표식 가림 0건.
- 성능: 전체 16/295=`5.42%`, frozen 0, 짧은 전환 miss 0/22, bitmap upload 0, PSS 88,556 KiB. 3% 미만 관문 실패로 후보를 원복했다.
- 자동 그래픽 디자인 역할: 화면 보존 통과. 자동 QA 역할·아이 대리 QA: 성능 P2 1건으로 실패. 신규 P0 0건, P1 0건, 진행 방해 P2 1건. 실제 아이 관찰: 실행 안 함.

## 루프 239 반복 13 — 보호자 저장소 IO 지연 후보

- 변경 전: `captures/loop239/iteration12/after/selection.png`, `writing.png`·각 hierarchy. 반복 12 원복 production 상태이다.
- 변경 후: `captures/loop239/iteration13/after/selection.png`, `writing.png`·각 hierarchy. 둘 다 2340 × 1080 RGBA이며 LimDo focus다.
- APK SHA-256: `d906d75132f374198b7fab499e0f59181e37e58d648a8a3d57e12747f6a836be`.
- 자산: 새 생성 없음. 기존 production 정원·clay·action atlas 소비와 APK 포함을 보존했다.
- 자동 그래픽 디자인 역할: 화면 표현 통과. 14개 선택 글자·카드 깊이·외곽·홈·쓰기 guide·action icon 회귀 0건.
- 자동 QA 역할: 실패. 전체 deadline miss 15/176=`8.52%`로 3% 미만 관문 미충족.
- 아이 대리 QA: 실패. 과제·시작점·네 조작은 글 없이 구분되지만 최초 흐름 성능 P2가 남음.
- 신규 P0 0건, P1 0건, 진행 방해 P2 1건. 실제 아이 관찰: 실행 안 함.
- 판정: 후보 기각·소스 원복. 완료 근거로 사용하지 않음.
