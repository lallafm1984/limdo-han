# 루프 목표 197 — 직관적 정답 이펙트 production 반영

## 작업 가치 관문

분류: 사용자 지시
사용자 가치: 글을 읽지 못하는 아이가 정답 순간을 큰 체크 하나와 기쁜 캐릭터 모양으로 즉시 이해한다.
새로운 근거: 현재 production 성공 원형은 별·리본 축하 분위기만 있고 정답을 뜻하는 직접 표식이 없다. 사용자가 이미 생성한 투명 RGBA 후보의 production 적용을 명시적으로 요청했다.
중복 방지: 기존 후보 하나의 규격화·production 교체·실화면 판정만 한다. 자동 다음·녹음·버튼·geometry는 바꾸지 않는다.

## 목표

`.loop/runtime/generated/limdo_success_correct_feedback_v2.png`를 production 성공 이펙트 규격에 맞게 1024 × 1024 투명 RGBA로 보정해 실제 `SuccessFeedbackOverlay`에 적용하고, 완성 글자와 네 조작을 가리지 않으면서 큰 초록 체크 하나가 정답 의미의 가장 강한 표식이 되게 한다.

## 성공 조건

1. `./scripts/verify.sh`, `git diff --check`, `scripts/check-visual-loop.sh`가 통과한다.
2. 후보와 production 파일이 1024 × 1024 RGBA·네 모서리 alpha 0·안전한 alpha bbox·충분한 투명 중앙·셀 번짐 0건·균일 배율 관문을 통과한다.
3. production PNG가 최종 APK에 포함되고 `SuccessFeedbackOverlay`가 실제로 소비하며, 접근성 설명이 정답 의미를 짧게 전달한다.
4. 2340 × 1080·LimDo focus의 동일 lesson 변경 전·후 성공 근거에서 큰 초록 체크는 정확히 하나이고 첫눈에 정답으로 읽힌다.
5. 완성 글자·네 조작의 가림·pointer 소비·잘림·흐림·halo·검은 배경·색 번짐이 0건이고 자동 다음·수동 취소가 회귀하지 않는다.
6. 자동 그래픽 디자인·자동 QA·아이 대리 QA에서 새 P0·P1·진행 방해 P2가 0건이다.

## 완료 정의

생성 후보가 자산 자동 검사·APK production 소비·동일 성공 상태 변경 전·후 실화면·자동 다음 회귀 관문을 모두 통과할 때 완료한다. 완료 체크포인트만 `git push origin HEAD`로 일반 push한다.
