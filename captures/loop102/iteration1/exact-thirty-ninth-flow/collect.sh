#!/bin/zsh
set -eu

# 루프 098의 직접 수집 절차를 한 번만 확장해 39/38 관문과 전면 포커스를 검증한다.
sed \
  -e 's#loop098/iteration1/exact-thirty-fifth-flow#loop102/iteration1/exact-thirty-ninth-flow#g' \
  -e '/^ADB=(adb/a\
adb() { command adb "$@" </dev/null; }' \
  -e '/^normal_trace() {/i\
assert_focus() {\
  local label=$1\
  local focus=$("${ADB[@]}" shell dumpsys window | grep "mCurrentFocus=" | head -n 1 | tr -d "\\r")\
  print "$focus" >> "$OUT/focus-checks.log"\
  [[ "$focus" == *"com.example.limdo/com.example.limdo.MainActivity"* ]] || { print "$label: $focus" > "$OUT/focus-failure.txt"; exit 43; }\
}' \
  -e '/^normal_trace$/c\
assert_focus before-normal-trace\
normal_trace\
assert_focus after-normal-trace' \
  -e '/^sleep 4$/a\
assert_focus after-clean-start' \
  -e '/^wait "\$starter"$/a\
assert_focus after-restore-start' \
  -e '/^  assert_no_recreate "\$new_pid"$/a\
  assert_focus "before-replay-$replay"' \
  -e '/^  (( reached == 1 )); assert_no_recreate "\$new_pid"$/a\
  assert_focus "after-replay-$replay"' \
  -e 's/for replay in {2\.\.35}/for replay in {2..39}/' \
  -e 's/replay == 34/replay == 37/g' \
  -e 's/replay < 35/replay < 39/' \
  -e 's/34-before-clear/38-before-clear/g' \
  -e 's/34a-clear-after-thirty-fourth/38a-clear-after-thirty-eighth/g' \
  -e 's/starts == 35 && dones == 34/starts == 39 \&\& dones == 38/' \
  -e 's/replay-35/replay-39/g' \
  -e 's/success-after-thirty-fifth/success-after-thirty-ninth/g' \
  captures/loop098/iteration1/exact-thirty-fifth-flow/collect.sh | zsh -s
