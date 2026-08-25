package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class KoreanCurriculumTest {
    private val width = 1_962f
    private val height = 775f
    private val safeInset = 24f

    @Test
    fun educationFlowKeepsTheMinistrySequenceForFutureExpansion() {
        assertEquals(
            listOf(
                CurriculumStage.PENCIL_PREPARATION,
                CurriculumStage.CONSONANTS,
                CurriculumStage.VOWELS,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.FINAL_CONSONANTS,
                CurriculumStage.DOUBLE_FINAL_CONSONANTS,
            ),
            KoreanCurriculum.educationFlow,
        )
        assertEquals(
            listOf(
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.VOWELS,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
            ),
            KoreanCurriculum.lessons.map(LessonSpec::stage),
        )
    }

    @Test
    fun firstCurriculumTeachesComponentsBeforeTheirCombinations() {
        assertEquals(
            listOf("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅏ", "가", "나", "다"),
            KoreanCurriculum.lessons.map(LessonSpec::glyph),
        )
        assertEquals(0, KoreanCurriculum.nextIndex(KoreanCurriculum.lessons.lastIndex))
        assertEquals(1, KoreanCurriculum.nextIndex(0))
    }

    @Test
    fun everyLessonUsesItsDeclaredEducationalStrokeCountAndFirstDirection() {
        KoreanCurriculum.lessons.forEach { lesson ->
            val geometry = WritingCanvasGeometry.glyph(lesson, width, height)
            assertEquals(lesson.strokeCount, geometry.strokes.size)
            assertEquals(lesson.strokeCount, lesson.strokeDirections.size)
            assertTrue(geometry.strokeWidth >= geometry.emSize * 0.20f)

            geometry.strokes.zip(lesson.strokeDirections).forEach { (stroke, direction) ->
                val dx = stroke[1].x - stroke[0].x
                val dy = stroke[1].y - stroke[0].y
                when (direction) {
                    StrokeDirection.RIGHT -> assertTrue(dx > 0f && abs(dx) > abs(dy))
                    StrokeDirection.LEFT -> assertTrue(dx < 0f && abs(dx) > abs(dy))
                    StrokeDirection.DOWN -> assertTrue(dy > 0f && abs(dy) > abs(dx))
                }
            }
        }
    }

    @Test
    fun everyLessonAcceptsItsOwnProductionStrokesInOrder() {
        KoreanCurriculum.lessons.forEach { lesson ->
            val geometry = WritingCanvasGeometry.glyph(lesson, width, height)
            val completed = geometry.strokes.fold(TraceAttempt()) { attempt, target ->
                val drawing = target.drop(1).fold(
                    attempt.start(target.first(), width, height, safeInset),
                ) { current, point ->
                    current.append(point, width, height, safeInset)
                }
                drawing.finish(width, height, lesson)
            }

            assertEquals("${lesson.glyph} 결과", GieokTraceResult.SUCCESS, completed.result)
            assertEquals(lesson.strokeCount, completed.completedStrokes.size)
        }
    }

    @Test
    fun everyLessonRejectsAnOppositeFirstMovement() {
        KoreanCurriculum.lessons.forEach { lesson ->
            val geometry = WritingCanvasGeometry.glyph(lesson, width, height)
            val target = geometry.strokes.first()
            val start = target.first()
            val next = target[1]
            val reverse = CanvasPoint(
                x = start.x - (next.x - start.x) * 0.30f,
                y = start.y - (next.y - start.y) * 0.30f,
            )
            val stroke = StrokePath(listOf(start, reverse))

            assertEquals(
                "${lesson.glyph} 반대 방향",
                GieokTraceResult.WRONG_DIRECTION,
                LessonTraceEvaluator.evaluateStroke(lesson, width, height, 0, stroke),
            )
        }
    }

    @Test
    fun lessonSpeechNamesTheCurrentGlyphAndCoversEveryStrokeDemonstration() {
        KoreanCurriculum.lessons.forEach { lesson ->
            assertEquals(lesson.initialCue, SpokenCueModel.forResult(null, lesson = lesson))
            assertEquals(
                lesson.successCue,
                SpokenCueModel.forResult(GieokTraceResult.SUCCESS, lesson = lesson),
            )
            assertTrue(lesson.initialCue.utterance.isNotBlank())
            assertTrue(lesson.successCue.utterance.contains(lesson.glyph) || lesson.id in setOf(
                LessonId.A,
                LessonId.GIEOK,
                LessonId.NIEUN,
                LessonId.DIGEUT,
                LessonId.RIEUL,
                LessonId.MIEUM,
                LessonId.BIEUP,
                LessonId.SIOT,
                LessonId.IEUNG,
                LessonId.JIEUT,
            ))

            val demonstrated = lesson.initialCue.utterance.indices
                .mapNotNull(lesson.initialCue::demonstrationStrokeIndex)
                .toSet()
            assertEquals((0 until lesson.strokeCount).toSet(), demonstrated)
        }
    }

    @Test
    fun rewardStepsAndSafeOverlaysFollowTheCurrentLesson() {
        KoreanCurriculum.lessons.forEach { lesson ->
            val reward = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, lesson)
            assertEquals(lesson.strokeCount, reward.targetSteps)

            val vehicleCenter = RewardPathGeometry.vehicleCenterX(
                containerWidth = 891.43f,
                containerHeight = 411.43f,
                completedSteps = lesson.strokeCount.toFloat(),
                targetSteps = lesson.strokeCount,
                lesson = lesson,
            )
            val marker = SuccessMarkerGeometry.center(891.43f, 411.43f, lesson)
            assertTrue(vehicleCenter > 0f)
            assertTrue(marker.x in 0f..891.43f)
            assertTrue(marker.y in 0f..411.43f)
        }
    }

    @Test
    fun visuallyDifferentLessonsDoNotReuseOneHardcodedGeometry() {
        val signatures = KoreanCurriculum.lessons.map { lesson ->
            WritingCanvasGeometry.glyph(lesson, width, height).strokes.flatten()
                .joinToString { "${it.x.toInt()},${it.y.toInt()}" }
        }
        assertEquals(signatures.size, signatures.toSet().size)
        assertNotEquals(signatures.first(), signatures.last())
    }
}
