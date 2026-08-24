# 현재 루프 상태

루프: 007 — 초기 음성 재생 중 다시 듣기 상태 QA

상태: 준비

반복: 0

마지막 검증: 2026-08-24 루프 006 반복 7에서 도마뱀을 24 dp 아래로 이동했다. `./scripts/verify.sh`의 자동화 계약, 단위 테스트 26개, Android lint, debug build와 `git diff --check`가 통과했다. 새 2340 × 1080 초기·재시도·성공·지우기·다시 듣기 근거에서 시스템 영역 겹침을 없앰고 `design-qa.md`가 `final result: passed`를 기록했다.

완료한 조건: 루프 006 성공 조건 1~14 모두 새 자동·에뮬레이터·아이 대리 QA 근거로 통과했다. `실제 아이 관찰: 실행 안 함`.

현재 근거: `captures/loop006/iteration7/initial.png`, `retry.png`, `success.png`, `cleared-replay.png`과 hierarchy, `LimDoSpeech` callback으로 루프 006 핵심 흐름을 확인했다. 새 QA에서 초기 자동 안내 재생 중의 다시 듣기 버튼 상태 전환은 연속 근거로 아직 검증하지 않아 `QA-004`로 기록했다.

남은 조건: 루프 007 성공 조건 1~6은 아직 미검증이다.

다음 작업: 새 세션에서 루프 007 반복 1로 cold launch 직후부터 초기 음성 완료 후까지 짧은 간격의 화면·hierarchy·callback을 수집하고 문제 유무를 먼저 판정한다.
