#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "시각 루프 계약 실패: $1" >&2
    exit 1
}

state_file=".loop/state.md"
evidence_file=".loop/visual-evidence.md"

[[ -s "$state_file" ]] || fail "$state_file 파일이 없음"

loop="$(sed -n 's/^루프: \([0-9][0-9][0-9]\).*/\1/p' "$state_file")"
status="$(sed -n 's/^상태: //p' "$state_file")"
phase="$(sed -n 's/^단계: //p' "$state_file")"
visual_change="$(sed -n 's/^시각 변경: //p' "$state_file")"
asset_decision="$(sed -n 's/^자산 필요 판정: //p' "$state_file")"

[[ -n "$loop" ]] || fail "루프 번호를 읽을 수 없음"
case "$phase" in
    그래픽·시스템\ 구현|그래픽·시스템\ 완료\ 감사|최종\ 전체\ 플레이) ;;
    *) fail "단계가 정의되지 않음: $phase" ;;
esac

case "$visual_change" in
    예|아니오) ;;
    *) fail "시각 변경은 예 또는 아니오여야 함" ;;
esac

if [[ "$visual_change" == "아니오" ]]; then
    [[ "$asset_decision" == 불필요\ —\ * ]] || fail "비시각 루프도 자산 불필요의 구체 근거가 필요함"
    echo "시각 루프 계약 통과: 비시각 변경"
    exit 0
fi

case "$asset_decision" in
    필요\ —\ *|불필요\ —\ *) ;;
    *) fail "시각 루프의 자산 필요·불필요 판정과 구체 근거가 없음" ;;
esac

if [[ "$status" != "완료" ]]; then
    echo "시각 루프 계약 통과: 완료 증거 수집 전"
    exit 0
fi

[[ -s "$evidence_file" ]] || fail "완료 시각 루프의 $evidence_file 파일이 없음"
grep -q "^루프: $loop$" "$evidence_file" || fail "시각 증거 루프가 현재 루프와 다름"
grep -q '^기준 화면: 2340 × 1080$' "$evidence_file" || fail "2340 × 1080 기준이 없음"
grep -q '^자동 그래픽 디자인 역할: 통과$' "$evidence_file" || fail "자동 그래픽 디자인 역할이 통과하지 않음"
grep -q '^자동 QA 역할: 통과$' "$evidence_file" || fail "자동 QA 역할이 통과하지 않음"
grep -q '^아이 대리 QA: 통과$' "$evidence_file" || fail "아이 대리 QA가 통과하지 않음"
grep -q '^새 P0: 0$' "$evidence_file" || fail "새 P0가 0이 아님"
grep -q '^새 P1: 0$' "$evidence_file" || fail "새 P1이 0이 아님"
grep -q '^진행 방해 P2: 0$' "$evidence_file" || fail "진행 방해 P2가 0이 아님"
grep -Eq '^APK SHA-256: [0-9a-f]{64}$' "$evidence_file" || fail "APK SHA-256 근거가 없음"
grep -Eq '^package: [a-zA-Z0-9_.]+$' "$evidence_file" || fail "package 근거가 없음"
grep -q '^focus: 통과$' "$evidence_file" || fail "focus 근거가 통과하지 않음"

check_png_field() {
    local label="$1"
    local path
    path="$(sed -n "s/^${label}: //p" "$evidence_file")"
    [[ -n "$path" && -s "$path" ]] || fail "$label 파일이 없거나 비어 있음: $path"
    file "$path" | grep -q 'PNG image data, 2340 x 1080' || fail "$label 파일이 2340 × 1080 PNG가 아님"
}

check_file_field() {
    local label="$1"
    local path
    path="$(sed -n "s/^${label}: //p" "$evidence_file")"
    [[ -n "$path" && -s "$path" ]] || fail "$label 파일이 없거나 비어 있음: $path"
}

check_png_field "변경 전 PNG"
check_file_field "변경 전 hierarchy"
check_png_field "변경 후 PNG"
check_file_field "변경 후 hierarchy"

if [[ "$asset_decision" == 필요\ —\ * ]]; then
    asset_scope="$(sed -n 's/^자산 적용 범위: //p' "$evidence_file")"
    case "$asset_scope" in
        production)
            asset_path="$(sed -n 's/^production 자산 경로: //p' "$evidence_file")"
            [[ "$asset_path" == app/src/main/res/* && -s "$asset_path" ]] || fail "필요 자산이 production res 경로에 없음"
            grep -q '^production 소비 검사: 통과$' "$evidence_file" || fail "production 소비 검사가 통과하지 않음"
            grep -q '^자산 자동 검사: 통과$' "$evidence_file" || fail "자산 자동 검사가 통과하지 않음"
            ;;
        preview-only)
            grep -Eq '사용자가.*선택.*전.*production.*(적용|구현).*않' LOOP_GOAL.md || fail "preview-only는 사용자 선택 전 production 적용 금지 목표에서만 허용됨"
            grep -q '^preview production 미소비 검사: 통과$' "$evidence_file" || fail "preview production 미소비 검사가 통과하지 않음"
            grep -q '^preview 사용자 선택 관문: 통과$' "$evidence_file" || fail "preview 사용자 선택 관문이 통과하지 않음"
            preview_count="$(grep -c '^preview 자산 경로: ' "$evidence_file")"
            (( preview_count > 0 )) || fail "preview 자산 경로가 없음"
            while IFS= read -r preview_path; do
                [[ "$preview_path" != app/src/main/res/* ]] || fail "preview 자산이 production res에 포함됨: $preview_path"
                [[ -s "$preview_path" ]] || fail "preview 자산 파일이 없거나 비어 있음: $preview_path"
                file "$preview_path" | grep -q 'PNG image data, 2340 x 1080' || fail "preview 자산이 2340 × 1080 PNG가 아님: $preview_path"
            done < <(sed -n 's/^preview 자산 경로: //p' "$evidence_file")
            ;;
        *) fail "필요 자산은 production 또는 preview-only 적용 범위를 명시해야 함" ;;
    esac
fi

echo "시각 루프 계약 통과: 완료 증거 확인"
