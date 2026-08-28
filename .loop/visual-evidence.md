# 현재 시각 루프 증거

루프: 193
상태: 통과

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop193/iteration1/before/sa-initial.png
변경 전 hierarchy: captures/loop193/iteration1/before/sa-initial.xml
변경 후 PNG: captures/loop193/iteration4/after/sa-initial.png
변경 후 hierarchy: captures/loop193/iteration4/after/sa-initial.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: a2fbdffd3240f647edd0a8c080ba844b21808fa4822050f23d09631a89f47236
production 자산 경로: 새 bitmap 없음 — `사` 전용 Canvas production 중심선만 교정
production 소비 검사: 통과
자산 자동 검사: 불필요
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- `alarmquest-qa` 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, `com.limdo.hangul/.MainActivity` focus에서 변경 전·후 동일한 `사` 초기 화면과 변경 후 초성 1·2획, 중성 세로획, 성공을 실제 pointer 입력으로 방문했다.
- 변경 전 초성은 왼쪽 다리 6점·오른쪽 다리 4점의 작은 방향 변화가 굵은 guide에서 굴곡으로 보였다. 변경 후는 위 시작점·중앙 접합부·아래 좌우 끝만 남아 세로 시작과 두 사선이 단정하게 이어지고 S자·갈고리·접합 틈·비정상 겹침·잘림이 0건이다.
- `sa-after-stroke1.png`, `sa-after-stroke2.png`, `sa-after-medial-vertical.png`, `sa-success.png`에서 흰 guide·현재 원형 점선·파란 아이 선이 같은 중심선에 놓이고 입력 중 추종 화살표는 0개다. WritingCanvas는 `[189,63][2151,1017]` = 1962 × 954 px, 네 조작은 각각 168 × 168 px다.
- 자동 검사에서 두 초성 획의 점 수·공유 접합부·단조 하강·좌우 진행, 정방향 수락·역방향·큰 이탈 거부를 고정했다. 전체 unit·lint·debug build와 `git diff --check`가 통과했다.
- 자동 그래픽 디자인·자동 QA·아이 대리 QA는 모두 통과했고 새 P0·P1·진행 방해 P2는 0건이다. 실제 아이 관찰: 실행 안 함.

## 반복 3 최종 후보 판정

- `captures/loop193/iteration3/after/`의 초기·초성 1획·초성 2획·중성 세로·성공 PNG와 hierarchy는 모두 최종 APK SHA-256 `8434e7a9725ee9fdbd0861d617b629d9764937967f4edb6263720a4d73a6729a`에서 새로 수집했다. 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, LimDo focus를 확인했다.
- 네 획은 production 중심선의 정방향 입력으로 모두 수락되어 성공했고 입력 중 추종 화살표 0개, WritingCanvas 1962 × 954 px, 네 조작 168 × 168 px를 유지했다.
- 그러나 첨부 원본과 초기·초성 완료·성공 화면을 원본 크기로 직접 비교하면 왼쪽 획이 긴 수직 막대 뒤 왼쪽 대각선으로 꺾이는 외곽 각이 뚜렷하다. 첨부 글꼴의 위쪽부터 연속적으로 왼쪽으로 휘는 인상과 일치하지 않아 성공 조건 4를 실패한다.
- 새 P0·P1은 0건이며 이 자형 불일치는 학습 목표 자체를 막는 진행 방해 P2 1건이다. 실제 아이 관찰: 실행 안 함.

## 반복 4 최종 판정

- `alarmquest-qa` 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, LimDo focus에서 최종 APK를 설치하고 `사` 초기·초성 1·2획·중성 세로·성공을 실제 pointer 입력으로 방문했다.
- 반복 3의 수직 막대 뒤 대각선 모서리 대신, 반복 4 왼쪽 획은 두 cubic의 연속 접선으로 시작점에서 접합부·왼쪽 아래로 부드럽게 휘어 첨부 글꼴의 인상과 일치한다.
- 각진 3점형·직선 V자·꾸불거림·S자·갈고리·접합 틈·비정상 겹침·잘림은 각각 0건이다.
- 흰 guide·원형 점선·파란 아이 선은 같은 production geometry에 놓이고 네 획이 정방향으로 수락되어 성공했다. 입력 중 추종 화살표는 0개이며 네 조작은 유지됐다.
- 자동 검사는 인접 샘플 회전각 12° 이하, x·y 단조 진행, 공유 접합, 정방향 수락·역방향·큰 이탈 거부를 고정했다.
- 실제 아이 관찰: 실행 안 함.
