package com.limdo.hangul

import android.provider.Settings
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReducedMotionSuccessTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private var originalAnimatorScale = "1"

    @Before
    fun disableAnimatorDuration() {
        originalAnimatorScale = Settings.Global.getFloat(
            instrumentation.targetContext.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ).toString()
        instrumentation.uiAutomation.executeShellCommand(
            "settings put global animator_duration_scale 0",
        ).close()
    }

    @After
    fun restoreAnimatorDuration() {
        instrumentation.uiAutomation.executeShellCommand(
            "settings put global animator_duration_scale $originalAnimatorScale",
        ).close()
    }

    @Test
    fun zeroAnimatorScaleKeepsMinimumSuccessVisibilityBeforeAutoNext() {
        composeRule.onNode(hasText("ㄱ ㄴ ㄷ")).performClick()
        composeRule.onNode(hasContentDescription("ㄱ 쓰기 시작")).performClick()

        val gieokCanvas = composeRule.onNode(
            hasContentDescription("0/1획 완료", substring = true) and hasClickAction(),
        )
        val completedAt = composeRule.mainClock.currentTime
        gieokCanvas.performSemanticsAction(SemanticsActions.OnClick)

        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                hasContentDescription("ㄴ", substring = true) and
                    hasContentDescription("0/1획 완료", substring = true),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        assertTrue(
            composeRule.mainClock.currentTime - completedAt >= SuccessCelebrationSpec.DURATION_MS,
        )
    }
}
