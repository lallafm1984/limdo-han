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

    @Test fun targetsKeepGaThroughGuAsDistinctProductionLessons() {
        assertTrue(GaAssemblyTarget.GA.lessonId == LessonId.GA)
        assertTrue(GaAssemblyTarget.GEO.lessonId == LessonId.GEO)
        assertTrue(GaAssemblyTarget.GYEO.lessonId == LessonId.GYEO)
        assertTrue(GaAssemblyTarget.GO.lessonId == LessonId.GO)
        assertTrue(GaAssemblyTarget.GYO.lessonId == LessonId.GYO)
        assertTrue(GaAssemblyTarget.GU.lessonId == LessonId.GU)
        assertTrue(GaAssemblyTarget.entries.map { it.glyph } == listOf("가", "거", "겨", "고", "교", "구"))
        assertFalse(GaAssemblyTarget.GA.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GO.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GYO.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GU.isHorizontalVowel)
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

    @Test fun goUsesOneHorizontalVowelBelowTheProductionInitial() {
        val go = KoreanCurriculum.lessons.single { it.id == LessonId.GO }
        val geometry = WritingCanvasGeometry.glyph(go, 1962f, 954f)

        assertTrue(go.strokeCount == 3)
        assertTrue(go.strokeDirections.drop(1) == listOf(StrokeDirection.RIGHT, StrokeDirection.UP))
        assertTrue(geometry.strokes.first().maxOf { it.y } < geometry.strokes.drop(1).flatten().maxOf { it.y })
    }

    @Test fun gyoUsesTwoUpwardVowelStrokesBelowTheProductionInitial() {
        val gyo = KoreanCurriculum.lessons.single { it.id == LessonId.GYO }
        val geometry = WritingCanvasGeometry.glyph(gyo, 1962f, 954f)

        assertTrue(gyo.strokeCount == 4)
        assertTrue(gyo.strokeDirections.drop(1) == listOf(
            StrokeDirection.RIGHT,
            StrokeDirection.UP,
            StrokeDirection.UP,
        ))
        assertTrue(geometry.strokes.first().maxOf { it.y } < geometry.strokes.drop(1).flatten().maxOf { it.y })
        assertTrue(geometry.strokes.drop(2).map { it.first().x }.distinct().size == 2)
    }

    @Test fun guUsesOneDownwardVowelStrokeBelowTheProductionInitial() {
        val gu = KoreanCurriculum.lessons.single { it.id == LessonId.GU }
        val geometry = WritingCanvasGeometry.glyph(gu, 1962f, 954f)

        assertTrue(gu.strokeCount == 3)
        assertTrue(gu.strokeDirections.drop(1) == listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN))
        assertTrue(geometry.strokes.first().maxOf { it.y } < geometry.strokes.drop(1).flatten().maxOf { it.y })
        assertTrue(geometry.strokes.last().first().y < geometry.strokes.last().last().y)
    }
}
