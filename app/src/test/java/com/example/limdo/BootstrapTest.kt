package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapTest {
    @Test
    fun testEnvironmentRuns() {
        assertTrue(true)
    }

    @Test
    fun writingBoardUsesSeventyPercentOfLearningArea() {
        assertEquals(0.7f, LearningShellSpec.writingBoardFraction, 0.001f)
        assertTrue(LearningShellSpec.writingBoardFraction in 0.65f..0.75f)
    }

    @Test
    fun gieokPreviewUsesTwoSegmentsWithinPaddedBounds() {
        val points = WritingCanvasGeometry.gieokPoints(width = 1_000f, height = 500f)

        assertEquals(3, points.size)
        assertEquals(points[0].y, points[1].y, 0.001f)
        assertEquals(points[1].x, points[2].x, 0.001f)
        assertTrue(points.all { it.x in 0f..1_000f && it.y in 0f..500f })
        assertTrue(points.first().x > 0f && points.first().y > 0f)
        assertTrue(points.last().x < 1_000f && points.last().y < 500f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun gieokPreviewRejectsEmptyCanvas() {
        WritingCanvasGeometry.gieokPoints(width = 0f, height = 500f)
    }

    @Test
    fun strokePathConstrainsPointsToChildSafeInsetAndClears() {
        val started = StrokePath().start(
            point = CanvasPoint(-20f, 900f),
            width = 1_200f,
            height = 500f,
            safeInset = 24f,
        )
        val extended = started.append(
            point = CanvasPoint(1_400f, -10f),
            width = 1_200f,
            height = 500f,
            safeInset = 24f,
        )

        assertEquals(CanvasPoint(24f, 476f), extended.points.first())
        assertEquals(CanvasPoint(1_176f, 24f), extended.points.last())
        assertTrue(extended.clear().points.isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun strokePathRejectsCanvasSmallerThanSafeInset() {
        StrokePath().start(
            point = CanvasPoint(10f, 10f),
            width = 40f,
            height = 40f,
            safeInset = 24f,
        )
    }
}
