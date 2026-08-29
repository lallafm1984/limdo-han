# 프리미엄 D0 full-screen mock 3안 비교

## 목적과 경계

루프 237 D0에서 현재 production 홈·자음 선택·쓰기 화면을 공통 입력으로 사용해 사용자 선택용 full-screen 방향을 정확히 세 가지 만들었다. 세 이미지는 production에 연결하지 않은 preview이며, 사용자가 한 방향을 선택하기 전에는 D1 화면 구현을 시작하지 않는다.

공통 보존 조건은 홈의 큰 세 메뉴 영역, 왼쪽 위 보호자 진입 단서, blue·orange·green 메뉴 식별, 중앙 글자 렌더링용 빈 공간, 차량·임의 한글·숫자·가짜 UI·워터마크 0건이다. production 한글과 교육 geometry는 Compose·Canvas가 계속 렌더링한다.

## 생성·규격 근거

- 생성 방식: `imagegen` 스킬의 Codex 내장 이미지 생성기
- 공통 참조: `captures/design-audit-20260830/baseline/01-home.png`, `02-consonant-selection.png`, `03-writing.png`
- 최종 규격: 각 2340 × 1080 PNG, 완전 불투명 full-screen preview
- production 소비: 0건. 사용자 선택 뒤 선택안만 D1에서 production 자산 계약에 따라 재생성·분해·적용한다.

| 안 | 파일 | SHA-256 | 핵심 재질·공간 |
| --- | --- | --- | --- |
| A 햇살 정원 글자 공방 | `captures/loop237/iteration10/mocks/direction-a-sunny-garden-workshop-2340x1080.png` | `dd4f1cddd0a24f034d852a973c82772529f32e9231b8d58bb5bfd581f1647c60` | warm cream 정원, matte clay 카드, 가장자리 식물·공방 소품 |
| B 구름빛 글자 놀이터 | `captures/loop237/iteration10/mocks/direction-b-cloud-playground-2340x1080.png` | `b2665c1829fc20bd8ea5557e7d27644c290c701de86344982b3ab26524c25def` | pale aqua 하늘, felt 카드·구름 받침, 열린 수평 깊이 |
| C 포근한 종이 글자 공방 | `captures/loop237/iteration10/mocks/direction-c-paper-workshop-2340x1080.png` | `74977e551db0dd26d654d472563e300a7bc025a430fedccc38e932a8bccacdfb` | warm ivory 종이, 제본 모서리·박음선, 정돈된 공예 책상 |

원본 생성 파일도 같은 폴더의 `*-source.png`로 보존했다. 최종 파일은 중앙 crop 뒤 2340 × 1080으로 균일 리샘플해 카드 비율을 임의 축별로 늘이지 않았다.

## 자동 역할 비교

| 판정 축 | A 햇살 정원 | B 구름빛 놀이터 | C 종이 공방 |
| --- | --- | --- | --- |
| 자동 아트 디렉션 | 기존 성공 자산의 clay 질감과 연결이 가장 쉽고 브랜드 서사가 강하다. 우측 공방 소품 밀도는 D1에서 낮춰야 한다. | 세 안 중 가장 밝고 즉시 놀이터로 읽히며 시각적 개방감이 크다. 기존 warm cream·도마뱀 자산과의 palette 연결 비용이 더 든다. | 반복 9 art bible의 종이·공방 재질과 보호자 화면 확장이 가장 자연스럽다. 아이 화면의 축하감은 별도 캐릭터·빛 연출로 보강해야 한다. |
| 자동 UI/UX | 세 카드 중심이 비고 색·위치 구분이 분명하다. 가장자리 소품이 카드보다 강해지지 않도록 background density 상한이 필요하다. | 카드와 배경 명도 분리가 가장 명료하고 세 받침이 선택 가능성을 강화한다. 하단 구름 두께가 실제 카드 touch bounds를 줄이지 않도록 배경으로만 사용해야 한다. | 카드 내부 여백과 경계가 가장 정돈됐고 성인 보호자 화면에도 확장하기 쉽다. 박음선은 실제 선택·진행 표시와 경쟁하지 않도록 비상태 장식으로 고정해야 한다. |
| 자동 접근성·아이 대리 QA | 글을 읽지 않아도 큰 세 영역과 보호자 단서를 5초 안에 구분할 수 있다. 장식은 카드 중심을 가리지 않는다. | 세 영역의 색·형태·받침이 가장 강하게 분리된다. 별빛은 의미 없는 상태 표시로 오해되지 않도록 정적 저밀도로 제한해야 한다. | 세 영역은 충분히 크고 색·테두리로 구분된다. 세 카드의 재질이 유사하므로 production 대표 glyph·선택 motion이 메뉴 차이를 보완해야 한다. |
| D1 구현 위험 | 가장자리 raster 밀도·APK decode 예산 | 기존 warm palette·성공 자산과 화풍 통합 | 박음선과 실제 상태선의 의미 충돌 |

## 판정

- 정확히 3안: 통과
- 2340 × 1080: 3/3 통과
- 임의 한글·숫자·가짜 UI·차량·워터마크: 0건
- 큰 세 주 영역과 왼쪽 위 보호자 단서: 3/3 보존
- 중앙 production glyph 안전 영역: 3/3 보존
- 방향 간 선택 가능성: clay 정원·felt 하늘·paper 공방의 palette·재질·공간감이 명확히 달라 통과
- 자동 아트 디렉션 역할: 통과
- 자동 UI/UX 역할: 통과
- 자동 접근성·아이 대리 QA 역할: 통과
- 실제 아이 관찰: 실행 안 함

세 안 모두 D0 선택 후보로 수용한다. 기본 권장안은 기존 성공 clay 자산과 연결 비용이 가장 낮고 브랜드 서사가 강한 A `햇살 정원 글자 공방`이다. 다만 production 적용안은 사용자 선택으로 확정하며, 선택 전 D1을 시작하지 않는다.

## 최종 생성 prompt

세 prompt는 공통으로 `ui-mockup`, production 세 화면 참조, 2340:1080 landscape, 정확히 세 개의 큰 좌·중·우 카드, 왼쪽 위 보호자 단서, 카드 중앙 공백, blue·orange·green 식별을 지정했다. 공통 금지 항목은 생성 한글·문자·숫자·가짜 UI·차량·워터마크·로고·과밀 파티클이다.

- A: premium soft 3D toy/clay, warm cream sunlit garden workshop, deep teal·coral·sun yellow, upper-left sunlight, outer-edge foliage·paper-craft tools.
- B: premium soft 3D felt-and-clay, pale aqua sky playground, layered clouds·rounded floating platforms, diffuse morning skylight, restrained star-light accents.
- C: premium dimensional paper craft and bookbinding, warm ivory desk-paper world, layered cut-paper frame·stitched edge·print texture, sparse border craft tools.
