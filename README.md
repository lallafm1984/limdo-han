# LimDo

다섯 살 아이를 위한 로컬 우선 Android 한글 쓰기 학습 앱이다. 현재 구현 기준과 1차 완료 조건은 `docs/1차-목표-작업-기획서.md`, 다음 제품 방향은 `docs/2차-목표-제품-기획서.md`, 한글 교육 근거는 `docs/한글-교육-순서-근거.md`를 따른다.

## CLI 루프

설정은 `.loop/env.sh`, 작업자 지시는 `.loop/cli-worker-prompt.md`, 현재 상태는 `.loop/state.md`, 사용자 피드백은 `.loop/user-directives.md`에 있다.

```bash
./scripts/start-cli-loop.sh
./scripts/stop-cli-loop.sh
./scripts/cli-loop-status.sh
```

`start-cli-loop.sh`는 현재 로그인 세션의 분리 `screen`에서 실행한다. `stop-cli-loop.sh`는 현재 작업자 한 바퀴가 끝난 뒤 정상 종료하도록 `.loop/STOP`과 런타임 중지 신호를 만든다.

## macOS 로그인 자동 실행

현재는 자동으로 설치하거나 켜지 않는다. 공용 에뮬레이터를 다른 프로젝트가 사용하지 않는지 확인한 뒤 명시적으로 실행한다.

```bash
./scripts/cli-loop-service.sh prepare
./scripts/cli-loop-service.sh enable
./scripts/cli-loop-service.sh disable
./scripts/cli-loop-service.sh status
./scripts/cli-loop-service.sh uninstall
```

launchd 설정은 PATH와 Codex 실행 파일을 절대 경로로 기록한다. 정상 종료에는 재시작하지 않고 비정상 종료에만 재시작한다.

## 로그

- 날짜별 요약: `logs/YYYY-MM-DD/`
- launchd 출력: `logs/launchd/`
- 세션별 상세 JSONL·stderr·최종 응답: `.loop/runtime/sessions/<run-id>/`

## 검증

```bash
./scripts/check-automation.sh
./scripts/verify.sh
git diff --check
```

화면 또는 입력 변경은 정확한 2340 × 1080 에뮬레이터 화면을 직접 확인해야 완료된다. 자동 QA와 아이 대리 시뮬레이션을 실제 사람 QA나 실제 아이 관찰로 표현하지 않는다.
