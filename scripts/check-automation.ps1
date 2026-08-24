$ErrorActionPreference = "Stop"

function Stop-AutomationCheck {
    param([string]$Message)
    Write-Error "AUTOMATION CONTRACT FAILED: $Message"
    exit 1
}

$RequiredFiles = @(
    "AGENTS.md",
    "APP_AUTOMATION.md",
    "QA_CHECKLIST.md",
    "LOOP_GOAL.md",
    ".loop/queue.md",
    ".loop/state.md",
    ".loop/history.md"
)

foreach ($RequiredFile in $RequiredFiles) {
    if (-not (Test-Path $RequiredFile) -or (Get-Item $RequiredFile).Length -eq 0) {
        Stop-AutomationCheck "missing or empty $RequiredFile"
    }
}

$Automation = Get-Content APP_AUTOMATION.md -Raw
$Agents = Get-Content AGENTS.md -Raw
$Goal = Get-Content LOOP_GOAL.md -Raw
$QaChecklist = Get-Content QA_CHECKLIST.md -Raw
$Queue = Get-Content .loop/queue.md -Raw
$State = Get-Content .loop/state.md -Raw

if ($Automation -notmatch '(?m)^Stage: `CODEX_APP`$') { Stop-AutomationCheck "Codex App stage is not selected" }
if ($Queue -notmatch '(?m)^Execution Stage: CODEX_APP$') { Stop-AutomationCheck "queue stage does not match" }
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
Write-Host "Stage: CODEX_APP"
Write-Host "Loop: $GoalLoop"
Write-Host "Status: $Status"
Write-Host "Iteration: $IterationText"
Write-Host "Active Loop: $ActiveLoop"
exit 0
