# 현재 시각 루프 증거

루프: 201
상태: 완료

기준 화면: 2340 × 1080
변경 전 PNG: captures/loop201/iteration1/after/complete-final.png
변경 전 hierarchy: captures/loop201/iteration1/after/complete-final.xml
변경 후 PNG: captures/loop201/iteration2/after/emulator/complete-final-v2.png
변경 후 hierarchy: captures/loop201/iteration2/after/emulator/complete-final-v2.xml
package: com.limdo.hangul
focus: 통과
APK SHA-256: 13be3bc85f3fb173f0303f4f43b108237c99b50a2a04d0a2a47b0f9df9eaa1e3
production 자산 경로: 기존 production HOME·다시쓰기 atlas와 완료 카드·색·모서리 token 재사용
production 소비 검사: 통과 — production 완료 화면이 atlas icon과 label을 630 × 210 px clickable 동작으로 소비
자산 자동 검사: 불필요 — 새 raster 자산 0건
자동 그래픽 디자인 역할: 통과
자동 QA 역할: 통과
아이 대리 QA: 통과
새 P0: 0
새 P1: 0
진행 방해 P2: 0

## 판정 근거

- 변경 전 최종 후보는 HOME·지우기 atlas가 과대 확대돼 별·완료 제목을 화면에서 밀어냈다. 변경 후에는 별·`다 했어요!`·HOME 그림+`홈`·지우기 그림+`처음부터`가 동시에 보이고 잘림·겹침·과대 확대가 0건이다.
- 두 완료 동작은 각각 630 × 210 px이며 hierarchy의 clickable node와 짧은 문구가 함께 있다. `처음부터`는 index 0 저장 뒤 첫 lesson으로 복귀하고 `홈`은 마지막 index를 보존한 채 세 메뉴 홈으로 복귀했다.
- `GIEOK·GIEOK·A` fixture는 index `0→1→2`로 같은 lesson 두 순번을 각각 소비했고 index 1 강제 종료·재진입 뒤 다시 `ㄱ`, 두 번째 성공 뒤 `ㅏ`가 나타났다. 빈 목록 index `-1`에서는 자유 `ㄱ→ㄴ` 다음 순환이 유지됐다.
- 유효 단일 녹음 `gieok.m4a` SHA-256 `e395589b6f8ca5d62d0033d302e4e59af77d3878710af5a81c5039033b983c8e`가 보존됐고 성공 노출·재생 뒤에만 전진했다. 자동 그래픽 디자인·자동 QA·아이 대리 QA는 통과했으며 실제 아이 관찰: 실행 안 함.
