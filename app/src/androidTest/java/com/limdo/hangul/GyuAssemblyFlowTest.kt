package com.limdo.hangul

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import org.junit.Rule
import org.junit.Test

class GyuAssemblyFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun gyuAssemblyRejectsVowelFirstThenWritesAndAdvancesToGeu() {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[2].performClick()
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()

        composeRule.onNode(hasContentDescription("규 조립 선택")).performClick()
        composeRule.onNode(hasContentDescription("유 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("규 조립 미완성")).assertExists()

        composeRule.onNode(hasContentDescription("기역 조각")).performClick()
        composeRule.onNode(hasContentDescription("유 모음 조각")).performClick()
        composeRule.onNode(hasContentDescription("완성한 규 쓰기 시작")).performClick()

        val canvas = composeRule.onNode(
            hasContentDescription("규를 4획으로", substring = true) and hasClickAction(),
        )
        repeat(4) { canvas.performSemanticsAction(SemanticsActions.OnClick) }
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                hasContentDescription("그를 2획으로", substring = true),
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
