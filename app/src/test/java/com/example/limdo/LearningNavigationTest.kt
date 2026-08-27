package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningNavigationTest {
    @Test
    fun menuTransitionKeepsSelectedColorVehicleAndFiniteStartMiddleEndStates() {
        val start = menuTransitionVisuals(0f)
        val middle = menuTransitionVisuals(0.5f)
        val end = menuTransitionVisuals(1f)

        assertTrue(start.vehicleScale < middle.vehicleScale)
        assertTrue(middle.vehicleScale < end.vehicleScale)
        assertEquals(0f, start.symbolAlpha)
        assertTrue(middle.symbolAlpha in 0.4f..0.6f)
        assertEquals(1f, end.symbolAlpha)
        assertTrue(LimDoPlaygroundTokens.MENU_TRANSITION_DURATION_MS in 300..600)
        LearningMenu.entries.forEach { menu ->
            assertTrue(menu.icon.isNotBlank())
            assertTrue(menu.symbol.isNotBlank())
            assertTrue(menu.visuals().accent != menu.visuals().softSurface)
            assertEquals(
                LearningDestination.Home,
                LearningNavigation.back(LearningDestination.MenuTransition(menu)),
            )
        }
    }

    @Test
    fun homeCardPressUsesScaleAndBrightGlowWithoutChangingRestState() {
        val rest = homeCardPressVisuals(isPressed = false)
        val pressed = homeCardPressVisuals(isPressed = true)

        assertEquals(1f, rest.scale)
        assertEquals(0f, rest.glowBorderDp)
        assertTrue(pressed.scale in 0.92f..0.97f)
        assertTrue(pressed.glowBorderDp >= 8f)
    }

    @Test
    fun homeEntranceHasFiniteStaggeredStartMiddleAndEndStates() {
        val start = homeEntranceVisuals(0f)
        val middle = homeEntranceVisuals(0.5f)
        val end = homeEntranceVisuals(1f)

        assertEquals(0f, start.alpha)
        assertTrue(start.scale < middle.scale)
        assertTrue(middle.scale < end.scale)
        assertTrue(start.offsetDp > middle.offsetDp)
        assertTrue(middle.offsetDp > end.offsetDp)
        assertEquals(1f, end.alpha)
        assertEquals(1f, end.scale)
        assertEquals(0f, end.offsetDp)
        assertTrue(LimDoPlaygroundTokens.HOME_ENTRANCE_DURATION_MS in 200..500)
        assertTrue(LimDoPlaygroundTokens.HOME_ENTRANCE_STAGGER_MS in 50..120)
        assertTrue(
            LimDoPlaygroundTokens.HOME_ENTRANCE_DURATION_MS +
                (LearningMenu.entries.lastIndex * LimDoPlaygroundTokens.HOME_ENTRANCE_STAGGER_MS) < 700,
        )
    }

    @Test
    fun playgroundTokensKeepSharedCardLanguageAndDistinctMenuColors() {
        assertTrue(LimDoPlaygroundTokens.CARD_CORNER_DP >= 28f)
        assertTrue(LimDoPlaygroundTokens.CARD_BORDER_DP >= 3f)
        assertTrue(LimDoPlaygroundTokens.CARD_SHADOW_DP >= 5f)
        assertTrue(LimDoPlaygroundTokens.CARD_GAP_DP >= 12f)
        assertEquals(3, LearningMenu.entries.map { it.visuals().accent }.distinct().size)
        assertEquals(3, LearningMenu.entries.map { it.visuals().softSurface }.distinct().size)
    }

    @Test
    fun menuVisualsKeepHomeVehicleIdentityInWriting() {
        assertEquals(
            listOf("경찰차", "소방차", "버스"),
            LearningMenu.entries.map { menu ->
                VehicleCarousel.vehicles[menu.visuals().startingVehicleIndex].koreanName
            },
        )
    }

    @Test
    fun lessonCardsDistinguishDefaultSelectedAndDisabledWithoutColorAlone() {
        LearningMenu.entries.forEach { menu ->
            val default = menu.lessonCardVisuals(LessonCardVisualState.DEFAULT)
            val selected = menu.lessonCardVisuals(LessonCardVisualState.SELECTED)
            val disabled = menu.lessonCardVisuals(LessonCardVisualState.DISABLED)

            assertNotEquals(default.surface, selected.surface)
            assertNotEquals(default.outlineWidthDp, selected.outlineWidthDp)
            assertNotEquals(default.cornerDp, selected.cornerDp)
            assertNotEquals(default.surface, disabled.surface)
            assertNotEquals(default.outlineWidthDp, disabled.outlineWidthDp)
            assertNotEquals(default.cornerDp, disabled.cornerDp)
            assertTrue(selected.outlineWidthDp > default.outlineWidthDp)
            assertTrue(disabled.shadowDp < default.shadowDp)
        }
    }

    @Test
    fun menusHaveDistinctShortKoreanSelectionCues() {
        assertEquals(
            listOf(SpokenCue.MENU_CONSONANTS, SpokenCue.MENU_VOWELS, SpokenCue.MENU_GANADA),
            LearningMenu.entries.map(LearningMenu::spokenCue),
        )
        assertEquals(
            listOf("자음", "모음", "가나다"),
            LearningMenu.entries.map { it.spokenCue.utterance },
        )
    }

    @Test
    fun menusExposeCurriculumLessonsAndFourteenGanadaSyllables() {
        assertEquals(
            KoreanCurriculum.lessons.filter { it.stage == CurriculumStage.CONSONANTS },
            LearningNavigation.lessons(LearningMenu.CONSONANTS),
        )
        assertEquals(
            KoreanCurriculum.lessons.filter { it.stage == CurriculumStage.VOWELS },
            LearningNavigation.lessons(LearningMenu.VOWELS),
        )
        assertEquals(
            listOf(
                LessonId.GA, LessonId.NA, LessonId.DA, LessonId.RA, LessonId.MA, LessonId.BA,
                LessonId.SA, LessonId.AH, LessonId.JA, LessonId.CHA, LessonId.KA, LessonId.TA,
                LessonId.PA, LessonId.HA,
            ),
            LearningNavigation.lessons(LearningMenu.GANADA).map(LessonSpec::id),
        )
    }

    @Test
    fun nextLessonStaysInsideEachMenuAndGanadaMatchesTheFourteenLessonOracle() {
        val expectedGanadaCycle = listOf(
            LessonId.GA, LessonId.NA, LessonId.DA, LessonId.RA, LessonId.MA, LessonId.BA,
            LessonId.SA, LessonId.AH, LessonId.JA, LessonId.CHA, LessonId.KA, LessonId.TA,
            LessonId.PA, LessonId.HA, LessonId.GA,
        )
        val actualGanadaCycle = LearningNavigation.lessons(LearningMenu.GANADA)
            .map { lesson -> LearningNavigation.nextLesson(LearningMenu.GANADA, lesson).id }

        assertEquals(expectedGanadaCycle.drop(1), actualGanadaCycle)

        listOf(LearningMenu.CONSONANTS, LearningMenu.VOWELS).forEach { menu ->
            val lessons = LearningNavigation.lessons(menu)
            val lessonIds = lessons.map(LessonSpec::id).toSet()

            lessons.forEach { lesson ->
                assertTrue(LearningNavigation.nextLesson(menu, lesson).id in lessonIds)
            }
            assertEquals(lessons.first(), LearningNavigation.nextLesson(menu, lessons.last()))
        }
    }

    @Test
    fun fourteenGanadaSelectionsKeepWritingSpeechGeometryRewardAndHomeReturnIntegrated() {
        val lessons = LearningNavigation.lessons(LearningMenu.GANADA)

        lessons.forEachIndexed { index, lesson ->
            val destination = LearningDestination.Writing(
                menu = LearningMenu.GANADA,
                lessonId = lesson.id,
                sessionId = index + 1,
            )
            val geometry = WritingCanvasGeometry.glyph(lesson, width = 1962f, height = 954f)
            val reward = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, lesson)

            assertEquals(lesson.strokeCount, geometry.strokes.size)
            assertEquals(lesson.strokeCount, lesson.strokeDirections.size)
            assertTrue(lesson.initialCue.utterance.isNotBlank())
            assertTrue(lesson.successCue.utterance.isNotBlank())
            assertEquals(lesson.strokeCount, reward.targetSteps)
            assertEquals(
                LearningDestination.Selection(LearningMenu.GANADA),
                LearningNavigation.back(destination),
            )
        }
    }

    @Test
    fun fourteenGanadaLessonsMatchIndependentProductionMappingOracle() {
        data class ExpectedLesson(
            val id: LessonId,
            val glyph: String,
            val strokeCount: Int,
            val initialCue: SpokenCue,
            val successCue: SpokenCue,
        )

        val expected = listOf(
            ExpectedLesson(LessonId.GA, "가", 3, SpokenCue.INITIAL, SpokenCue.SUCCESS),
            ExpectedLesson(LessonId.NA, "나", 3, SpokenCue.INITIAL_NA, SpokenCue.SUCCESS_NA),
            ExpectedLesson(LessonId.DA, "다", 4, SpokenCue.INITIAL_DA, SpokenCue.SUCCESS_DA),
            ExpectedLesson(LessonId.RA, "라", 5, SpokenCue.INITIAL_RA, SpokenCue.SUCCESS_RA),
            ExpectedLesson(LessonId.MA, "마", 5, SpokenCue.INITIAL_MA, SpokenCue.SUCCESS_MA),
            ExpectedLesson(LessonId.BA, "바", 6, SpokenCue.INITIAL_BA, SpokenCue.SUCCESS_BA),
            ExpectedLesson(LessonId.SA, "사", 4, SpokenCue.INITIAL_SA, SpokenCue.SUCCESS_SA),
            ExpectedLesson(LessonId.AH, "아", 3, SpokenCue.INITIAL_AH, SpokenCue.SUCCESS_AH),
            ExpectedLesson(LessonId.JA, "자", 4, SpokenCue.INITIAL_JA, SpokenCue.SUCCESS_JA),
            ExpectedLesson(LessonId.CHA, "차", 5, SpokenCue.INITIAL_CHA, SpokenCue.SUCCESS_CHA),
            ExpectedLesson(LessonId.KA, "카", 4, SpokenCue.INITIAL_KA, SpokenCue.SUCCESS_KA),
            ExpectedLesson(LessonId.TA, "타", 5, SpokenCue.INITIAL_TA, SpokenCue.SUCCESS_TA),
            ExpectedLesson(LessonId.PA, "파", 6, SpokenCue.INITIAL_PA, SpokenCue.SUCCESS_PA),
            ExpectedLesson(LessonId.HA, "하", 5, SpokenCue.INITIAL_HA, SpokenCue.SUCCESS_HA),
        )
        val actual = LearningNavigation.lessons(LearningMenu.GANADA)

        assertEquals(expected.map(ExpectedLesson::id), actual.map(LessonSpec::id))
        assertEquals(expected.size, actual.map(LessonSpec::id).distinct().size)
        expected.zip(actual).forEachIndexed { index, (oracle, lesson) ->
            val geometry = WritingCanvasGeometry.glyph(lesson, width = 1962f, height = 954f)
            val reward = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, lesson)
            val writing = LearningDestination.Writing(
                menu = LearningMenu.GANADA,
                lessonId = lesson.id,
                sessionId = index + 1,
            )

            assertEquals(oracle.glyph, lesson.glyph)
            assertEquals(oracle.strokeCount, lesson.strokeCount)
            assertEquals(oracle.initialCue, lesson.initialCue)
            assertEquals(oracle.successCue, lesson.successCue)
            assertEquals(oracle.strokeCount, geometry.strokes.size)
            assertEquals(oracle.strokeCount, reward.targetSteps)
            assertEquals(
                LearningDestination.Selection(LearningMenu.GANADA),
                LearningNavigation.back(writing),
            )
        }
    }

    @Test
    fun threeMenuRepresentativePathsKeepLessonRewardNextAndHomeDestinationsIndependent() {
        LearningMenu.entries.forEachIndexed { menuIndex, menu ->
            val menuLessons = LearningNavigation.lessons(menu)
            val representative = menuLessons.first()
            val writing = LearningDestination.Writing(
                menu = menu,
                lessonId = representative.id,
                sessionId = menuIndex + 1,
            )
            val reward = LessonRewardState().onTraceResult(
                GieokTraceResult.SUCCESS,
                representative,
            )
            val curriculumIndex = KoreanCurriculum.lessons.indexOfFirst {
                it.id == representative.id
            }
            val nextLesson = KoreanCurriculum.lessons[
                KoreanCurriculum.nextIndex(curriculumIndex)
            ]

            assertTrue(menuLessons.size > 1)
            assertEquals(representative.strokeCount, reward.targetSteps)
            assertEquals(menuLessons[1].id, nextLesson.id)
            assertEquals(
                LearningDestination.Selection(menu),
                LearningNavigation.back(writing),
            )
            assertEquals(
                LearningDestination.Home,
                LearningNavigation.back(LearningDestination.Selection(menu)),
            )
        }
    }

    @Test
    fun threeMenuRetryReplayClearAndNextStatesStayConsistent() {
        LearningMenu.entries.forEach { menu ->
            val lesson = LearningNavigation.lessons(menu).first()
            val retryResult = GieokTraceResult.OFF_GUIDE
            val retryReward = LessonRewardState().onTraceResult(retryResult, lesson)
            val retryVehicle = VehicleCarouselState(
                index = menu.visuals().startingVehicleIndex,
            ).onTraceResult(retryResult)
            val retryCue = SpokenCueModel.forResult(retryResult, strokeIndex = 0, lesson = lesson)

            assertEquals(SpokenCue.RETRY_GUIDE, retryCue)
            assertEquals(0, retryReward.targetSteps)
            assertEquals(RewardMovePhase.IDLE, retryReward.phase)
            assertFalse(retryVehicle.nextVehiclePending)
            assertTrue(SpeechPlaybackState.Ready.canReplay)
            assertEquals(ReplayVisualState.AVAILABLE, SpeechPlaybackState.Ready.replayVisualState)
            assertFalse(
                shouldStartNextInitialCue(
                    moveCompleted = retryReward.phase == RewardMovePhase.COMPLETE,
                    nextVehiclePending = retryVehicle.nextVehiclePending,
                ),
            )

            val successReward = LessonRewardState()
                .onTraceResult(GieokTraceResult.SUCCESS, lesson)
            val successVehicle = VehicleCarouselState(
                index = menu.visuals().startingVehicleIndex,
            ).onTraceResult(GieokTraceResult.SUCCESS)

            assertFalse(
                shouldStartNextInitialCue(
                    moveCompleted = successReward.phase == RewardMovePhase.COMPLETE,
                    nextVehiclePending = successVehicle.nextVehiclePending,
                ),
            )

            val completedReward = successReward.moving().complete()
            assertTrue(
                shouldStartNextInitialCue(
                    moveCompleted = completedReward.phase == RewardMovePhase.COMPLETE,
                    nextVehiclePending = successVehicle.nextVehiclePending,
                ),
            )

            val clearedReward = LessonRewardState()
            val clearedVehicle = successVehicle.clearCurrentInput()
            assertEquals(RewardMovePhase.IDLE, clearedReward.phase)
            assertEquals(0, clearedReward.targetSteps)
            assertFalse(clearedVehicle.nextVehiclePending)
            assertTrue(clearedVehicle.successArmed)
            assertFalse(
                shouldStartNextInitialCue(
                    moveCompleted = clearedReward.phase == RewardMovePhase.COMPLETE,
                    nextVehiclePending = clearedVehicle.nextVehiclePending,
                ),
            )
        }
    }

    @Test
    fun rapidCallbacksBackHomeAndRelaunchDoNotLeakWritingStateAcrossMenus() {
        LearningMenu.entries.forEachIndexed { menuIndex, menu ->
            val lesson = LearningNavigation.lessons(menu).first()
            val initialVehicle = VehicleCarouselState(
                index = menu.visuals().startingVehicleIndex,
            )
            val firstReward = LessonRewardState()
                .onTraceResult(GieokTraceResult.SUCCESS, lesson)
            val duplicateReward = firstReward
                .onTraceResult(GieokTraceResult.SUCCESS, lesson)
            val firstVehicle = initialVehicle.onTraceResult(GieokTraceResult.SUCCESS)
            val duplicateVehicle = firstVehicle.onTraceResult(GieokTraceResult.SUCCESS)

            assertEquals(firstReward, duplicateReward)
            assertEquals(lesson.strokeCount, duplicateReward.targetSteps)
            assertEquals(firstVehicle, duplicateVehicle)

            val completedReward = duplicateReward.moving().complete()
            val afterFirstNext = duplicateVehicle.prepareNextInput(
                moveCompleted = completedReward.phase == RewardMovePhase.COMPLETE,
            )
            val afterRapidSecondNext = afterFirstNext.prepareNextInput(moveCompleted = true)
            assertEquals(afterFirstNext, afterRapidSecondNext)
            assertEquals(
                (menu.visuals().startingVehicleIndex + 1) % VehicleCarousel.vehicles.size,
                afterRapidSecondNext.index,
            )

            val writing = LearningDestination.Writing(
                menu = menu,
                lessonId = lesson.id,
                sessionId = menuIndex + 1,
            )
            assertEquals(
                LearningDestination.Selection(menu),
                LearningNavigation.back(writing),
            )
            assertEquals(
                LearningDestination.Home,
                LearningNavigation.back(LearningNavigation.back(writing)),
            )

            val freshReward = LessonRewardState()
            val freshVehicle = VehicleCarouselState(
                index = menu.visuals().startingVehicleIndex,
            )
            val relaunchedDestination = LearningDestination.Home
            assertEquals(RewardMovePhase.IDLE, freshReward.phase)
            assertEquals(0, freshReward.targetSteps)
            assertFalse(freshReward.inputLocked)
            assertTrue(freshVehicle.successArmed)
            assertFalse(freshVehicle.nextVehiclePending)
            assertEquals(LearningDestination.Home, relaunchedDestination)

            val reopened = LearningDestination.Writing(
                menu = menu,
                lessonId = lesson.id,
                sessionId = menuIndex + LearningMenu.entries.size + 1,
            )
            assertNotEquals(writing, reopened)
            assertEquals(writing.menu, reopened.menu)
            assertEquals(writing.lessonId, reopened.lessonId)
        }
    }

    @Test
    fun backMovesWritingToSelectionThenHome() {
        val writing = LearningDestination.Writing(LearningMenu.GANADA, LessonId.GA, sessionId = 1)
        val selection = LearningNavigation.back(writing)
        assertEquals(LearningDestination.Selection(LearningMenu.GANADA), selection)
        assertEquals(LearningDestination.Home, LearningNavigation.back(selection))
        assertEquals(LearningDestination.Home, LearningNavigation.back(LearningDestination.Home))
    }

    @Test
    fun reopeningSameLessonUsesADistinctWritingSession() {
        val first = LearningDestination.Writing(LearningMenu.GANADA, LessonId.GA, sessionId = 1)
        val reopened = LearningDestination.Writing(LearningMenu.GANADA, LessonId.GA, sessionId = 2)

        assertEquals(first.lessonId, reopened.lessonId)
        assertNotEquals(first, reopened)
    }
}
