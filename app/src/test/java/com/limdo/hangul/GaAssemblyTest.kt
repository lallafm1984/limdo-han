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

    @Test fun targetsKeepGaThroughGiAndNaNeoNyeoAsDistinctProductionLessons() {
        assertTrue(GaAssemblyTarget.GA.lessonId == LessonId.GA)
        assertTrue(GaAssemblyTarget.GEO.lessonId == LessonId.GEO)
        assertTrue(GaAssemblyTarget.GYEO.lessonId == LessonId.GYEO)
        assertTrue(GaAssemblyTarget.GO.lessonId == LessonId.GO)
        assertTrue(GaAssemblyTarget.GYO.lessonId == LessonId.GYO)
        assertTrue(GaAssemblyTarget.GU.lessonId == LessonId.GU)
        assertTrue(GaAssemblyTarget.GYU.lessonId == LessonId.GYU)
        assertTrue(GaAssemblyTarget.GEU.lessonId == LessonId.GEU)
        assertTrue(GaAssemblyTarget.GI.lessonId == LessonId.GI)
        assertTrue(GaAssemblyTarget.NA.lessonId == LessonId.NA)
        assertTrue(GaAssemblyTarget.NEO.lessonId == LessonId.NEO)
        assertTrue(GaAssemblyTarget.NYEO.lessonId == LessonId.NYEO)
        assertTrue(GaAssemblyTarget.entries.map { it.glyph } == listOf("가", "거", "겨", "고", "교", "구", "규", "그", "기", "나", "너", "\uB140"))
        assertFalse(GaAssemblyTarget.GA.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GO.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GYO.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GU.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GYU.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.GEU.isHorizontalVowel)
        assertFalse(GaAssemblyTarget.GI.isHorizontalVowel)
        assertFalse(GaAssemblyTarget.NA.isHorizontalVowel)
        assertFalse(GaAssemblyTarget.NEO.isHorizontalVowel)
        assertFalse(GaAssemblyTarget.NYEO.isHorizontalVowel)
        assertTrue(GaAssemblyTarget.NA.initialName == "니은")
        assertTrue(GaAssemblyTarget.NA.initialStrokeCount == 1)
        assertTrue(GaAssemblyTarget.NEO.initialName == "니은")
        assertTrue(GaAssemblyTarget.NEO.initialStrokeCount == 1)
        assertTrue(GaAssemblyTarget.NYEO.initialName == "니은")
        assertTrue(GaAssemblyTarget.NYEO.initialStrokeCount == 1)
    }

    @Test fun neoUsesTheProductionNieunAndLeftwardEoStrokes() {
        val neo = KoreanCurriculum.lessons.single { it.id == LessonId.NEO }
        val geometry = WritingCanvasGeometry.glyph(neo, 1962f, 954f)

        assertTrue(neo.strokeCount == 3)
        assertTrue(neo.strokeDirections.drop(1) == listOf(StrokeDirection.DOWN, StrokeDirection.LEFT))
        assertTrue(geometry.strokes.take(GaAssemblyTarget.NEO.initialStrokeCount).size == 1)
        assertTrue(geometry.strokes.drop(GaAssemblyTarget.NEO.initialStrokeCount).size == 2)
        assertTrue(geometry.strokes.first().maxOf { it.x } < geometry.strokes.drop(1).flatten().maxOf { it.x })
        assertTrue(geometry.strokes.last().first().x > geometry.strokes.last().last().x)
    }

    @Test fun nyeoUsesTheProductionNieunAndTwoLeftwardYeoStrokes() {
        val nyeo = KoreanCurriculum.lessons.single { it.id == LessonId.NYEO }
        val geometry = WritingCanvasGeometry.glyph(nyeo, 1962f, 954f)

        assertTrue(nyeo.strokeCount == 4)
        assertTrue(nyeo.strokeDirections.drop(1) == listOf(
            StrokeDirection.DOWN,
            StrokeDirection.LEFT,
            StrokeDirection.LEFT,
        ))
        assertTrue(geometry.strokes.take(GaAssemblyTarget.NYEO.initialStrokeCount).size == 1)
        assertTrue(geometry.strokes.drop(GaAssemblyTarget.NYEO.initialStrokeCount).size == 3)
        assertTrue(geometry.strokes.first().maxOf { it.x } < geometry.strokes.drop(1).flatten().maxOf { it.x })
        assertTrue(geometry.strokes.drop(2).all { it.first().x > it.last().x })
        assertTrue(geometry.strokes.drop(2).map { it.first().y }.distinct().size == 2)
    }

    @Test fun naUsesOneInitialStrokeAndTwoAStrokesFromProductionGeometry() {
        val na = KoreanCurriculum.lessons.single { it.id == LessonId.NA }
        val geometry = WritingCanvasGeometry.glyph(na, 1962f, 954f)

        assertTrue(na.strokeCount == 3)
        assertTrue(geometry.strokes.take(GaAssemblyTarget.NA.initialStrokeCount).size == 1)
        assertTrue(geometry.strokes.drop(GaAssemblyTarget.NA.initialStrokeCount).size == 2)
        assertTrue(geometry.strokes.first().maxOf { it.x } < geometry.strokes.drop(1).flatten().maxOf { it.x })
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

    @Test fun gyuUsesTwoDownwardVowelStrokesBelowTheProductionInitial() {
        val gyu = KoreanCurriculum.lessons.single { it.id == LessonId.GYU }
        val geometry = WritingCanvasGeometry.glyph(gyu, 1962f, 954f)

        assertTrue(gyu.strokeCount == 4)
        assertTrue(gyu.strokeDirections.drop(1) == listOf(
            StrokeDirection.RIGHT,
            StrokeDirection.DOWN,
            StrokeDirection.DOWN,
        ))
        assertTrue(geometry.strokes.first().maxOf { it.y } < geometry.strokes.drop(1).flatten().maxOf { it.y })
        assertTrue(geometry.strokes.drop(2).map { it.first().x }.distinct().size == 2)
        assertTrue(geometry.strokes.drop(2).all { it.first().y < it.last().y })
    }

    @Test fun geuUsesOnlyOneHorizontalVowelBelowTheProductionInitial() {
        val geu = KoreanCurriculum.lessons.single { it.id == LessonId.GEU }
        val geometry = WritingCanvasGeometry.glyph(geu, 1962f, 954f)

        assertTrue(geu.strokeCount == 2)
        assertTrue(geu.strokeDirections.drop(1) == listOf(StrokeDirection.RIGHT))
        assertTrue(geometry.strokes.first().maxOf { it.y } < geometry.strokes.last().minOf { it.y })
        assertTrue(geometry.strokes.last().first().y == geometry.strokes.last().last().y)
    }

    @Test fun giUsesOnlyOneVerticalVowelRightOfTheProductionInitial() {
        val gi = KoreanCurriculum.lessons.single { it.id == LessonId.GI }
        val geometry = WritingCanvasGeometry.glyph(gi, 1962f, 954f)

        assertTrue(gi.strokeCount == 2)
        assertTrue(gi.strokeDirections.drop(1) == listOf(StrokeDirection.DOWN))
        assertTrue(geometry.strokes.first().maxOf { it.x } < geometry.strokes.last().minOf { it.x })
        assertTrue(geometry.strokes.last().first().x == geometry.strokes.last().last().x)
    }
}
