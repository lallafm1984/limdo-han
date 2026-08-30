#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "에뮬레이터 준비 실패: $1" >&2
    exit 1
}

repo_root="$(cd "$(dirname "$0")/.." && pwd -P)"
runtime_dir="$repo_root/.loop/runtime"
expected_serial="emulator-5554"
expected_avd="alarmquest-qa"
real_adb="${LIMDO_REAL_ADB:-/Users/lim/Library/Android/sdk/platform-tools/adb}"
emulator_bin="${LIMDO_EMULATOR_BIN:-/Users/lim/Library/Android/sdk/emulator/emulator}"
emulator_log="$runtime_dir/emulator.log"
emulator_pid_file="$runtime_dir/emulator.pid"
boot_wait_seconds="${LIMDO_EMULATOR_BOOT_WAIT_SECONDS:-240}"

export ANDROID_SERIAL="$expected_serial"
export ADB_MDNS_AUTO_CONNECT="0"

[[ -x "$real_adb" ]] || fail "실제 adb 실행 파일을 찾을 수 없음: $real_adb"
[[ -x "$emulator_bin" ]] || fail "에뮬레이터 실행 파일을 찾을 수 없음: $emulator_bin"
[[ "$boot_wait_seconds" =~ ^[0-9]+$ ]] || fail "부팅 대기 시간이 숫자가 아님: $boot_wait_seconds"
(( boot_wait_seconds >= 1 )) || fail "부팅 대기 시간은 1초 이상이어야 함"

mkdir -p "$runtime_dir"

device_rows() {
    "$real_adb" devices | sed '1d' | sed '/^[[:space:]]*$/d'
}

assert_no_physical_device() {
    local rows physical_rows
    rows="$(device_rows)"
    physical_rows="$(printf '%s\n' "$rows" | awk '$1 !~ /^emulator-/ {print}' || true)"
    [[ -z "$physical_rows" ]] || \
        fail "실기기나 실기기 후보가 ADB에 연결됨; 기기를 건드리지 않고 중지함: $physical_rows"
}

selected_state() {
    "$real_adb" -s "$expected_serial" get-state 2>/dev/null || true
}

wait_until_serial_gone() {
    local elapsed=0
    while [[ -n "$(device_rows | awk -v serial="$expected_serial" '$1 == serial {print $1}')" ]]; do
        (( elapsed < 30 )) || fail "${expected_serial}이 30초 안에 종료되지 않음"
        sleep 1
        elapsed=$((elapsed + 1))
    done
}

wait_until_booted() {
    local elapsed=0 state boot_completed
    while (( elapsed < boot_wait_seconds )); do
        assert_no_physical_device
        state="$(selected_state)"
        if [[ "$state" == "device" ]]; then
            boot_completed="$($real_adb -s "$expected_serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
            if [[ "$boot_completed" == "1" ]]; then
                return 0
            fi
        fi
        sleep 1
        elapsed=$((elapsed + 1))
    done
    fail "${expected_serial}이 ${boot_wait_seconds}초 안에 부팅되지 않음"
}

verify_identity() {
    local qemu_flag avd_name
    qemu_flag="$($real_adb -s "$expected_serial" shell getprop ro.kernel.qemu | tr -d '\r')"
    avd_name="$($real_adb -s "$expected_serial" shell getprop ro.boot.qemu.avd_name | tr -d '\r')"
    [[ "$qemu_flag" == "1" ]] || fail "${expected_serial}이 Android 에뮬레이터가 아님"
    [[ "$avd_name" == "$expected_avd" ]] || \
        fail "AVD가 ${expected_avd}가 아님: ${avd_name:-없음}"
}

verify_display() {
    local wm_size rotation
    wm_size="$($real_adb -s "$expected_serial" shell wm size | tr -d '\r')"
    rotation="$($real_adb -s "$expected_serial" shell settings get system user_rotation | tr -d '\r')"
    grep -Eq 'Physical size: 1080x2340' <<< "$wm_size" || \
        fail "물리 화면이 1080x2340이 아님: $wm_size"
    [[ "$rotation" == "1" ]] || fail "user_rotation이 1이 아님: ${rotation:-없음}"
}

stabilize_display() {
    local attempt rotation
    for attempt in {1..15}; do
        "$real_adb" -s "$expected_serial" shell settings put system accelerometer_rotation 0
        "$real_adb" -s "$expected_serial" shell settings put system user_rotation 1
        sleep 1
        rotation="$($real_adb -s "$expected_serial" shell settings get system user_rotation | tr -d '\r')"
        if [[ "$rotation" == "1" ]]; then
            verify_display
            return 0
        fi
    done
    fail "cold boot 뒤 user_rotation=1이 15회 안에 안정화되지 않음"
}

focus_package() {
    "$real_adb" -s "$expected_serial" shell dumpsys window windows 2>/dev/null \
        | sed -n -E 's/.*mCurrentFocus=.* ([A-Za-z0-9._]+)\/.*/\1/p' \
        | head -n 1 \
        | tr -d '\r'
}

may_restart_for_age() {
    local focus
    focus="$(focus_package)"
    case "$focus" in
        ""|com.limdo.hangul|com.android.launcher3|com.google.android.apps.nexuslauncher|com.android.systemui|com.android.settings)
            return 0
            ;;
        *)
            fail "다른 프로젝트 앱이 전면을 사용 중이라 오래된 에뮬레이터를 종료하지 않음: $focus"
            ;;
    esac
}

start_cold_boot() {
    : > "$emulator_log"
    nohup "$emulator_bin" \
        -avd "$expected_avd" \
        -no-snapshot-load \
        -no-snapshot-save \
        -no-boot-anim \
        -gpu host \
        </dev/null >> "$emulator_log" 2>&1 &
    printf '%s\n' "$!" > "$emulator_pid_file"
    echo "에뮬레이터 cold boot 시작: avd=$expected_avd pid=$!"
}

assert_no_physical_device

state="$(selected_state)"
if [[ "$state" == "device" ]]; then
    verify_identity
    uptime_seconds="$($real_adb -s "$expected_serial" shell cat /proc/uptime | awk '{print int($1)}')"
    [[ "$uptime_seconds" =~ ^[0-9]+$ ]] || fail "에뮬레이터 uptime을 읽을 수 없음"
    if (( uptime_seconds >= 7200 )); then
        may_restart_for_age
        echo "에뮬레이터 2시간 관문: uptime=${uptime_seconds}초, cold boot 수행"
        "$real_adb" -s "$expected_serial" emu kill >/dev/null
        wait_until_serial_gone
        start_cold_boot
    fi
else
    start_cold_boot
fi

wait_until_booted
verify_identity

stabilize_display

uptime_seconds="$($real_adb -s "$expected_serial" shell cat /proc/uptime | awk '{print int($1)}')"
echo "에뮬레이터 준비 통과: serial=$expected_serial avd=$expected_avd physical=0 uptime=${uptime_seconds}초 display=1080x2340 rotation=1"
