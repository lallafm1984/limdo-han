# LimDo Codex 앱 인계 기록

## 실행 단계

단계: `CLI`

초기 Codex 앱 Goal 단계는 검증된 루프 004 완료 후 종료되었다. 현재 기준 계약은 새 세션 CLI 자동화이며 `CLI_AUTOMATION.md`를 따른다.

Codex 앱은 상태를 확인하거나 사용자가 요청한 설정을 바꿀 수 있지만 `.loop/runtime/supervisor.lock`이 살아 있는 동안 두 번째 감독자를 실행하면 안 된다.

CLI 단계도 로컬 전용이다. push, 출시, 배포, 외부 메시지, 파괴적 작업, 큐에 없는 제품 범위는 명시적 사용자 승인이 필요하다.
