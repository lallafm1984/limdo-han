# 현재 루프 상태

루프: 118 — 첫 조합 글자 `가` 표시 연결

상태: 준비

반복: 0

마지막 검증: 루프 117 반복 1에서 `gaFixture`를 production `ga(width, height)` API로 전환하고 3획 순서·정규화 자모 배치·가로형/세로형 Canvas의 균일 배율·잘못된 크기 거부를 새 단위 테스로 확정했다. `./scripts/verify.sh`의 단위 테스·lint·debug build가 모두 통과했다.

완료한 조건: 루프 117 성공 조건 1~7 전체. 정확한 2340 × 1080 최종 아이 대리 QA에서 현재 화면은 의도대로 `ㄱ`만 표시하고 `가`는 아직 표시하지 않음을 확인했다.

현재 실패: 없음.

현재 가설: `WritingCanvas`가 production `WritingCanvasGeometry.ga`의 3획을 별도 좌표 중복 없이 그리게 하면, 정사각형 em·자모 배치·20% 굵기를 유지한 `가`를 정확한 화면에 표시할 수 있다.

다음 작업: `WritingCanvas`의 `ㄱ` guide 렌더링과 lesson 선택 경계를 확인하고, production `ga` strokes를 그리는 최소 표시 연결과 실측 테스를 구현한다.

남은 조건: 루프 118 성공 조건 1~7 전체.
