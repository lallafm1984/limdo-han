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

run_step "[1/3] Unit tests" ./gradlew --console=plain testDebugUnitTest
run_step "[2/3] Android lint" ./gradlew --console=plain lintDebug
run_step "[3/3] Debug build" ./gradlew --console=plain assembleDebug

echo
if [[ "$FAILED" -eq 0 ]]; then
    echo "ALL VERIFICATIONS PASSED"
    exit 0
fi

echo "VERIFICATION FAILED"
exit 1

