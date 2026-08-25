# 루프 목표 121 — `가` 입력 중 다음 방향 안내 일치

## 작업 가치 관문

분류: 결함 수정
사용자 가치: 아이가 `가`의 둘째·셋째 획을 쓰는 동안 움직이는 화살표가 현재 손가락보다 앞에서 올바른 방향을 보여 준다.
새로운 근거: 루프 120의 정확한 2340 × 1080 3획 입력은 성공했지만 `WritingCanvas`의 입력 중 안내는 여전히 단일 `ㄱ` 전용 `gieokInputDirectionGuide`를 호출한다.
중복 방지: production `ga` geometry와 완료 획 index에서 현재 획의 앞쪽 위치·방향을 파생하고 1·2·3획 입력 중 연속 프레임에서 일치를 확인하면 종료한다. 3획 판정을 다시 구현하거나 반복 횟수를 늘리지 않는다.

## 목표

`TraceAttempt.completedStrokes.size`와 production `ga(width, height)`에서 현재 획의 입력 중 다음 방향 안내를 파생한다.

## 성공 조건

1. 입력 중 안내는 production `ga(width, height)`의 해당 획과 완료 획 index에서 파생되고 별도 좌표를 중복하지 않는다.
2. `ㄱ` 가로→세로, `ㅏ` 위→아래, `ㅏ` 왼쪽→오른쪽에서 화살표촉과 앞쪽 움직임이 일치한다.
3. 완료한 획은 보존되고 현재 획의 안내만 활성화된다.
4. 정확한 2340 × 1080에서 세 획 입력 중 연속 프레임과 최종 `SUCCESS`를 확인한다.
5. 루프 120의 판정, 보상·음성·지우기를 회귀시키지 않는다.
6. `./scripts/verify.sh`, `./scripts/check-automation.sh`, `git diff --check`를 통과한다.
7. 아이 대리 QA와 실제 아이 관찰을 구분하고 `실제 아이 관찰: 실행 안 함`을 기록한다.

## 다음 반복

`gieokInputDirectionGuide`의 단일 획 가정을 조사하고 production `ga` 획 index를 받는 최소 geometry API와 테스트를 추가한다.

## 완료 정의

모든 조건을 통과하면 새 아이 대리 QA에서 가장 중요한 미해결 불편 하나만 다음 루프로 준비한다. 완료 루프와 준비된 다음 루프를 체크포인트 커밋 하나로 만들고 `git push origin HEAD`로 일반 push한다.
