package com.limdo.hangul

import androidx.compose.ui.semantics.SemanticsActions
import android.graphics.Bitmap
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test

class GeuAssemblyFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun geuAssemblyWritesTwoStrokesAdvancesToGiAndReturnsHome() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()
        composeRule.onNode(hasContentDescription("그 조립 선택")).performClick()
        composeRule.onNode(hasContentDescription("기역 조각")).performClick()
        composeRule.onNode(hasContentDescription("으 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("완성한 그 쓰기 시작")).performClick()

        val canvas = composeRule.onNode(
            hasContentDescription("그를 2획으로", substring = true) and hasClickAction(),
        )
        canvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
        canvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNode(
            hasContentDescription("정답이에요", substring = true),
            useUnmergedTree = true,
        ).assertExists()
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        composeRule.onNode(
            hasContentDescription("큰 쓰기판. 초록 점에서 시작해 그를 2획으로 그려요"),
        ).assertExists()
        val evidenceDir = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
            "loop212-iteration3",
        ).apply { mkdirs() }
        listOf("홈으로 돌아가기", "현재 글자 다시 쓰기", "이전 글자로 이동", "다음 글자로 이동")
            .forEach { composeRule.onNode(hasContentDescription(it)).assertExists() }
        FileOutputStream(File(evidenceDir, "geu-success.png")).use { output ->
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
                .compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        File(evidenceDir, "geu-success-semantics.txt").writeText(
            "정답이에요\n큰 쓰기판. 그를 2획으로 그려요\n홈으로 돌아가기\n현재 글자 다시 쓰기\n이전 글자로 이동\n다음 글자로 이동\n",
        )
        File(evidenceDir, "geu-success-hierarchy.txt").writeText(
            composeRule.onRoot(useUnmergedTree = true).printToString(),
        )
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        listOf(
            "cp ${File(evidenceDir, "geu-success.png").absolutePath} /data/local/tmp/loop212-geu-success.png",
            "cp ${File(evidenceDir, "geu-success-semantics.txt").absolutePath} /data/local/tmp/loop212-geu-success-semantics.txt",
            "cp ${File(evidenceDir, "geu-success-hierarchy.txt").absolutePath} /data/local/tmp/loop212-geu-success-hierarchy.txt",
        ).forEach { command ->
            uiAutomation.executeShellCommand(command).use { descriptor ->
                FileOutputStream(File(evidenceDir, "shell-command.tmp")).use { output ->
                    descriptor.fileDescriptor.let { java.io.FileInputStream(it).copyTo(output) }
                }
            }
        }
        Thread.sleep(20_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(
                hasContentDescription("기를 2획으로", substring = true),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                hasContentDescription("보호자 메뉴"),
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
