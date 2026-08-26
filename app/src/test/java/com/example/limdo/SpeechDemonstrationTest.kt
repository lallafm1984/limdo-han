package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpeechDemonstrationTest {
    @Test
    fun mapsInitialSpeechRangesToThreeProductionStrokes() {
        val utterance = SpokenCue.INITIAL.utterance
        val boundaries = sentenceStarts(utterance)

        assertEquals(0, SpokenCue.INITIAL.demonstrationStrokeIndex(0))
        assertEquals(1, SpokenCue.INITIAL.demonstrationStrokeIndex(boundaries[1]))
        assertEquals(2, SpokenCue.INITIAL.demonstrationStrokeIndex(boundaries[2]))
    }

    @Test
    fun mapsDaInitialSpeechRangesToFourProductionStrokes() {
        val boundaries = sentenceStarts(SpokenCue.INITIAL_DA.utterance)

        assertEquals(4, boundaries.size)
        boundaries.forEachIndexed { strokeIndex, characterStart ->
            assertEquals(strokeIndex, SpokenCue.INITIAL_DA.demonstrationStrokeIndex(characterStart))
        }
    }

    @Test
    fun ignoresNonInitialAndInvalidRanges() {
        assertNull(SpokenCue.MENU_CONSONANTS.demonstrationStrokeIndex(0))
        assertNull(SpokenCue.MENU_VOWELS.demonstrationStrokeIndex(0))
        assertNull(SpokenCue.MENU_GANADA.demonstrationStrokeIndex(0))
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
            listOf(
                SpokenCue.RETRY_FOURTH_START,
                SpokenCue.RETRY_FOURTH_RIGHT_DIRECTION,
                SpokenCue.RETRY_FOURTH_GUIDE,
                SpokenCue.RETRY_FOURTH_FINISH,
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

    private fun sentenceStarts(utterance: String): List<Int> = buildList {
        add(0)
        var boundary = utterance.indexOf(". ")
        while (boundary >= 0) {
            add(boundary + 2)
            boundary = utterance.indexOf(". ", boundary + 2)
        }
    }
}
