# 루프 목표 062 — 복원 대기 `INITIAL` 지우기 취소 탐색

## 목표

수동 다시 듣기 `INITIAL` pending을 저장한 뒤 새 PID로 복원하는 즉시, 새 TTS가 `Ready`가 되기 전 지우기를 누르면 대기 음성이 취소되고 빈 입력·차량·후속 성공이 일관되는지 좁게 검증하며, 실제 실패가 확인될 때만 최소 보정을 적용한다.

## 성공 조건

1. `INITIAL` pending이 저장된 `mHaveState=true` Bundle과 PID 교체, 새 TTS `Ready` 전 지우기 실행 순서를 확정한다.
2. 지우기 뒤 늦은 `INITIAL` 재생 시작·완료가 모두 0회이고 음성 상태가 정상 준비로 끝난다.
3. 소방차는 `[99,62][435,288]` 시작 위치, 빈 획·무표식·입력 가능 상태를 유지한다.
4. 이어진 첫 정상 입력은 `SUCCESS` 시작·완료 각 1회와 소방차 126 px 한 칸 이동만 만든다.
5. 다시 듣기·지우기·비활성 다음·차량 비가림·비상호작과 음성 callback이 회귀하지 않는다.
6. 제품 변경이 있으면 자동 상태 회귀를 추가하고 `./scripts/verify.sh`, `git diff --check`를 통과한다.
7. 정확한 2340 × 1080 화면·hierarchy·PID·saved-state·callback과 아이 대리 QA, `실제 아이 관찰: 실행 안 함`을 기록한다.

## 다음 반복

제품 코드를 먼저 바꾸지 않고 `INITIAL` pending 저장→PID 종료→기존 task 복원→TTS `Ready` 전 지우기→늦은 callback 없음→첫 정상 입력을 수집한다.

## 완료 정의

모든 조건을 통과하면 새 아이 대리 QA로 다음 루프 하나를 준비하고 체크포인트 커밋 후 `git push origin HEAD`로 일반 push한다.
