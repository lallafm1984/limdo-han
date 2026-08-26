# LimDo 자동 실행 로그

CLI 감독자를 실행하면 이 폴더 아래 `YYYY-MM-DD/`에 날짜별 `supervisor.log`와 `sessions.tsv`가 생성된다. launchd 표준 출력과 오류는 `launchd/`에 생성된다.

실행 중 생성되는 로그는 Git에 포함하지 않는다. 각 세션의 상세 JSONL·stderr·최종 응답은 `.loop/runtime/sessions/<run-id>/`에서 확인한다.
