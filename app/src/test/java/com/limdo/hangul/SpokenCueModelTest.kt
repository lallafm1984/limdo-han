package com.limdo.hangul

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpokenCueModelTest {
    @Test
    fun initialAndEveryTraceResultHaveDeterministicCueMappings() {
        val expected = mapOf(
            null to SpokenCue.INITIAL,
            GieokTraceResult.EMPTY to SpokenCue.INITIAL,
            GieokTraceResult.SUCCESS to SpokenCue.SUCCESS,
            GieokTraceResult.WRONG_START to SpokenCue.RETRY_START,
            GieokTraceResult.WRONG_DIRECTION to SpokenCue.RETRY_DIRECTION,
            GieokTraceResult.OFF_GUIDE to SpokenCue.RETRY_GUIDE,
            GieokTraceResult.INCOMPLETE to SpokenCue.RETRY_FINISH,
        )

        expected.forEach { (result, cue) ->
            assertEquals(cue, SpokenCueModel.forResult(result))
            assertEquals(cue, SpokenCueModel.forResult(result))
        }
    }

    @Test
    fun everyReplayIdentityHasOneShortConcreteUtterance() {
        SpokenCue.entries.forEach { cue ->
            assertFalse(cue.utterance.isBlank())
            assertEquals(cue.utterance.trim(), cue.utterance)
            assertFalse(cue.utterance.contains('\n'))
            assertFalse(cue.utterance.length > 55)
        }
    }

    @Test
    fun gaInitialAndDirectionRetriesMatchTheCurrentProductionStroke() {
        assertEquals(
            "오른쪽, 모서리에서 아래쪽으로 그려봐. 다음은 아래쪽으로 그려봐. 마지막은 오른쪽으로 그려봐.",
            SpokenCue.INITIAL.utterance,
        )
        assertEquals(SpokenCue.RETRY_DIRECTION, SpokenCueModel.forResult(GieokTraceResult.WRONG_DIRECTION, 0))
        assertEquals(SpokenCue.RETRY_SECOND_DIRECTION, SpokenCueModel.forResult(GieokTraceResult.WRONG_DIRECTION, 1))
        assertEquals(SpokenCue.RETRY_THIRD_DIRECTION, SpokenCueModel.forResult(GieokTraceResult.WRONG_DIRECTION, 2))

        val secondStrokeCues = listOf(
            GieokTraceResult.WRONG_START to SpokenCue.RETRY_SECOND_START,
            GieokTraceResult.OFF_GUIDE to SpokenCue.RETRY_SECOND_GUIDE,
            GieokTraceResult.INCOMPLETE to SpokenCue.RETRY_SECOND_FINISH,
        )
        val thirdStrokeCues = listOf(
            GieokTraceResult.WRONG_START to SpokenCue.RETRY_THIRD_START,
            GieokTraceResult.OFF_GUIDE to SpokenCue.RETRY_THIRD_GUIDE,
            GieokTraceResult.INCOMPLETE to SpokenCue.RETRY_THIRD_FINISH,
        )
        (secondStrokeCues + thirdStrokeCues).forEach { (result, cue) ->
            val strokeIndex = if (cue.name.contains("SECOND")) 1 else 2
            assertEquals(cue, SpokenCueModel.forResult(result, strokeIndex))
        }

        assertEquals(
            SpokenCue.RETRY_FOURTH_RIGHT_DIRECTION,
            SpokenCueModel.forResult(GieokTraceResult.WRONG_DIRECTION, 3, DaLesson),
        )
    }

    @Test
    fun restoredSuccessResumesOnlyOnceWhenNewSpeechEngineIsReady() {
        assertFalse(shouldResumeSuccessCue(SpeechPlaybackState.Initializing, GieokTraceResult.SUCCESS, true, false))
        assertTrue(shouldResumeSuccessCue(SpeechPlaybackState.Ready, GieokTraceResult.SUCCESS, true, false))
        assertFalse(shouldResumeSuccessCue(SpeechPlaybackState.Ready, GieokTraceResult.SUCCESS, true, true))
        assertFalse(shouldResumeSuccessCue(SpeechPlaybackState.Ready, GieokTraceResult.SUCCESS, false, false))
        assertFalse(shouldResumeSuccessCue(SpeechPlaybackState.Ready, null, true, false))
        assertFalse(shouldResumeSuccessCue(SpeechPlaybackState.Ready, GieokTraceResult.OFF_GUIDE, true, false))
    }

    @Test
    fun restoredManualInitialResumesOnlyOnceWhenNewSpeechEngineIsReady() {
        assertFalse(shouldResumeInitialCue(SpeechPlaybackState.Initializing, true, false))
        assertTrue(shouldResumeInitialCue(SpeechPlaybackState.Ready, true, false))
        assertFalse(shouldResumeInitialCue(SpeechPlaybackState.Ready, true, true))
        assertFalse(shouldResumeInitialCue(SpeechPlaybackState.Ready, false, false))
        assertFalse(shouldResumeInitialCue(SpeechPlaybackState.Completed(SpokenCue.INITIAL), true, false))
        assertFalse(shouldResumeInitialCue(SpeechPlaybackState.Error(SpokenCue.INITIAL), true, false))
    }

}
