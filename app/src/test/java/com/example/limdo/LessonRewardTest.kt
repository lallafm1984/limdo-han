package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonRewardTest {
    @Test
    fun allPlaygroundAnimationsStayFiniteBoundedAndDuplicateSafeTogether() {
        val animationDurations = listOf(
            LimDoPlaygroundTokens.HOME_ENTRANCE_DURATION_MS,
            LimDoPlaygroundTokens.MENU_TRANSITION_DURATION_MS,
            RetryAnimationSpec.DURATION_MS,
            SuccessCelebrationSpec.DURATION_MS,
        )
        assertTrue(animationDurations.all { it in 200..1_500 })

        (0..100).forEach { sample ->
            val progress = sample / 100f
            val entrance = homeEntranceVisuals(progress)
            val transition = menuTransitionVisuals(progress)
            val retry = retryAnimationVisuals(progress)
            val celebration = successCelebrationVisuals(progress)

            assertTrue(entrance.alpha in 0f..1f)
            assertTrue(entrance.scale in 0.92f..1f)
            assertTrue(entrance.offsetDp in 0f..LimDoPlaygroundTokens.HOME_ENTRANCE_OFFSET_DP)
            assertTrue(transition.vehicleScale in 0.82f..1f)
            assertTrue(transition.symbolAlpha in 0f..1f)
            assertTrue(kotlin.math.abs(retry.offsetDp) <= RetryAnimationSpec.MAX_OFFSET_DP)
            assertTrue(retry.startMarkerScale in 1f..RetryAnimationSpec.START_MARKER_MAX_SCALE)
            assertTrue(celebration.starScale in 0f..1.1f)
            assertTrue(celebration.glowAlpha in 0f..1f)
            assertTrue(celebration.confettiAlpha in 0f..1f)
        }

        val rewardStarted = LessonRewardState()
            .onTraceResult(GieokTraceResult.SUCCESS, GaLesson)
        val rewardAfterDuplicate = rewardStarted
            .onTraceResult(GieokTraceResult.SUCCESS, GaLesson)
        assertEquals(rewardStarted, rewardAfterDuplicate)
        assertEquals(GaLesson.strokeCount, rewardAfterDuplicate.targetSteps)

        val vehicleStarted = VehicleCarouselState()
            .onTraceResult(GieokTraceResult.SUCCESS)
        val vehicleAfterDuplicate = vehicleStarted
            .onTraceResult(GieokTraceResult.SUCCESS)
        assertEquals(vehicleStarted, vehicleAfterDuplicate)
        assertTrue(vehicleAfterDuplicate.nextVehiclePending)
    }

    @Test
    fun retryAnimationShakesThenSettlesAndReemphasizesCurrentStart() {
        val start = retryAnimationVisuals(0f)
        val firstSide = retryAnimationVisuals(0.125f)
        val middle = retryAnimationVisuals(0.5f)
        val otherSide = retryAnimationVisuals(0.375f)
        val end = retryAnimationVisuals(1f)

        assertEquals(0f, start.offsetDp, 0.001f)
        assertTrue(firstSide.offsetDp > 0f)
        assertTrue(otherSide.offsetDp < 0f)
        assertTrue(kotlin.math.abs(otherSide.offsetDp) < kotlin.math.abs(firstSide.offsetDp))
        assertTrue(middle.startMarkerScale > start.startMarkerScale)
        assertEquals(1f, end.startMarkerScale, 0.001f)
        assertEquals(0f, end.offsetDp, 0.001f)
        assertTrue(RetryAnimationSpec.DURATION_MS in 300..500)
        assertTrue(RetryAnimationSpec.MAX_OFFSET_DP <= 12f)
    }

    @Test
    fun successCelebrationHasFiniteOrderedStartMiddleAndEndStates() {
        val start = successCelebrationVisuals(0f)
        val star = successCelebrationVisuals(0.2f)
        val glow = successCelebrationVisuals(0.5f)
        val confetti = successCelebrationVisuals(0.7f)
        val end = successCelebrationVisuals(1f)

        assertEquals(0f, start.starScale)
        assertEquals(0f, start.glowAlpha)
        assertEquals(0f, start.confettiAlpha)
        assertTrue(star.starScale > start.starScale)
        assertTrue(glow.glowAlpha > star.glowAlpha)
        assertTrue(confetti.confettiAlpha > glow.confettiAlpha)
        assertEquals(0f, end.glowAlpha)
        assertEquals(0f, end.confettiAlpha)
        assertTrue(SuccessCelebrationSpec.DURATION_MS in 800..1_500)
    }

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
    fun fourStepRewardPathKeepsDaVehicleFullyVisibleAndClearOfTheGlyph() {
        val containerWidth = 2340f / 2.625f
        val containerHeight = 1080f / 2.625f
        val canvasWidth = containerWidth - 144f
        val canvasHeight = containerHeight - 116f
        val glyph = WritingCanvasGeometry.visibleLessonGlyph(canvasWidth, canvasHeight, DaLesson)
        val guideLeft = 72f + glyph.strokes.flatten().minOf { it.x } - glyph.strokeWidth / 2f
        val centers = (0..DaLesson.strokeCount).map { step ->
            RewardPathGeometry.vehicleCenterX(
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                completedSteps = step.toFloat(),
                targetSteps = DaLesson.strokeCount,
                lesson = DaLesson,
            )
        }
        val distances = centers.zipWithNext().map { (start, end) -> end - start }

        assertTrue(centers.first() >= RewardPathGeometry.VEHICLE_WIDTH / 2f)
        assertTrue(distances.first() < RewardPathGeometry.STEP_DISTANCE)
        distances.forEach { distance -> assertEquals(distances.first(), distance, 0.001f) }
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
