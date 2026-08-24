$ErrorActionPreference = "Stop"

function Stop-AutomationCheck {
    param([string]$Message)
    Write-Error "AUTOMATION CONTRACT FAILED: $Message"
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
        Stop-AutomationCheck "missing or empty $RequiredFile"
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

if ($Automation -notmatch '(?m)^Stage: `CLI`$') { Stop-AutomationCheck "Codex App handoff does not select CLI" }
if ($CliAutomation -notmatch '(?m)^Stage: `CLI`$') { Stop-AutomationCheck "CLI stage is not selected" }
if ($Queue -notmatch '(?m)^Execution Stage: CLI$') { Stop-AutomationCheck "queue stage does not match" }
if ($CliAutomation -notmatch 'exactly one loop iteration') { Stop-AutomationCheck "single-iteration session contract missing" }
if ($CliAutomation -notmatch 'codex exec --ephemeral --json --sandbox danger-full-access') { Stop-AutomationCheck "fresh-session command contract missing" }
if ($WorkerPrompt -notmatch 'Perform exactly ONE loop iteration') { Stop-AutomationCheck "worker iteration boundary missing" }
if ($WorkerPrompt -notmatch 'Never invoke `codex exec`') { Stop-AutomationCheck "nested CLI prohibition missing" }
if ($Runner -notmatch '--ephemeral') { Stop-AutomationCheck "ephemeral CLI flag missing" }
if ($Runner -notmatch 'CODEX_LOOP_SANDBOX:-danger-full-access') { Stop-AutomationCheck "Android tooling sandbox mode missing" }
if ($Runner -notmatch 'env -u CODEX_SESSION_ID') { Stop-AutomationCheck "fresh-session environment isolation missing" }
if ($Starter -notmatch 'screen -dmS') { Stop-AutomationCheck "detached CLI session host missing" }
if ($Agents -notmatch '2340 × 1080') { Stop-AutomationCheck "reference viewport missing from AGENTS.md" }
if ($Goal -notmatch '2340 × 1080') { Stop-AutomationCheck "reference viewport missing from LOOP_GOAL.md" }
if ($QaChecklist -notmatch '1170 px') { Stop-AutomationCheck "minimum writing width missing from QA_CHECKLIST.md" }
if ($QaChecklist -notmatch '378 px') { Stop-AutomationCheck "minimum writing height missing from QA_CHECKLIST.md" }
if ($QaChecklist -notmatch 'OBSERVED_CHILD') { Stop-AutomationCheck "observed-child evidence boundary missing" }

$GoalLoop = [regex]::Match($Goal, '(?m)^# Loop Goal ([0-9]{3})').Groups[1].Value
$StateLoop = [regex]::Match($State, '(?m)^Loop: ([0-9]{3})').Groups[1].Value
$ActiveLoop = [regex]::Match($Queue, '(?m)^Active Loop: (.+)$').Groups[1].Value.Trim()
$Status = [regex]::Match($State, '(?m)^Status: (.+)$').Groups[1].Value.Trim()
$IterationText = [regex]::Match($State, '(?m)^Iteration: ([0-9]+)$').Groups[1].Value

if (-not $GoalLoop) { Stop-AutomationCheck "cannot read goal loop id" }
if ($StateLoop -ne $GoalLoop) { Stop-AutomationCheck "state loop $StateLoop does not match goal loop $GoalLoop" }
if ($Status -notin @("READY", "IN_PROGRESS", "COMPLETE", "BLOCKED")) { Stop-AutomationCheck "invalid loop status: $Status" }
if (-not $IterationText) { Stop-AutomationCheck "iteration is not numeric" }
if ([int]$IterationText -gt 15) { Stop-AutomationCheck "iteration exceeds the 15-iteration stop condition" }

if ($ActiveLoop -eq "NONE") {
    if ($Status -notin @("COMPLETE", "BLOCKED")) { Stop-AutomationCheck "no active loop but state is $Status" }
}
elseif ($ActiveLoop -ne $GoalLoop) {
    Stop-AutomationCheck "active loop $ActiveLoop does not match goal loop $GoalLoop"
}

Write-Host "AUTOMATION CONTRACT PASSED"
Write-Host "Stage: CLI"
Write-Host "Loop: $GoalLoop"
Write-Host "Status: $Status"
Write-Host "Iteration: $IterationText"
Write-Host "Active Loop: $ActiveLoop"
exit 0
