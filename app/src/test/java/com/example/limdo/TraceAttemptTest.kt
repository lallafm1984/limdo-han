package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TraceAttemptTest {
    private val width = 1_000f
    private val height = 500f
    private val safeInset = 24f
    private val guide = WritingCanvasGeometry.gieokPoints(width, height)

    @Test
    fun resultIsPublishedOnlyWhenAttemptFinishes() {
        val drawing = guide.drop(1).fold(
            TraceAttempt().start(guide.first(), width, height, safeInset),
        ) { attempt, point ->
            attempt.append(point, width, height, safeInset)
        }

        assertNull(drawing.result)
        assertEquals(GieokTraceResult.SUCCESS, drawing.finish(width, height).result)
    }

    @Test
    fun newStrokeReplacesPreviousAttemptAndResult() {
        val completed = TraceAttempt(stroke = StrokePath(guide)).finish(width, height)
        val newStart = CanvasPoint(120f, 140f)

        val restarted = completed.start(newStart, width, height, safeInset)

        assertNull(restarted.result)
        assertEquals(listOf(newStart), restarted.stroke.points)
    }

    @Test
    fun clearResetsStrokeAndResult() {
        val completed = TraceAttempt(stroke = StrokePath(guide)).finish(width, height)

        val cleared = completed.clear()

        assertTrue(cleared.stroke.points.isEmpty())
        assertNull(cleared.result)
    }
}
