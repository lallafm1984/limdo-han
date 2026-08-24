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

    @Test
    fun gieokGuideUsesSquareEmUniformScaleAndEducationalStrokeWidth() {
        val glyph = WritingCanvasGeometry.gieok(width = 1_962f, height = 775f)
        val points = glyph.strokes.single()
        val horizontal = points[1].x - points[0].x
        val vertical = points[2].y - points[1].y

        assertEquals(620f, glyph.emSize, 0.01f)
        assertEquals(582.8f, horizontal, 0.01f)
        assertEquals(582.8f, vertical, 0.01f)
        assertTrue(horizontal >= 580f && vertical >= 580f)
        assertEquals(0.20f, glyph.strokeWidth / glyph.emSize, 0.001f)
        assertEquals(124f, glyph.strokeWidth, 0.001f)
        assertEquals(horizontal / vertical, 1f, 0.001f)
    }

    @Test
    fun visibleLearningBoardUsesCenteredSquareEmBounds() {
        val board = WritingCanvasGeometry.learningBoard(width = 1_962f, height = 775f)

        assertEquals(620f, board.width, 0.01f)
        assertEquals(620f, board.height, 0.01f)
        assertEquals(671f, board.left, 0.01f)
        assertEquals(77.5f, board.top, 0.01f)
        assertEquals(671f, 1_962f - board.right, 0.01f)
        assertEquals(77.5f, 775f - board.bottom, 0.01f)
    }

    @Test
    fun inputBeforeDemonstrationMovesFromStartThroughCornerToEnd() {
        val points = WritingCanvasGeometry.gieokPoints(width = 1_962f, height = 775f)

        assertEquals(
            points[0],
            WritingCanvasGeometry.gieokDemonstrationPoint(1_962f, 775f, 0f),
        )
        assertEquals(
            CanvasPoint((points[0].x + points[1].x) / 2f, points[0].y),
            WritingCanvasGeometry.gieokDemonstrationPoint(1_962f, 775f, 0.25f),
        )
        assertEquals(
            points[1],
            WritingCanvasGeometry.gieokDemonstrationPoint(1_962f, 775f, 0.5f),
        )
        val verticalMidpoint = WritingCanvasGeometry.gieokDemonstrationPoint(1_962f, 775f, 0.75f)
        assertEquals(points[1].x, verticalMidpoint.x, 0.001f)
        assertEquals((points[1].y + points[2].y) / 2f, verticalMidpoint.y, 0.001f)
        assertEquals(
            points[2],
            WritingCanvasGeometry.gieokDemonstrationPoint(1_962f, 775f, 1f),
        )
    }

    @Test
    fun childStrokeUsesSixtyPercentOfGuideWidth() {
        val glyph = WritingCanvasGeometry.gieok(width = 1_962f, height = 775f)
        val childStrokeWidth = WritingCanvasGeometry.childStrokeWidth(width = 1_962f, height = 775f)

        assertEquals(124f, glyph.strokeWidth, 0.001f)
        assertEquals(74.4f, childStrokeWidth, 0.001f)
        assertEquals(0.60f, childStrokeWidth / glyph.strokeWidth, 0.001f)
        assertTrue(childStrokeWidth < glyph.strokeWidth)
    }

    @Test
    fun gaFixturePreservesNormalizedJamoLayoutAtUniformScale() {
        val first = WritingCanvasGeometry.gaFixture(width = 1_962f, height = 775f)
        val second = WritingCanvasGeometry.gaFixture(width = 1_000f, height = 500f)

        assertEquals(3, first.strokes.size)
        first.strokes.zip(second.strokes).forEach { (largeStroke, smallStroke) ->
            val largeDx = largeStroke.last().x - largeStroke.first().x
            val largeDy = largeStroke.last().y - largeStroke.first().y
            val smallDx = smallStroke.last().x - smallStroke.first().x
            val smallDy = smallStroke.last().y - smallStroke.first().y
            if (smallDx != 0f) assertEquals(1.55f, largeDx / smallDx, 0.001f)
            if (smallDy != 0f) assertEquals(1.55f, largeDy / smallDy, 0.001f)
        }
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
