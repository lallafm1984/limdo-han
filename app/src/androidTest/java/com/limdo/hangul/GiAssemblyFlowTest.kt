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

class GiAssemblyFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun giAssemblyWritesTwoStrokesAdvancesToRaAndReturnsHome() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()
        composeRule.onNode(hasContentDescription("기 조립 선택")).performClick()
        composeRule.onNode(hasContentDescription("이 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("기 조립 미완성")).assertExists()
        composeRule.onNode(hasContentDescription("기역 조각")).performClick()
        composeRule.onNode(hasContentDescription("이 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("완성한 기 쓰기 시작")).performClick()

        val canvas = composeRule.onNode(
            hasContentDescription("기를 2획으로", substring = true) and hasClickAction(),
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
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(600)
        composeRule.waitForIdle()
        composeRule.onNode(
            hasContentDescription("큰 쓰기판. 초록 점에서 시작해 기를 2획으로 그려요"),
        ).assertExists()
        val evidenceDir = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
            "loop213-iteration2",
        ).apply { mkdirs() }
        listOf("홈으로 돌아가기", "현재 글자 다시 쓰기", "이전 글자로 이동", "다음 글자로 이동")
            .forEach { composeRule.onNode(hasContentDescription(it)).assertExists() }
        val successFile = File(evidenceDir, "gi-success.png")
        FileOutputStream(successFile).use { output ->
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
                .compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        val hierarchyFile = File(evidenceDir, "gi-success-hierarchy.txt").apply {
            writeText(composeRule.onRoot(useUnmergedTree = true).printToString())
        }
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        listOf(
            "cp ${successFile.absolutePath} /data/local/tmp/loop213-gi-success.png",
            "cp ${hierarchyFile.absolutePath} /data/local/tmp/loop213-gi-success-hierarchy.txt",
        ).forEach { command -> uiAutomation.executeShellCommand(command).close() }
        Thread.sleep(20_000)
        composeRule.mainClock.autoAdvance = true
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(
                hasContentDescription("라를 5획으로", substring = true),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("보호자 메뉴")).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
