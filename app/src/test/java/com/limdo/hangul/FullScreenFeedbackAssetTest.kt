package com.limdo.hangul

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FullScreenFeedbackAssetTest {
    private data class PngHeader(val width: Int, val height: Int, val colorType: Int)

    @Test
    fun successImageIsSquareRgbaAndRetryImageIsRemoved() {
        val success = feedbackImage("limdo_success_fullscreen_feedback.png")
        assertEquals(FullScreenFeedbackSpec.SOURCE_SIZE_PX, success.width)
        assertEquals(FullScreenFeedbackSpec.SOURCE_SIZE_PX, success.height)
        assertEquals(PNG_TRUECOLOR_WITH_ALPHA, success.colorType)
        assertFalse(File("src/main/res/drawable-nodpi/limdo_retry_fullscreen_feedback.png").exists())
    }

    private fun feedbackImage(filename: String): PngHeader {
        val bytes = File("src/main/res/drawable-nodpi/$filename")
            .also { assertTrue(it.isFile) }
            .readBytes()
        assertTrue(bytes.copyOfRange(0, PNG_SIGNATURE.size).contentEquals(PNG_SIGNATURE))
        assertEquals("IHDR", String(bytes, 12, 4, StandardCharsets.US_ASCII))
        val dimensions = ByteBuffer.wrap(bytes, 16, 8).order(ByteOrder.BIG_ENDIAN)
        return PngHeader(
            width = dimensions.int,
            height = dimensions.int,
            colorType = bytes[25].toInt() and 0xFF,
        )
    }

    private companion object {
        val PNG_SIGNATURE = byteArrayOf(
            0x89.toByte(),
            0x50,
            0x4E,
            0x47,
            0x0D,
            0x0A,
            0x1A,
            0x0A,
        )
        const val PNG_TRUECOLOR_WITH_ALPHA = 6
    }
}
