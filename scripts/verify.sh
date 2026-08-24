#!/usr/bin/env bash

set -u
set -o pipefail

FAILED=0

run_step() {
    local label="$1"
    shift

    echo
    echo "$label"
    if "$@"; then
        echo "$label: PASSED"
    else
        echo "$label: FAILED"
        FAILED=1
    fi
}

echo "LimDo Loop Verification"

run_step "[1/4] Automation contract" ./scripts/check-automation.sh
run_step "[2/4] Unit tests" ./gradlew --console=plain testDebugUnitTest
run_step "[3/4] Android lint" ./gradlew --console=plain lintDebug
run_step "[4/4] Debug build" ./gradlew --console=plain assembleDebug

echo
if [[ "$FAILED" -eq 0 ]]; then
    echo "ALL VERIFICATIONS PASSED"
    exit 0
fi

echo "VERIFICATION FAILED"
exit 1
