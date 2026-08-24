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
        assertFalse(completed.inputLocked)
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
}
