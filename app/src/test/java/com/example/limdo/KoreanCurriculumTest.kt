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
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.CONSONANTS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.VOWELS,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.OPEN_SYLLABLES,
                CurriculumStage.FINAL_CONSONANTS,
                CurriculumStage.FINAL_CONSONANTS,
                CurriculumStage.FINAL_CONSONANTS,
            ),
            KoreanCurriculum.lessons.map(LessonSpec::stage),
        )
    }

    @Test
    fun firstCurriculumTeachesComponentsBeforeTheirCombinations() {
        assertEquals(
            listOf("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ", "ㅏ", "ㅐ", "ㅑ", "ㅓ", "ㅕ", "ㅗ", "ㅛ", "ㅜ", "ㅠ", "ㅡ", "ㅣ", "가", "나", "다", "라", "마", "바", "사", "아", "자", "차", "카", "타", "파", "하", "각", "간", "갇"),
            KoreanCurriculum.lessons.map(LessonSpec::glyph),
        )
        assertEquals(0, KoreanCurriculum.nextIndex(KoreanCurriculum.lessons.lastIndex))
        assertEquals(1, KoreanCurriculum.nextIndex(0))
    }

    @Test
    fun firstFinalConsonantsKeepGaAboveAndDistinctJongseongBelow() {
        val finalLessons = listOf(
            LessonId.GAK to 4,
            LessonId.GAN to 4,
            LessonId.GAT to 5,
        ).map { (id, strokeCount) ->
            KoreanCurriculum.lessons.single { it.id == id }.also {
                assertEquals(CurriculumStage.FINAL_CONSONANTS, it.stage)
                assertEquals(strokeCount, it.strokeCount)
            }
        }
        val geometries = finalLessons.map { WritingCanvasGeometry.glyph(it, width, height) }
        geometries.forEach { geometry ->
            val top = geometry.strokes.take(3)
            val bottom = geometry.strokes.drop(3)
            assertTrue(top.flatten().maxOf { it.y } < bottom.flatten().minOf { it.y })
            assertEquals(top, geometries.first().strokes.take(3))
        }
        assertNotEquals(geometries[0].strokes.drop(3), geometries[1].strokes.drop(3))
        assertNotEquals(geometries[1].strokes.drop(3), geometries[2].strokes.drop(3))
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
                    StrokeDirection.UP -> assertTrue(dy < 0f && abs(dy) > abs(dx))
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
    fun correctedGlyphsFollowOneSystemSansEducationalShapeContract() {
        val lessons = listOf(
            LessonId.BIEUP to 4,
            LessonId.SIOT to 2,
            LessonId.AE to 3,
            LessonId.JIEUT to 3,
            LessonId.CHIEUT to 4,
            LessonId.TIEUT to 3,
            LessonId.PIEUP to 4,
            LessonId.HIEUH to 3,
            LessonId.JA to 5,
            LessonId.CHA to 6,
            LessonId.TA to 5,
            LessonId.PA to 6,
            LessonId.HA to 5,
        ).associate { (id, expectedStrokes) ->
            val lesson = KoreanCurriculum.lessons.single { it.id == id }
            assertEquals(expectedStrokes, lesson.strokeCount)
            id to WritingCanvasGeometry.glyph(lesson, width, height)
        }

        val bieup = lessons.getValue(LessonId.BIEUP).strokes
        assertTrue(bieup[0].first().y < bieup[2].first().y)
        assertEquals(bieup[0].last().y, bieup[3].first().y)
        assertEquals(bieup[1].last().y, bieup[3].last().y)

        val siot = lessons.getValue(LessonId.SIOT).strokes
        siot.forEach { leg ->
            assertTrue(leg.size >= 5)
            assertTrue(abs(leg[1].x - leg[0].x) < abs(leg[1].y - leg[0].y))
        }

        val ae = lessons.getValue(LessonId.AE).strokes
        assertTrue(ae[0].last().y > ae[0].first().y)
        assertTrue(ae[1].last().x > ae[1].first().x)
        assertTrue(ae[2].last().y > ae[2].first().y)

        val jieut = lessons.getValue(LessonId.JIEUT).strokes
        assertTrue(jieut[0].last().x > jieut[0].first().x)
        jieut.drop(1).forEach { leg ->
            assertTrue(leg.size >= 5)
            assertTrue(leg.last().y > leg.first().y)
        }

        listOf(LessonId.CHIEUT, LessonId.CHA).forEach { id ->
            val chieut = lessons.getValue(id).strokes
            val shortTop = chieut[0]
            val longBar = chieut[1]
            assertTrue(shortTop.last().x - shortTop.first().x < longBar.last().x - longBar.first().x)
            assertTrue(chieut[2].last().y > chieut[2].first().y)
            assertTrue(chieut[3].last().y > chieut[3].first().y)
        }

        listOf(LessonId.TIEUT, LessonId.TA).forEach { id ->
            val tieut = lessons.getValue(id).strokes
            assertTrue(tieut[0].last().x > tieut[0].first().x)
            assertTrue(tieut[1].last().x > tieut[1].first().x)
            assertEquals(tieut[0].first(), tieut[2].first())
            assertEquals(3, tieut[2].size)
            assertTrue(tieut[2][1].y > tieut[2][0].y)
            assertTrue(tieut[2][2].x > tieut[2][1].x)
        }

        listOf(LessonId.PIEUP, LessonId.PA).forEach { id ->
            val pieup = lessons.getValue(id).strokes
            assertTrue(pieup[0].last().x > pieup[0].first().x)
            assertTrue(pieup[1].last().x > pieup[1].first().x)
            assertTrue(pieup[2].last().y > pieup[2].first().y)
            assertTrue(pieup[3].last().y > pieup[3].first().y)
        }

        listOf(LessonId.HIEUH, LessonId.HA).forEach { id ->
            val hieuh = lessons.getValue(id).strokes
            assertTrue(hieuh[0].last().x - hieuh[0].first().x < hieuh[1].last().x - hieuh[1].first().x)
            assertTrue(hieuh[2].first().y > hieuh[1].first().y)
            assertEquals(hieuh[2].first(), hieuh[2].last())
        }
    }

    @Test
    fun everyLessonRejectsAnOppositeFirstMovement() {
        KoreanCurriculum.lessons.forEach { lesson ->
            val geometry = WritingCanvasGeometry.glyph(lesson, width, height)
            val target = geometry.strokes.first()
            val start = target.first()
            val next = target[1]
            val dx = next.x - start.x
            val dy = next.y - start.y
            val length = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
            val reverse = CanvasPoint(
                x = start.x - (dx / length) * geometry.emSize * 0.15f,
                y = start.y - (dy / length) * geometry.emSize * 0.15f,
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
                LessonId.AE,
                LessonId.GIEOK,
                LessonId.NIEUN,
                LessonId.DIGEUT,
                LessonId.RIEUL,
                LessonId.MIEUM,
                LessonId.BIEUP,
                LessonId.SIOT,
                LessonId.IEUNG,
                LessonId.JIEUT,
                LessonId.CHIEUT,
                LessonId.KIEUK,
                LessonId.TIEUT,
                LessonId.PIEUP,
                LessonId.HIEUH,
                LessonId.YA,
                LessonId.EO,
                LessonId.YEO,
                LessonId.O,
                LessonId.YO,
                LessonId.U,
                LessonId.YU,
                LessonId.EU,
                LessonId.I,
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
