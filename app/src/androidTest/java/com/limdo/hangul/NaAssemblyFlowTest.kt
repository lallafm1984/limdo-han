package com.limdo.hangul

import android.graphics.Bitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.printToString
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test

class NaAssemblyFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun naAssemblyWritesThreeStrokesAdvancesToDaAndReturnsHome() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()
        composeRule.onNode(hasContentDescription("나 조립 선택")).performClick()
        composeRule.onNode(hasContentDescription("니은 조각")).performClick()
        composeRule.onNode(hasContentDescription("아 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("완성한 나 쓰기 시작")).performClick()

        val canvas = composeRule.onNode(
            hasContentDescription("나를 3획으로", substring = true) and hasClickAction(),
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
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(600)
        composeRule.waitForIdle()
        composeRule.onNode(
            hasContentDescription("큰 쓰기판. 초록 점에서 시작해 나를 3획으로 그려요"),
        ).assertExists()
        listOf("홈으로 돌아가기", "현재 글자 다시 쓰기", "이전 글자로 이동", "다음 글자로 이동")
            .forEach { composeRule.onNode(hasContentDescription(it)).assertExists() }
        Thread.sleep(1_000)

        val evidenceDir = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
            "loop214-iteration2",
        ).apply { mkdirs() }
        val successFile = File(evidenceDir, "na-success.png")
        FileOutputStream(successFile).use { output ->
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
                .compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        val hierarchyFile = File(evidenceDir, "na-success-hierarchy.txt").apply {
            writeText(composeRule.onRoot(useUnmergedTree = true).printToString())
        }
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val focusFile = File(evidenceDir, "na-success-focus.txt")
        uiAutomation.executeShellCommand("dumpsys window").use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).use { input ->
                focusFile.outputStream().use { output -> input.copyTo(output) }
            }
        }
        listOf(
            "cp ${successFile.absolutePath} /data/local/tmp/loop214-na-success.png",
            "cp ${hierarchyFile.absolutePath} /data/local/tmp/loop214-na-success-hierarchy.txt",
            "cp ${focusFile.absolutePath} /data/local/tmp/loop214-na-success-focus.txt",
        ).forEach { command ->
            uiAutomation.executeShellCommand(command).use { descriptor ->
                FileInputStream(descriptor.fileDescriptor).use { it.readBytes() }
            }
        }

        composeRule.onNode(hasContentDescription("다음 글자로 이동")).performClick()
        composeRule.mainClock.autoAdvance = true
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(
                hasContentDescription("다를 4획으로", substring = true),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("보호자 메뉴")).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
