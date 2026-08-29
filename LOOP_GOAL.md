# 루프 목표 237 — 프리미엄 디자인 D0 전 씬 inventory·삭제 안전·아트 방향 3안

## 작업 가치 관문

분류: 사용자 지시
사용자 가치: 전면 리디자인 전에 실제로 도달 가능한 전 씬과 데이터 삭제 위험을 확정하고, 일관된 프리미엄 시각 방향 세 가지 중 하나를 안전하게 선택할 수 있다.
새로운 근거: 2026-08-30 최신 사용자 지시는 기능만 동작하는 현재 화면의 상품성 부족을 지적하고 전 씬 프리미엄 UI·UX를 요청했으며, 보호자 녹음 삭제가 확인·실행 취소 없이 즉시 실행되는 별도 데이터 안전 결함이 production 감사에서 확인됐다.
중복 방지: 이번 루프는 D0의 도달 씬 inventory, 녹음 삭제 확인 또는 즉시 실행 취소, 성능 baseline, 공통 art bible과 full-screen mock 정확히 3안까지만 다룬다. 사용자가 한 방향을 선택하기 전 production 전면 리디자인이나 D1 이후 화면군 구현은 시작하지 않는다.

## 목표

`docs/전-씬-프리미엄-디자인-루프-작업지시.md`의 D0를 수행한다. 같은 새 APK에서 도달 가능한 씬·중요 상태를 inventory로 확정하고, 보호자 녹음 삭제에 확인 또는 즉시 실행 취소를 추가하며, 성능·자산 기준과 공통 art bible을 고정한다. 현재 production 화면을 기준으로 full-screen mock 방향을 정확히 세 가지 준비한 뒤 사용자 선택을 기다린다.
모든 화면 근거는 정확한 2340 × 1080에서 수집한다.

시각 변경: 예
자산 필요 판정: 필요 — 현재 production 기준 화면을 바탕으로 서로 구분되는 full-screen mock 세 방향을 제시하려면 슬롯과 안전 영역을 측정한 전용 bitmap 시안이 필요하며, `imagegen`의 Codex 내장 생성기로 만들고 production 적용은 사용자 선택 뒤로 미룬다.

## 성공 조건

1. D0 범위의 자동 검사와 `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`, `scripts/check-automation.sh`가 통과한다.
2. 새 APK에서 홈부터 실제 입력으로 도달 가능한 모든 씬·중요 상태의 inventory를 PNG·hierarchy·focus·진입 동작과 연결하고 `GaAssembly`는 도달 불가로 분리한다.
3. 보호자 녹음 삭제는 대상 글자와 결과를 미리 보여 주는 확인 또는 즉시 실행 취소를 제공하며 취소 시 원본 파일을 보존한다.
4. 공통 palette·재질·광원·corner·elevation·spacing·typography·icon·background density·motion token을 art bible과 재사용 가능한 코드 기준으로 고정한다.
5. 현재 APK 크기, raster 압축·decode 크기, 대표 흐름 framestats·PSS 기준값을 기록한다.
6. 현재 production 기준 화면을 사용해 full-screen mock 방향을 정확히 세 가지 만들고, 각 방향을 자동 아트 디렉션·UI/UX·접근성 역할로 비교한다.
7. WritingCanvas 1962 × 954 px, 네 조작 168 × 168 px, 교육 geometry, 보호자 비스크롤 계약과 승인된 38개 음성 기능에 회귀가 없다.
8. 사용자가 세 방향 중 하나를 선택하기 전 D1 production 전면 구현을 시작하지 않는다.

## 완료 정의

D0 근거와 삭제 안전 수정·성능 baseline·art bible·mock 정확히 3안이 모두 준비되고 자동 역할 검토를 통과하면 사용자 선택 대기로 전환한다. 같은 작업자는 D1 production 구현을 시작하지 않는다.
완료 체크포인트만 `git push origin HEAD`로 일반 push한다.
