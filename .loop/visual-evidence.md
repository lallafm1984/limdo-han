# 현재 시각 루프 증거

루프: 183
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop183/iteration1/before/consonant-selection.png
변경 전 hierarchy: captures/loop183/iteration1/before/consonant-selection.xml
변경 후 PNG: captures/loop183/iteration1/after/consonant-selection.png
변경 후 hierarchy: captures/loop183/iteration1/after/consonant-selection.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 4fea6befa4cd72909a596992a8bada20653015245471d23b94d66dbcfc6bf7f1
production 자산 경로: app/src/main/res/drawable-nodpi/limdo_action_button_atlas.png
production 소비 검사: 통과
자산 자동 검사: 통과
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- 변경 전은 시스템 문자 `⌂`와 평면 흰 원, 변경 후는 쓰기 화면과 같은 HOME column·기본 row의 입체 집 그림이다.
- 변경 후 HOME은 `[84,84][305,305]` = 221 × 221 px, `clickable=true`, `enabled=true`, content description `홈으로 돌아가기`다.
- HOME을 한 번 누른 뒤 세 홈 카드가 다시 나타났고 focus는 계속 `com.limdo.hangul/.MainActivity`였다.
- 변경 후 PNG를 직접 읽어 집 그림의 왜곡·흐림·잘림·검은 배경·alpha halo가 없고, 14개 lesson 카드보다 과도하게 강조되지 않음을 확인했다.
- 기존 atlas는 1254 × 1254 RGBA(`hasAlpha: yes`)이며 변경되지 않았다. 루프 174에서 자동 검사한 9개 셀 네 모서리 alpha 0, 최소 21 px 안전 여백과 셀 간 번짐 0 근거를 유지하며 최종 APK에 production resource가 포함됐다.
- 실제 아이 관찰: 실행 안 함.
