package com.nullplaying.limdo

import org.junit.Assert.assertEquals
import org.junit.Test

class KoreanParticlesTest {
    @Test
    fun `final consonant selects eul and open syllable selects reul`() {
        assertEquals("를", objectiveParticleFor("가"))
        assertEquals("을", objectiveParticleFor("각"))
        assertEquals("을", objectiveParticleFor("갈"))
        assertEquals("을", objectiveParticleFor("감"))
        assertEquals("을", objectiveParticleFor("갑"))
        assertEquals("를", objectiveParticleFor("ㄱ"))
    }

    @Test
    fun `only the final displayed syllable decides the particle`() {
        assertEquals("를", objectiveParticleFor("한글가"))
        assertEquals("을", objectiveParticleFor("한글"))
    }

    @Test
    fun `empty and non Hangul text use reul as a stable fallback`() {
        assertEquals("를", objectiveParticleFor(""))
        assertEquals("를", objectiveParticleFor("A"))
        assertEquals("를", objectiveParticleFor("1"))
    }

    @Test
    fun `canvas descriptions contain the selected particle`() {
        assertEquals(
            "큰 쓰기판. 초록 점에서 시작해 가를 3획으로 그려요",
            writingCanvasDescription("가", 3),
        )
        assertEquals(
            "큰 쓰기판. 초록 점에서 시작해 갈을 6획으로 그려요",
            writingCanvasDescription("갈", 6),
        )
    }
}
