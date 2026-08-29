package com.limdo.hangul

import android.graphics.Bitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import java.io.*
import org.junit.Rule
import org.junit.Test

class RyeoAssemblyFlowTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun ryeoAssemblyRejectsVowelFirstThenWritesAndAdvances() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()
        captureEvidence("target-selection")
        composeRule.onNode(hasContentDescription("려 조립 선택")).performClick()
        composeRule.waitUntil(3_000) { composeRule.onAllNodes(hasContentDescription("려 조립 미완성")).fetchSemanticsNodes().isNotEmpty() }
        captureEvidence("ryeo-start")
        composeRule.onNode(hasContentDescription("여 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("려 조립 미완성")).assertExists()
        captureEvidence("ryeo-wrong-order")
        composeRule.onNode(hasContentDescription("리을 조각")).performClick()
        composeRule.onNode(hasContentDescription("여 모음 조각")).performClick()
        captureEvidence("ryeo-complete")
        composeRule.onNode(hasContentDescription("완성한 려 쓰기 시작")).performClick()
        captureEvidence("ryeo-writing")

        composeRule.mainClock.autoAdvance = false
        repeat(6) {
            composeRule.onNode(hasContentDescription("려를 6획으로", substring = true) and hasClickAction())
                .performSemanticsAction(SemanticsActions.OnClick)
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()
        }
        composeRule.onNode(hasContentDescription("정답이에요", substring = true), useUnmergedTree = true).assertExists()
        composeRule.mainClock.advanceTimeBy(1_000)
        Thread.sleep(1_000)
        captureEvidence("ryeo-success")
        composeRule.onNode(hasContentDescription("다음 글자로 이동")).performClick()
        composeRule.mainClock.autoAdvance = true
        captureEvidence("ryeo-after-next")
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()
    }

    private fun captureEvidence(name: String) {
        composeRule.waitForIdle()
        Thread.sleep(500)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation
        val evidenceDir = File(instrumentation.targetContext.getExternalFilesDir(null), "loop234-iteration1").apply { mkdirs() }
        val screenshot = File(evidenceDir, "$name.png")
        val focus = File(evidenceDir, "$name-focus.txt")
        val hierarchy = File(evidenceDir, "$name-hierarchy.txt")
        FileOutputStream(screenshot).use { uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, it) }
        uiAutomation.executeShellCommand("dumpsys window").use { descriptor -> FileInputStream(descriptor.fileDescriptor).use { input -> focus.outputStream().use { input.copyTo(it) } } }
        hierarchy.writeText(composeRule.onRoot(useUnmergedTree = true).printToString())
        listOf(screenshot, focus, hierarchy).forEach { file ->
            uiAutomation.executeShellCommand("cp ${file.absolutePath} /data/local/tmp/loop234-iteration1-${file.name}").use { FileInputStream(it.fileDescriptor).use(FileInputStream::readBytes) }
        }
    }
}
