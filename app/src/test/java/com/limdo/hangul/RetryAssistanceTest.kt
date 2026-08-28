package com.limdo.hangul

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RetryAssistanceTest {
    @Test
    fun assistanceHasFiniteMonotonicLevels() {
        assertEquals(0, RetryAssistanceSpec.level(-1))
        assertEquals(0, RetryAssistanceSpec.level(0))
        assertEquals(1, RetryAssistanceSpec.level(1))
        assertEquals(2, RetryAssistanceSpec.level(2))
        assertEquals(2, RetryAssistanceSpec.level(100))

        val levels = 0..RetryAssistanceSpec.MAX_LEVEL
        val durations = levels.map(RetryAssistanceSpec::demonstrationDurationMs)
        val startScales = levels.map(RetryAssistanceSpec::startMarkerScale)
        val dotScales = levels.map(RetryAssistanceSpec::guideDotScale)

        assertEquals(listOf(3_000, 3_600, 4_200), durations)
        assertTrue(durations.zipWithNext().all { (before, after) -> after > before })
        assertTrue(startScales.zipWithNext().all { (before, after) -> after > before })
        assertTrue(dotScales.zipWithNext().all { (before, after) -> after > before })
        assertEquals(1.3f, startScales.last())
        assertEquals(1.4f, dotScales.last())
    }
}
