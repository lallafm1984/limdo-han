package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonRewardTest {
    @Test
    fun gieokLessonDeclaresOneEducationalStroke() {
        assertEquals("ㄱ", GieokLesson.glyph)
        assertEquals(1, GieokLesson.strokeCount)
    }

    @Test
    fun gaLessonDeclaresThreeEducationalStrokes() {
        assertEquals("가", GaLesson.glyph)
        assertEquals(3, GaLesson.strokeCount)
    }

    @Test
    fun gaSuccessTargetsExactlyThreeSteps() {
        val started = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, GaLesson)

        assertEquals(3, started.targetSteps)
        assertEquals(RewardMovePhase.START, started.phase)
        assertTrue(started.inputLocked)
    }

    @Test
    fun threeStepRewardPathKeepsEveryVehiclePositionClearOfProductionGa() {
        val containerWidth = 2340f / 2.625f
        val containerHeight = 1080f / 2.625f
        val canvasWidth = containerWidth - 144f
        val canvasHeight = containerHeight - 116f
        val glyph = WritingCanvasGeometry.visibleLessonGlyph(canvasWidth, canvasHeight)
        val guideLeft = 72f + glyph.strokes.flatten().minOf { it.x } - glyph.strokeWidth / 2f

        val centers = (0..GaLesson.strokeCount).map { step ->
            RewardPathGeometry.vehicleCenterX(
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                completedSteps = step.toFloat(),
                targetSteps = GaLesson.strokeCount,
            )
        }

        centers.forEach { center ->
            val vehicleRight = center + RewardPathGeometry.VEHICLE_WIDTH / 2f
            assertTrue(guideLeft - vehicleRight >= RewardPathGeometry.GLYPH_SAFETY_MARGIN)
        }
        centers.zipWithNext().forEach { (start, end) ->
            assertEquals(RewardPathGeometry.STEP_DISTANCE, end - start, 0.001f)
        }
        assertEquals(
            RewardPathGeometry.GLYPH_SAFETY_MARGIN,
            guideLeft - (centers.last() + RewardPathGeometry.VEHICLE_WIDTH / 2f),
            0.001f,
        )
    }

    @Test
    fun successMarkerKeepsTwentyFourDpClearOfProductionGa() {
        val containerWidth = 2340f / 2.625f
        val containerHeight = 1080f / 2.625f
        val canvasWidth = containerWidth - 144f
        val canvasHeight = containerHeight - 116f
        val glyph = WritingCanvasGeometry.visibleLessonGlyph(canvasWidth, canvasHeight)
        val guideRight = 72f + glyph.strokes.flatten().maxOf { it.x } + glyph.strokeWidth / 2f
        val center = SuccessMarkerGeometry.center(containerWidth, containerHeight)
        val markerLeft = center.x - SuccessMarkerGeometry.WIDTH / 2f

        assertEquals(
            SuccessMarkerGeometry.GLYPH_SAFETY_MARGIN,
            markerLeft - guideRight,
            0.001f,
        )
        assertTrue(center.x + SuccessMarkerGeometry.WIDTH / 2f <= containerWidth)
        assertTrue(center.y - SuccessMarkerGeometry.HEIGHT / 2f >= 0f)
        assertTrue(center.y + SuccessMarkerGeometry.HEIGHT / 2f <= containerHeight)
    }

    @Test
    fun successMovesExactlyStrokeCountStepsThroughClearPhases() {
        val started = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, GieokLesson)
        val moving = started.moving()
        val completed = moving.complete()

        assertEquals(1, started.targetSteps)
        assertEquals(RewardMovePhase.START, started.phase)
        assertTrue(started.inputLocked)
        assertEquals(RewardMovePhase.MOVING, moving.phase)
        assertTrue(moving.inputLocked)
        assertEquals(1, completed.completedSteps)
        assertEquals(RewardMovePhase.COMPLETE, completed.phase)
        assertTrue(completed.inputLocked)
    }

    @Test
    fun duplicateSuccessFailureAndClearDoNotAddMovement() {
        val started = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, GieokLesson)
        assertEquals(started, started.onTraceResult(GieokTraceResult.SUCCESS, GieokLesson))

        val retry = LessonRewardState().onTraceResult(GieokTraceResult.OFF_GUIDE, GieokLesson)
        val clear = LessonRewardState().onTraceResult(null, GieokLesson)
        assertEquals(0, retry.targetSteps)
        assertEquals(0, clear.targetSteps)
    }

    @Test
    fun gaDuplicateSuccessFailureAndClearDoNotAddMovement() {
        val started = LessonRewardState().onTraceResult(GieokTraceResult.SUCCESS, GaLesson)
        assertEquals(started, started.onTraceResult(GieokTraceResult.SUCCESS, GaLesson))

        val retry = LessonRewardState().onTraceResult(GieokTraceResult.OFF_GUIDE, GaLesson)
        val clear = LessonRewardState().onTraceResult(null, GaLesson)
        assertEquals(0, retry.targetSteps)
        assertEquals(0, clear.targetSteps)
    }

    @Test
    fun restoredCompleteStateStaysLockedUntilClearCreatesIdleState() {
        val restored = LessonRewardState(
            completedSteps = 1,
            targetSteps = 1,
            successConsumed = true,
            phase = RewardMovePhase.COMPLETE,
        )

        assertTrue(restored.inputLocked)
        assertFalse(LessonRewardState().inputLocked)
    }
}
