package com.limdo.hangul

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceAttemptTest {
    private val width = 1_000f
    private val height = 500f
    private val safeInset = 24f
    private val guide = WritingCanvasGeometry.ga(width, height).strokes

    @Test
    fun resultIsPublishedOnlyWhenAttemptFinishes() {
        val firstStroke = guide.first()
        val drawing = firstStroke.drop(1).fold(
            TraceAttempt().start(firstStroke.first(), width, height, safeInset),
        ) { attempt, point ->
            attempt.append(point, width, height, safeInset)
        }

        assertNull(drawing.result)
        assertNull(drawing.finish(width, height).result)
    }

    @Test
    fun newStrokeReplacesPreviousAttemptAndResult() {
        val completed = finishAllStrokes()
        val newStart = CanvasPoint(120f, 140f)

        val restarted = completed.start(newStart, width, height, safeInset)

        assertNull(restarted.result)
        assertEquals(listOf(newStart), restarted.stroke.points)
    }

    @Test
    fun clearResetsStrokeAndResult() {
        val completed = finishAllStrokes()

        val cleared = completed.clear()

        assertTrue(cleared.stroke.points.isEmpty())
        assertNull(cleared.result)
        assertTrue(cleared.completedStrokes.isEmpty())
    }

    @Test
    fun threeProductionStrokesPublishSuccessInOrder() {
        val completed = finishAllStrokes()

        assertEquals(GieokTraceResult.SUCCESS, completed.result)
        assertEquals(3, completed.completedStrokes.size)
    }

    @Test
    fun accessibilityActionAdvancesExactlyOneProductionStroke() {
        val advanced = TraceAttempt().followCurrentGuide(width, height, GaLesson)

        assertNull(advanced.result)
        assertEquals(1, advanced.completedStrokes.size)
        assertEquals(guide.first(), advanced.completedStrokes.single().points)
    }

    @Test
    fun accessibilityActionCompletesInProductionStrokeOrderAndStops() {
        val completed = generateSequence(TraceAttempt()) {
            it.followCurrentGuide(width, height, GaLesson)
        }.drop(3).first()

        assertEquals(GieokTraceResult.SUCCESS, completed.result)
        assertEquals(guide, completed.completedStrokes.map(StrokePath::points))
        assertEquals(completed, completed.followCurrentGuide(width, height, GaLesson))
    }

    private fun finishAllStrokes(): TraceAttempt = guide.fold(TraceAttempt()) { attempt, points ->
        val drawing = points.drop(1).fold(
            attempt.start(points.first(), width, height, safeInset),
        ) { current, point -> current.append(point, width, height, safeInset) }
        drawing.finish(width, height)
    }
}
