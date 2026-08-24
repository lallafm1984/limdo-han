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

Invoke-VerificationStep "[1/3] Unit tests" "testDebugUnitTest"
Invoke-VerificationStep "[2/3] Android lint" "lintDebug"
Invoke-VerificationStep "[3/3] Debug build" "assembleDebug"

Write-Host ""
if ($Failed) {
    Write-Host "VERIFICATION FAILED"
    exit 1
}

Write-Host "ALL VERIFICATIONS PASSED"
exit 0
