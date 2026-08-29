# LimDo 프리미엄 art bible

## 적용 범위

이 문서는 D0의 공통 표현 계약이다. D1~D8의 화면군은 새 수치를 임의로 만들지 않고 `LimDoPlaygroundTokens`에서 재사용한다. full-screen mock 3안 중 하나가 선택되면 palette·surface 표현값은 교체할 수 있지만 아래 교육·터치·접근성 경계는 바꾸지 않는다.

## 세계관과 층위

공통 세계관은 `햇살 정원 글자 공방`이다. 아이 화면은 낮은 대비의 환경 배경, 손에 잡힐 듯한 기능 surface, 실제 학습 glyph·시작점·주 행동의 세 깊이 층으로 구성한다. 학습 대상이 항상 가장 강하고, 기능 surface가 다음, 배경과 장식이 마지막이다. 보호자 화면은 같은 재질을 쓰되 장식 밀도를 낮춰 정보 위계를 우선한다.

## 공통 token

| 분류 | 고정 기준 | 코드 기준 |
| --- | --- | --- |
| palette | warm cream `#FFF8EC`, deep teal `#3F725E`, coral `#D95D4F`, sun yellow `#FFD85A`, ink `#26332D`; 자음 blue·모음 orange·가나다 green은 보조색 | `warmCream`, `deepTeal`, `coral`, `sunYellow`, `ink`, `LearningMenu.visuals()` |
| 재질 | 아이 surface는 soft toy clay, 보호자 surface는 warm paper; 과한 플라스틱 광택·검은 halo 금지 | `MATERIAL_NAME` |
| 광원 | 왼쪽 위 단일 주광, 하단·오른쪽으로 부드러운 음영; 화면마다 방향 변경 금지 | `LIGHT_DIRECTION_NAME` |
| corner | 조작 20dp, 일반 card 32dp, hero surface 40dp | `CORNER_CONTROL_DP`, `CARD_CORNER_DP`, `CORNER_HERO_DP` |
| elevation | 평면 0dp, 조작 4dp, card 8dp, hero 12dp; 선택 상태는 색만이 아니라 외곽·깊이도 바꾼 | `ELEVATION_FLAT_DP`, `ELEVATION_CONTROL_DP`, `CARD_SHADOW_DP`, `ELEVATION_HERO_DP` |
| spacing | compact 12dp, related 20dp, section 32dp, scene 48dp; 터치 간격은 최소 12dp | `SPACING_COMPACT_DP`, `SPACING_RELATED_DP`, `SPACING_SECTION_DP`, `SPACING_SCENE_DP` |
| typography | 보조 16sp, 조작 22sp, section 30sp, hero 48sp; 한글 glyph는 이미지로 만들지 않음 | `TYPE_SUPPORT_SP`, `TYPE_ACTION_SP`, `TYPE_SECTION_SP`, `TYPE_HERO_SP` |
| icon | 둥글고 촉각적인 단일 silhouette; 조작 56dp, 주 행동 72dp; emoji·시스템 glyph 금지 | `ICON_STYLE_NAME`, `ICON_CONTROL_DP`, `ICON_PRIMARY_DP` |
| background density | 아이 화면 장식은 가장자리 면적 22% 이하, 보호자 화면은 10% 이하; 중앙 글자 길·시작점·조작에는 지속 장식 0개 | `BACKGROUND_CHILD_MAX_EDGE_OCCUPANCY`, `BACKGROUND_GUARDIAN_MAX_DECORATION_OCCUPANCY` |
| motion | 눌림 180ms, 진입 360ms, 전환 480ms, 재시도 500ms 이하, 성공 1,000ms 이하; animator scale 0은 이동·흔들림·overshoot·particle 0개와 동등한 정적 의미를 제공 | `MOTION_FEEDBACK_DURATION_MS`, `HOME_ENTRANCE_DURATION_MS`, `MENU_TRANSITION_DURATION_MS`, `MOTION_RETRY_MAX_DURATION_MS`, `MOTION_SUCCESS_MAX_DURATION_MS`, `REDUCED_MOTION_PARTICLE_COUNT` |

## 바꾸지 않는 제품 경계

- WritingCanvas는 2340 × 1080에서 1962 × 954 px 이상으로 유지하고 네 조작은 각각 168 × 168 px를 유지한다.
- 아이·보호자의 필수 조작은 최소 64 × 64dp이다. 보호자 lesson cell은 기존 192px 이상과 비스크롤 그리드·명시적 페이지 버튼을 유지한다.
- 교육 glyph·점선·시작·끝·판정은 같은 production geometry와 균일 배율을 쓴다. bitmap이나 decoration이 이 경로를 대체하지 않는다.
- 필수 텍스트 대비는 4.5:1, 큰 텍스트·필수 비텍스트 대비는 3:1 이상이다. `font_scale=1.0·1.5·2.0`에서 잘림·겹침 0건을 요구한다.
- 화면 bitmap은 한 장만 동시 상주하고 production raster decode 합계 16 MiB 이하, particle 24개 이하를 유지한다.

## 자산과 mock 인계

현재 성공 도마뱀과 action atlas의 soft toy/clay 재질·둥글 실루엣을 품질 기준으로 삼는다. 차량, AI가 그린 한글·숫자·UI, watermark는 사용하지 않는다. 다음 반복의 full-screen mock 3안은 이 경계와 실제 production 안전 영역을 공통으로 지키되 표현 강조점을 서로 다르게 만든다. 사용자가 선택하기 전에는 어느 mock도 production composable에 적용하지 않는다.
