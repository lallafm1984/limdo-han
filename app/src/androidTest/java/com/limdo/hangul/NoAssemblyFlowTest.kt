package com.limdo.hangul

import android.graphics.Bitmap
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

class NoAssemblyFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun noAssemblyRejectsVowelFirstThenWritesAndAdvancesToDa() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()
        captureEvidence("target-selection")
        composeRule.onNode(hasContentDescription("노 조립 선택")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("노 조립 미완성")).fetchSemanticsNodes().isNotEmpty()
        }
        captureEvidence("no-start")
        composeRule.onNode(hasContentDescription("오 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("노 조립 미완성")).assertExists()
        captureEvidence("no-wrong-order")
        composeRule.onNode(hasContentDescription("니은 조경")).assertDoesNotExist()
        composeRule.onNode(hasContentDescription("니은 조각")).performClick()
        composeRule.onNode(hasContentDescription("오 모음 조각")).performClick()
        captureEvidence("no-complete")
        composeRule.onNode(hasContentDescription("완성한 노 쓰기 시작")).performClick()
        captureEvidence("no-writing")

        val canvas = composeRule.onNode(
            hasContentDescription("노를 3획으로", substring = true) and hasClickAction(),
        )
        repeat(2) {
            canvas.performSemanticsAction(SemanticsActions.OnClick)
            composeRule.waitForIdle()
        }
        composeRule.mainClock.autoAdvance = false
        canvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNode(
            hasContentDescription("정답이에요", substring = true),
            useUnmergedTree = true,
        ).assertExists()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()
        Thread.sleep(1_000)
        captureEvidence("no-success")
        composeRule.onNode(hasContentDescription("다음 글자로 이동")).performClick()
        composeRule.mainClock.autoAdvance = true
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasContentDescription("다를 4획으로", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("보호자 메뉴")).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun captureEvidence(name: String) {
        composeRule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val uiAutomation = instrumentation.uiAutomation
        val evidenceDir = File(
            instrumentation.targetContext.getExternalFilesDir(null),
            "loop217-iteration2",
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
            uiAutomation.executeShellCommand("cp ${file.absolutePath} /data/local/tmp/loop217-iteration2-${file.name}").use {
                FileInputStream(it.fileDescriptor).use(FileInputStream::readBytes)
            }
        }
    }
}
