package com.example.limdo

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HeroVoiceAssetTest {
    @Test
    fun everyProductionStateHasOneShortFixedCue() {
        assertEquals(HeroVoiceCue.MENU_CONSONANTS, HeroVoiceCue.forMenu(LearningMenu.CONSONANTS))
        assertEquals(HeroVoiceCue.MENU_VOWELS, HeroVoiceCue.forMenu(LearningMenu.VOWELS))
        assertEquals(HeroVoiceCue.MENU_GANADA, HeroVoiceCue.forMenu(LearningMenu.GANADA))
        assertEquals(HeroVoiceCue.SUCCESS, HeroVoiceCue.forTraceResult(GieokTraceResult.SUCCESS))
        assertEquals(null, HeroVoiceCue.forTraceResult(GieokTraceResult.EMPTY))
        listOf(
            GieokTraceResult.WRONG_START,
            GieokTraceResult.WRONG_DIRECTION,
            GieokTraceResult.OFF_GUIDE,
            GieokTraceResult.INCOMPLETE,
        ).forEach { assertEquals(HeroVoiceCue.RETRY, HeroVoiceCue.forTraceResult(it)) }
        HeroVoiceCue.entries.forEach {
            assertTrue(it.transcript.length in 3..32)
            assertEquals(it.transcript.trim(), it.transcript)
        }
    }

    @Test
    fun bundledVoiceFilesHaveSafePcmShapeAndAudibleContent() {
        val rawDirectory = File("src/main/res/raw")
        val expectedNames = HeroVoiceCue.entries.map { "hero_${it.name.lowercase()}.wav" }.toSet()
        val files = rawDirectory.listFiles { file -> file.name.startsWith("hero_") && file.extension == "wav" }
            ?.associateBy { it.name }.orEmpty()
        assertEquals(expectedNames, files.keys)

        files.values.forEach { file ->
            val bytes = file.readBytes()
            assertEquals("RIFF", bytes.copyOfRange(0, 4).decodeToString())
            assertEquals("WAVE", bytes.copyOfRange(8, 12).decodeToString())
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            assertEquals("${file.name} PCM", 1, buffer.getShort(20).toInt())
            assertEquals("${file.name} mono", 1, buffer.getShort(22).toInt())
            assertEquals("${file.name} sample rate", 22_050, buffer.getInt(24))
            assertEquals("${file.name} bit depth", 16, buffer.getShort(34).toInt())
            val dataOffset = bytes.indexOfSequence("data".encodeToByteArray()) + 8
            assertTrue("${file.name} data chunk", dataOffset >= 8)
            val samples = bytes.copyOfRange(dataOffset, bytes.size).asShortSamples()
            val durationSeconds = samples.size / 22_050f
            assertTrue("${file.name} duration $durationSeconds", durationSeconds in 0.8f..5.0f)
            val peak = samples.maxOf { abs(it.toInt()) }
            assertTrue("${file.name} audible peak $peak", peak >= 2_000)
            assertTrue("${file.name} unclipped peak $peak", peak < 32_767)
            val edgeFrames = 220
            assertTrue("${file.name} quiet start", samples.take(edgeFrames).maxOf { abs(it.toInt()) } < 1_500)
            assertTrue("${file.name} quiet end", samples.takeLast(edgeFrames).maxOf { abs(it.toInt()) } < 1_500)
        }
    }

    private fun ByteArray.indexOfSequence(target: ByteArray): Int =
        indices.firstOrNull { start ->
            start + target.size <= size && target.indices.all { this[start + it] == target[it] }
        } ?: -1

    private fun ByteArray.asShortSamples(): List<Short> =
        ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().let { buffer ->
            List(buffer.remaining()) { buffer.get() }
        }
}
