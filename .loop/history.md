# 루프 이력

이 파일은 루프 엔지니어링의 가설, 변경, 검증, 결과, 다음 작업을 한글로 덧붙이는 기록이다.

2026-08-24 사용자 요청에 따라 기존 영문 기록을 아래 한글 연대기로 한 번 정리했다. 커밋과 이전 Git 이력에는 원본 세부 기록이 남아 있으며, 핵심 실패 원인, 검증 수치, APK hash, 에뮬레이터 측정값은 이 문서에 보존했다. 이후 기록은 한글로만 덧붙이고 기존 항목을 수정하거나 삭제하지 않는다.

## 루프 000 — Android 개발 환경 구성

### 반복 1

- 가설: 최소 Compose 프로젝트와 Gradle wrapper만으로 전체 검증을 통과할 수 있다.
- 변경: Android 골격, 루프 제어 문서, macOS/Linux·Windows 검증 스크립트, Gradle 8.13 wrapper를 만들었다.
- 검증: `./scripts/verify.sh`를 실행했다.
- 결과: 실패. 선택적 Compose preview 의존성 없이 `@Preview`를 참조해 `compileDebugKotlin`에서 단위 테스트, lint, build가 모두 멈췄다.
- 다음 작업: 불필요한 preview 코드만 제거한다.

### 반복 2

- 가설: 사용하지 않는 preview 코드를 제거하면 의존성 추가 없이 컴파일 실패가 해결된다.
- 변경: `@Preview` import와 preview 전용 composable을 제거했다.
- 검증: `testDebugUnitTest`, `lintDebug`, `assembleDebug`가 모두 통과했고 `ALL VERIFICATIONS PASSED`로 종료했다.
- 결과: 루프 000 완료. 비차단 경고는 native symbol strip 안내, 사용 가능한 버전 알림 4개, launcher icon 1개였다.
- 다음 작업: 가로형 앱 껍데기 루프 001.

## 루프 001 — 가로형 학습 화면 껍데기

### 준비

- 가설: 추가 의존성 없는 Compose 30/70 중앙 배치로 아이 중심 시각 계층을 만들 수 있다.
- 검증 기준을 정의했고 루프 000 커밋 `a03869d`를 당시 `origin/main`에 반영했다.

### 반복 1

- 변경: 가로 manifest, launcher icon, 한글 문구, 30/70 학습 영역, 동작 자리표시자, 비율 단위 테스트를 추가했다.
- 결과: 실패. 파일 수준 `weight` import가 내부 Compose 심볼로 해석되어 세 검증 단계가 `compileDebugKotlin`에서 멈췄다.
- 다음 작업: 충돌 import 하나만 제거한다.

### 반복 2

- 변경: 직접 `weight` import 하나를 제거했다.
- 자동 검증: 2개 테스트, lint, debug build 통과.
- 에뮬레이터: APK 설치, `com.example.limdo/.MainActivity` cold launch, focus, 2400 × 1080, `ROTATION_90`, 새 화면 확인 통과.
- 결과: 의도한 header, guide, 쓰기 미리보기, action shelf가 보였다.
- 개선: 앱 이름 리소스 미사용과 숫자 진행 문구 lint 경고를 정리하기로 했다.

### 반복 3

- 변경: manifest label을 `R.string.app_name`에 연결하고 숫자 진행 자리표시자를 `첫 단계`로 바꿨다.
- 검증: 2개 테스트, lint, debug build, 최종 APK 재설치와 cold launch 통과. 화면 잘림·겹침 없음.
- 결과: 루프 001 조건 통과. Android 16 대화면에서 고정 방향이 무시될 수 있는 위험은 남겼다.

### 반복 4 — 기준 해상도 변경

- 변경: `alarmquest-qa` AVD를 기기 해상도 1080 × 2340으로 바꾸고 앱 기준을 가로 2340 × 세로 1080으로 기록했다. 앱 배치 값은 바꾸지 않았다.
- 검증: AVD 재시작, APK 재설치, focus, 2340 × 1080, `ROTATION_90`, 정확한 화면 확인 통과.
- 결과: 잘림·겹침·계층 회귀 없음. 이전 2400 × 1080 근거를 대체한다.

## Codex 앱 자동화 초기 구성 — 2026-08-24

- 목표: CLI 자동화 전에 Codex 앱 Goal 모드에서 중단 후 재개 가능한 로컬 자동화 환경을 만든다.
- 변경: `CODEX_APP` 단계, 로컬 전용 계약, 단일 활성 큐, 루프 002 목표, 자동화 계약 검사, 상태 반복 0을 구성했다.
- 검증: `bash -n`, `./scripts/check-automation.sh`, `./scripts/verify.sh`, `git diff --check` 통과. 이 Mac에 PowerShell이 없어 Windows 검사는 실행하지 않았다.
- 결과: 명시적 범위, 재개 경로, 사람 검토 중지 조건을 갖췄다.

## 루프 002 — 입력 전 `ㄱ` 쓰기 미리보기

### 반복 1

- 가설: 정규화한 두 구간 `ㄱ` geometry로 의존성과 입력 동작을 추가하지 않고 크기 대응 Canvas를 만들 수 있다.
- 변경: 순수 Kotlin geometry, 비상호작용 `WritingCanvas`, 연습선·미리보기 획·시작 표시, 접근성 문구, bounds 테스트를 추가했다.
- 자동 검증: 네 단계 통과. 앱 코드 기준 가설 확인.

### 완료 검증

- APK 설치와 cold launch 1541 ms, focus 통과.
- 앱 bounds `Rect(0, 0 - 2340, 1080)`, `ROTATION_90` 확인.
- `captures/loop002-iteration1-2340x1080.png`을 정확한 2340 × 1080로 확인하고 잘림·겹침·guide 가독성 문제 없음.
- UI hierarchy에서 `손가락 입력 전 단계인 기역 쓰기 길 미리보기` 접근성 설명 확인.
- 단위 테스트 4개 통과.
- APK 8.7 MB, SHA-256 `f585651a1406759fc51f63e653624aa5b847115be4e6689176169dd5d6655e10`.
- package `com.example.limdo`, version `0.1.0`, min SDK 26, target SDK 36.
- 결과: 루프 002 완료. `HUMAN_REVIEW_AFTER_LOOP_002`에서 중지했다.

## 다섯 살 아이 사용성 QA 정책 — 2026-08-24

- 사용자 우선순위: 아이가 쉽게 알아보고 사용할 수 있는지와 입력 화면이 충분히 큰지를 핵심 QA로 삼는다.
- 변경: `자동`, `에뮬레이터`, `아이 대리 점검`, `실제 아이 관찰` 근거를 분리했다. 2340 × 1080에서 실제 그리기 내부 최소 1170 × 378 px, 버튼 최소 64 × 64 dp, 간격 최소 12 dp를 정했다.
- 당시 루프 002 Canvas: `[820,447][2214,632]` = 1394 × 185 px. 너비 59.6%는 통과했으나 높이 17.1%로 입력 기준 35%에 실패했다.
- 판정: 비상호작용 미리보기 완료 상태는 유효하지만 입력을 활성화하는 다음 루프에서 높이를 먼저 늘려야 한다.
- `실제 아이 관찰: 실행 안 함`.
- 검증: 자동화 계약, 테스트 4개, lint, debug build, `git diff --check` 통과. APK 내용 변경 없음.

## 루프 003 — 큰 아이용 입력 영역과 지우기

### 활성화

- 목표: 편한 한 손가락 입력 영역, 획 한 개 캡처, 안전한 지우기를 추가하되 정답 판정과 진행은 제외한다.
- 기준: 이전 Canvas 1394 × 185 px, 최소 1170 × 378 px, 입력 없음, 아이 대리 높이 실패.

### 반복 1

- 가설: 쓰기 카드 내부의 세로 문구를 빼면 30/70 계층을 유지하면서 높이 기준을 넘을 수 있고, 순수 bounded stroke 상태로 한 손가락과 지우기를 만들 수 있다.
- 변경: Canvas 확대, bounded stroke, pointer capture, 즉시 획 표시, 활성 지우기, 64 dp action, 간결한 안내, 안전 여백·지우기 테스트.
- 결과: 실패. `MainActivity.kt`에 `semantics`, `contentDescription` import가 없어 컴파일이 멈췄다.

### 반복 2

- 변경: 누락된 semantics import 두 개만 추가했다.
- 검증: 자동화 계약, 단위 테스트, lint, debug build 통과.

### 에뮬레이터 검토

- 설치와 cold launch 1687 ms, 2340 × 1080, `ROTATION_90` 통과.
- Canvas `[789,321][2245,759]` = 1456 × 438 px, 화면 너비 62.2%, 높이 40.6%로 기준 통과.
- 지우기 `[811,849][1470,1017]` = 659 × 168 px, 약 251 × 64 dp.
- 최소 action 간격 37 px, 약 14.1 dp로 기준 통과.
- ADB swipe 획 표시와 지우기 후 guide 보존 통과.
- 아이 대리 실패: 쓰기 영역과 시작점은 알 수 있으나 쓰는 방향과 현재 과제는 첫 화면만으로 충분히 명확하지 않았다.

### 반복 3

- 변경: 시각 시작점·경로를 강화하고 과제 계층을 단순화했다.
- 자동 검증과 정확한 에뮬레이터 입력·지우기·bounds를 다시 통과했다.
- 아이 대리 점검에서 쓰는 곳, 시작점, 사용할 수 없는 버튼, 지우기를 글을 읽지 않고 식별했다.
- `실제 아이 관찰: 실행 안 함`.
- 결과: 루프 003 완료. 사람 관찰 전까지 실제 다섯 살 사용성을 입증하지 않았다는 위험을 유지했다.

## 루프 004 — 안내된 `ㄱ` 따라 쓰기

### 활성화

- 목표: 큰 쓰기 영역을 유지하고 한 획의 시작, 가로→세로 순서, 방향, guide 근접, 완성을 로컬에서 판정해 즉시 성공 또는 부드러운 재시도를 보여 준다.
- 범위 제외: 점수, 벌점, 등급, 음성, 저장, 보상, 이동, 여러 글자, 네트워크.

### 반복 1

- 가설: 정규화 guide 투영으로 크기와 무관한 순수 판정기를 만들 수 있다.
- 변경: `GieokTraceEvaluator`, 결과 enum, 기본 결과 테스트를 추가했다.
- 결과: 일부 off-guide fixture가 방향 오류를 먼저 일으켜 기대 결과와 달랐다.

### 반복 2

- 변경: 판정 순서와 fixture를 좁게 조정했다.
- 결과: off-guide 표본 비율이 경계값과 같아 `>` 조건을 넘지 못하는 테스트 실패가 남았다.

### 반복 3

- 변경: 세로선과 평행하게 120 px 떨어진 단조 증가 표본 세 개로 fixture만 바꿨다.
- 검증: 자동화 계약, 14개 테스트, lint, debug build 통과. 여섯 결과, jitter, scaling, invalid bounds 근거 확보.

### 반복 4

- 변경: `TraceAttempt` 생명주기, pointer release 뒤 판정, 다음 gesture·지우기 초기화, 결과별 아이 문구와 semantics, lifecycle 테스트를 연결했다.
- 결과: `stateDescription` import 누락으로 컴파일 실패.

### 반복 5

- 변경: 누락 import 하나만 추가했다.
- 검증: 17개 테스트, lint, debug build 통과.
- 새 사용자 조건: 아이는 듣기는 가능하지만 글을 읽지 못하므로 보이는 한글 문구만으로 지시·결과·방향·초기화를 판단하게 해서는 안 된다.

### 반복 6

- 변경: AGENTS와 QA에 듣기 가능·읽기 불가 특성을 기록하고 guide에 오른쪽·아래쪽 화살표, 큰 `✓`, `↻`, `⌫` 단서를 추가했다.
- 검증: 17개 테스트, lint, debug build 통과.

### 반복 7 — 에뮬레이터 검토

- APK SHA-256 `8dfb02f45785a4850a16d82bd0d75d1b3209d26791de34063ef58687cd445b9c`.
- 설치, cold launch 1432 ms, focus, 2340 × 1080, `ROTATION_90` 통과.
- Canvas 1456 × 438 px, 지우기 659 × 168 px 유지.
- 초기 시작점·오른쪽·아래 화살표, 손을 뗀 뒤 `↻` 재시도 확인.
- 부분 통과: 새 시각 단서 행이 고정 guide card 높이를 넘겨 보호자·접근성용 한글 도움말이 화면과 hierarchy에서 사라졌다.

### 반복 8 — 완료

- 변경: guide card padding 20→12 dp, 결과 원 88→72 dp, 해당 글자 크기만 줄였다. Canvas와 action shelf는 유지했다.
- 최종 자동 검증: 네 단계, 17개 테스트, lint 오류 0개, 기존 비차단 버전·방향 알림 5개, debug build 통과.
- 최종 APK SHA-256 `68da1dc7d3599f373ef4de115809eb1e1ed91c6d3ed599f8d6f099eb6c5c0c4f`.
- 에뮬레이터: 최종 hash APK 재설치, cold launch 1300 ms, focus, 2340 × 1080, `ROTATION_90` 통과.
- Canvas 1456 × 438 px, 너비 62.2%, 높이 40.6%. 지우기 약 251 × 64 dp, 최소 간격 약 14.1 dp.
- 초기, 잘못된 시작, 올바른 한 획 `ㄱ`, 성공 유지, 지우기 복원 화면을 새로 확인했다.
- 아이 대리 점검: 큰 guide, 주황 시작점, 오른쪽·아래 화살표, `↻`, 별+`✓`, `⌫`로 글을 읽지 않고 과제·위치·시작·방향·결과·초기화를 식별했다.
- `아이 대리 점검: 글을 읽지 않고 통과`.
- `실제 아이 관찰: 실행 안 함`.
- 결과: 13개 조건 통과, 루프 004 완료, `HUMAN_REVIEW_AFTER_LOOP_004`에서 중지.

## 새 세션 CLI 단계 전환 — 루프 005 승인

### 사용자 지시

Codex 앱 Goal 단계에서 CLI로 옮기고 매 반복을 완전히 새로운 세션에서 계속한다.

### 자동화 결정과 검증

- 저장소 전용 감독자가 새 임시 `codex exec` 작업자를 반복마다 하나씩 시작한다.
- resume, fork, 중첩 Codex, 원격 변경, 파괴적 작업, 큐 밖 범위를 금지했다.
- 원자적 중복 잠금, 실행 근거, 정상 중지 신호, 프로세스 세 번 연속 실패 중지를 추가했다.
- 지속 맥락은 목표, 큐, 상태, 이력 파일로만 이어진다.
- CLI `codex-cli 0.149.0-alpha.4.1`, ChatGPT 인증 확인.
- 분리 스모크 thread `01a03206-d5f5-7b83-b102-a321f4ef5a4c` 종료 0.
- 셸 문법, 자동화 계약, 17개 테스트, lint, debug build 통과.

### 실행 호스트 보정

- 평범한 `nohup` 자식은 Codex 앱 명령 호스트 종료와 함께 끝났다. 두 thread가 `thread.started`까지만 도달했고 지속 상태를 바꾸지 않아 루프 반복으로 세지 않았다.
- launchd는 등록됐지만 Desktop 아래 프로젝트 접근을 macOS가 `Operation not permitted`로 차단했다. 사용자에게 넓은 시스템 권한을 요구하지 않았다.
- 내장 `screen` 분리 호스트가 시작 명령 종료 뒤에도 살아 있음을 교차 명령으로 확인하고 채택했다.

### Android 도구 샌드박스 보정

- 루프 005 반복 1에서 `workspace-write`가 Gradle file-lock contention 로컬 소켓을 막았다.
- 읽기 전용 스모크 thread `01a03212-e525-7a60-9633-f515ef9057c9`가 `--sandbox danger-full-access`로 `./gradlew --no-daemon --version`을 종료 0으로 실행했다.
- Gradle, ADB, emulator 로컬 소켓을 위해 기본 작업자 sandbox만 `danger-full-access`로 바꿨다. `--dangerously-bypass-approvals-and-sandbox`는 사용하지 않는다.

## 루프 005 — 로컬 음성 안내와 다시 듣기

### 반복 1 — 순수 음성 안내 모델

- 가설: 초기, 성공, 네 재시도마다 안정적인 식별자를 가진 순수 mapper가 Android 생명주기와 분리된 현재·다시 듣기 계약을 만든다.
- 변경: `SpokenCue`, `GieokTraceResult?` 전체 mapper, 식별자 안정성·한 줄 발화 테스트를 추가했다. UI, manifest, 의존성은 바꾸지 않았다.
- 검증: 자동화 계약은 통과했으나 Gradle 세 단계가 `FileLockContentionHandler`의 `java.net.SocketException: Operation not permitted`로 시작 전 실패했다.
- 결과: 검증 실패 첫 발생. 코드 결함 근거는 없었다.

### 반복 2 — sandbox 보정 뒤 재검증

- 가설: full-access 작업자에서 같은 코드와 테스트가 통과하면 이전 실패는 환경 문제다.
- 변경: 앱 코드는 그대로 유지했다.
- 검증: 자동화 계약, 19개 테스트, lint, debug build 통과. `SpokenCueModelTest` 2개가 모든 결과 매핑과 식별자·발화 제약을 확인했다.
- 비교: 기준 17개에서 19개로 증가했고 이전 실패가 환경 원인임을 확인했다.
- 결과: 성공 조건 2 자동 근거 확보. `실제 아이 관찰: 실행 안 함`.

### 반복 3 — 생명주기 기반 로컬 한국어 음성

- 가설: 순수 최신 안내 상태 기계와 Activity 소유 `TextToSpeech` adapter가 비동기 초기화, 네트워크 불필요 한국어 음성 선택, 최신 요청 교체, callback 상태, 안전한 종료를 제공한다.
- 변경: `LocalKoreanSpeech`, `SpeechPlaybackTracker`, 초기화·준비·재생·완료·오류·사용 불가·해제 상태, `QUEUE_FLUSH`, 오래된 callback 무시, tracker 테스트 3개를 추가했다. 아직 UI cue 요청은 연결하지 않았다.
- 첫 검증: 한글 경로 오타로 adapter 파일이 저장소에 들어오지 않아 `MainActivity.kt`의 `LocalKoreanSpeech` 참조 컴파일 실패.
- 보정: 누락 파일 위치만 바로잡았다.
- 최종 검증: 자동화 계약, 22개 테스트, lint, debug build 통과. manifest와 `app/build.gradle.kts` diff 없음.
- 결과: 인프라 자동 검증 통과. 실제 음성 선택·callback과 UI는 에뮬레이터 근거가 남았다.

### 반복 4 — Compose와 다시 듣기 연결

- 가설: Activity 음성 상태, 저장 가능한 초기 1회 플래그, 현재 결과에서 만든 안내를 연결하면 초기 안내는 준비 뒤 한 번, 결과 안내는 손을 뗀 뒤만, 지우기는 무음, 다시 듣기는 최신 안내만 실행된다.
- 변경: Activity callback을 Compose 상태로 전달하고 초기 1회 요청 guard를 추가했다. 결과 callback이 null이 아닐 때만 발화하며 drawing·지우기 경로는 말하지 않는다. 다시 듣기 자리표시자를 최소 64 dp `🔊`/`🔇` 버튼과 enabled semantics로 바꿨고 `다시 듣기` 접근성 문구를 추가했다.
- 검증: 자동화 계약, 22개 테스트, lint, debug build 통과. 인터넷·마이크 권한, service, 의존성 변경 없음.
- 결과: 자동 검증 통과. 에뮬레이터, audio callback, 정확한 bounds, 시각 실패 대체 검증은 실행하지 않았다.
- `자동: 통과`.
- `에뮬레이터: 실행 안 함`.
- `변경 UI 아이 대리 점검: 실행 안 함`.
- `실제 아이 관찰: 실행 안 함`.
- 다음 작업: `alarmquest-qa` 정확한 2340 × 1080에서 설치와 cold launch 후 초기 음성, 다시 듣기, 잘못된·올바른 결과, 결과 다시 듣기, 지우기 뒤 다시 듣기, 회전, 활성 또는 완전한 사용 불가 상태의 화면·hierarchy·callback 로그를 확인한다.

## 한글 기록 및 Codex 앱 보고 전환 — 2026-08-24

- 사용자 지시: 상태, 이력, 문서를 모두 한글로 기록하고 진행 상황을 Codex 앱에 주기적으로 알린다.
- 변경: 기존 영문 이력을 핵심 근거가 보존된 한글 연대기로 한 번 정리했다. AGENTS, 앱·CLI 자동화 계약, 목표, QA, 큐, 상태, worker 지시문을 한글로 전환했다.
- 기록 계약: 앞으로 Markdown 상태·이력·보고는 한글만 사용한다. 정확성이 필요한 파일명, 명령어, 코드·API 식별자와 기계 상태 토큰은 원문을 유지할 수 있다.
- Codex 앱: 현재 대화로 돌아오는 heartbeat 자동화 `LimDo 진행 상황 보고`를 10분 간격으로 활성화했다. 저장소를 읽기 전용으로 확인하며 파일 수정, 커밋, 감독자 시작·중지는 하지 않는다.
- 실제 전환 검증과 CLI 재시작 결과는 다음 항목에 덧붙인다.

### 전환 검증

- `bash -n`: 자동화 검사, 감독자, 시작·상태·중지 셸 스크립트 문법 통과.
- `shellcheck`: 사용 가능한 로컬 환경에서 관련 셸 스크립트 통과.
- `./scripts/check-automation.sh`: 한글 키로 단계 `CLI`, 루프 `005`, 상태 `진행 중`, 반복 `4`, 활성 루프 `005` 정합성 통과.
- 문서 검사: 한글 문장으로 시작해야 하는 Markdown 범위에서 독립된 영문 문장 없음. 파일명, 명령어, 코드·API 식별자는 허용된 기술 표기로 유지했다.
- `./scripts/verify.sh`: 자동화 계약, 단위 테스트 22개, Android lint, debug build 모두 통과.
- Codex 앱 자동화 ID `limdo`의 활성 카드를 앱에서 다시 확인했다.
- 제품 코드 변경은 추가하지 않았고 반복 4의 미커밋 작업을 보존했다.

### 반복 5 — 기준 에뮬레이터 음성·UI 검증

- 가설: 반복 4의 APK를 `alarmquest-qa`에 새로 설치해 정확한 2340 × 1080 가로 화면에서 실행하면, 초기 안내와 현재 안내 다시 듣기 및 결과 안내는 최신 안내 callback으로 관찰되고, 지우기는 자동 발화 없이 초기 안내를 복원하며, 음성 사용 불가 시에도 큰 `🔇` 버튼과 기존 시각 단서로 안전하게 유지된다.
- 변경: 제품 코드는 바꾸지 않고 반복 4의 APK를 새로 빌드·설치해 에뮬레이터 검증 근거만 추가했다. 캡처와 hierarchy 및 로그는 `captures/loop005/`에 저장했다.
- 자동 검증: `./scripts/check-automation.sh`와 `./scripts/verify.sh`의 자동화 계약, 단위 테스트 22개, Android lint, debug build가 모두 통과했다.
- 에뮬레이터 환경: `alarmquest-qa` 물리 해상도 1080 × 2340, 앱 bounds 2340 × 1080, `ROTATION_90`, focus `com.example.limdo/.MainActivity`, 새 설치 뒤 cold launch 1407 ms를 확인했다.
- 크기·배치: 실제 Canvas `[789,321][2245,759]` = 1456 × 438 px로 화면의 62.2% × 40.6%이며 가장 큰 아이 상호작용 영역이다. 다시 듣기 `[116,849][774,1017]` = 658 × 168 px(약 250.7 × 64 dp), 지우기와의 간격 37 px(약 14.1 dp)로 최소 기준을 통과했다. 안내, Canvas, 다시 듣기, 지우기, 시각적으로 약한 다음 자리표시자에 잘림이나 겹침이 없었다.
- 동작 근거: cold launch 뒤 `🔊`와 `현재 안내 다시 듣기`가 활성화됐다. 다시 듣기, 잘못된 시작, 한 번의 연속 `ㄱ` 입력, 성공 안내 다시 듣기, 지우기, 지우기 뒤 다시 듣기를 수행했다. hierarchy에서 재시도 `괜찮아! 주황 점에서 시작해보자!`, 성공 `해냈다! ㄱ 완성!`, 지우기 뒤 초기 `주황 점에서 시작! 오른쪽, 아래로!`가 현재 시각 결과와 일치했다. 지우기 직후에는 합성 요청이 없고 다시 듣기를 누른 뒤에만 새 요청이 나타났다.
- 음성 성공 근거: Google TTS가 `ko-kr-x-kod-local` 요청을 받고 후속 다시 듣기·결과 안내에서 `ko-kr-x-kod-seanet-embedded`로 dispatch했으며 `AudioTrack stop`까지 기록됐다. 이는 에뮬레이터의 기술 재생 근거일 뿐 사람이 실제 발화를 듣고 이해했다는 근거는 아니다.
- 정확한 실패: 새 설치 뒤 첫 초기 합성은 선택된 voice 이름이 `ko-kr-x-kod-local`이고 `isNetworkConnectionRequired == false`를 통과했지만 엔진 로그에서 `ko-kr-x-kod-server`로 dispatch되고 한국어 voice 다운로드 URL을 요청했다. 따라서 네트워크가 필요 없는 설치 음성만 사용한다는 조건 6·8·13은 이번 근거로 통과할 수 없으며 가설은 부분 반증됐다. 앱 자체 callback 상태도 로그나 별도 semantics로 직접 구분되지 않아 완료 callback 검증은 간접 근거에 머문다.
- 이전 반복 비교: 반복 4의 자동 검증만 있던 상태에서 정확한 화면, bounds, 초기·재시도·성공·지우기·다시 듣기와 embedded 재생 근거를 새로 확보했다. 동시에 이전에 알 수 없던 첫 실행 server fallback을 발견했다.
- 아이 대리 점검: 큰 Canvas와 `ㄱ` guide, 주황 시작점, `→`·`↓`, `↻`, 별과 `✓`, `⌫`, 큰 `🔊`, 회색 다음 자리표시자로 글을 읽지 않고 과제·쓰기 위치·시작·방향·결과·지우기·다시 듣기·사용할 수 없는 다음 동작을 식별했다. `아이 대리 점검: 통과`.
- `실제 아이 관찰: 실행 안 함`.
- 다음 작업: 오프라인 voice 선택이 엔진의 server fallback을 허용하지 않도록 설치 데이터 가용성을 검증하거나 네트워크를 끈 합성 probe 후에만 준비 상태로 전환하는 최소 변경을 만들고, callback 상태를 직접 검증 가능한 로그 또는 semantics로 노출한 뒤 같은 cold launch 흐름을 다시 확인한다.

### 반복 6 — 설치된 오프라인 음성 선택

- 가설: 한국어이고 `isNetworkConnectionRequired == false`인 조건에 더해 `TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED`가 없는 voice만 선택하면, 미설치 로컬 voice에서 발생한 첫 합성의 server/download fallback을 차단하고 설치된 embedded voice가 없을 때는 안전한 사용 불가 상태로 전환할 수 있다.
- 변경: `LocalKoreanSpeech`의 voice 선택을 순수 판정 함수로 분리하고, 한국어·네트워크 불필요 조건과 함께 `KEY_FEATURE_NOT_INSTALLED`가 없는 voice만 허용했다. 설치된 voice가 없으면 기존 `Unavailable` 안전 대체 경로를 유지한다. Android JVM stub에서 `Voice` 생성자가 실행되지 않는 제약 때문에 첫 테스트 fixture 실패 뒤 순수 판정 함수 테스트로 최소 보정했다.
- 첫 검증: 컴파일, lint, debug build는 통과했으나 새 테스트 2개가 JVM Android stub의 `Voice` 생성자 `RuntimeException`으로 실패했다. 제품 판정 실패가 아니라 테스트 환경 문제였으며 fixture만 보정했다.
- 최종 자동 검증: `./scripts/verify.sh`의 자동화 계약, 단위 테스트 24개, Android lint, debug build가 모두 통과했다. `git diff --check`도 통과했고 manifest와 Gradle 의존성은 바꾸지 않았다.
- 에뮬레이터 검증: `alarmquest-qa`의 물리 1080 × 2340, 앱 2340 × 1080, `ROTATION_90`에서 Wi-Fi와 mobile data를 끄고 새 APK를 설치했다. cold launch 2136 ms와 focus를 확인했다. 화면과 hierarchy는 `captures/loop005/loop005-iteration6-offline.png`, `captures/loop005/loop005-iteration6-offline.xml`에 저장했다.
- 오프라인 음성 근거: Google TTS가 `ko-kr-x-ism-local`을 유효한 voice로 받고 `ko-kr-x-ism-seanet-embedded`를 디스크에서 초기화해 합성 dispatch했으며 `AudioTrack stop`까지 기록됐다. 이전 반복의 한국어 `server` dispatch와 한국어 voice 다운로드 URL은 나타나지 않았다. 화면에는 활성 `🔊`와 `현재 안내 다시 듣기`가 유지됐다. 이는 기술 재생 근거이며 사람이 실제 발화를 듣고 이해했다는 근거는 아니다.
- 이전 반복 비교: 반복 5의 첫 초기 합성은 `ko-kr-x-kod-server`와 한국어 voice 다운로드 요청으로 오프라인 조건에 실패했지만, 이번 네트워크 차단 cold launch는 설치된 embedded 한국어 합성으로 완료됐다. 가설은 확인됐다.
- 남은 조건: 앱 자체 완료·오류 callback 상태를 직접 식별 가능한 로그 또는 semantics로 노출하고, 회전·Activity 해제와 전체 초기·재시도·성공·지우기 흐름을 네트워크 차단 상태에서 최종 검증해야 한다.
- `아이 대리 점검: 이전 통과 유지`. 이번 변경은 UI 배치를 바꾸지 않았고 활성 `🔊` 단서를 새 화면에서 확인했다.
- `실제 아이 관찰: 실행 안 함`.
- 다음 작업: callback 상태를 직접 검증 가능한 한글 로그 또는 semantics로 노출하는 최소 변경을 추가하고, 네트워크 차단 상태에서 결과 안내·다시 듣기·지우기와 회전·해제를 검증한다.

### 반복 7 — 가설

- 가설: 발화 내용은 기록하지 않고 `SpeechPlaybackState`를 안정적인 한글 진단 토큰으로 변환해 상태 변경마다 앱 로그에 남기면, 완료·오류·사용 불가·해제를 엔진 간접 로그가 아닌 앱 callback 계약으로 직접 구분할 수 있고 순수 매핑 단위 테스트로 회귀를 막을 수 있다.
- 변경: `SpeechPlaybackState.diagnosticToken()`을 추가해 초기화·준비·재생 중·완료·오류·사용 불가·해제를 안정적인 한글 토큰으로 매핑하고, `LocalKoreanSpeech`가 상태를 UI에 전달할 때 `LimDoSpeech` 태그로 같은 토큰을 기록하게 했다. 아이 발화문은 로그에 남기지 않는다. 모든 상태와 발화문 비노출을 확인하는 단위 테스트 1개를 추가했다.
- 첫 검증: 새 테스트의 `assertFalse` import가 빠져 단위 테스트 컴파일이 실패했다. Android lint와 debug build는 통과했다. 제품 코드 실패가 아니므로 누락 import 한 줄만 보정했다.
- 최종 자동 검증: `./scripts/verify.sh`의 자동화 계약, 단위 테스트 25개, Android lint, debug build가 모두 통과했다. `git diff --check`도 통과했고 manifest와 Gradle 의존성 diff는 없다.
- 에뮬레이터 근거: 새 APK를 `alarmquest-qa`에 설치하고 cold launch 1666 ms, focus, 물리 1080 × 2340과 앱 2340 × 1080 `ROTATION_90`을 확인했다. 앱 로그에서 `준비 완료` → `재생 중:INITIAL` → `재생 완료:INITIAL`을 직접 확인했다. font scale 구성 변경과 복원으로 Activity를 두 번 재생성했을 때 각각 `자원 해제` 뒤 새 `준비 완료`가 나타났고 초기 안내는 다시 재생되지 않았다.
- 이전 반복 비교: 반복 6은 Google TTS의 embedded dispatch와 `AudioTrack stop`만 있어 앱 callback 완료·해제를 간접 추론했지만, 이번에는 앱 자체 상태 계약으로 초기 재생 완료와 Activity 자원 해제를 직접 구분했다. 가설은 확인됐다.
- 남은 조건: 네트워크 차단 상태에서 재시도·성공·현재 안내 다시 듣기·지우기 뒤 다시 듣기의 앱 상태 로그를 한 흐름으로 최종 확인하고, 실제 callback 오류 또는 안전한 사용 불가 상태 근거를 확보해야 한다.
- `아이 대리 점검: 이전 통과 유지`. 이번 변경은 UI나 배치를 바꾸지 않았다.
- `실제 아이 관찰: 실행 안 함`.
- 다음 작업: 네트워크 차단 상태의 전체 입력 흐름을 앱 `LimDoSpeech` 로그와 화면·hierarchy로 검증하고, 오류 또는 사용 불가 안전 대체 상태를 재현 가능한 방법으로 확인한다.

## 루프 완료 일반 push 승인 — 2026-08-24

- 사용자 승인: 모든 성공 조건과 최종 검증을 통과한 루프 완료 체크포인트 커밋을 원격 저장소까지 일반 push한다.
- 고정 명령: 현재 브랜치를 대상으로 `git push origin HEAD`를 사용한다.
- 안전 경계: force push, tag, release, pull request와 배포는 이 승인에 포함되지 않는다.
- 실패 처리: 일반 push가 실패하면 원격 동기화를 완료했다고 주장하지 않고 정확한 오류를 기록하며 force push하지 않는다.

### 반복 8 — 가설

- 가설: 네트워크를 차단한 `alarmquest-qa`에서 재시도·성공·현재 안내 다시 듣기·지우기 후 다시 듣기 흐름을 수행하면, 화면·hierarchy와 앱 `LimDoSpeech` 로그에서 현재 시각 안내와 `재생 중`·`재생 완료` 토큰이 일치하고 지우기 자체는 재생 토큰을 만들지 않으며, 오류 callback 또는 사용 불가 상태에서도 앱이 멈추거나 종료되지 않는다.
- 변경: 제품 코드는 바꾸지 않고 반복 7의 APK로 새 검증 근거만 수집했다.
- 자동 검증: `./scripts/check-automation.sh`와 `./scripts/verify.sh`의 자동화 계약, 단위 테스트 25개, Android lint, debug build가 모두 통과했다. `git diff --check`도 통과했다.
- 에뮬레이터: `alarmquest-qa` 물리 1080 × 2340, 앱 bounds 2340 × 1080, `ROTATION_90`, focus와 cold launch 1335 ms를 확인했다. Wi-Fi와 mobile data를 끈 상태에서 초기·재시도·성공·현재 안내 다시 듣기·지우기·지우기 후 다시 듣기를 한 흐름으로 수행했다.
- callback 근거: 초기와 초기 다시 듣기에서 `INITIAL`, 잘못된 시작과 결과 다시 듣기에서 `RETRY_START`, 올바른 한 획 `ㄱ`과 결과 다시 듣기에서 `SUCCESS`가 각각 `재생 중` → `재생 완료`로 나타났다. 지우기 자체에서는 새 재생 토큰이 없었고, 지우기 후 다시 듣기에서만 새 `INITIAL` 요청이 나타났다.
- 안전 대체: 검증을 위해 Google TTS를 일시 비활성화하고 앱 데이터를 지운 다음 cold launch했다. 앱 로그에 `사용 불가`가 나타났고, 화면에서 큰 `🔇`와 비활성 다시 듣기 버튼을 확인했다. 엔진을 다시 활성화한 다음 `준비 완료` → `INITIAL` 재생 완료까지 확인하고 원상 복구했다.
- 정확한 실패: 첫 입력의 `motionevent UP`에 좌표를 빼먹어 명령이 중단됐다. 앱·제품 결함이 아니며, 같은 끝점 좌표를 넣어 검증을 완료했다.
- 이전 반복 비교: 반복 7까지는 초기 callback·해제 근거만 있었지만, 이번에는 전체 이력과 사용 불가 UI를 직접 재현해 가설을 모두 확인했다.
- 아이 대리 점검: 큰 Canvas, `ㄱ` guide, 주황 시작점, `→`·`↓`, `↻`, 별·`✓`, `⌫`, 큰 `🔊`, 회색 다음 자리표시자로 글을 읽지 않고 과제·쓰기 위치·시작·방향·결과·지우기·다시 듣기·사용 불가·다음 동작 비활성을 식별했다. `아이 대리 점검: 통과`.
- `실제 아이 관찰: 실행 안 함`.
- 결론: 새 근거로 13개 조건이 모두 통과했고 루프 005를 완료했다. 다음 `준비`: 큐에 정의된 `준비` 항목이 없으며 `HUMAN_REVIEW_AFTER_LOOP_005`에서 멈춘다.
