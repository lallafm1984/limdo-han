# 루프 목표 188 — 사용되지 않는 합성 음성 model 정리

## 작업 가치 관문

분류: 결함 수정
사용자 가치: 과거의 사용되지 않는 합성 음성 문구·재생 상태를 보호자 START·SUCCESS event와 무음 대안으로 단순화한다.
새로운 근거: 루프 187까지 보호자 녹음 파일·callback·lifecycle을 사용하지만 이전 합성 음성 model의 production 소비 여부는 고정되지 않았다.
중복 방지: 사용되지 않는 문구·상태 model 정리와 전체 회귀까지만 다루고 전체 lesson 확장·아이 화면 재생·자동 다음은 후속 루프로 분리한다.

## 목표

1. production과 test에서 사용되지 않는 합성 음성 문구·재생 상태 model의 소비 여부를 확인한다.
2. 사용되지 않거나 중복된 model을 제거하고 보호자 START·SUCCESS event model과 무음 대안으로 정리한다.
3. 기존 학습·geometry·파일 저장·아이 무음 흐름을 보존한다.

## 성공 조건

1. `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`가 통과한다.
2. 이후 합성 음성 model의 경로가 자동 검사에 고정되어 production 호출·상태 표현을 제거한다.
3. 보호자 START·SUCCESS event·파일명·callback·background 해제·손상 fallback이 전체 검사와 실제 기기에서 통과한다.
4. 정확한 2340 × 1080에서 기존 화면·조작·내용·터치·focus 회귀가 없고 새 P0·P1·진행 방해 P2가 0건이다.

## 완료 정의

사용되지 않는 합성 음성 model이 제거되고 보호자 녹음 event만 production·검사에 남으며 전체 회귀가 없을 때 완료한다. 다음에는 M1 완료 관문의 전체 lesson 두 녹음 칸 확장을 준비한다.
완료 체크포인트만 `git push origin HEAD`로 일반 push하며 실패한 반복은 커밋하거나 push하지 않는다.
