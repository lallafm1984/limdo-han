$Failed = $false

function Invoke-VerificationStep {
    param(
        [string]$Label,
        [string]$Task
    )

    Write-Host ""
    Write-Host $Label
    & .\gradlew.bat --console=plain $Task
    if ($LASTEXITCODE -ne 0) {
        Write-Host "${Label}: FAILED"
        $script:Failed = $true
    }
    else {
        Write-Host "${Label}: PASSED"
    }
}

Write-Host "LimDo Loop Verification"

Write-Host ""
Write-Host "[1/4] Automation contract"
& .\scripts\check-automation.ps1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[1/4] Automation contract: FAILED"
    $Failed = $true
}
else {
    Write-Host "[1/4] Automation contract: PASSED"
}

Invoke-VerificationStep "[2/4] Unit tests" "testDebugUnitTest"
Invoke-VerificationStep "[3/4] Android lint" "lintDebug"
Invoke-VerificationStep "[4/4] Debug build" "assembleDebug"

Write-Host ""
if ($Failed) {
    Write-Host "VERIFICATION FAILED"
    exit 1
}

Write-Host "ALL VERIFICATIONS PASSED"
exit 0
