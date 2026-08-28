# 현재 시각 루프 증거

루프: 202
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop202/iteration2/before/guardian-empty.png
변경 전 hierarchy: captures/loop202/iteration2/before/guardian-empty.xml
변경 후 PNG: captures/loop202/iteration2/after/guardian-completed-relaunch-final.png
변경 후 hierarchy: captures/loop202/iteration2/after/guardian-completed-relaunch-final.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 2c3be003526904c157d7e3488573e5a4d5c97c02a1e1fb668bfb50d049c71ed3
production 자산 경로: 기존 production 보호자 카드·색·모서리·외곽선 token과 Canvas 상태 도형 재사용
production 소비 검사: 통과 — GuardianLessonGroupCard가 저장 매핑을 읽어 38개 production 카드에 상태를 표시
자산 자동 검사: 불필요 — 새 raster 자산 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- 변경 전 같은 보호자 자음 분류는 글자만 표시했다. 변경 후 연습 전은 회색 빈 원, 연습 중은 주황 진행 원, 완료는 초록 체크를 쓰고 연습 전·연습 중·혼자 완성·도움 후 완성 문구와 최근 연습·완료 시각을 함께 표시한다.
- 첫 후보 guardian-empty.png는 상태 행 하단이 잘려 기각했고, 다음 완료 후보는 최근 시각이 보이지 않아 기각했다. 최종 후보는 글자와 상태·시각을 2열로 배치해 자음·모음·글자 첫·마지막 항목이 모두 보이며 잘림·겹침이 0건이다.
- hierarchy에서 자음 14개, 모음 10개, 글자 14개가 각각 한 화면에 있고 카드 bounds는 자음·글자 최소 266 × 193 px, 모음 최소 388 × 193 px다. 내부 scroll node는 0개다.
- 실제 쓰기 진입으로 ㄱ 연습 중 상태를 만든 뒤 보호자 화면에 반영했고, 저장 fixture의 혼자 완성·도움 후 완성·모음 연습 중 상태는 앱 강제 종료·재실행 뒤 복원됐다. fixture는 화면 상태 검수용이며 실제 정답 입력 근거로 표현하지 않는다.
- 자동 그래픽 디자인은 기존 보호자 색·모서리·그림자와 일관되고 상태가 색 하나에 의존하지 않는다고 판정했다. 자동 QA와 아이 대리 QA는 보호자 전용 정보가 아이 학습 화면과 경쟁하지 않고 기존 카드 callback·비스크롤 탐색이 유지된다고 판정했다. 실제 아이 관찰: 실행 안 함.
- 반복 3 최종 회귀는 APK SHA-256 `2c3be003526904c157d7e3488573e5a4d5c97c02a1e1fb668bfb50d049c71ed3`로 `captures/loop202/iteration3/emulator/` 근거를 새로 수집했다. `free-next-nieun.png`에서 자유 글자 `가 → 나`, `guardian-syllable-regression.png`에서 가·나만 연습 중·최근 시각으로 갱신되고 나머지는 연습 전으로 유지됨을 확인했다. 화면은 2340 × 1080·LimDo focus이며 잘림·겹침은 0건이다.
