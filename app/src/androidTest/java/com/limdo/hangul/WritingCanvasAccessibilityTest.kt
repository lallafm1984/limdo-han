package com.limdo.hangul

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performSemanticsAction
import org.junit.Rule
import org.junit.Test

class WritingCanvasAccessibilityTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun semanticsClickAdvancesOneProductionStrokeAtATimeAndCompletes() {
        composeRule.setContent {
            WritingCanvas(
                lesson = GaLesson,
                contentDescription = "큰 쓰기판",
                clearRequest = 0,
                inputEnabled = true,
                demonstrationStrokeIndex = null,
                demonstrationDurationMs = 3_000,
                retryStartMarkerScale = 1f,
                guideDotScale = 1f,
                onTraceResult = { _, _ -> },
                modifier = Modifier.fillMaxSize(),
            )
        }

        val actionableCanvas = composeRule.onNode(
            hasContentDescription("큰 쓰기판", substring = true) and hasClickAction(),
        )
        composeRule.onNode(hasContentDescription("0/3획 완료", substring = true)).assertExists()

        actionableCanvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(hasContentDescription("1/3획 완료", substring = true)).assertExists()

        actionableCanvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(hasContentDescription("2/3획 완료", substring = true)).assertExists()

        actionableCanvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(
            hasContentDescription("큰 쓰기판", substring = true) and hasClickAction(),
        ).assertDoesNotExist()
        composeRule.onNode(hasContentDescription("큰 쓰기판")).assertExists()
    }
}
