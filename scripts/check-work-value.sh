#!/usr/bin/env bash

set -euo pipefail

goal_file="${1:-LOOP_GOAL.md}"

fail() {
    echo "작업 가치 관문 실패: $1" >&2
    exit 1
}

[[ -s "$goal_file" ]] || fail "$goal_file 파일이 없거나 비어 있음"
goal_content="$(< "$goal_file")"
grep -q '^## 작업 가치 관문$' <<< "$goal_content" || fail "LOOP_GOAL.md에 작업 가치 관문이 없음"

read_field() {
    local field="$1"
    sed -n "/^## 작업 가치 관문$/,/^## / s/^${field}: //p" <<< "$goal_content" | head -n 1
}

classification="$(read_field "분류")"
user_value="$(read_field "사용자 가치")"
new_evidence="$(read_field "새로운 근거")"
deduplication="$(read_field "중복 방지")"

case "$classification" in
    제품\ 변경|결함\ 수정|새\ 사용자\ 위험|사용자\ 지시) ;;
    *) fail "분류는 제품 변경, 결함 수정, 새 사용자 위험, 사용자 지시 중 하나여야 함" ;;
esac

[[ -n "$user_value" && "$user_value" != "없음" ]] || fail "구체적인 사용자 가치가 없음"
[[ -n "$new_evidence" && "$new_evidence" != "없음" ]] || fail "이전 루프와 다른 새로운 근거가 없음"
[[ -n "$deduplication" && "$deduplication" != "없음" ]] || fail "중복 실행을 막는 종료 경계가 없음"

if grep -Eq '반복(만| 횟수만)[^[:cntrl:]]*(연장|늘리|증가)' <<< "$goal_content"; then
    fail "같은 검증의 반복 횟수만 늘리는 목표는 실행할 수 없음"
fi

if grep -Eq '(횟수|상한|회차)(만|만을|만 한 단계)[^[:cntrl:]]*(연장|늘리|증가)' <<< "$goal_content"; then
    fail "숫자만 바꾼 재검증 목표는 실행할 수 없음"
fi

echo "작업 가치 관문 통과"
echo "분류: $classification"
echo "사용자 가치: $user_value"
