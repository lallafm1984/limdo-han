package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}
