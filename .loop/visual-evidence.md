# 현재 시각 루프 증거

루프: 184
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop184/iteration1/before/home.png
변경 전 hierarchy: captures/loop184/iteration1/before/home.xml
변경 후 PNG: captures/loop184/iteration1/after/home.png
변경 후 hierarchy: captures/loop184/iteration1/after/home.xml
보호자 PNG: captures/loop184/iteration1/after/guardian.png
보호자 hierarchy: captures/loop184/iteration1/after/guardian.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 14806b06755642b47c576a6c43aaa30de70618a1c42e30c6ff52340a73eb111a
production 자산 경로: 새 bitmap 없음 — MainActivity.kt Canvas 잠금 geometry와 기존 token 사용
production 소비 검사: 통과
자산 자동 검사: 불필요
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- 보호자 영역은 `[1081,869][1260,1048]` = 179 × 179 px, `long-clickable=true`다. 일반 탭·1.5초 hold는 홈에 남고 2.2초 hold는 보호자 화면을 한 번 열었다.
- 세 아이 카드 bounds는 변경 전후 각각 `[84,84][772,996]`, `[825,84][1514,996]`, `[1567,84][2256,996]`로 동일하다.
- 보호자 화면은 자음 14개·모음 10개·글자 14개를 세 production 색 묶음으로 표시한다. hierarchy의 lesson 38개는 모두 고유하다.
- 닫기와 Android 뒤로 가기는 각각 한 번에 홈으로 돌아갔고 일반 자음 카드 탭은 기존 선택 화면을 열었다. 전 과정 focus는 `com.limdo.hangul/.MainActivity`였다.
- PNG를 직접 읽어 작은 muted 잠금은 세 학습 카드보다 훨씬 작고 보호자 목록은 잘림·겹침 없이 기존 색·모서리 언어를 유지함을 확인했다.
- manifest 권한 선언은 0건이다. 자동 역할 판정은 실제 사람 팀의 승인이 아니며 실제 아이 관찰: 실행 안 함.
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
