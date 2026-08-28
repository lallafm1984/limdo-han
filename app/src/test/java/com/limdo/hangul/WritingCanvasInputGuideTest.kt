package com.limdo.hangul

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WritingCanvasInputGuideTest {
    @Test
    fun inputFollowingArrowIsRemovedWhileIdleDemonstrationRemains() {
        val source = File("src/main/java/com/limdo/hangul/WritingCanvas.kt").readText()

        assertFalse(source.contains("inputGuideMotion"))
        assertFalse(source.contains("WritingCanvasGeometry.inputDirectionGuide("))
        assertFalse(source.contains("입력 중 다음 방향"))
        assertTrue(source.contains("demonstrationProgress"))
        assertTrue(source.contains("현재 획 시범"))
    }
}
