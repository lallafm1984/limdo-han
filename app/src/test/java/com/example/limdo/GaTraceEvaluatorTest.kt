package com.example.limdo

import org.junit.Assert.assertEquals
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
        val start = strokes[1].first()
        assertEquals(
            GieokTraceResult.WRONG_DIRECTION,
            GaTraceEvaluator.evaluateStroke(
                width,
                height,
                1,
                StrokePath(
                    listOf(
                        CanvasPoint(start.x, start.y - 100f),
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
}
