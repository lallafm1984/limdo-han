# Codex CLI 루프 큐

실행 단계: CLI

활성 루프: 없음

검토 관문: HUMAN_REVIEW_AFTER_LOOP_005

| 루프 | 상태 | 목표 출처 | 자동 전환 |
| --- | --- | --- | --- |
| 002 | 완료 | `.loop/history.md` | 검증 완료 후 중지 |
| 003 | 완료 | `.loop/history.md` | 검증 완료 후 중지 |
| 004 | 완료 | `.loop/history.md` | 검증 완료 후 중지 |
| 005 | 완료 | `LOOP_GOAL.md` | `HUMAN_REVIEW_AFTER_LOOP_005`에서 중지 |

큐 규칙:

- `활성` 루프는 정확히 하나만 존재할 수 있다.
- 명시적으로 정의되고 `준비`로 표시된 항목만 다음 루프로 올릴 수 있다.
- 작업자는 활성 루프를 완료할 수 있지만 다음 루프를 임의로 만들 수 없다.
- 큐 전환은 `.loop/history.md`에 한글로 기록한다.
