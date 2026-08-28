package com.limdo.hangul

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GaAssemblyTest {
    @Test fun gieokThenACompletesButAFirstDoesNot() {
        val wrongFirst = GaAssemblyState().place(GaAssemblyPiece.VOWEL)
        assertFalse(wrongFirst.complete)
        assertFalse(wrongFirst.vowelPlaced)
        assertTrue(wrongFirst.retryPiece == GaAssemblyPiece.VOWEL)

        val afterGieok = wrongFirst.place(GaAssemblyPiece.GIEOK)
        assertTrue(afterGieok.gieokPlaced)
        assertFalse(afterGieok.complete)
        assertTrue(afterGieok.place(GaAssemblyPiece.VOWEL).complete)
    }

    @Test fun targetsKeepGaGeoAndGyeoAsDistinctProductionLessons() {
        assertTrue(GaAssemblyTarget.GA.lessonId == LessonId.GA)
        assertTrue(GaAssemblyTarget.GEO.lessonId == LessonId.GEO)
        assertTrue(GaAssemblyTarget.GYEO.lessonId == LessonId.GYEO)
        assertTrue(GaAssemblyTarget.entries.map { it.glyph } == listOf("가", "거", "겨"))
    }

    @Test fun geoWritingKeepsStageNavigationWithoutExpandingGanadaSelection() {
        val geo = KoreanCurriculum.lessons.single { it.id == LessonId.GEO }

        assertTrue(LearningNavigation.lessons(LearningMenu.GANADA).none { it.id == LessonId.GEO })
        assertTrue(LearningNavigation.nextLesson(LearningMenu.GANADA, geo).id == LessonId.GYEO)
        assertTrue(LearningNavigation.previousLesson(LearningMenu.GANADA, geo).id == LessonId.GYA)
    }

    @Test fun gyeoUsesTwoLeftwardVowelStrokesFromProductionGeometry() {
        val gyeo = KoreanCurriculum.lessons.single { it.id == LessonId.GYEO }

        assertTrue(gyeo.strokeCount == 4)
        assertTrue(gyeo.strokeDirections.drop(1) == listOf(
            StrokeDirection.DOWN,
            StrokeDirection.LEFT,
            StrokeDirection.LEFT,
        ))
    }
}
