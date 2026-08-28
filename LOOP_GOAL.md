# 루프 목표 186 — 한 lesson의 정답 후 보호자 녹음

## 작업 가치 관문

분류: 제품 변경
사용자 가치: 보호자가 선택한 lesson을 아이가 바르게 쓴 직후 들을 짧은 목소리를 기기 안에 직접 녹음하고 확인·교체·삭제할 수 있다.
새로운 근거: 루프 185에서 대표 lesson의 `쓰기 전` 녹음 lifecycle과 no-backup 저장은 통과했지만 `정답 후` event의 독립 파일·상태·동작은 production에 없다.
중복 방지: 이번 루프는 대표 lesson 하나의 `정답 후` 녹음 시작·정지·미리 듣기·다시 녹음·삭제와 START 파일 비회귀까지만 구현한다. 모든 lesson 확장, 아이 화면 재생과 자동 다음은 후속 루프로 분리한다.

## 목표

1. 대표 lesson의 보호자 화면에서 `정답 후` 녹음 없음·녹음 중·완료 상태를 확인한다.
2. SUCCESS 녹음은 기존 START와 다른 lesson·event 고유 no-backup 경로에 원자적으로 저장한다.
3. 미리 듣기·정지·다시 녹음·삭제가 START 녹음과 아이 학습 흐름을 바꾸지 않는다.

## 성공 조건

1. `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`가 통과한다.
2. `RECORD_AUDIO` 요청 시점·최대 8초·임시 파일→완료 파일 원자 교체와 lifecycle 해제가 START 계약과 같다.
3. SUCCESS 파일을 녹음·교체·삭제해도 같은 lesson의 START 파일과 다른 lesson 파일이 byte-for-byte 유지된다.
4. 녹음 없음·중·완료·미리 듣기·다시 녹음·삭제 상태와 callback이 자동 검사와 실제 기기에서 일치한다.
5. 정확한 2340 × 1080 화면 PNG·hierarchy·focus에서 두 event가 혼동되지 않고 주요 동작이 최소 64 × 64 dp이며 잘림·겹침이 없다.
6. `.loop/visual-evidence.md`에 package·APK SHA, 자동 그래픽 디자인·자동 QA·아이 대리 QA 통과와 새 P0·P1·진행 방해 P2 0건을 기록한다.

## 완료 정의

대표 lesson 하나의 `정답 후` 녹음 lifecycle과 START 파일 격리가 자동·실제 기기·화면 근거로 통과하면 루프 186을 완료한다. 다음에는 현재 lesson 전체 확장 또는 M1의 권한·background lifecycle 잔여 조건 중 가장 앞선 단일 제품 작업을 준비한다.
완료 체크포인트만 `git push origin HEAD`로 일반 push하며 실패한 반복은 커밋하거나 push하지 않는다.
