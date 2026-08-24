$ErrorActionPreference = "Stop"

function Stop-AutomationCheck {
    param([string]$Message)
    Write-Error "자동화 계약 실패: $Message"
    exit 1
}

$RequiredFiles = @(
    "AGENTS.md",
    "APP_AUTOMATION.md",
    "CLI_AUTOMATION.md",
    "QA_CHECKLIST.md",
    "LOOP_GOAL.md",
    ".loop/queue.md",
    ".loop/state.md",
    ".loop/history.md",
    ".loop/cli-worker-prompt.md",
    "scripts/run-cli-loop.sh",
    "scripts/start-cli-loop.sh",
    "scripts/cli-loop-status.sh",
    "scripts/stop-cli-loop.sh"
)

foreach ($RequiredFile in $RequiredFiles) {
    if (-not (Test-Path $RequiredFile) -or (Get-Item $RequiredFile).Length -eq 0) {
        Stop-AutomationCheck "$RequiredFile 파일이 없거나 비어 있음"
    }
}

$Automation = Get-Content APP_AUTOMATION.md -Raw
$CliAutomation = Get-Content CLI_AUTOMATION.md -Raw
$Agents = Get-Content AGENTS.md -Raw
$Goal = Get-Content LOOP_GOAL.md -Raw
$QaChecklist = Get-Content QA_CHECKLIST.md -Raw
$Queue = Get-Content .loop/queue.md -Raw
$State = Get-Content .loop/state.md -Raw
$WorkerPrompt = Get-Content .loop/cli-worker-prompt.md -Raw
$Runner = Get-Content scripts/run-cli-loop.sh -Raw
$Starter = Get-Content scripts/start-cli-loop.sh -Raw

if ($Automation -notmatch '(?m)^단계: `CLI`$') { Stop-AutomationCheck "Codex 앱 인계 문서가 CLI 단계를 선택하지 않음" }
if ($CliAutomation -notmatch '(?m)^단계: `CLI`$') { Stop-AutomationCheck "CLI 단계가 선택되지 않음" }
if ($Queue -notmatch '(?m)^실행 단계: CLI$') { Stop-AutomationCheck "큐 실행 단계가 일치하지 않음" }
if ($CliAutomation -notmatch '정확히 한 번 반복') { Stop-AutomationCheck "한 세션 한 반복 계약이 없음" }
if ($CliAutomation -notmatch 'codex exec --ephemeral --json --sandbox danger-full-access') { Stop-AutomationCheck "새 세션 명령 계약이 없음" }
if ($WorkerPrompt -notmatch '정확히 한 번의 루프 반복') { Stop-AutomationCheck "작업자 반복 경계가 없음" }
if ($WorkerPrompt -notmatch '중첩 agent 프로세스') { Stop-AutomationCheck "중첩 CLI 금지 규칙이 없음" }
if ($Agents -notmatch 'git push origin HEAD') { Stop-AutomationCheck "AGENTS.md에 완료 일반 push 계약이 없음" }
if ($CliAutomation -notmatch 'git push origin HEAD') { Stop-AutomationCheck "CLI 자동화 문서에 완료 일반 push 계약이 없음" }
if ($Goal -notmatch 'git push origin HEAD') { Stop-AutomationCheck "루프 목표에 완료 일반 push 계약이 없음" }
if ($WorkerPrompt -notmatch 'git push origin HEAD') { Stop-AutomationCheck "작업자 지시문에 완료 일반 push 계약이 없음" }
if ($WorkerPrompt -notmatch 'force push하지 않는다') { Stop-AutomationCheck "일반 push 실패 시 강제 push 금지 규칙이 없음" }
if ($Runner -notmatch '--ephemeral') { Stop-AutomationCheck "임시 CLI 플래그가 없음" }
if ($Runner -notmatch 'CODEX_LOOP_SANDBOX:-danger-full-access') { Stop-AutomationCheck "Android 도구 sandbox 모드가 없음" }
if ($Runner -notmatch 'env -u CODEX_SESSION_ID') { Stop-AutomationCheck "새 세션 환경 분리가 없음" }
if ($Starter -notmatch 'screen -dmS') { Stop-AutomationCheck "분리 CLI 세션 호스트가 없음" }
if ($Agents -notmatch 'Markdown 문서는 한글로 작성') { Stop-AutomationCheck "한글 문서 기록 계약이 없음" }
if ($Agents -notmatch '2340 × 1080') { Stop-AutomationCheck "AGENTS.md에 기준 화면이 없음" }
if ($Goal -notmatch '2340 × 1080') { Stop-AutomationCheck "LOOP_GOAL.md에 기준 화면이 없음" }
if ($QaChecklist -notmatch '1170 px') { Stop-AutomationCheck "QA에 최소 쓰기 너비가 없음" }
if ($QaChecklist -notmatch '378 px') { Stop-AutomationCheck "QA에 최소 쓰기 높이가 없음" }
if ($QaChecklist -notmatch '실제 아이 관찰') { Stop-AutomationCheck "실제 아이 관찰 근거 경계가 없음" }

$GoalLoop = [regex]::Match($Goal, '(?m)^# 루프 목표 ([0-9]{3})').Groups[1].Value
$StateLoop = [regex]::Match($State, '(?m)^루프: ([0-9]{3})').Groups[1].Value
$ActiveLoop = [regex]::Match($Queue, '(?m)^활성 루프: (.+)$').Groups[1].Value.Trim()
$Status = [regex]::Match($State, '(?m)^상태: (.+)$').Groups[1].Value.Trim()
$IterationText = [regex]::Match($State, '(?m)^반복: ([0-9]+)$').Groups[1].Value

if (-not $GoalLoop) { Stop-AutomationCheck "목표 루프 번호를 읽을 수 없음" }
if ($StateLoop -ne $GoalLoop) { Stop-AutomationCheck "상태 루프 $StateLoop와 목표 루프 $GoalLoop가 일치하지 않음" }
if ($Status -notin @("준비", "진행 중", "완료", "차단")) { Stop-AutomationCheck "잘못된 루프 상태: $Status" }
if (-not $IterationText) { Stop-AutomationCheck "반복 번호가 숫자가 아님" }
if ([int]$IterationText -gt 15) { Stop-AutomationCheck "15회 반복 중지 조건을 넘음" }

if ($ActiveLoop -eq "없음") {
    if ($Status -notin @("완료", "차단")) { Stop-AutomationCheck "활성 루프가 없지만 상태가 $Status임" }
}
elseif ($ActiveLoop -ne $GoalLoop) {
    Stop-AutomationCheck "활성 루프 $ActiveLoop와 목표 루프 $GoalLoop가 일치하지 않음"
}

Write-Host "자동화 계약 통과"
Write-Host "단계: CLI"
Write-Host "루프: $GoalLoop"
Write-Host "상태: $Status"
Write-Host "반복: $IterationText"
Write-Host "활성 루프: $ActiveLoop"
exit 0
