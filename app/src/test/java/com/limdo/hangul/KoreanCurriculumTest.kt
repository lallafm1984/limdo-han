package com.limdo.hangul

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.hypot

class KoreanCurriculumTest {
    private val width = 1_962f
    private val height = 775f
    private val safeInset = 24f

    @Test
    fun basicJamoMatchTheTwentyFourEntryReferenceStrokeMatrix() {
        val reference = linkedMapOf(
            "ㄱ" to listOf(StrokeDirection.RIGHT),
            "ㄴ" to listOf(StrokeDirection.DOWN),
            "ㄷ" to listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㄹ" to listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅁ" to listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT, StrokeDirection.RIGHT),
            "ㅂ" to listOf(StrokeDirection.DOWN, StrokeDirection.DOWN, StrokeDirection.RIGHT, StrokeDirection.RIGHT),
            "ㅅ" to listOf(StrokeDirection.DOWN, StrokeDirection.DOWN),
            "ㅇ" to listOf(StrokeDirection.LEFT),
            "ㅈ" to listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅊ" to listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅋ" to listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT),
            "ㅌ" to listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅍ" to listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN, StrokeDirection.DOWN, StrokeDirection.RIGHT),
            "ㅎ" to listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.LEFT),
            "ㅏ" to listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT),
            "ㅑ" to listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT, StrokeDirection.RIGHT),
            "ㅓ" to listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅕ" to listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅗ" to listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT),
            "ㅛ" to listOf(StrokeDirection.DOWN, StrokeDirection.DOWN, StrokeDirection.RIGHT),
            "ㅜ" to listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN),
            "ㅠ" to listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN, StrokeDirection.DOWN),
            "ㅡ" to listOf(StrokeDirection.RIGHT),
            "ㅣ" to listOf(StrokeDirection.DOWN),
        )
        val lessons = KoreanCurriculum.lessons.filter {
            it.stage == CurriculumStage.CONSONANTS ||
                (it.stage == CurriculumStage.VOWELS && it.id != LessonId.AE)
        }

        assertEquals(reference.keys.toList(), lessons.map { it.glyph })
        lessons.forEach { lesson ->
            val expectedDirections = requireNotNull(reference[lesson.glyph])
            val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes
            assertEquals("${lesson.glyph} 획수", expectedDirections.size, lesson.strokeCount)
            assertEquals("${lesson.glyph} geometry 획수", expectedDirections.size, strokes.size)
            assertEquals("${lesson.glyph} 참고 방향", expectedDirections, lesson.strokeDirections)
            assertEquals("${lesson.glyph} 초기 음성", lesson.initialCue, SpokenCueModel.forResult(null, lesson = lesson))

            strokes.forEachIndexed { strokeIndex, stroke ->
                assertEquals(
                    "${lesson.glyph} ${strokeIndex + 1}획 정방향",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        strokeIndex,
                        StrokePath(stroke),
                    ),
                )
                assertNotEquals(
                    "${lesson.glyph} ${strokeIndex + 1}획 역방향",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        strokeIndex,
                        StrokePath(stroke.reversed()),
                    ),
                )
            }

            if (strokes.size > 1) {
                assertNotEquals(
                    "${lesson.glyph} 잘못된 첫 획 순서",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        0,
                        StrokePath(strokes[1]),
                    ),
                )
            }
        }
    }

    @Test
    fun correctedVowelsMatchTheReferenceStrokeOrderAndJunctions() {
        fun strokes(id: LessonId) = WritingCanvasGeometry.glyph(
            KoreanCurriculum.lessons.single { it.id == id },
            width,
            height,
        ).strokes

        val eo = strokes(LessonId.EO)
        assertEquals(eo[0].last().x, eo[1].first().x, 0.01f)
        assertTrue(eo[0].last().y in eo[1].first().y..eo[1].last().y)
        assertTrue(eo[0].last().x > eo[0].first().x)
        assertTrue(eo[1].last().y > eo[1].first().y)

        val yeo = strokes(LessonId.YEO)
        assertTrue(yeo[0].last().x > yeo[0].first().x)
        assertTrue(yeo[1].last().x > yeo[1].first().x)
        assertTrue(yeo[2].last().y > yeo[2].first().y)
        assertEquals(yeo[0].last().x, yeo[2].first().x, 0.01f)
        assertEquals(yeo[1].last().x, yeo[2].first().x, 0.01f)

        val o = strokes(LessonId.O)
        assertTrue(o[0].last().y > o[0].first().y)
        assertTrue(o[1].last().x > o[1].first().x)
        assertTrue(o[0].last().x in o[1].first().x..o[1].last().x)
        assertEquals(o[0].last().y, o[1].first().y, 0.01f)

        val yo = strokes(LessonId.YO)
        assertTrue(yo[0].last().y > yo[0].first().y)
        assertTrue(yo[1].last().y > yo[1].first().y)
        assertTrue(yo[2].last().x > yo[2].first().x)
        assertEquals(yo[0].last().y, yo[2].first().y, 0.01f)
        assertEquals(yo[1].last().y, yo[2].first().y, 0.01f)
    }

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
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
                CurriculumStage.SYLLABLE_STRUCTURE,
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
            listOf("ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ", "ㅏ", "ㅐ", "ㅑ", "ㅓ", "ㅕ", "ㅗ", "ㅛ", "ㅜ", "ㅠ", "ㅡ", "ㅣ", "가", "나", "너", "\uB140", "노", "뇨", "누", "뉴", "느", "다", "갸", "거", "겨", "고", "교", "구", "규", "그", "기", "라", "마", "바", "사", "아", "자", "차", "카", "타", "파", "하", "각", "간", "갇", "갈", "감", "갑"),
            KoreanCurriculum.lessons.map(LessonSpec::glyph),
        )
        assertEquals(0, KoreanCurriculum.nextIndex(KoreanCurriculum.lessons.lastIndex))
        assertEquals(1, KoreanCurriculum.nextIndex(0))
    }

    @Test
    fun firstGieokVowelRowKeepsOneInitialAndSwitchesVerticalAndHorizontalLayouts() {
        val ids = listOf(
            LessonId.GYA, LessonId.GEO, LessonId.GYEO, LessonId.GO, LessonId.GYO,
            LessonId.GU, LessonId.GYU, LessonId.GEU, LessonId.GI,
        )
        val lessons = ids.map { id -> KoreanCurriculum.lessons.single { it.id == id } }
        assertEquals(listOf("갸", "거", "겨", "고", "교", "구", "규", "그", "기"), lessons.map { it.glyph })
        assertTrue(KoreanCurriculum.lessons.indexOf(DaLesson) + 1 == KoreanCurriculum.lessons.indexOf(GyaLesson))
        lessons.forEach { assertEquals(CurriculumStage.OPEN_SYLLABLES, it.stage) }

        val geometries = lessons.associate { it.id to WritingCanvasGeometry.glyph(it, width, height) }
        val vertical = listOf(LessonId.GYA, LessonId.GEO, LessonId.GYEO, LessonId.GI)
        val horizontal = listOf(LessonId.GO, LessonId.GYO, LessonId.GU, LessonId.GYU, LessonId.GEU)
        vertical.map { geometries.getValue(it).strokes.first() }.zipWithNext().forEach { (a, b) -> assertEquals(a, b) }
        horizontal.map { geometries.getValue(it).strokes.first() }.zipWithNext().forEach { (a, b) -> assertEquals(a, b) }
        vertical.forEach { id ->
            val strokes = geometries.getValue(id).strokes
            assertTrue(strokes.first().flattenXMax() < strokes.drop(1).flatten().maxOf { it.x })
        }
        horizontal.forEach { id ->
            val strokes = geometries.getValue(id).strokes
            assertTrue(strokes.first().maxOf { it.y } < strokes.drop(1).flatten().maxOf { it.y })
        }
    }

    private fun List<CanvasPoint>.flattenXMax(): Float = maxOf { it.x }

    @Test
    fun firstFinalConsonantsRecomposeInitialMedialAndFinalByClosedSyllableShape() {
        val finalLessons = listOf(
            LessonId.GAK to 4,
            LessonId.GAN to 4,
            LessonId.GAT to 5,
            LessonId.GAL to 6,
            LessonId.GAM to 6,
            LessonId.GAP to 7,
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
            assertEquals(geometry.emSize * 0.12f, geometry.strokeWidth, 0.001f)
            assertTrue(
                bottom.flatten().minOf { it.y } - top.flatten().maxOf { it.y } >=
                    geometry.strokeWidth,
            )
        }

        val openGa = WritingCanvasGeometry.ga(width, height)
        val gal = geometries[3]
        val openInitial = openGa.strokes.first()
        val galInitial = gal.strokes.first()
        assertTrue(galInitial.maxOf { it.y } - galInitial.minOf { it.y } < openInitial.maxOf { it.y } - openInitial.minOf { it.y })
        assertTrue(galInitial.maxOf { it.x } - galInitial.minOf { it.x } < openInitial.maxOf { it.x } - openInitial.minOf { it.x })
        assertNotEquals(openGa.strokes.take(3), gal.strokes.take(3))

        val gakTop = geometries[0].strokes.take(3)
        val ganTop = geometries[1].strokes.take(3)
        val galTop = geometries[3].strokes.take(3)
        assertNotEquals(gakTop, ganTop)
        assertNotEquals(gakTop, galTop)

        val galFinal = gal.strokes.drop(3).flatten()
        assertEquals(gal.emSize * 0.60f, galFinal.maxOf { it.x } - galFinal.minOf { it.x }, 0.001f)
        assertEquals(gal.emSize * 0.26f, galFinal.maxOf { it.y } - galFinal.minOf { it.y }, 0.001f)
        assertEquals(gal.emSize * 0.14f, galFinal.minOf { it.y } - galTop.flatten().maxOf { it.y }, 0.001f)

        val board = WritingCanvasGeometry.learningBoard(width, height)
        val inkHalf = gal.strokeWidth / 2f
        val inkLeft = gal.strokes.flatten().minOf { it.x } - inkHalf
        val inkTop = gal.strokes.flatten().minOf { it.y } - inkHalf
        val inkRight = gal.strokes.flatten().maxOf { it.x } + inkHalf
        val inkBottom = gal.strokes.flatten().maxOf { it.y } + inkHalf
        assertTrue(inkLeft >= board.left + gal.emSize * 0.04f)
        assertTrue(inkTop >= board.top + gal.emSize * 0.04f)
        assertTrue(inkRight <= board.right - gal.emSize * 0.04f)
        assertTrue(inkBottom <= board.bottom - gal.emSize * 0.04f)
        assertTrue(abs((inkRight - inkLeft) - (inkBottom - inkTop)) <= gal.emSize * 0.03f)

        assertNotEquals(geometries[0].strokes.drop(3), geometries[1].strokes.drop(3))
        assertNotEquals(geometries[1].strokes.drop(3), geometries[2].strokes.drop(3))
    }

    @Test
    fun everyLessonUsesItsDeclaredEducationalStrokeCountAndFirstDirection() {
        KoreanCurriculum.lessons.forEach { lesson ->
            val geometry = WritingCanvasGeometry.glyph(lesson, width, height)
            assertEquals(lesson.strokeCount, geometry.strokes.size)
            assertEquals(lesson.strokeCount, lesson.strokeDirections.size)
            val minimumStrokeFraction = if (lesson.stage == CurriculumStage.FINAL_CONSONANTS) 0.12f else 0.20f
            assertTrue(geometry.strokeWidth >= geometry.emSize * minimumStrokeFraction)

            geometry.strokes.zip(lesson.strokeDirections).forEach { (stroke, direction) ->
                val dx = stroke[1].x - stroke[0].x
                val dy = stroke[1].y - stroke[0].y
                when (direction) {
                    StrokeDirection.RIGHT -> assertTrue(dx > 0f && abs(dx) > abs(dy))
                    StrokeDirection.LEFT -> assertTrue(dx < 0f && abs(dx) > abs(dy))
                    StrokeDirection.UP -> assertTrue(dy < 0f && abs(dy) > abs(dx))
                    StrokeDirection.DOWN -> assertTrue(dy > 0f && abs(dy) >= abs(dx) * 0.75f)
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
            LessonId.JIEUT to 2,
            LessonId.CHIEUT to 3,
            LessonId.TIEUT to 3,
            LessonId.PIEUP to 4,
            LessonId.HIEUH to 3,
            LessonId.JA to 4,
            LessonId.CHA to 5,
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
        assertTrue(siot[0].size >= 5)
        assertTrue(abs(siot[0][1].x - siot[0][0].x) < abs(siot[0][1].y - siot[0][0].y))
        assertTrue(siot[1].size >= 4)
        assertTrue(siot[1][1].x > siot[1][0].x)
        assertTrue(siot[1][1].y > siot[1][0].y)

        val ae = lessons.getValue(LessonId.AE).strokes
        assertTrue(ae[0].last().y > ae[0].first().y)
        assertTrue(ae[1].last().x > ae[1].first().x)
        assertTrue(ae[2].last().y > ae[2].first().y)

        val jieut = lessons.getValue(LessonId.JIEUT).strokes
        assertTrue(jieut[0][1].x > jieut[0][0].x)
        assertTrue(jieut[0].last().x < jieut[0][1].x)
        assertTrue(jieut[0].last().y > jieut[0][1].y)
        assertTrue(jieut[1].last().y > jieut[1].first().y)

        listOf(LessonId.CHIEUT, LessonId.CHA).forEach { id ->
            val chieut = lessons.getValue(id).strokes
            val shortTop = chieut[0]
            val longBar = chieut[1]
            assertTrue(shortTop.last().x - shortTop.first().x < longBar[1].x - longBar[0].x)
            assertTrue(longBar.last().y > longBar.first().y)
            assertTrue(chieut[2].last().y > chieut[2].first().y)
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
            val geometry = lessons.getValue(id)
            val pieup = geometry.strokes
            assertTrue(pieup[0].last().x > pieup[0].first().x)
            assertTrue(pieup[1].last().y > pieup[1].first().y)
            assertTrue(pieup[2].last().y > pieup[2].first().y)
            assertTrue(pieup[3].last().x > pieup[3].first().x)
            if (id == LessonId.PA) {
                assertTrue(pieup[2].first().x - pieup[1].first().x > geometry.strokeWidth)
                assertTrue(pieup[2].last().x - pieup[1].last().x > geometry.strokeWidth)
            }
        }

        listOf(LessonId.HIEUH, LessonId.HA).forEach { id ->
            val hieuh = lessons.getValue(id).strokes
            assertTrue(hieuh[0].last().x - hieuh[0].first().x < hieuh[1].last().x - hieuh[1].first().x)
            assertTrue(hieuh[2].first().y > hieuh[1].first().y)
            assertEquals(49, hieuh[2].size)
            assertEquals(hieuh[2].first(), hieuh[2].last())
            assertTrue(hieuh[2][1].x < hieuh[2][0].x)
            val centerX = (hieuh[2].minOf { it.x } + hieuh[2].maxOf { it.x }) / 2f
            val centerY = (hieuh[2].minOf { it.y } + hieuh[2].maxOf { it.y }) / 2f
            val radii = hieuh[2].dropLast(1).map { hypot(it.x - centerX, it.y - centerY) }
            assertTrue(radii.max() - radii.min() < 0.01f)
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
    fun visuallyDifferentLessonsDoNotReuseOneHardcodedGeometry() {
        val signatures = KoreanCurriculum.lessons.map { lesson ->
            WritingCanvasGeometry.glyph(lesson, width, height).strokes.flatten()
                .joinToString { "${it.x.toInt()},${it.y.toInt()}" }
        }
        assertEquals(signatures.size, signatures.toSet().size)
        assertNotEquals(signatures.first(), signatures.last())
    }

    @Test
    fun digeutRieulAndIeungUseTheCorrectedEducationalGeometry() {
        fun geometry(id: LessonId) = WritingCanvasGeometry.glyph(
            KoreanCurriculum.lessons.single { it.id == id },
            width,
            height,
        )

        listOf(LessonId.DIGEUT, LessonId.DA, LessonId.GAT).forEach { id ->
            val digeut = geometry(id).strokes
            val digeutOffset = if (id == LessonId.GAT) 3 else 0
            assertEquals(digeut[digeutOffset].first(), digeut[digeutOffset + 1].first())
        }

        listOf(LessonId.RIEUL, LessonId.RA).forEach { id ->
            val rieul = geometry(id).strokes
            assertEquals(rieul[0].last().y, rieul[1].first().y, 0.001f)
            assertTrue(rieul[1].last().x > rieul[1].first().x)
            assertEquals(rieul[1].first(), rieul[2].first())
            assertTrue(rieul[2][1].y > rieul[2][0].y)
            assertTrue(rieul[2].last().x > rieul[2][1].x)
        }

        listOf(LessonId.IEUNG, LessonId.AH).forEach { id ->
            val circle = geometry(id).strokes.first()
            assertEquals(49, circle.size)
            assertEquals(circle.first().x, circle.last().x, 0.001f)
            assertEquals(circle.first().y, circle.last().y, 0.001f)
            val centerX = (circle.minOf { it.x } + circle.maxOf { it.x }) / 2f
            val centerY = (circle.minOf { it.y } + circle.maxOf { it.y }) / 2f
            val radii = circle.dropLast(1).map { hypot(it.x - centerX, it.y - centerY) }
            assertTrue(radii.max() - radii.min() < 0.01f)
        }
    }

    @Test
    fun siotJieutChieutFamiliesShareConnectedStrokeDirectionAndJudgmentContract() {
        val lessonById = KoreanCurriculum.lessons.associateBy { it.id }

        listOf(LessonId.SIOT, LessonId.SA).forEach { id ->
            val lesson = lessonById.getValue(id)
            val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes
            val junctionIndex = if (id == LessonId.SA) 6 else 2
            assertEquals(strokes[0][junctionIndex], strokes[1].first())
            assertTrue(strokes[1].first().y > strokes[0].first().y)
            assertTrue(strokes[0].last().x < strokes[0].first().x)
            assertTrue(strokes[1].last().x > strokes[1].first().x)
            assertEquals(
                "${lesson.glyph} 둘째 획의 옛 첫 시작점 거부",
                GieokTraceResult.WRONG_START,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    1,
                    StrokePath(listOf(strokes[0].first()) + strokes[1].drop(1)),
                ),
            )
        }

        listOf(LessonId.JIEUT, LessonId.JA).forEach { id ->
            val lesson = lessonById.getValue(id)
            val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes
            assertEquals(2, lesson.strokeCount - if (id == LessonId.JA) 2 else 0)
            assertEquals(4, strokes[0].size)
            assertEquals(2, strokes[1].size)
            assertTrue(strokes[0][1].x > strokes[0][0].x)
            assertEquals(strokes[0][2], strokes[1].first())
            assertTrue(strokes[0][2].x < strokes[0][1].x)
            assertTrue(strokes[0][3].x < strokes[0][2].x)
            assertTrue(strokes[1].last().x > strokes[1].first().x)
        }

        listOf(LessonId.CHIEUT, LessonId.CHA).forEach { id ->
            val lesson = lessonById.getValue(id)
            val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes
            assertEquals(3, lesson.strokeCount - if (id == LessonId.CHA) 2 else 0)
            assertTrue(strokes[0].last().x > strokes[0].first().x)
            assertEquals(4, strokes[1].size)
            assertEquals(2, strokes[2].size)
            assertTrue(strokes[1][1].x > strokes[1][0].x)
            assertEquals(strokes[1][2], strokes[2].first())
        }

        listOf(LessonId.SIOT, LessonId.SA, LessonId.JIEUT, LessonId.JA, LessonId.CHIEUT, LessonId.CHA)
            .forEach { id ->
                val lesson = lessonById.getValue(id)
                val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes
                val rightLegIndex = when (id) {
                    LessonId.SIOT, LessonId.SA -> 1
                    LessonId.JIEUT, LessonId.JA -> 1
                    else -> 2
                }
                assertEquals(
                    "${lesson.glyph} 오른쪽 사선 정방향",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        rightLegIndex,
                        StrokePath(strokes[rightLegIndex]),
                    ),
                )
                assertEquals(
                    "${lesson.glyph} 오른쪽 사선 역방향",
                    GieokTraceResult.WRONG_START,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        rightLegIndex,
                        StrokePath(strokes[rightLegIndex].reversed()),
                    ),
                )
                assertNotEquals(
                    "${lesson.glyph} 잘못된 첫 획 순서",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        0,
                        StrokePath(strokes[rightLegIndex]),
                    ),
                )
            }
    }

    @Test
    fun saSiotKeepsContinuousEducationalCurvesWithoutDirectionReversal() {
        val lesson = KoreanCurriculum.lessons.single { it.id == LessonId.SA }
        val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes
        val leftLeg = strokes[0]
        val rightLeg = strokes[1]

        assertEquals(13, leftLeg.size)
        assertEquals(5, rightLeg.size)
        assertEquals(leftLeg[6], rightLeg.first())
        assertTrue(leftLeg.zipWithNext().all { (start, end) -> end.y > start.y })
        assertTrue(leftLeg.zipWithNext().all { (start, end) -> end.x <= start.x })
        assertTrue(rightLeg.zipWithNext().all { (start, end) -> end.x > start.x })
        assertTrue(rightLeg.zipWithNext().all { (start, end) -> end.y > start.y })
        val directionCosines = leftLeg.windowed(3).map { (first, middle, last) ->
            val incomingX = middle.x - first.x
            val incomingY = middle.y - first.y
            val outgoingX = last.x - middle.x
            val outgoingY = last.y - middle.y
            val dot = incomingX * outgoingX + incomingY * outgoingY
            dot / (hypot(incomingX, incomingY) * hypot(outgoingX, outgoingY))
        }
        assertTrue(directionCosines.all { cosine -> cosine >= 0.978f })

        listOf(leftLeg, rightLeg).forEachIndexed { index, stroke ->
            assertEquals(
                "사 초성 ${index + 1}획 정방향",
                GieokTraceResult.SUCCESS,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    index,
                    StrokePath(stroke),
                ),
            )
            assertEquals(
                "사 초성 ${index + 1}획 역방향",
                GieokTraceResult.WRONG_START,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    index,
                    StrokePath(stroke.reversed()),
                ),
            )
            val farAway = stroke.map { point -> CanvasPoint(point.x, point.y + height * 0.30f) }
            assertNotEquals(
                "사 초성 ${index + 1}획 큰 이탈",
                GieokTraceResult.SUCCESS,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    index,
                    StrokePath(farAway),
                ),
            )
        }
    }

    @Test
    fun rieulAndRaShareTheThreeStrokeOrderDirectionAndJudgmentContract() {
        listOf(RieulLesson, RaLesson).forEach { lesson ->
            val rieulStrokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes.take(3)
            assertEquals(
                listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.DOWN),
                lesson.strokeDirections.take(3),
            )
            rieulStrokes.forEachIndexed { strokeIndex, points ->
                assertEquals(
                    "${lesson.glyph} ${strokeIndex + 1}획 정방향",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        strokeIndex,
                        StrokePath(points),
                    ),
                )
            }

            val middleReversed = rieulStrokes[1].reversed()
            assertEquals(
                "${lesson.glyph} 가운데 가로 역방향",
                GieokTraceResult.WRONG_START,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    1,
                    StrokePath(middleReversed),
                ),
            )
            assertEquals(
                "${lesson.glyph} 잘못된 첫 획 순서",
                GieokTraceResult.WRONG_START,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    0,
                    StrokePath(rieulStrokes[2]),
                ),
            )
        }
        assertEquals(3, RieulLesson.strokeCount)
        assertEquals(5, RaLesson.strokeCount)
        assertTrue(SpokenCue.INITIAL_RIEUL.utterance.contains("가운데를 오른쪽"))
        assertTrue(SpokenCue.INITIAL_RA.utterance.contains("가운데를 오른쪽"))
    }

    @Test
    fun basicConsonantsShareOneOpticalFrame() {
        val consonantIds = listOf(
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
        )
        val lessons = KoreanCurriculum.lessons.associateBy { it.id }
        val geometries = consonantIds.map { id ->
            WritingCanvasGeometry.glyph(lessons.getValue(id), width, height)
        }
        val reference = geometries.first()
        geometries.forEachIndexed { index, geometry ->
            assertEquals("${consonantIds[index]} em", reference.emSize, geometry.emSize, 0.001f)
            assertEquals("${consonantIds[index]} 굵기", reference.strokeWidth, geometry.strokeWidth, 0.001f)
            val points = geometry.strokes.flatten()
            val spanX = points.maxOf { it.x } - points.minOf { it.x }
            val spanY = points.maxOf { it.y } - points.minOf { it.y }
            val centerX = (points.maxOf { it.x } + points.minOf { it.x }) / 2f
            val centerY = (points.maxOf { it.y } + points.minOf { it.y }) / 2f
            assertTrue("${consonantIds[index]} 가로 점유", spanX in geometry.emSize * 0.50f..geometry.emSize * 0.95f)
            assertTrue("${consonantIds[index]} 세로 점유", spanY in geometry.emSize * 0.60f..geometry.emSize * 0.95f)
            assertTrue("${consonantIds[index]} 가로 중심", abs(centerX - width / 2f) <= geometry.emSize * 0.06f)
            assertTrue("${consonantIds[index]} 세로 중심", abs(centerY - height / 2f) <= geometry.emSize * 0.06f)
        }
    }

    @Test
    fun rightAngleConsonantsShareTheSameVisibleOuterFrame() {
        val lessons = KoreanCurriculum.lessons.associateBy { it.id }
        fun geometry(id: LessonId) = WritingCanvasGeometry.glyph(
            lessons.getValue(id),
            width,
            height,
        )

        val geometries = listOf(
            LessonId.GIEOK,
            LessonId.NIEUN,
            LessonId.DIGEUT,
        ).associateWith(::geometry)
        val reference = geometries.getValue(LessonId.DIGEUT)
        val referencePoints = reference.strokes.flatten()
        val referenceInkWidth =
            referencePoints.maxOf { it.x } - referencePoints.minOf { it.x } + reference.strokeWidth
        val referenceInkHeight =
            referencePoints.maxOf { it.y } - referencePoints.minOf { it.y } + reference.strokeWidth

        geometries.forEach { (id, glyph) ->
            val points = glyph.strokes.flatten()
            val centerlineWidth = points.maxOf { it.x } - points.minOf { it.x }
            val centerlineHeight = points.maxOf { it.y } - points.minOf { it.y }
            val inkWidth = centerlineWidth + glyph.strokeWidth
            val inkHeight = centerlineHeight + glyph.strokeWidth
            assertEquals("$id 중심선 가로", glyph.emSize * 0.76f, centerlineWidth, 0.001f)
            assertEquals("$id 중심선 세로", glyph.emSize * 0.76f, centerlineHeight, 0.001f)
            assertEquals("$id 보이는 가로 외곽", referenceInkWidth, inkWidth, 0.001f)
            assertEquals("$id 보이는 세로 외곽", referenceInkHeight, inkHeight, 0.001f)
            assertEquals(
                "$id 가로 중심",
                width / 2f,
                (points.maxOf { it.x } + points.minOf { it.x }) / 2f,
                0.001f,
            )
            assertEquals(
                "$id 세로 중심",
                height / 2f,
                (points.maxOf { it.y } + points.minOf { it.y }) / 2f,
                0.001f,
            )
        }
    }

    @Test
    fun rieulJieutAndChieutKeepBalancedVisibleExtents() {
        val lessons = KoreanCurriculum.lessons.associateBy { it.id }
        fun geometry(id: LessonId) = WritingCanvasGeometry.glyph(
            lessons.getValue(id),
            width,
            height,
        )

        listOf(LessonId.RIEUL, LessonId.RA).forEach { id ->
            val strokes = geometry(id).strokes.take(3)
            assertEquals("$id 왼쪽 축", strokes[0].first().x, strokes[2].first().x, 0.001f)
            assertEquals("$id 오른쪽 축", strokes[0][1].x, strokes[2].last().x, 0.001f)
            assertEquals("$id 가운데 왼쪽 축", strokes[0].first().x, strokes[1].first().x, 0.001f)
            assertEquals("$id 가운데 오른쪽 축", strokes[0][1].x, strokes[1].last().x, 0.001f)
        }

        listOf(
            Triple(LessonId.JIEUT, 0, 1),
            Triple(LessonId.JA, 0, 1),
            Triple(LessonId.CHIEUT, 1, 2),
            Triple(LessonId.CHA, 1, 2),
        ).forEach { (id, bodyIndex, rightLegIndex) ->
            val strokes = geometry(id).strokes
            val body = strokes[bodyIndex]
            val rightLeg = strokes[rightLegIndex]
            val topLeft = body[0]
            val topRight = body[1]
            val junction = body[2]
            val leftBottom = body[3]
            val rightBottom = rightLeg.last()
            val topWidth = topRight.x - topLeft.x
            val bottomWidth = rightBottom.x - leftBottom.x
            assertEquals("$id 왼쪽 끝", topLeft.x, leftBottom.x, 0.001f)
            assertEquals("$id 오른쪽 끝", topRight.x, rightBottom.x, 0.001f)
            assertEquals("$id 상하 폭", topWidth, bottomWidth, 0.001f)
            assertEquals("$id 접합부 공유", junction, rightLeg.first())
            val incomingX = junction.x - topRight.x
            val incomingY = junction.y - topRight.y
            val wholeX = leftBottom.x - topRight.x
            val wholeY = leftBottom.y - topRight.y
            assertEquals("$id 왼쪽 내려쓰기 공선", 0f, incomingX * wholeY - incomingY * wholeX, 0.01f)
            val leftLegLength = hypot(junction.x - leftBottom.x, junction.y - leftBottom.y)
            val rightLegLength = hypot(rightBottom.x - junction.x, rightBottom.y - junction.y)
            assertEquals("$id 양쪽 다리 길이", leftLegLength, rightLegLength, 0.001f)
        }
    }

    @Test
    fun pieupAndPaShareTheFourStrokeOrderDirectionAndJudgmentContract() {
        listOf(PieupLesson, PaLesson).forEach { lesson ->
            val strokes = WritingCanvasGeometry.glyph(lesson, width, height).strokes.take(4)
            assertEquals(
                listOf(
                    StrokeDirection.RIGHT,
                    StrokeDirection.DOWN,
                    StrokeDirection.DOWN,
                    StrokeDirection.RIGHT,
                ),
                lesson.strokeDirections.take(4),
            )
            strokes.forEachIndexed { strokeIndex, points ->
                assertEquals(
                    "${lesson.glyph} ${strokeIndex + 1}획 정방향",
                    GieokTraceResult.SUCCESS,
                    LessonTraceEvaluator.evaluateStroke(
                        lesson,
                        width,
                        height,
                        strokeIndex,
                        StrokePath(points),
                    ),
                )
            }
            assertEquals(
                "${lesson.glyph} 왼쪽 세로 역방향",
                GieokTraceResult.WRONG_START,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    1,
                    StrokePath(strokes[1].reversed()),
                ),
            )
            assertNotEquals(
                "${lesson.glyph} 아래 가로 선행",
                GieokTraceResult.SUCCESS,
                LessonTraceEvaluator.evaluateStroke(
                    lesson,
                    width,
                    height,
                    0,
                    StrokePath(strokes[3]),
                ),
            )
        }
    }

    @Test
    fun haPreservesHieuhProportionsAndMarkersStayInsideTheGuide() {
        fun geometry(id: LessonId) = WritingCanvasGeometry.glyph(
            KoreanCurriculum.lessons.single { it.id == id },
            width,
            height,
        )

        val hieuh = geometry(LessonId.HIEUH).strokes
        val ha = geometry(LessonId.HA).strokes
        val hieuhCircleWidth = hieuh[2].maxOf { it.x } - hieuh[2].minOf { it.x }
        val haCircleWidth = ha[2].maxOf { it.x } - ha[2].minOf { it.x }
        listOf(0, 1).forEach { strokeIndex ->
            val hieuhWidth = hieuh[strokeIndex].last().x - hieuh[strokeIndex].first().x
            val haWidth = ha[strokeIndex].last().x - ha[strokeIndex].first().x
            assertEquals(hieuhWidth / hieuhCircleWidth, haWidth / haCircleWidth, 0.001f)
        }
        val guideStroke = geometry(LessonId.HA).strokeWidth
        val initialRight = ha.take(3).flatten().maxOf { it.x }
        val medialLeft = ha.drop(3).flatten().minOf { it.x }
        assertTrue(medialLeft - initialRight > guideStroke)
        assertTrue(WritingCanvasGeometry.startMarkerRadius(guideStroke) * 2f <= guideStroke * 0.40f)
        assertTrue(WritingCanvasGeometry.finishMarkerOuterRadius(guideStroke) * 2f <= guideStroke * 0.40f)
        assertTrue(
            WritingCanvasGeometry.demonstrationMarkerOuterRadius(guideStroke) * 2f <=
                guideStroke * 0.28f,
        )
        assertEquals(0.08f, WritingCanvasGeometry.demonstrationMarkerTravelProgress(0f), 0.001f)
        assertEquals(0.50f, WritingCanvasGeometry.demonstrationMarkerTravelProgress(0.5f), 0.001f)
        assertEquals(0.92f, WritingCanvasGeometry.demonstrationMarkerTravelProgress(1f), 0.001f)
        assertTrue(
            WritingCanvasGeometry.finishMarkerCenterRadius(guideStroke) <
                WritingCanvasGeometry.finishMarkerColorRadius(guideStroke),
        )
    }

    @Test
    fun kaCurvesDownLeftWhileKeepingProductionDirectionAndJudgment() {
        val lesson = KoreanCurriculum.lessons.single { it.id == LessonId.KA }
        val geometry = WritingCanvasGeometry.glyph(lesson, width, height)
        val firstStroke = geometry.strokes.first()
        val middleHorizontal = geometry.strokes[1]
        val corner = firstStroke[1]
        val descent = firstStroke.drop(1)
        val crossingSegment = descent.zipWithNext().single { (start, end) ->
            middleHorizontal.last().y in start.y..end.y
        }
        val crossingFraction =
            (middleHorizontal.last().y - crossingSegment.first.y) /
                (crossingSegment.second.y - crossingSegment.first.y)
        val curveXAtMiddleHorizontal = crossingSegment.first.x +
            (crossingSegment.second.x - crossingSegment.first.x) * crossingFraction

        assertEquals(14, firstStroke.size)
        assertEquals(firstStroke.first().y, corner.y, 0.001f)
        assertTrue(firstStroke.last().x < corner.x)
        assertTrue(firstStroke.last().y > corner.y)
        assertTrue(descent.zipWithNext().all { (start, end) -> end.x <= start.x && end.y > start.y })
        assertEquals(curveXAtMiddleHorizontal, middleHorizontal.last().x, 1f)
        assertEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(lesson, width, height, 0, StrokePath(firstStroke)),
        )
        assertNotEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(lesson, width, height, 0, StrokePath(firstStroke.reversed())),
        )
        assertNotEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(lesson, width, height, 0, StrokePath(middleHorizontal)),
        )
        assertNotEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(
                lesson,
                width,
                height,
                0,
                StrokePath(firstStroke.map { point -> point.copy(x = point.x + geometry.strokeWidth * 2f) }),
            ),
        )
    }

    @Test
    fun galFinalRieulKeepsThreeRenderedLayersApart() {
        val gal = WritingCanvasGeometry.glyph(
            KoreanCurriculum.lessons.single { it.id == LessonId.GAL },
            width,
            height,
        )
        val finalRieul = gal.strokes.drop(3)
        val layerCenters = listOf(
            finalRieul[0].first().y,
            finalRieul[1][1].y,
            finalRieul[2].first().y,
        )

        layerCenters.zipWithNext().forEach { (upper, lower) ->
            assertTrue(lower - upper >= gal.strokeWidth)
        }
        assertEquals(finalRieul[0].last(), finalRieul[1].first())
        assertEquals(finalRieul[1].last(), finalRieul[2].first())
    }

}
