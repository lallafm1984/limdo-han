package com.nullplaying.limdo

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
            assertTrue(retry.flashAlpha in 0f..RetryAnimationSpec.MAX_FLASH_ALPHA)
            assertTrue(celebration.scale in 0.72f..1.08f)
            assertTrue(celebration.alpha in 0f..1f)
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
        assertTrue(middle.flashAlpha > start.flashAlpha)
        assertEquals(0f, end.flashAlpha, 0.001f)
    }

    @Test
    fun successFeedbackStaysLargeAndRetryEffectStaysGentle() {
        assertEquals(1_024, FullScreenFeedbackSpec.SOURCE_SIZE_PX)
        assertTrue(
            FullScreenFeedbackSpec.SUCCESS_VISIBLE_FRACTION >=
                FullScreenFeedbackSpec.MIN_WRITING_BOARD_HEIGHT_FRACTION,
        )
        assertTrue(FullScreenFeedbackSpec.SUCCESS_VISIBLE_FRACTION <= 1f)
        assertTrue(RetryAnimationSpec.DURATION_MS <= 420)
        assertTrue(RetryAnimationSpec.MAX_OFFSET_DP <= 8f)
        assertTrue(RetryAnimationSpec.MAX_FLASH_ALPHA <= 0.16f)
    }

    @Test
    fun successCelebrationHasFiniteOrderedStartMiddleAndEndStates() {
        val start = successCelebrationVisuals(0f)
        val star = successCelebrationVisuals(0.2f)
        val glow = successCelebrationVisuals(0.5f)
        val confetti = successCelebrationVisuals(0.7f)
        val end = successCelebrationVisuals(1f)

        assertEquals(0f, start.alpha)
        assertTrue(star.scale > start.scale)
        assertTrue(star.alpha > start.alpha)
        assertTrue(star.scale > 1f)
        assertEquals(1f, glow.scale, 0.001f)
        assertEquals(1f, confetti.alpha)
        assertEquals(1f, end.scale, 0.001f)
        assertEquals(1f, end.alpha, 0.001f)
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

}
