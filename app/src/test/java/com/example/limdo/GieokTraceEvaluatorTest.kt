package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Test

class GieokTraceEvaluatorTest {
    private val width = 1_000f
    private val height = 500f
    private val guide = WritingCanvasGeometry.gieokPoints(width, height)
    private val start = guide[0]
    private val corner = guide[1]
    private val end = guide[2]

    @Test
    fun emptyStrokeHasNamedResult() {
        assertResult(GieokTraceResult.EMPTY, emptyList())
    }

    @Test
    fun strokeMustBeginNearOrangeMarker() {
        assertResult(
            GieokTraceResult.WRONG_START,
            listOf(CanvasPoint(corner.x, corner.y), end),
        )
    }

    @Test
    fun firstMeaningfulMovementMustBeHorizontalAndForward() {
        assertResult(
            GieokTraceResult.WRONG_DIRECTION,
            listOf(start, CanvasPoint(start.x, start.y + 90f), corner, end),
        )
    }

    @Test
    fun substantialGuideDepartureRequestsRetry() {
        assertResult(
            GieokTraceResult.OFF_GUIDE,
            listOf(
                start,
                CanvasPoint(start.x + 55f, start.y),
                corner,
                CanvasPoint(corner.x - 120f, corner.y + 160f),
                CanvasPoint(corner.x - 120f, corner.y + 240f),
                CanvasPoint(corner.x - 120f, corner.y + 310f),
                end,
            ),
        )
    }

    @Test
    fun partialStrokeIsIncomplete() {
        assertResult(
            GieokTraceResult.INCOMPLETE,
            listOf(start, midpoint(start, corner), corner),
        )
    }

    @Test
    fun jitterySparseChildStrokeSucceeds() {
        assertResult(
            GieokTraceResult.SUCCESS,
            listOf(
                CanvasPoint(start.x - 12f, start.y + 8f),
                CanvasPoint(start.x + 75f, start.y - 18f),
                CanvasPoint(corner.x - 38f, corner.y + 14f),
                CanvasPoint(corner.x + 20f, corner.y + 82f),
                CanvasPoint(end.x - 15f, end.y - 10f),
            ),
        )
    }

    @Test
    fun sparseDiagonalOutsideVisibleCorridorRequestsRetry() {
        val actualWidth = 1_962f
        val actualHeight = 775f

        assertEquals(
            GieokTraceResult.OFF_GUIDE,
            GieokTraceEvaluator.evaluate(
                width = actualWidth,
                height = actualHeight,
                stroke = StrokePath(
                    listOf(
                        CanvasPoint(690f, 96f),
                        CanvasPoint(861f, 96f),
                        CanvasPoint(1_011f, 246f),
                        CanvasPoint(1_161f, 246f),
                        CanvasPoint(1_272f, 246f),
                        CanvasPoint(1_272f, 387f),
                        CanvasPoint(1_272f, 679f),
                    ),
                ),
            ),
        )
    }

    @Test
    fun shortSixCrossingBoundaryJitterStillSucceeds() {
        assertActualCanvasResult(
            GieokTraceResult.SUCCESS,
            listOf(
                879f to 159f, 1000f to 159f, 1050f to 270f, 1100f to 295f,
                1150f to 270f, 1200f to 295f, 1250f to 270f, 1300f to 295f,
                1350f to 270f, 1400f to 159f, 1461f to 159f, 1461f to 350f,
                1461f to 550f, 1461f to 742f,
            ),
        )
    }

    @Test
    fun shortEightCrossingBoundaryJitterStillSucceeds() {
        assertActualCanvasResult(
            GieokTraceResult.SUCCESS,
            listOf(
                879f to 159f, 1000f to 159f, 1040f to 270f, 1080f to 295f,
                1120f to 270f, 1160f to 295f, 1200f to 270f, 1240f to 295f,
                1280f to 270f, 1320f to 295f, 1360f to 270f, 1410f to 159f,
                1461f to 159f, 1461f to 350f, 1461f to 550f, 1461f to 742f,
            ),
        )
    }

    @Test
    fun repeatedLargeBoundaryDepartureReportsOffGuide() {
        assertActualCanvasResult(
            GieokTraceResult.OFF_GUIDE,
            listOf(
                879f to 159f, 1000f to 159f, 1050f to 250f, 1100f to 340f,
                1150f to 250f, 1200f to 340f, 1250f to 250f, 1300f to 340f,
                1350f to 250f, 1400f to 159f, 1461f to 159f, 1461f to 350f,
                1461f to 550f, 1461f to 742f,
            ),
        )
    }

    @Test
    fun traceCanBeEvaluatedAtAnotherCanvasSize() {
        val scaledGuide = WritingCanvasGeometry.gieokPoints(width = 2_000f, height = 1_000f)

        assertEquals(
            GieokTraceResult.SUCCESS,
            GieokTraceEvaluator.evaluate(
                width = 2_000f,
                height = 1_000f,
                stroke = StrokePath(scaledGuide),
            ),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun evaluatorRejectsEmptyCanvasBounds() {
        GieokTraceEvaluator.evaluate(
            width = 0f,
            height = height,
            stroke = StrokePath(listOf(start)),
        )
    }

    private fun assertResult(expected: GieokTraceResult, points: List<CanvasPoint>) {
        assertEquals(
            expected,
            GieokTraceEvaluator.evaluate(
                width = width,
                height = height,
                stroke = StrokePath(points),
            ),
        )
    }

    private fun assertActualCanvasResult(
        expected: GieokTraceResult,
        screenPoints: List<Pair<Float, Float>>,
    ) {
        assertEquals(
            expected,
            GieokTraceEvaluator.evaluate(
                width = 1_962f,
                height = 775f,
                stroke = StrokePath(
                    screenPoints.map { (x, y) -> CanvasPoint(x - 189f, y - 63f) },
                ),
            ),
        )
    }

    private fun midpoint(first: CanvasPoint, second: CanvasPoint): CanvasPoint = CanvasPoint(
        x = (first.x + second.x) / 2f,
        y = (first.y + second.y) / 2f,
    )
}
