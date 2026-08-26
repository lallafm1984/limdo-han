package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningNavigationTest {
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
