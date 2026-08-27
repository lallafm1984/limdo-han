package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonRewardTest {
    @Test
    fun actionButtonAtlasKeepsNineSquareCellsAndDistinctStates() {
        assertEquals(3, ActionButtonAtlasSpec.COLUMNS)
        assertEquals(3, ActionButtonAtlasSpec.ROWS)
        assertEquals(ActionButtonAtlasSpec.CELL_SIZE_PX * 3, ActionButtonAtlasSpec.SOURCE_WIDTH_PX)
        assertEquals(ActionButtonAtlasSpec.CELL_SIZE_PX * 3, ActionButtonAtlasSpec.SOURCE_HEIGHT_PX)
        assertTrue(ActionButtonAtlasSpec.BUTTON_MIN_DP >= 64f)
        assertTrue(ActionButtonAtlasSpec.PRESSED_SCALE < 1f)
        assertTrue(LearningShellSpec.ACTION_COLUMN_SPACING_DP >= 12f)
    }

    @Test
    fun allRemainingPlaygroundAnimationsStayFiniteAndBounded() {
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
            assertTrue(transition.symbolScale in 0.82f..1f)
            assertTrue(transition.symbolAlpha in 0f..1f)
            assertTrue(kotlin.math.abs(retry.offsetDp) <= RetryAnimationSpec.MAX_OFFSET_DP)
            assertTrue(retry.startMarkerScale in 1f..RetryAnimationSpec.START_MARKER_MAX_SCALE)
            assertTrue(retry.feedbackScale in 0.78f..1f)
            assertTrue(retry.feedbackAlpha in 0f..1f)
            assertTrue(retry.sparkleAlpha in 0f..1f)
            assertTrue(celebration.starScale in 0f..1.1f)
            assertTrue(celebration.glowAlpha in 0f..1f)
            assertTrue(celebration.confettiAlpha in 0f..1f)
        }
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
        assertEquals(0f, end.feedbackAlpha, 0.001f)
        assertEquals(0f, end.sparkleAlpha, 0.001f)
    }

    @Test
    fun retryFeedbackAtlasKeepsTwoSquareCellsAndBoundedPresentation() {
        assertEquals(2, RetryFeedbackAtlasSpec.COLUMNS)
        assertEquals(1, RetryFeedbackAtlasSpec.ROWS)
        assertEquals(
            RetryFeedbackAtlasSpec.CELL_SIZE_PX * RetryFeedbackAtlasSpec.COLUMNS,
            RetryFeedbackAtlasSpec.SOURCE_WIDTH_PX,
        )
        assertEquals(
            RetryFeedbackAtlasSpec.CELL_SIZE_PX * RetryFeedbackAtlasSpec.ROWS,
            RetryFeedbackAtlasSpec.SOURCE_HEIGHT_PX,
        )
        assertEquals(0, RetryFeedbackAtlasSpec.RETURN_ARROW_CELL)
        assertEquals(1, RetryFeedbackAtlasSpec.SPARKLES_CELL)
        assertTrue(RetryFeedbackAtlasSpec.CONTAINER_WIDTH_DP < 160f)
        assertTrue(RetryFeedbackAtlasSpec.CONTAINER_HEIGHT_DP < 100f)
        assertTrue(RetryAnimationSpec.DURATION_MS <= 1_500)
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
    }

    @Test
    fun successFeedbackAtlasKeepsTwoSquareCellsAndBoundedPresentation() {
        assertEquals(2, SuccessFeedbackAtlasSpec.COLUMNS)
        assertEquals(1, SuccessFeedbackAtlasSpec.ROWS)
        assertEquals(
            SuccessFeedbackAtlasSpec.CELL_SIZE_PX * SuccessFeedbackAtlasSpec.COLUMNS,
            SuccessFeedbackAtlasSpec.SOURCE_WIDTH_PX,
        )
        assertEquals(
            SuccessFeedbackAtlasSpec.CELL_SIZE_PX * SuccessFeedbackAtlasSpec.ROWS,
            SuccessFeedbackAtlasSpec.SOURCE_HEIGHT_PX,
        )
        assertEquals(0, SuccessFeedbackAtlasSpec.STAR_CELL)
        assertEquals(1, SuccessFeedbackAtlasSpec.CONFETTI_CELL)
        assertTrue(
            SuccessFeedbackAtlasSpec.CONTAINER_WIDTH_DP <= SuccessMarkerGeometry.WIDTH,
        )
        assertTrue(SuccessFeedbackAtlasSpec.STAR_SIZE_DP <= SuccessFeedbackAtlasSpec.CONTAINER_HEIGHT_DP)
        assertTrue(SuccessFeedbackAtlasSpec.CONFETTI_SIZE_DP <= SuccessFeedbackAtlasSpec.CONTAINER_HEIGHT_DP)
    }

    @Test
    fun lessonsKeepTheirEducationalStrokeCounts() {
        assertEquals(1, GieokLesson.strokeCount)
        assertEquals(3, GaLesson.strokeCount)
        KoreanCurriculum.lessons.forEach { lesson ->
            assertEquals(
                lesson.strokeCount,
                WritingCanvasGeometry.glyph(lesson, width = 1962f, height = 954f).strokes.size,
            )
        }
    }

    @Test
    fun successMarkerStaysVisibleForEveryProductionGlyph() {
        val containerWidth = 2340f / 2.625f
        val containerHeight = 1080f / 2.625f
        KoreanCurriculum.lessons.forEach { lesson ->
            val center = SuccessMarkerGeometry.center(containerWidth, containerHeight, lesson)
            assertTrue(center.x - SuccessMarkerGeometry.WIDTH / 2f >= 0f)
            assertTrue(center.x + SuccessMarkerGeometry.WIDTH / 2f <= containerWidth)
            assertTrue(center.y - SuccessMarkerGeometry.HEIGHT / 2f >= 0f)
            assertTrue(center.y + SuccessMarkerGeometry.HEIGHT / 2f <= containerHeight)
        }
    }
}
