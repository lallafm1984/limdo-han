package com.nullplaying.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BootstrapTest {
    @Test
    fun guideDotsStayEvenAcrossPolylineCorners() {
        val dots = WritingCanvasGeometry.evenlySpacedGuideDots(
            stroke = listOf(
                CanvasPoint(0f, 0f),
                CanvasPoint(100f, 0f),
                CanvasPoint(100f, 100f),
            ),
            targetSpacing = 24f,
        )

        assertEquals(CanvasPoint(0f, 0f), dots.first())
        assertEquals(CanvasPoint(100f, 100f), dots.last())
        val pathDistances = dots.map { point ->
            if (point.y == 0f) point.x else 100f + point.y
        }
        val gaps = pathDistances.zipWithNext { start, end -> end - start }
        gaps.forEach { gap -> assertEquals(gaps.first(), gap, 0.001f) }
    }

    @Test
    fun testEnvironmentRuns() {
        assertTrue(true)
    }

    @Test
    fun edgeActionsLeaveA1962By954WritingCanvasAtReferenceResolution() {
        val canvas = LearningShellSpec.writingCanvasSizePx(
            screenWidthPx = 2_340,
            screenHeightPx = 1_080,
            density = 2.625f,
        )

        assertEquals(1_962 to 954, canvas)
        assertTrue(canvas.first >= 1_872)
        assertTrue(canvas.second >= 900)
        assertEquals(64f, LearningShellSpec.ACTION_COLUMN_WIDTH_DP, 0.001f)
        assertEquals(12f, LearningShellSpec.ACTION_COLUMN_SPACING_DP, 0.001f)
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
        assertEquals(471.2f, horizontal, 0.01f)
        assertEquals(471.2f, vertical, 0.01f)
        assertEquals(glyph.emSize * 0.76f, horizontal, 0.01f)
        assertEquals(glyph.emSize * 0.76f, vertical, 0.01f)
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
    fun inputDirectionGuideStaysAheadAndSwitchesAtCorner() {
        val points = WritingCanvasGeometry.gieokPoints(width = 1_962f, height = 775f)
        val horizontal = WritingCanvasGeometry.gieokInputDirectionGuide(
            width = 1_962f,
            height = 775f,
            input = CanvasPoint((points[0].x + points[1].x) / 2f, points[0].y),
            motionProgress = 0f,
        )
        val horizontalMoved = WritingCanvasGeometry.gieokInputDirectionGuide(
            width = 1_962f,
            height = 775f,
            input = CanvasPoint((points[0].x + points[1].x) / 2f, points[0].y),
            motionProgress = 1f,
        )
        assertTrue(horizontal.center.x > (points[0].x + points[1].x) / 2f)
        assertTrue(horizontalMoved.center.x > horizontal.center.x)
        assertEquals(CanvasPoint(1f, 0f), horizontal.direction)

        val vertical = WritingCanvasGeometry.gieokInputDirectionGuide(
            width = 1_962f,
            height = 775f,
            input = CanvasPoint(points[1].x, (points[1].y + points[2].y) / 2f),
            motionProgress = 0f,
        )
        assertTrue(vertical.center.y > (points[1].y + points[2].y) / 2f)
        assertEquals(CanvasPoint(0f, 1f), vertical.direction)
    }

    @Test
    fun gaInputDirectionGuideUsesCurrentProductionStroke() {
        val strokes = WritingCanvasGeometry.ga(width = 1_962f, height = 775f).strokes
        val gieokHorizontalInput = CanvasPoint(
            x = (strokes[0][0].x + strokes[0][1].x) / 2f,
            y = strokes[0][0].y,
        )
        val gieokHorizontal = WritingCanvasGeometry.gaInputDirectionGuide(
            1_962f, 775f, 0, gieokHorizontalInput, 0f,
        )
        assertTrue(gieokHorizontal.center.x > gieokHorizontalInput.x)
        assertEquals(CanvasPoint(1f, 0f), gieokHorizontal.direction)

        val gieokCurveInput = strokes[0][strokes[0].size / 2]
        val gieokCurve = WritingCanvasGeometry.gaInputDirectionGuide(
            1_962f, 775f, 0, gieokCurveInput, 0f,
        )
        assertTrue(gieokCurve.center.x < gieokCurveInput.x)
        assertTrue(gieokCurve.center.y > gieokCurveInput.y)
        assertTrue(gieokCurve.direction.x < 0f)
        assertTrue(gieokCurve.direction.y > 0f)
        assertEquals(1f, kotlin.math.hypot(gieokCurve.direction.x, gieokCurve.direction.y), 0.001f)

        val aVerticalInput = midpoint(strokes[1])
        val aVertical = WritingCanvasGeometry.gaInputDirectionGuide(
            1_962f, 775f, 1, aVerticalInput, 0f,
        )
        assertTrue(aVertical.center.y > aVerticalInput.y)
        assertEquals(CanvasPoint(0f, 1f), aVertical.direction)

        val aHorizontalInput = midpoint(strokes[2])
        val aHorizontal = WritingCanvasGeometry.gaInputDirectionGuide(
            1_962f, 775f, 2, aHorizontalInput, 0f,
        )
        assertTrue(aHorizontal.center.x > aHorizontalInput.x)
        assertEquals(CanvasPoint(1f, 0f), aHorizontal.direction)
    }

    private fun midpoint(stroke: List<CanvasPoint>) = CanvasPoint(
        x = (stroke[0].x + stroke[1].x) / 2f,
        y = (stroke[0].y + stroke[1].y) / 2f,
    )

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
    fun gaUsesThreeEducationalStrokesInOrder() {
        val glyph = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)
        val normalized = glyph.strokes.map { stroke ->
            stroke.map { point ->
                CanvasPoint(
                    x = (point.x - 671f) / glyph.emSize,
                    y = (point.y - 77.5f) / glyph.emSize,
                )
            }
        }

        assertEquals(3, glyph.strokes.size)
        assertEquals(14, normalized[0].size)
        assertEquals(0.05f, normalized[0].first().x, 0.0001f)
        assertEquals(0.12f, normalized[0].first().y, 0.0001f)
        assertEquals(0.48f, normalized[0][1].x, 0.0001f)
        assertEquals(0.12f, normalized[0][1].y, 0.0001f)
        assertEquals(0.10f, normalized[0].last().x, 0.0001f)
        assertEquals(0.88f, normalized[0].last().y, 0.0001f)
        val expectedRemaining = listOf(
            listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
            listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
        )
        expectedRemaining.zip(normalized.drop(1)).forEach { (expectedStroke, actualStroke) ->
            expectedStroke.zip(actualStroke).forEach { (expectedPoint, actualPoint) ->
                assertEquals(expectedPoint.x, actualPoint.x, 0.0001f)
                assertEquals(expectedPoint.y, actualPoint.y, 0.0001f)
            }
        }
    }

    @Test
    fun onlyGaInitialFollowsTheReferenceCurveWithoutChangingOtherGiyeokLessons() {
        val ga = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)
        val gaInitial = ga.strokes.first()
        val curve = gaInitial.drop(1)

        assertEquals(gaInitial.first().y, gaInitial[1].y, 0.001f)
        assertEquals(14, gaInitial.size)
        assertEquals(ga.emSize * 0.38f, gaInitial[1].x - gaInitial.last().x, 0.001f)
        assertTrue(gaInitial.last().x < gaInitial[1].x)
        assertTrue(gaInitial.last().y > gaInitial[1].y)
        assertTrue(curve.zipWithNext().all { (start, end) -> end.x <= start.x && end.y > start.y })
        assertTrue(curve[1].x >= curve.first().x - ga.emSize * 0.01f)
        assertTrue(curve[1].y > curve.first().y)

        val unchangedRightAngleLessons = listOf(
            LessonId.GIEOK,
            LessonId.GYA,
            LessonId.GEO,
            LessonId.GYEO,
            LessonId.GI,
        )
        unchangedRightAngleLessons.forEach { lessonId ->
            val lesson = KoreanCurriculum.lessons.single { it.id == lessonId }
            val initial = WritingCanvasGeometry.glyph(lesson, 1_962f, 775f).strokes.first()
            assertEquals("${lesson.glyph} 직각 보존", initial[1].x, initial.last().x, 0.001f)
        }
    }

    @Test
    fun visibleLessonUsesProductionGaGeometryWithoutDuplicatedCoordinates() {
        val visible = WritingCanvasGeometry.visibleLessonGlyph(width = 1_962f, height = 775f)
        val production = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)

        assertEquals(production, visible)
        assertEquals(3, visible.strokes.size)
        assertEquals(620f, visible.emSize, 0.001f)
        assertEquals(124f, visible.strokeWidth, 0.001f)
    }

    @Test
    fun currentStartMarkerUsesProductionGaStrokeOrderAndDisappearsAfterCompletion() {
        val production = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)

        production.strokes.forEachIndexed { completedStrokeCount, stroke ->
            assertEquals(
                stroke.first(),
                WritingCanvasGeometry.currentVisibleStrokeStart(
                    width = 1_962f,
                    height = 775f,
                    completedStrokeCount = completedStrokeCount,
                ),
            )
        }
        assertEquals(
            null,
            WritingCanvasGeometry.currentVisibleStrokeStart(
                width = 1_962f,
                height = 775f,
                completedStrokeCount = production.strokes.size,
            ),
        )
    }

    @Test
    fun currentEndMarkerUsesProductionGaStrokeOrderAndDisappearsAfterCompletion() {
        val production = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)

        production.strokes.forEachIndexed { completedStrokeCount, stroke ->
            assertEquals(
                stroke.last(),
                WritingCanvasGeometry.currentVisibleStrokeEnd(
                    width = 1_962f,
                    height = 775f,
                    completedStrokeCount = completedStrokeCount,
                ),
            )
        }
        assertEquals(
            null,
            WritingCanvasGeometry.currentVisibleStrokeEnd(
                width = 1_962f,
                height = 775f,
                completedStrokeCount = production.strokes.size,
            ),
        )
    }

    @Test
    fun gaDemonstrationTraversesEverySegmentInStrokeOrderWithDirection() {
        val glyph = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)
        val segmentLengths = glyph.strokes.flatMap { stroke ->
            stroke.zipWithNext().map { (start, end) ->
                kotlin.math.hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble()).toFloat()
            }
        }
        val totalLength = segmentLengths.sum()
        val boundaries = segmentLengths.runningFold(0f) { distance, length -> distance + length }
            .map { it / totalLength }
        val firstStrokeSegmentCount = glyph.strokes[0].lastIndex

        val start = WritingCanvasGeometry.gaDemonstrationGuide(1_962f, 775f, 0f)
        assertEquals(glyph.strokes[0][0], start.center)
        assertEquals(CanvasPoint(1f, 0f), start.direction)
        assertEquals(0, start.strokeIndex)
        assertEquals(0, start.segmentIndex)

        val firstEnd = WritingCanvasGeometry.gaDemonstrationGuide(1_962f, 775f, boundaries[1])
        assertEquals(glyph.strokes[0][1].x, firstEnd.center.x, 0.001f)
        assertEquals(glyph.strokes[0][1].y, firstEnd.center.y, 0.001f)
        assertEquals(CanvasPoint(1f, 0f), firstEnd.direction)

        val gieokCurve = WritingCanvasGeometry.gaDemonstrationGuide(
            1_962f,
            775f,
            (boundaries[6] + boundaries[7]) / 2f,
        )
        assertEquals(0, gieokCurve.strokeIndex)
        assertTrue(gieokCurve.segmentIndex in 1..12)
        assertTrue(gieokCurve.direction.x < 0f)
        assertTrue(gieokCurve.direction.y > 0f)
        assertEquals(1f, kotlin.math.hypot(gieokCurve.direction.x, gieokCurve.direction.y), 0.001f)

        val aVertical = WritingCanvasGeometry.gaDemonstrationGuide(
            1_962f,
            775f,
            (boundaries[firstStrokeSegmentCount] + boundaries[firstStrokeSegmentCount + 1]) / 2f,
        )
        assertEquals(1, aVertical.strokeIndex)
        assertEquals(0, aVertical.segmentIndex)
        assertEquals(CanvasPoint(0f, 1f), aVertical.direction)

        val aHorizontal = WritingCanvasGeometry.gaDemonstrationGuide(
            1_962f,
            775f,
            (boundaries[firstStrokeSegmentCount + 1] + boundaries[firstStrokeSegmentCount + 2]) / 2f,
        )
        assertEquals(2, aHorizontal.strokeIndex)
        assertEquals(0, aHorizontal.segmentIndex)
        assertEquals(CanvasPoint(1f, 0f), aHorizontal.direction)

        val end = WritingCanvasGeometry.gaDemonstrationGuide(1_962f, 775f, 1f)
        assertEquals(glyph.strokes[2][1].x, end.center.x, 0.001f)
        assertEquals(glyph.strokes[2][1].y, end.center.y, 0.001f)
    }

    @Test
    fun speechSelectedDemonstrationProgressStaysWithinRequestedStroke() {
        for (strokeIndex in 0..2) {
            val start = WritingCanvasGeometry.gaDemonstrationGuide(
                1_962f,
                775f,
                WritingCanvasGeometry.gaStrokeDemonstrationProgress(strokeIndex, 0f),
            )
            val middle = WritingCanvasGeometry.gaDemonstrationGuide(
                1_962f,
                775f,
                WritingCanvasGeometry.gaStrokeDemonstrationProgress(strokeIndex, 0.5f),
            )
            assertEquals(strokeIndex, start.strokeIndex)
            assertEquals(strokeIndex, middle.strokeIndex)
        }
    }

    @Test
    fun idleDemonstrationAdvancesOnlyToTheCurrentUnfinishedStroke() {
        for (completedStrokeCount in 0..2) {
            listOf(0f, 0.5f, 0.99f).forEach { progress ->
                val guide = WritingCanvasGeometry.gaDemonstrationGuide(
                    1_962f,
                    775f,
                    WritingCanvasGeometry.gaCurrentStrokeDemonstrationProgress(
                        completedStrokeCount,
                        progress,
                    ),
                )
                assertEquals(completedStrokeCount, guide.strokeIndex)
            }
        }
    }

    @Test
    fun shortThirdStrokeScalesAndInsetsItsDirectionGuideWithoutChangingLongStrokes() {
        val width = 1_962f
        val height = 775f
        val glyph = WritingCanvasGeometry.ga(width, height)

        assertEquals(1f, WritingCanvasGeometry.gaStrokeGuideScale(0), 0.001f)
        assertEquals(1f, WritingCanvasGeometry.gaStrokeGuideScale(1), 0.001f)
        assertEquals(0.5f, WritingCanvasGeometry.gaStrokeGuideScale(2), 0.001f)

        val longArrowLength = height * 0.10f * 0.58f
        val shortArrowLength = longArrowLength * 0.5f
        assertEquals(
            longArrowLength * 0.30f,
            WritingCanvasGeometry.gaDirectionArrowHeadLength(width, height, 0, longArrowLength),
            0.001f,
        )
        assertEquals(
            longArrowLength * 0.30f,
            WritingCanvasGeometry.gaDirectionArrowHeadLength(width, height, 1, longArrowLength),
            0.001f,
        )
        assertEquals(
            (glyph.strokes[2].last().x - glyph.strokes[2].first().x) * 0.5f * 0.22f,
            WritingCanvasGeometry.gaDirectionArrowHeadLength(width, height, 2, shortArrowLength),
            0.001f,
        )

        val thirdStart = WritingCanvasGeometry.gaDemonstrationGuide(
            width,
            height,
            WritingCanvasGeometry.gaCurrentStrokeDemonstrationProgress(2, 0f),
        )
        val thirdEnd = WritingCanvasGeometry.gaDemonstrationGuide(
            width,
            height,
            WritingCanvasGeometry.gaCurrentStrokeDemonstrationProgress(2, 1f),
        )
        val stroke = glyph.strokes[2]
        val strokeLength = stroke.last().x - stroke.first().x

        assertEquals(0.5f, thirdStart.visualScale, 0.001f)
        assertEquals(CanvasPoint(1f, 0f), thirdStart.direction)
        assertEquals(stroke.first().x + strokeLength * 0.25f, thirdStart.center.x, 0.01f)
        assertEquals(stroke.last().x - strokeLength * 0.25f, thirdEnd.center.x, 0.01f)
        assertEquals(stroke.first().y, thirdStart.center.y, 0.01f)
        assertEquals(stroke.last().y, thirdEnd.center.y, 0.01f)

        val firstStart = WritingCanvasGeometry.gaDemonstrationGuide(
            width,
            height,
            WritingCanvasGeometry.gaCurrentStrokeDemonstrationProgress(0, 0f),
        )
        val secondStart = WritingCanvasGeometry.gaDemonstrationGuide(
            width,
            height,
            WritingCanvasGeometry.gaCurrentStrokeDemonstrationProgress(1, 0f),
        )
        assertEquals(glyph.strokes[0].first(), firstStart.center)
        assertEquals(glyph.strokes[1].first().x, secondStart.center.x, 0.01f)
        assertEquals(glyph.strokes[1].first().y, secondStart.center.y, 0.01f)
        assertEquals(1f, firstStart.visualScale, 0.001f)
        assertEquals(1f, secondStart.visualScale, 0.001f)
    }

    @Test
    fun gaPreservesNormalizedJamoLayoutWithUniformScaleAcrossCanvasRatios() {
        val wide = WritingCanvasGeometry.ga(width = 1_962f, height = 775f)
        val tall = WritingCanvasGeometry.ga(width = 900f, height = 1_200f)
        val wideBoard = WritingCanvasGeometry.learningBoard(width = 1_962f, height = 775f)
        val tallBoard = WritingCanvasGeometry.learningBoard(width = 900f, height = 1_200f)

        assertEquals(620f, wide.emSize, 0.001f)
        assertEquals(720f, tall.emSize, 0.001f)
        wide.strokes.zip(tall.strokes).forEach { (wideStroke, tallStroke) ->
            wideStroke.zip(tallStroke).forEach { (widePoint, tallPoint) ->
                assertEquals(
                    (widePoint.x - wideBoard.left) / wide.emSize,
                    (tallPoint.x - tallBoard.left) / tall.emSize,
                    0.0001f,
                )
                assertEquals(
                    (widePoint.y - wideBoard.top) / wide.emSize,
                    (tallPoint.y - tallBoard.top) / tall.emSize,
                    0.0001f,
                )
            }
        }
        assertEquals(0.20f, wide.strokeWidth / wide.emSize, 0.001f)
        assertEquals(0.20f, tall.strokeWidth / tall.emSize, 0.001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun gaRejectsEmptyCanvas() {
        WritingCanvasGeometry.ga(width = 1_000f, height = 0f)
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
