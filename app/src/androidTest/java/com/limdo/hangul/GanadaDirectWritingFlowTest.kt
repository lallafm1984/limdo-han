package com.limdo.hangul

import android.graphics.Bitmap
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test

class GanadaDirectWritingFlowTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun gaSelectionOpensWritingBoardWithoutAssemblySelection() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()

        composeRule.onNode(
            hasContentDescription("가를 3획으로", substring = true),
        ).assertExists()
        composeRule.onNode(hasContentDescription("가 조립 선택")).assertDoesNotExist()
        captureEvidence("ga-direct-writing")
    }

    private fun captureEvidence(name: String) {
        composeRule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation
        val evidenceDir = File(
            instrumentation.targetContext.getExternalFilesDir(null),
            "loop235-iteration1",
        ).apply { mkdirs() }
        val screenshot = File(evidenceDir, "$name.png")
        val focus = File(evidenceDir, "$name-focus.txt")
        val hierarchy = File(evidenceDir, "$name-hierarchy.txt")
        FileOutputStream(screenshot).use {
            uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        uiAutomation.executeShellCommand("dumpsys window").use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).use { input ->
                focus.outputStream().use { input.copyTo(it) }
            }
        }
        hierarchy.writeText(composeRule.onRoot(useUnmergedTree = true).printToString())
        listOf(screenshot, focus, hierarchy).forEach { file ->
            uiAutomation.executeShellCommand(
                "cp ${file.absolutePath} /data/local/tmp/loop235-iteration1-${file.name}",
            ).use { descriptor ->
                FileInputStream(descriptor.fileDescriptor).use(FileInputStream::readBytes)
            }
        }
    }
}
