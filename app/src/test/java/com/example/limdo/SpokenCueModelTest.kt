package com.example.limdo

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
            assertFalse(cue.utterance.length > 40)
        }
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
