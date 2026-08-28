#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "최종 전체 플레이 계약 실패: $1" >&2
    exit 1
}

queue_file=".loop/queue.md"
matrix_file="docs/전체-플레이-QA-행렬.md"

[[ -s "$queue_file" ]] || fail "$queue_file 파일이 없음"
[[ -s "$matrix_file" ]] || fail "$matrix_file 파일이 없음"

active_loop="$(sed -n 's/^활성 루프: //p' "$queue_file")"
gate="$(sed -n 's/^최종 전체 플레이 관문: //p' "$queue_file")"

case "$gate" in
    대기|진행\ 중|통과) ;;
    *) fail "최종 전체 플레이 관문 값이 올바르지 않음: $gate" ;;
esac

if [[ "$active_loop" == "없음" && "$gate" != "통과" ]]; then
    fail "최종 전체 플레이 통과 전에는 활성 큐를 비울 수 없음"
fi

if [[ "$gate" != "통과" ]]; then
    echo "최종 전체 플레이 계약 통과: 최종 수집 전 활성 작업 유지"
    exit 0
fi

grep -q '^최종 판정: 통과$' "$matrix_file" || fail "최종 판정이 통과가 아님"
grep -Eq '^- Git HEAD: [0-9a-f]{40}$' "$matrix_file" || fail "최종 Git HEAD가 없음"
grep -Eq '^- APK SHA-256: [0-9a-f]{64}$' "$matrix_file" || fail "최종 APK SHA-256이 없음"
grep -Eq '^- package: `?[a-zA-Z0-9_.]+`?$' "$matrix_file" || fail "최종 package가 없음"
grep -q '^- 실제 휴대폰 설치: 통과$' "$matrix_file" || fail "실제 휴대폰 설치가 통과하지 않음"
grep -q '^- 실제 휴대폰 focus: 통과$' "$matrix_file" || fail "실제 휴대폰 focus가 통과하지 않음"
grep -q '^- 전체 화면 누락: 0$' "$matrix_file" || fail "전체 화면 누락이 0이 아님"
grep -q '^- 미해결 P0: 0$' "$matrix_file" || fail "미해결 P0가 0이 아님"
grep -q '^- 미해결 P1: 0$' "$matrix_file" || fail "미해결 P1이 0이 아님"
grep -q '^- 진행 방해 P2: 0$' "$matrix_file" || fail "진행 방해 P2가 0이 아님"
grep -q '^- 자동 그래픽 디자인 역할: 통과$' "$matrix_file" || fail "자동 그래픽 디자인 역할이 통과하지 않음"
grep -q '^- 자동 QA 역할: 통과$' "$matrix_file" || fail "자동 QA 역할이 통과하지 않음"
grep -q '^- 아이 대리 QA: 통과$' "$matrix_file" || fail "아이 대리 QA가 통과하지 않음"

if grep -q '| 대기 |' "$matrix_file"; then
    fail "최종 수집 행렬에 대기 행이 남음"
fi

png_count="$(grep -oE 'captures/[^`| ]+\.png' "$matrix_file" | sort -u | wc -l | tr -d ' ')"
xml_count="$(grep -oE 'captures/[^`| ]+\.xml' "$matrix_file" | sort -u | wc -l | tr -d ' ')"
(( png_count > 0 )) || fail "최종 행렬에 PNG가 없음"
(( png_count == xml_count )) || fail "PNG와 hierarchy 개수가 다름"

while IFS= read -r png_path; do
    [[ -s "$png_path" ]] || fail "최종 PNG가 없거나 비어 있음: $png_path"
    file "$png_path" | grep -q 'PNG image data, 2340 x 1080' || fail "최종 PNG가 2340 × 1080이 아님: $png_path"
done < <(grep -oE 'captures/[^`| ]+\.png' "$matrix_file" | sort -u)
while IFS= read -r xml_path; do
    [[ -s "$xml_path" ]] || fail "최종 hierarchy가 없거나 비어 있음: $xml_path"
done < <(grep -oE 'captures/[^`| ]+\.xml' "$matrix_file" | sort -u)

echo "최종 전체 플레이 계약 통과"
