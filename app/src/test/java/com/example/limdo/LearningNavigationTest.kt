package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class LearningNavigationTest {
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
