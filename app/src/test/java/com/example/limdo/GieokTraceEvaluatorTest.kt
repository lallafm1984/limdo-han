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
        val (actualStart, actualCorner, actualEnd) =
            WritingCanvasGeometry.gieokPoints(actualWidth, actualHeight)

        assertEquals(
            GieokTraceResult.OFF_GUIDE,
            GieokTraceEvaluator.evaluate(
                width = actualWidth,
                height = actualHeight,
                stroke = StrokePath(
                    listOf(
                        actualStart,
                        CanvasPoint(actualStart.x + 140f, actualStart.y),
                        CanvasPoint(actualStart.x + 200f, actualStart.y + 220f),
                        CanvasPoint(actualStart.x + 270f, actualStart.y + 220f),
                        CanvasPoint(actualStart.x + 340f, actualStart.y + 220f),
                        CanvasPoint(actualCorner.x - 80f, actualStart.y + 220f),
                        CanvasPoint(actualCorner.x, actualStart.y + 220f),
                        CanvasPoint(actualCorner.x, (actualCorner.y + actualEnd.y) / 2f),
                        actualEnd,
                    ),
                ),
            ),
        )
    }

    @Test
    fun shortSixCrossingBoundaryJitterStillSucceeds() {
        val (actualStart, actualCorner, actualEnd) = actualScreenGuide()
        val horizontalLength = actualCorner.first - actualStart.first
        assertActualCanvasResult(
            GieokTraceResult.SUCCESS,
            listOf(
                actualStart,
                actualStart.first + horizontalLength * 0.20f to actualStart.second,
                actualStart.first + horizontalLength * 0.30f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.40f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.50f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.60f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.70f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.80f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.90f to actualStart.second + 111f,
                actualCorner,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.33f,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.67f,
                actualEnd,
            ),
        )
    }

    @Test
    fun shortEightCrossingBoundaryJitterStillSucceeds() {
        val (actualStart, actualCorner, actualEnd) = actualScreenGuide()
        val horizontalLength = actualCorner.first - actualStart.first
        assertActualCanvasResult(
            GieokTraceResult.SUCCESS,
            listOf(
                actualStart,
                actualStart.first + horizontalLength * 0.18f to actualStart.second,
                actualStart.first + horizontalLength * 0.26f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.34f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.42f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.50f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.58f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.66f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.74f to actualStart.second + 111f,
                actualStart.first + horizontalLength * 0.82f to actualStart.second + 136f,
                actualStart.first + horizontalLength * 0.90f to actualStart.second + 111f,
                actualCorner,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.33f,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.67f,
                actualEnd,
            ),
        )
    }

    @Test
    fun repeatedLargeBoundaryDepartureReportsOffGuide() {
        val (actualStart, actualCorner, actualEnd) = actualScreenGuide()
        val horizontalLength = actualCorner.first - actualStart.first
        assertActualCanvasResult(
            GieokTraceResult.OFF_GUIDE,
            listOf(
                actualStart,
                actualStart.first + horizontalLength * 0.20f to actualStart.second,
                actualStart.first + horizontalLength * 0.30f to actualStart.second + 91f,
                actualStart.first + horizontalLength * 0.40f to actualStart.second + 181f,
                actualStart.first + horizontalLength * 0.50f to actualStart.second + 91f,
                actualStart.first + horizontalLength * 0.60f to actualStart.second + 181f,
                actualStart.first + horizontalLength * 0.70f to actualStart.second + 91f,
                actualStart.first + horizontalLength * 0.80f to actualStart.second + 181f,
                actualStart.first + horizontalLength * 0.90f to actualStart.second + 91f,
                actualCorner,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.33f,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.67f,
                actualEnd,
            ),
        )
    }

    @Test
    fun exactBacktrackBoundarySucceedsAcrossDifferentSampleSpacing() {
        val (actualStart, actualCorner, actualEnd) = actualScreenGuide()
        listOf(0.33f, 0.67f).forEach { penultimateProgress ->
            assertActualCanvasResult(
                GieokTraceResult.SUCCESS,
                listOf(
                    actualStart,
                    (actualStart.first + actualCorner.first) / 2f to actualStart.second,
                    actualCorner,
                    actualCorner.first to
                        actualCorner.second + (actualEnd.second - actualCorner.second) * penultimateProgress,
                    actualEnd,
                    actualEnd.first to actualEnd.second - 155f,
                ),
            )
        }
    }

    @Test
    fun onePixelBeyondBacktrackBoundaryRequestsDirectionRetry() {
        val (actualStart, actualCorner, actualEnd) = actualScreenGuide()
        assertActualCanvasResult(
            GieokTraceResult.WRONG_DIRECTION,
            listOf(
                actualStart,
                (actualStart.first + actualCorner.first) / 2f to actualStart.second,
                actualCorner,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.33f,
                actualCorner.first to actualCorner.second + (actualEnd.second - actualCorner.second) * 0.67f,
                actualEnd,
                actualEnd.first to actualEnd.second - 156f,
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

    private fun actualScreenGuide(): List<Pair<Float, Float>> =
        WritingCanvasGeometry.gieokPoints(width = 1_962f, height = 775f).map { point ->
            point.x + 189f to point.y + 63f
        }

    private fun midpoint(first: CanvasPoint, second: CanvasPoint): CanvasPoint = CanvasPoint(
        x = (first.x + second.x) / 2f,
        y = (first.y + second.y) / 2f,
    )
}
