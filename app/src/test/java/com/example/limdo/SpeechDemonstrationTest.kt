package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpeechDemonstrationTest {
    @Test
    fun mapsInitialSpeechRangesToThreeProductionStrokes() {
        val utterance = SpokenCue.INITIAL.utterance

        assertEquals(0, SpokenCue.INITIAL.demonstrationStrokeIndex(0))
        assertEquals(1, SpokenCue.INITIAL.demonstrationStrokeIndex(utterance.indexOf(". ") + 2))
        assertEquals(2, SpokenCue.INITIAL.demonstrationStrokeIndex(utterance.lastIndexOf(", ") + 2))
    }

    @Test
    fun ignoresNonInitialAndInvalidRanges() {
        assertNull(SpokenCue.SUCCESS.demonstrationStrokeIndex(0))
        assertNull(SpokenCue.INITIAL.demonstrationStrokeIndex(-1))
    }

    @Test
    fun mapsRetrySpeechToItsProductionStroke() {
        val retryCuesByStroke = listOf(
            listOf(
                SpokenCue.RETRY_START,
                SpokenCue.RETRY_DIRECTION,
                SpokenCue.RETRY_GUIDE,
                SpokenCue.RETRY_FINISH,
            ),
            listOf(
                SpokenCue.RETRY_SECOND_START,
                SpokenCue.RETRY_SECOND_DIRECTION,
                SpokenCue.RETRY_SECOND_GUIDE,
                SpokenCue.RETRY_SECOND_FINISH,
            ),
            listOf(
                SpokenCue.RETRY_THIRD_START,
                SpokenCue.RETRY_THIRD_DIRECTION,
                SpokenCue.RETRY_THIRD_GUIDE,
                SpokenCue.RETRY_THIRD_FINISH,
            ),
        )

        retryCuesByStroke.forEachIndexed { strokeIndex, cues ->
            cues.forEach { cue ->
                assertEquals(strokeIndex, cue.demonstrationStrokeIndex(0))
                assertEquals(strokeIndex, cue.demonstrationStrokeIndex(cue.utterance.lastIndex))
                assertNull(cue.demonstrationStrokeIndex(-1))
            }
        }
    }
}
