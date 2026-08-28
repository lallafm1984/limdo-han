package com.limdo.hangul

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test

class NyeoAssemblyFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun nyeoAssemblyRejectsVowelFirstThenWritesAndAdvancesToDa() {
        composeRule.onAllNodes(hasContentDescription("\uD559\uC2B5", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("\uAC00 \uC4F0\uAE30 \uC2DC\uC791")).performClick()
        captureEvidence("target-selection")
        composeRule.onNode(hasContentDescription("\uB140 \uC870\uB9BD \uC120\uD0DD")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("\uB140 \uC870\uB9BD \uBBF8\uC644\uC131")).fetchSemanticsNodes().isNotEmpty()
        }
        SystemClock.sleep(300)
        captureEvidence("nyeo-start")
        composeRule.onNode(hasContentDescription("\uC5EC \uBAA8\uC74C \uC870\uAC01")).performClick()
        composeRule.onNode(hasContentDescription("\uB140 \uC870\uB9BD \uBBF8\uC644\uC131")).assertExists()
        captureEvidence("nyeo-wrong-order")
        composeRule.onNode(hasContentDescription("\uB2C8\uC740 \uC870\uAC01")).performClick()
        composeRule.onNode(hasContentDescription("\uC5EC \uBAA8\uC74C \uC870\uAC01")).performClick()
        captureEvidence("nyeo-complete")
        composeRule.onNode(hasContentDescription("\uC644\uC131\uD55C \uB140 \uC4F0\uAE30 \uC2DC\uC791")).performClick()
        captureEvidence("nyeo-writing")

        val canvas = composeRule.onNode(
            hasContentDescription("\uB140\uB97C 4\uD68D\uC73C\uB85C", substring = true) and hasClickAction(),
        )
        repeat(3) {
            canvas.performSemanticsAction(SemanticsActions.OnClick)
            composeRule.waitForIdle()
        }
        composeRule.mainClock.autoAdvance = false
        canvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNode(
            hasContentDescription("\uC815\uB2F5\uC774\uC5D0\uC694", substring = true),
            useUnmergedTree = true,
        ).assertExists()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(600)
        composeRule.waitForIdle()
        captureEvidence("nyeo-success")
        composeRule.onNode(hasContentDescription("\uB2E4\uC74C \uAE00\uC790\uB85C \uC774\uB3D9")).performClick()
        composeRule.mainClock.autoAdvance = true
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(
                hasContentDescription("\uB2E4\uB97C 4\uD68D\uC73C\uB85C", substring = true),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasContentDescription("\uD648\uC73C\uB85C \uB3CC\uC544\uAC00\uAE30")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("\uBCF4\uD638\uC790 \uBA54\uB274")).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun captureEvidence(name: String) {
        composeRule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation
        val evidenceDir = File(
            instrumentation.targetContext.getExternalFilesDir(null),
            "loop216-iteration2",
        ).apply { mkdirs() }
        val screenshot = File(evidenceDir, "$name.png")
        val focus = File(evidenceDir, "$name-focus.txt")
        val hierarchy = File(evidenceDir, "$name-hierarchy.txt")
        FileOutputStream(screenshot).use { output ->
            uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        uiAutomation.executeShellCommand("dumpsys window").use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).use { input ->
                focus.outputStream().use { output -> input.copyTo(output) }
            }
        }
        FileOutputStream(hierarchy).use { output ->
            output.write(composeRule.onRoot(useUnmergedTree = true).printToString().toByteArray())
        }
        listOf(screenshot, focus, hierarchy).forEach { file ->
            uiAutomation.executeShellCommand("cp ${file.absolutePath} /data/local/tmp/loop216-iteration2-${file.name}").use {
                FileInputStream(it.fileDescriptor).use(FileInputStream::readBytes)
            }
        }
    }
}
