# 현재 시각 루프 증거

루프: 194
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop190/iteration5/emulator/g-start.png
변경 전 hierarchy: captures/loop190/iteration5/emulator/g-start.xml
변경 후 PNG: captures/loop194/iteration7/emulator/nieun-deleted.png
변경 후 hierarchy: captures/loop194/iteration7/emulator/nieun-deleted.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 9239dd3e8bb42b2ad9e2d51c002cc354821f9479eb2d125a62090dd13f75cbd3
production 자산 경로: 새 bitmap 없음 — 기존 보호자 상태 실루엣·카드·색 token 재사용
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

## 루프 194 반복 1 — lesson당 단일 녹음 통합

- 변경 전: `captures/loop190/iteration5/emulator/g-start.png`, `g-start.xml` — `쓰기 전`·`정답 후` 탭과 event별 제목.
- 변경 후: `captures/loop194/iteration1/after/guardian-single-recording.png`, `guardian-single-recording.xml` — lesson당 단일 제목·상태·조작. 녹음 bounds 389 × 194 px.
- 목록 후: `captures/loop194/iteration1/after/guardian-list.png`, `guardian-list.xml` — 38개는 표시되지만 lesson 카드 폭 62~63 px로 진행 방해 P2 1건.
- focus·package: `com.limdo.hangul/.MainActivity` `topResumedActivity`, `emulator-5554`, 물리 1080 × 2340, `user_rotation=1`, PNG 2340 × 1080.
- APK SHA-256: `fc4cbc6d838c6a2f1a36fb1883a577b36e9e5ef8c431292b8264f4341f045ae5`.
- 자동 그래픽 디자인: 단일 상세 통과, 목록 실패. 자동 QA: 단일 경로·호환 통과, 목록 터치 폭 실패. 아이 대리 QA: 보호자 화면은 아이 흐름과 분리되지만 작은 lesson 카드로 종합 실패.
- 새 P0: 0, P1: 0, 진행 방해 P2: 1. 실제 아이 관찰: 실행 안 함.

## 루프 194 반복 3 — 분류 전환형 비스크롤 전량 그리드

- 변경 전: `captures/loop194/iteration1/after/guardian-list.png`, `guardian-list.xml` — 세 분류가 가로로 동시 배치되어 lesson 카드 폭이 62~63 px로 축소됨.
- 변경 후: `captures/loop194/iteration3/emulator/consonants-final.png`, `vowels-final.png`, `syllables-final.png` 및 각 XML — 상단 세 분류 전환과 7열×2시스템 행·5열×2시스템 행 비스크롤 전량 그리드. 상세는 `ha-recording.png`, `ha-recording.xml`.
- 환경: `alarmquest-qa` `emulator-5554`, 물리 1080 × 2340, `user_rotation=1`, PNG 2340 × 1080, package·focus `com.limdo.hangul/.MainActivity`. APK SHA-256 `a40a9c3ea629e9fddfe5516c7cb38a97e655ee84876b0f7db6117e86ac71aba5`.
- 실측: 세 분류 버튼 194 px, 14개 그리드 셀 266~267 × 195~196 px, 10개 셀 387~388 × 195~196 px. 첫·중간·마지막이 동시에 노출되고 스크롤·잘림·겹침은 0건이다. `하` 단일 녹음 상세 callback은 389 × 194 px로 보존됐다.
- 직접 판정: 원본 PNG에서 선택된 분류는 채운 색·흰 글자·그림자로, 비선택 분류는 연한 면·색 외곽으로 구분된다. 38개 글자는 셀 중앙에 왜곡·잘림 없이 보이고 닫기·제목·분류·그리드 간 시선 경쟁이나 가림은 없다.
- 역할 판정: 이 목록 변경의 자동 그래픽 디자인·자동 QA·아이 대리 QA는 통과. 새 P0 0건, P1 0건, 진행 방해 P2 0건. 실제 아이 관찰: 실행 안 함.

## 루프 194 반복 7 — 단일 녹음 lifecycle 최종 판정

- `alarmquest-qa` 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, LimDo focus에서 보호자 목록의 `ㄴ`을 실제 tap해 EMPTY→RECORDING→READY→PLAYING→EMPTY를 방문했다.
- 근거는 `captures/loop194/iteration7/emulator/`의 `nieun-empty`·`nieun-recording`·`nieun-ready`·`nieun-playing`·`nieun-deleted` PNG·hierarchy와 전후 hash 파일이다.
- READY의 듣기·다시 녹음·삭제는 각각 389 × 194 px, EMPTY의 녹음은 389 × 194 px이며 안정 상태에서 글자·상태·조작 잘림과 겹침은 0건이다.
- `nieun.m4a`만 명시적 삭제 뒤 사라졌고 기존 `gieok.m4a` SHA-256 `84f5313c4e6c90ef63ca59fc1b86009a352a6cdc56b98183e943608c47f67fc9`는 보존됐다.
- 자동 그래픽 디자인·자동 QA·아이 대리 QA는 통과했고 새 P0·P1·진행 방해 P2는 0건이다. 실제 아이 관찰: 실행 안 함.
