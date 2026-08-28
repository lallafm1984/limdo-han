# 현재 시각 루프 증거

루프: 198
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop198/iteration1/before/guardian-consonants.png
변경 전 hierarchy: captures/loop198/iteration1/before/guardian-consonants.xml
변경 후 PNG: captures/loop198/iteration1/after/guardian-duplicate.png
변경 후 hierarchy: captures/loop198/iteration1/after/guardian-duplicate.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 6d5d8169536a5e74426893c6c1f1b32f2b3bb4f4a2093c01287280fa07e63ab4
production 자산 경로: 기존 production lesson 카드·분류 color token 재사용
production 소비 검사: 통과 — 새 bitmap 없이 production Compose 보호자 그리드가 목록 모드·개수·중복 상태를 직접 소비
자산 자동 검사: 불필요 — 새 raster 자산 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- 변경 전은 lesson 셀 tap이 녹음 관리 하나로만 연결되고 학습 목록 상태가 없었다. 변경 후 상단의 `녹음 관리`·`목록 추가` 194 px 높이 모드가 채움·외곽·상태 문구로 구분되며, 목록 추가 모드의 기존 lesson 셀 전체 266~388 × 193~194 px를 그대로 사용한다.
- `guardian-empty`→`guardian-one`→`guardian-duplicate` PNG·hierarchy에서 같은 `ㄱ` tap마다 목록 수가 0→1→2로 정확히 늘고, 해당 셀은 옅은 선택 면과 `현재 목록에 2개` semantics를 함께 가진다. 자음·모음·글자 전 항목은 비스크롤 두 행에 동시에 보이며 잘림·겹침·왜곡은 0건이다.
- app-private `no_backup/guardian-learning-list.txt`에는 `GIEOK,GIEOK,I,HA` 순서가 그대로 저장됐고 강제 종료·재실행 뒤 `guardian-restart`에서 4개로 복원됐다. 단위 검사는 손상·미지원 ID 제외, 빈 목록 안전, 38개 allowlist, 중복·순서와 새 storage instance 복원을 고정한다.
- 녹음 관리 모드의 `ㄱ` tap은 기존 `보호자 녹음` 화면으로 진입해 callback 회귀가 없다. 전체 unit·lint·debug build, `git diff --check`, LimDo focus를 통과했다.
- 자동 그래픽 디자인·자동 QA·아이 대리 QA는 새 P0·P1·진행 방해 P2 0건으로 통과했다. 이 화면은 보호자 전용이며 아이의 홈 세 메뉴·쓰기·네 조작을 바꾸지 않는다. 실제 아이 관찰: 실행 안 함.
