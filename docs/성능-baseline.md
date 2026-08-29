# LimDo D0 성능 baseline

## 측정 조건

- 일시: 2026-08-30
- 기기: `emulator-5554` / `alarmquest-qa`, 물리 1080 × 2340, `user_rotation=1`
- 앱 화면: 2340 × 1080, `com.limdo.hangul/.MainActivity` focus
- 대표 흐름: cold launch → 홈 → 자음 선택 → `ㄱ` 쓰기 진입 → 3초 안정 후 측정
- APK SHA-256: `c944e2b6be137f46156c1db8f7b93fde6fdac3a161445710321d0ebaf9d0b9a3`

## APK·raster

- debug APK: 26,609,791 byte
- PNG 13개 APK 내 압축 합계: 16,837,080 byte. PNG는 APK에서 `Stored`로 재압축되지 않아 파일 크기와 같다.
- PNG 13개를 모두 RGBA8888로 decode할 때 예산: 74,970,928 byte(71.50 MiB).
- production 참조 3개(`action_button_atlas`, `sunny_flower_background`, `success_fullscreen_feedback`) decode 합계: 16,774,960 byte(15.9978 MiB). D9의 동시 decode 16 MiB 예산보다 2,256 byte 작다.
- 주요 리스크: 참조 3개가 동시 예산에 거의 맞지만 제공하지 않는다. `gecko_guide`와 차량 9개는 production 참조 0건이며 APK에서 제거 여유를 차지한다.

## 프레임·메모리

- `gfxinfo framestats`: 172 frames, deadline miss 4개(2.33%), frozen frame 0개, p50 17 ms, p90 22 ms, p95 23 ms, p99 150 ms.
- D9 목표인 deadline miss 3% 미만·frozen 0개를 현재 baseline이 통과한다. 단, debug 에뮬레이터 1회·단일 흐름의 baseline이며 최종 10회 내구성·5/10회 PSS 관문은 D9에서 따로 수집한다.
- 쓰기 안정 PSS: 95,559 KiB, RSS 202,088 KiB, swap PSS 189 KiB.

## 근거

- `captures/loop237/iteration8/performance/home.png`
- `captures/loop237/iteration8/performance/consonant-selection.png`
- `captures/loop237/iteration8/performance/writing.png`
- `captures/loop237/iteration8/performance/gfxinfo-framestats.txt`
- `captures/loop237/iteration8/performance/meminfo.txt`
- `captures/loop237/iteration8/performance/focus.txt`

