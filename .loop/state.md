# 현재 루프 상태

루프: 181 — 정식 앱 식별자·백업 차단과 보호자 음성 기획 확정
상태: 완료
반복: 1

전환 근거: 사용자가 정식 package 변경·진도 백업 제거와 함께 합성 음성 기획을 보호자 로컬 녹음으로 대체하고, 고정된 하루 학습량 없이 보호자가 lesson 수와 순서를 정하도록 지시했다.
완료 근거: source·test·Gradle·APK package를 `com.nullplaying.limdo`로 통일했고 `allowBackup=false`와 cloud backup·device transfer 전체 제외 규칙을 적용했다. 전체 108개 단위 테스트·lint·debug build와 새 package 에뮬레이터 cold launch·2340 × 1080 화면·focus를 확인했다. 2차 기획서는 합성·내장 음성을 제거하고 보호자별 자유 lesson 목록, lesson별 `쓰기 전`·`정답 후` 로컬 녹음, 성공 뒤 자동 다음 계약으로 교정했다.
남은 범위: 보호자 메뉴·실제 녹음·재생·자동 다음·자유 lesson 목록 UI는 기획만 확정했고 아직 구현하지 않았다. 사용자의 별도 구현 지시 전에는 2차 제품 루프를 자동 시작하지 않는다.
실제 아이 관찰: 실행 안 함. 자동·에뮬레이터·아이 대리 QA와 구분한다.
