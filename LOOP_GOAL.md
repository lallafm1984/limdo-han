# 루프 목표 118 — 첫 조합 글자 `가` 표시 연결

## 작업 가치 관문

분류: 제품 변경
사용자 가치: 아이가 `ㄱ` 다음 학습 단계인 `가`를 실제 학습판에서 정사각형 비율과 3획 구조로 볼 수 있게 한다.
새로운 근거: 루프 117은 production `ga(width, height)` geometry만 확정했고 현재 화면은 여전히 `ㄱ`만 표시하므로, `가` geometry를 실제 Compose 표시 경로에 연결한 새 2340 × 1080 근거가 필요하다.
중복 방지: `가`의 3획 guide가 production geometry와 같은 좌표로 화면에 표시되고 정확한 실측과 회귀 검증을 통과하면 표시 연결은 종료한다. 이 루프에서 동적 3획 안내나 판정까지 묶지 않는다.

## 목표

루프 117의 production `WritingCanvasGeometry.ga` API를 실제 Compose 학습판 표시 경로에 연결해 `가`의 자모 배치와 3획 guide를 보여 준다. 입력 판정과 동적 안내를 3획으로 확장하는 작업은 후속 루프로 남긴다.

## 성공 조건

1. 실제 `WritingCanvas` 표시 경로가 `WritingCanvasGeometry.ga(width, height)`의 3획을 그리며 별도 좌표를 중복하지 않는다.
2. 정사각형 em, 자모 상대 위치, 20% guide 굵기, 균일 x/y 배율을 유지한다.
3. 정확한 2340 × 1080에서 실제 Canvas 1962 × 775 px 이상과 `가` 3획 bounds를 새 화면·hierarchy로 확인한다.
4. 화면에서 `ㄱ`과 `ㅏ`가 표준 조합 배치로 구분되고 시스템 바·버튼·자동차에 가리지 않는다.
5. 기존 `ㄱ` geometry·보상·음성·초기화 테스를 회귀시키지 않는다.
6. `./scripts/verify.sh`, `./scripts/check-automation.sh`, `git diff --check`를 통과한다.
7. 아이 대리 QA에서 이번 루프는 표시 연결만 입증하며, 3획 동적 안내·입력 판정을 완료했다고 주장하지 않고 `실제 아이 관찰: 실행 안 함`을 기록한다.

## 다음 반복

먼저 `WritingCanvas`의 현재 `ㄱ` guide 렌더링과 lesson 선택 경계를 확인한 뒤, production `ga` strokes를 그리는 최소 표시 연결과 실측 테스만 구현한다.

## 완료 정의

모든 조건을 통과하면 다음 제품 단계인 `가` 3획 동적 안내 또는 입력 판정 연결 중 우선순위가 높은 하나를 새 아이 대리 QA 근거로 준비한다. 완료 루프와 준비된 다음 루프를 체크포인트 커밋 하나로 만들고 `git push origin HEAD`로 일반 push한다.
