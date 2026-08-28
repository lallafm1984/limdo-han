# 루프 목표 187 — 보호자 녹음 권한·background lifecycle

## 작업 가치 관문

분류: 결함 수정
사용자 가치: 보호자가 마이크 권한을 거부·취소하거나 녹음·재생 중 앱을 벗어나도 START·SUCCESS 파일과 아이의 무음 학습 흐름이 안전하게 유지된다.
새로운 근거: 루프 185·186은 정상 권한·녹음·재생·삭제와 일부 background를 통과했지만 START·SUCCESS 두 event의 권한 거부·요청 취소·설정 취소·재생 중 background 행렬은 고정되지 않았다.
중복 방지: 이번 루프는 대표 lesson의 두 event에서 lifecycle 예외 행렬과 필요한 최소 상태 처리까지만 다룬다. 전체 lesson 확장·아이 화면 재생·자동 다음은 후속 루프로 분리한다.

## 목표

1. START·SUCCESS의 권한 거부·요청 취소·OS 설정 취소가 반복 요청·파일 손실 없이 무음 상태로 복귀한다.
2. 녹음 중·미리 듣기 중 background·화면 이탈에서 recorder·player·임시 파일을 해제하고 이전 완료 파일을 보존한다.
3. 아이 쓰기 화면은 권한을 요청하지 않고 녹음 없음·실패에서 기존 무음 흐름을 유지한다.

## 성공 조건

1. `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`가 통과한다.
2. 권한은 보호자 녹음 callback에서만 요청하고 거부·취소 후 기존 완료 파일과 다른 event·lesson byte가 유지된다.
3. START·SUCCESS 녹음 중 background에서 임시 파일 0건, 기존 완료 파일 보존, 안정 상태 복귀가 자동·실제 기기에서 일치한다.
4. 미리 듣기 중 background·화면 이탈 후 재생이 멈추고 재진입 시 READY·EMPTY의 안전 상태와 callback이 일치한다.
5. 정확한 2340 × 1080 PNG·hierarchy·focus에서 권한 대안·두 event 상태·조작이 잘림·겹침 없이 구분되고 주요 동작은 64 × 64 dp 이상이다.
6. `.loop/visual-evidence.md`에 package·APK SHA, 자동 그래픽 디자인·자동 QA·아이 대리 QA 통과와 새 P0·P1·진행 방해 P2 0건을 기록한다.

## 완료 정의

대표 lesson의 START·SUCCESS 권한·background lifecycle 예외 행렬이 자동·실제 기기·화면 근거로 통과하면 루프 187을 완료한다. 다음에는 M1의 현재 lesson 전체 확장 또는 사용되지 않는 음성 model 정리 중 가장 앞선 단일 작업을 준비한다.
완료 체크포인트만 `git push origin HEAD`로 일반 push하며 실패한 반복은 커밋하거나 push하지 않는다.
