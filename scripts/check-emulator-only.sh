#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "에뮬레이터 전용 관문 실패: $1" >&2
    exit 1
}

expected_serial="emulator-5554"
expected_avd="alarmquest-qa"
real_adb="${LIMDO_REAL_ADB:-/Users/lim/Library/Android/sdk/platform-tools/adb}"

[[ "${ANDROID_SERIAL:-}" == "$expected_serial" ]] || \
    fail "ANDROID_SERIAL이 ${expected_serial}로 고정되지 않음"
[[ -x "$real_adb" ]] || fail "실제 adb 실행 파일을 찾을 수 없음: $real_adb"

device_rows="$($real_adb devices | sed '1d' | sed '/^[[:space:]]*$/d')"
physical_rows="$(printf '%s\n' "$device_rows" | awk '$1 !~ /^emulator-/ {print}' || true)"
[[ -z "$physical_rows" ]] || \
    fail "실기기나 실기기 후보가 ADB에 연결됨; 기기를 건드리지 않고 자동화를 중지함: $physical_rows"

selected_state="$($real_adb -s "$expected_serial" get-state 2>/dev/null || true)"
[[ "$selected_state" == "device" ]] || \
    fail "${expected_serial}이 device 상태가 아님: ${selected_state:-없음}"

qemu_flag="$($real_adb -s "$expected_serial" shell getprop ro.kernel.qemu | tr -d '\r')"
avd_name="$($real_adb -s "$expected_serial" shell getprop ro.boot.qemu.avd_name | tr -d '\r')"
[[ "$qemu_flag" == "1" ]] || fail "${expected_serial}이 Android 에뮬레이터가 아님"
[[ "$avd_name" == "$expected_avd" ]] || \
    fail "AVD가 ${expected_avd}가 아님: ${avd_name:-없음}"

echo "에뮬레이터 전용 관문 통과: serial=$expected_serial avd=$expected_avd physical=0"
