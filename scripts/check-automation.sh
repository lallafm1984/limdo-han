#!/usr/bin/env bash

set -euo pipefail

fail() {
    echo "자동화 계약 실패: $1" >&2
    exit 1
}

required_files=(
    "AGENTS.md"
    "APP_AUTOMATION.md"
    "CLI_AUTOMATION.md"
    "QA_CHECKLIST.md"
    "LOOP_GOAL.md"
    ".loop/queue.md"
    ".loop/state.md"
    ".loop/history.md"
    ".loop/qa-findings.md"
    ".loop/user-directives.md"
    ".loop/cli-worker-prompt.md"
    "scripts/run-cli-loop.sh"
    "scripts/start-cli-loop.sh"
    "scripts/cli-loop-status.sh"
    "scripts/stop-cli-loop.sh"
)

for required_file in "${required_files[@]}"; do
    [[ -s "$required_file" ]] || fail "$required_file 파일이 없거나 비어 있음"
done

grep -q '^단계: `CLI`$' APP_AUTOMATION.md || fail "Codex 앱 인계 문서가 CLI 단계를 선택하지 않음"
grep -q '^단계: `CLI`$' CLI_AUTOMATION.md || fail "CLI 단계가 선택되지 않음"
grep -q '^실행 단계: CLI$' .loop/queue.md || fail "큐 실행 단계가 일치하지 않음"
grep -q '정확히 한 번 반복' CLI_AUTOMATION.md || fail "한 세션 한 반복 계약이 없음"
grep -q 'codex exec --ephemeral --json --sandbox danger-full-access' CLI_AUTOMATION.md || fail "새 세션 명령 계약이 없음"
grep -q '정확히 한 번의 루프 반복' .loop/cli-worker-prompt.md || fail "작업자 반복 경계가 없음"
grep -q '중첩 agent 프로세스' .loop/cli-worker-prompt.md || fail "중첩 CLI 금지 규칙이 없음"
grep -q 'git push origin HEAD' AGENTS.md || fail "AGENTS.md에 완료 일반 push 계약이 없음"
grep -q 'git push origin HEAD' CLI_AUTOMATION.md || fail "CLI 자동화 문서에 완료 일반 push 계약이 없음"
grep -q 'git push origin HEAD' LOOP_GOAL.md || fail "루프 목표에 완료 일반 push 계약이 없음"
grep -q 'git push origin HEAD' .loop/cli-worker-prompt.md || fail "작업자 지시문에 완료 일반 push 계약이 없음"
grep -q 'force push하지 않는다' .loop/cli-worker-prompt.md || fail "일반 push 실패 시 강제 push 금지 규칙이 없음"
grep -q -- '--ephemeral' scripts/run-cli-loop.sh || fail "임시 CLI 플래그가 없음"
grep -q 'CODEX_LOOP_SANDBOX:-danger-full-access' scripts/run-cli-loop.sh || fail "Android 도구 sandbox 모드가 없음"
grep -q 'env -u CODEX_SESSION_ID' scripts/run-cli-loop.sh || fail "새 세션 환경 분리가 없음"
grep -q 'screen -dmS' scripts/start-cli-loop.sh || fail "분리 CLI 세션 호스트가 없음"
grep -q 'Markdown 문서는 한글로 작성' AGENTS.md || fail "한글 문서 기록 계약이 없음"
grep -q '2340 × 1080' AGENTS.md || fail "AGENTS.md에 기준 화면이 없음"
grep -q '2340 × 1080' LOOP_GOAL.md || fail "LOOP_GOAL.md에 기준 화면이 없음"
grep -q '1872 px' QA_CHECKLIST.md || fail "QA에 최소 쓰기 너비가 없음"
grep -q '648 px' QA_CHECKLIST.md || fail "QA에 최소 쓰기 높이가 없음"
grep -q '균일 배율' QA_CHECKLIST.md || fail "QA에 글자 비율 보존 기준이 없음"
grep -q '각각 최소 580 px' QA_CHECKLIST.md || fail "QA에 실제 글자 양방향 크기 기준이 없음"
grep -q '12% 이상 18% 이하' QA_CHECKLIST.md || fail "QA에 교육용 목표 획 굵기 기준이 없음"
grep -q '실제 아이 관찰' QA_CHECKLIST.md || fail "실제 아이 관찰 근거 경계가 없음"
grep -q '^검토 관문: AUTO_CHILD_PROXY_QA$' .loop/queue.md || fail "자동 아이 대리 QA 전환이 선택되지 않음"
grep -q '다음 루프 하나' CLI_AUTOMATION.md || fail "완료 뒤 단일 다음 루프 계약이 없음"
grep -q '.loop/qa-findings.md' .loop/cli-worker-prompt.md || fail "작업자 지시문에 QA 발견 기록이 없음"
grep -q '.loop/user-directives.md' .loop/cli-worker-prompt.md || fail "작업자 지시문에 Codex 앱 지시 기록이 없음"

goal_loop="$(sed -n 's/^# 루프 목표 \([0-9][0-9][0-9]\).*/\1/p' LOOP_GOAL.md)"
state_loop="$(sed -n 's/^루프: \([0-9][0-9][0-9]\).*/\1/p' .loop/state.md)"
active_loop="$(sed -n 's/^활성 루프: //p' .loop/queue.md)"
status="$(sed -n 's/^상태: //p' .loop/state.md)"
iteration="$(sed -n 's/^반복: //p' .loop/state.md)"

[[ -n "$goal_loop" ]] || fail "목표 루프 번호를 읽을 수 없음"
[[ "$state_loop" == "$goal_loop" ]] || fail "상태 루프 $state_loop와 목표 루프 $goal_loop가 일치하지 않음"

case "$status" in
    준비|진행\ 중|완료|차단) ;;
    *) fail "잘못된 루프 상태: $status" ;;
esac

[[ "$iteration" =~ ^[0-9]+$ ]] || fail "반복 번호가 숫자가 아님"
(( iteration <= 15 )) || fail "15회 반복 중지 조건을 넘음"

if [[ "$active_loop" == "없음" ]]; then
    [[ "$status" == "완료" || "$status" == "차단" ]] || fail "활성 루프가 없지만 상태가 $status임"
else
    [[ "$active_loop" == "$goal_loop" ]] || fail "활성 루프 $active_loop와 목표 루프 $goal_loop가 일치하지 않음"
fi

echo "자동화 계약 통과"
echo "단계: CLI"
echo "루프: $goal_loop"
echo "상태: $status"
echo "반복: $iteration"
echo "활성 루프: $active_loop"
