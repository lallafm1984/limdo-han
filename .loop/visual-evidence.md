# 현재 시각 루프 증거

루프: 189
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop189/iteration1/before/ha-initial.png
변경 전 hierarchy: captures/loop189/iteration1/before/ha-initial.xml
변경 후 PNG: captures/loop189/iteration2/after/ha-initial.png
변경 후 hierarchy: captures/loop189/iteration2/after/ha-initial.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: b5d34fbcbabcba4f46fa8ed3ee0c71972666fcee086ba6adcd9f9d72fff42e8c
production 자산 경로: 새 bitmap 없음 — WritingCanvasGeometry.kt production Canvas geometry 사용
production 소비 검사: 통과
자산 자동 검사: 불필요
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- 실제 `SM_S931N`은 물리 1080 × 2340, `user_rotation=1`이며 모든 PNG는 앱 영역 2340 × 1080, 전면 focus는 `com.limdo.hangul/.MainActivity`였다.
- `카`는 변경 전 수직 하강에서 변경 후 아래로 갈수록 왼쪽으로 완만하게 휘는 초성으로 바뀌었고, production 정방향 4획이 순서대로 수락되어 `captures/loop189/iteration2/after/ka-after-stroke1.png`부터 `ka-success.png`까지 이어졌다.
- `하`는 초성 오른쪽 외곽과 중성 세로 외곽 사이에 배경 간격이 보이며, production 정방향 5획이 순서대로 수락되어 `ha-after-stroke1.png`부터 `ha-success.png`까지 이어졌다.
- 두 초기·획별·성공 화면을 원본 크기로 직접 읽어 점선·동적 표식·시작·끝·아이 획이 같은 자형 위에 있고, 흐림·잘림·조작 가림·새 P0·P1·진행 방해 P2가 없음을 확인했다.
- 네 조작은 각각 192 × 192 px hierarchy bounds로 유지됐다. WritingCanvas 배치 계약과 다른 글자 geometry는 전체 자동 검사로 회귀가 없었다.
- 자동 역할 판정은 실제 사람 팀의 승인이 아니며 실제 아이 관찰: 실행 안 함.
### 루프 185 반복 1 — 대표 `ㄱ` 쓰기 전 보호자 녹음 (미완료 근거)

- package: `com.limdo.hangul`
- APK SHA-256: `147d69047576847474446345aa5c4c4db3085352bce3ad6a43e17bdbc4570d2c`
- 변경 전: `captures/loop184/iteration1/after/guardian.png`, `captures/loop184/iteration1/after/guardian.xml` — 읽기 전용 보호자 목록, 녹음 상세 callback 없음
- 변경 후: `captures/loop185/iteration1/after/guardian-list.png`, `recording-empty.png`, `permission-request.png`, `permission-denied.png`, `recording-active.png`, `recording-ready.png`, `playback.png`, `deleted.png`와 같은 이름의 hierarchy
- 기기·focus: `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`, 앱 2340 × 1080, `mCurrentFocus`·`mFocusedApp` `com.limdo.hangul/.MainActivity`
- 자동 그래픽 디자인 역할: 에뮬레이터 범위 통과 — 기존 카드·색 token과 원·사각형·삼각형 상태 도형이 일관되고 새 bitmap이 불필요하다.
- 자동 QA 역할: 미완료 — 자동·에뮬레이터는 통과했으나 필수 실제 `SM-S931N` 근거가 없다.
- 아이 대리 QA: 미실행 — 실제 기기 관문 뒤 수행한다.
- 새 P0: 0건
- 새 P1: 0건
- 진행 방해 P2: 0건
- 실제 아이 관찰: 실행 안 함

### 루프 186 반복 1 — 정답 후 녹음 최종 근거

- 상태: 완료, 기준 화면: 2340 × 1080
- 변경 전: `captures/loop185/iteration7/device/empty.png`, `empty.xml`, `ready.png`, `ready.xml`
- 변경 후: `captures/loop186/iteration1/device/` START·SUCCESS 없음·녹음 중·완료·미리 듣기·삭제 PNG·hierarchy
- package·focus·기기: `com.limdo.hangul`, `mCurrentFocus`·`mFocusedApp` 통과, `SM_S931N`, 물리 1080 × 2340
- APK SHA-256: `7a8c03d1dedaf84df34147fbea5a3e8002c535776d6c3e33b8624ca2ad68a059`
- production 자산: 새 bitmap 없음, 기존 Canvas 상태 실루엣·카드·색 token 재사용; 자산 검사 불필요
- 자동 그래픽 디자인: 통과 — event 제목·외곽·명도·실루엣 구분, 잘림·겹침 0건
- 자동 QA: 통과 — SUCCESS callback·저장·START·다른 lesson byte 격리, event 선택 192 px·동작 216 px
- 아이 대리 QA: 통과 — 보호자 전용 흐름과 아이 무음 쓰기 흐름 분리
- 새 P0: 0, 새 P1: 0, 진행 방해 P2: 0
- 실제 아이 관찰: 실행 안 함

### 루프 185 반복 7 — 보호자 녹음 카드 최종 통과 근거

- 상태: 완료
- 기준 화면: 2340 × 1080
- 변경 전 PNG·hierarchy: `captures/loop185/iteration6/device/ready.png`, `ready.xml`
- 변경 후 PNG·hierarchy: `captures/loop185/iteration7/device/empty.png`, `recording.png`, `ready.png`, `playing.png` 및 같은 이름의 XML
- package·focus: `com.limdo.hangul`, `mCurrentFocus`·`mFocusedApp` 통과
- 기기: `SM_S931N`, 물리 1080 × 2340, 앱 PNG 2340 × 1080
- APK SHA-256: `974bb19de2d8285752d751f6e7016dd9ed6e1279d287ea4b3a09be0cace45bfa`
- production 자산 경로: 새 bitmap 없음 — 기존 Canvas 상태 실루엣과 색 token 재사용
- production 소비·자산 자동 검사: 통과·불필요
- 자동 그래픽 디자인 역할: 통과 — 상태와 동작의 좌우 위계, 잘림·겹침 0건
- 자동 QA 역할: 통과 — 상태별 버튼 높이 216 px, callback·저장·손상 파일 회귀 통과
- 아이 대리 QA: 통과 — 보호자 전용 흐름이 아이 흐름과 분리되고 녹음 실패 시 무음 학습 유지
- 새 P0: 0건
- 새 P1: 0건
- 진행 방해 P2: 0건
- 실제 아이 관찰: 실행 안 함

### 루프 185 반복 5 — 손상 M4A 복귀 (미완료 근거)

- package: `com.limdo.hangul`
- APK SHA-256: `4e1035f787dbc03302980842a072bc23fb8424bb9aa07bedd1ac86e30a536a08`
- 기기·focus: `SM_S931N`, 물리 1080 × 2340, 앱 2340 × 1080, `com.limdo.hangul/.MainActivity`
- 손상 복귀 PNG·hierarchy: `captures/loop185/iteration5/device/corrupt-fallback.png`, `detail-before.xml`
- 정상 완료 PNG·hierarchy: `captures/loop185/iteration5/device/normal-ready.png`, `normal-ready.xml`
- 정상 재생 PNG·hierarchy: `captures/loop185/iteration5/device/normal-playing.png`, `normal-playing.xml`
- 자동 QA: 손상 3 bytes 파일 삭제·`EMPTY`, 정상 26,356 bytes 녹음·`PLAYING`, 전체 verify·lint·build 통과
- 자동 그래픽 디자인 역할: 실패 — 카드 하단 동작 글자 잘림 P2 1건
- 아이 대리 QA: 미실행 — 시각 P2 수정 뒤 최종 수행
- 새 P0: 0건
- 새 P1: 0건
- 진행 방해 P2: 1건(동작 글자 잘림; 실루엣 callback은 작동하지만 성공 조건 6 미충족)
- 실제 아이 관찰: 실행 안 함

### 루프 185 반복 6 — 녹음 카드 배치 조정 실패 근거

- package: `com.limdo.hangul`
- APK SHA-256: `2f3b0f0718b9243e5f267053e8f9fa90d601fb32347652c5e7c34554993cfb3b`
- 변경 전 PNG·hierarchy: `captures/loop185/iteration5/device/normal-ready.png`, `normal-ready.xml`
- 변경 후 PNG·hierarchy: `captures/loop185/iteration6/device/ready.png`, `ready.xml`
- 기기·focus: `SM_S931N`, 물리 1080 × 2340, 앱 2340 × 1080, `mCurrentFocus`·`mFocusedApp` `com.limdo.hangul/.MainActivity`
- production 자산 경로: 새 bitmap 없음 — 기존 Canvas 상태 실루엣과 색 token 유지
- 자산 자동 검사: 불필요
- 자동 그래픽 디자인 역할: 실패 — 세 버튼이 각각 444 × 29 px로 강제 축소되어 상단 조각만 보임
- 자동 QA 역할: 실패 — `./scripts/verify.sh`·설치·focus는 통과했으나 최소 터치 높이 192 px 대비 29 px로 성공 조건 6 미충족
- 아이 대리 QA: 미실행 — 진행 방해 P2 수정 후 최종 수행
- 새 P0: 0건
- 새 P1: 0건
- 진행 방해 P2: 1건(버튼·라벨 잘림 지속)
- 실제 아이 관찰: 실행 안 함

### 루프 187 반복 4 — 보호자 녹음 미리 듣기 lifecycle

- 상태·기준 화면: 완료, 2340 × 1080, `SM_S931N`, `com.limdo.hangul/.MainActivity` focus.
- 변경 전: `captures/loop186/iteration1/device/` START·SUCCESS READY·PLAYING.
- 변경 후: `captures/loop187/iteration4/device/` START·SUCCESS PLAYING·READY·EMPTY PNG·hierarchy.
- APK SHA-256: `69332dddc8634bb7903cb766aabe77a2e4074b409e8172d454d3e3cd208eae8f`.
- production 자산: 새 bitmap 없음. 기존 Canvas 파형·재생 삼각형·원형·붉은 원형·카드·색 token 재사용, 자산 검사 불필요.
- 자동 그래픽 디자인 역할: 통과 — 상태 형태·기존 카드·색·모서리·그림자 일관, 잘림·겹침·비가림 없음.
- 자동 QA 역할: 통과 — 전체 검증·focus·PLAYING→READY·손상→EMPTY·SHA 보존 통과.
- 아이 대리 QA: 통과 — 화면 이탈 후 재생이 멈추고 무음·손상 대안이 아이 학습을 멈추지 않음.
- 새 P0: 0, 새 P1: 0, 진행 방해 P2: 0. 실제 아이 관찰: 실행 안 함.
