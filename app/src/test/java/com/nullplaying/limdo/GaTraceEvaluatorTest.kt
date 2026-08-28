package com.nullplaying.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GaTraceEvaluatorTest {
    private val width = 1_962f
    private val height = 775f
    private val strokes = WritingCanvasGeometry.ga(width, height).strokes

    @Test
    fun productionGaStrokesSucceedOnlyAtTheirOwnIndex() {
        strokes.forEachIndexed { index, points ->
            assertEquals(
                GieokTraceResult.SUCCESS,
                GaTraceEvaluator.evaluateStroke(width, height, index, StrokePath(points)),
            )
        }
        assertEquals(
            GieokTraceResult.WRONG_START,
            GaTraceEvaluator.evaluateStroke(width, height, 0, StrokePath(strokes[1])),
        )
    }

    @Test
    fun reverseDirectionRequestsRetry() {
        val target = strokes[1]
        val start = target.first()
        assertEquals(
            GieokTraceResult.WRONG_DIRECTION,
            GaTraceEvaluator.evaluateStroke(
                width,
                height,
                1,
                StrokePath(
                    listOf(
                        start,
                        CanvasPoint(start.x, start.y - 240f),
                        start,
                    ),
                ),
            ),
        )
    }

    @Test
    fun guideDepartureRequestsRetry() {
        val target = strokes[2]
        val forward = CanvasPoint(target.first().x + 90f, target.first().y)
        assertEquals(
            GieokTraceResult.OFF_GUIDE,
            GaTraceEvaluator.evaluateStroke(
                width,
                height,
                2,
                StrokePath(
                    listOf(
                        target.first(),
                        forward,
                        CanvasPoint(forward.x, forward.y + 300f),
                        target.last(),
                    ),
                ),
            ),
        )
    }

    @Test
    fun unfinishedStrokeRequestsRetry() {
        val target = strokes[0]
        assertEquals(
            GieokTraceResult.INCOMPLETE,
            GaTraceEvaluator.evaluateStroke(width, height, 0, StrokePath(listOf(target.first(), target[1]))),
        )
    }

    @Test
    fun oldVerticalGaInitialNoLongerSucceeds() {
        val target = strokes[0]
        val oldVertical = StrokePath(
            listOf(
                target.first(),
                target[1],
                CanvasPoint(target[1].x, target.last().y),
            ),
        )

        assertNotEquals(
            GieokTraceResult.SUCCESS,
            GaTraceEvaluator.evaluateStroke(width, height, 0, oldVertical),
        )
    }

    @Test
    fun sharedChildToleranceAcceptsSmallStartCorridorAndFinishErrors() {
        val glyph = WritingCanvasGeometry.glyph(EuLesson, width, height)
        val target = glyph.strokes.single()
        val em = glyph.emSize

        val easedStart = listOf(
            CanvasPoint(target.first().x, target.first().y + em * 0.20f),
            target.last(),
        )
        assertEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(EuLesson, width, height, 0, StrokePath(easedStart)),
        )

        val middle = CanvasPoint(
            x = (target.first().x + target.last().x) / 2f,
            y = target.first().y + em * 0.19f,
        )
        assertEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(
                EuLesson,
                width,
                height,
                0,
                StrokePath(listOf(target.first(), middle, target.last())),
            ),
        )

        val easedFinish = CanvasPoint(target.last().x - em * 0.22f, target.last().y)
        assertEquals(
            GieokTraceResult.SUCCESS,
            LessonTraceEvaluator.evaluateStroke(
                EuLesson,
                width,
                height,
                0,
                StrokePath(listOf(target.first(), easedFinish)),
            ),
        )
    }

    @Test
    fun productionUsesOneBoundedChildToleranceContract() {
        assertEquals(0.22f, ChildTraceToleranceSpec.START_TOLERANCE_EM)
        assertEquals(0.20f, ChildTraceToleranceSpec.CORRIDOR_TOLERANCE_EM)
        assertEquals(0.24f, ChildTraceToleranceSpec.FINISH_TOLERANCE_EM)
        assertEquals(0.14f, ChildTraceToleranceSpec.DIRECTION_SAMPLE_DISTANCE_EM)
        assertEquals(-0.25f, ChildTraceToleranceSpec.STRONG_REVERSE_DOT_LIMIT)
        assertEquals(0.35f, ChildTraceToleranceSpec.MAX_OFF_GUIDE_FRACTION)
    }
}
