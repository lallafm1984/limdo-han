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
