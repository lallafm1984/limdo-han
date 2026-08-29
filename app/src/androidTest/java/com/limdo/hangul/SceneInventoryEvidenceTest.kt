package com.limdo.hangul

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.system.Os
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasStateDescription
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import org.junit.Rule
import org.junit.Test

class SceneInventoryEvidenceTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun reachableStableScenesAreCapturedFromHomeNavigation() {
        capture("01-home")

        openMenu(0)
        capture("02-consonant-selection")
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()

        openMenu(1)
        capture("03-vowel-selection")
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()

        openMenu(2)
        capture("04-ganada-selection")
        composeRule.onNode(hasContentDescription("가 쓰기 시작")).performClick()
        capture("05-writing-initial")

        val gaCanvas = composeRule.onNode(
            hasContentDescription("0/3획 완료", substring = true) and hasClickAction(),
        )
        gaCanvas.performSemanticsAction(SemanticsActions.OnClick)
        composeRule.onNode(hasContentDescription("1/3획 완료", substring = true)).assertExists()
        capture("14-writing-one-stroke-progress")

        composeRule.onNode(hasContentDescription("1/3획 완료", substring = true))
            .performTouchInput { click(Offset(width - 4f, height - 4f)) }
        capture("15-writing-retry", settle = false)
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("0/3획 완료", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }

        repeat(2) {
            composeRule.onNode(hasContentDescription("${it}/3획 완료", substring = true))
                .performSemanticsAction(SemanticsActions.OnClick)
        }
        composeRule.mainClock.autoAdvance = false
        composeRule.onNode(hasContentDescription("2/3획 완료", substring = true))
            .performSemanticsAction(SemanticsActions.OnClick)
        composeRule.mainClock.advanceTimeBy(200)
        capture("16-writing-success", settle = false)
        composeRule.mainClock.autoAdvance = true
        composeRule.onNode(hasContentDescription("홈으로 돌아가기")).performClick()

        composeRule.onNode(hasContentDescription("보호자 메뉴"))
            .performSemanticsAction(SemanticsActions.OnLongClick)
        capture("06-guardian-recording-grid")
        composeRule.onNode(hasContentDescription("목록 추가")).performClick()
        capture("07-guardian-add-list")
        composeRule.onNode(hasContentDescription("목록 편집")).performClick()
        capture("08-guardian-list-editor")
        composeRule.onNode(hasContentDescription("녹음 관리")).performClick()
        composeRule.onNode(hasContentDescription("lesson ㄱ, 녹음 열기")).performClick()
        capture("09-guardian-recording")

        composeRule.onNode(hasContentDescription("ㄱ 녹음 시작")).performClick()
        denyRecordingPermission()
        composeRule.onNode(hasContentDescription("ㄱ 녹음 시작")).assertExists()
        capture("18-guardian-recording-permission-denied")

        composeRule.onNode(hasContentDescription("ㄱ 녹음 시작")).performClick()
        acceptRecordingPermissionIfShown()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasStateDescription(GuardianVoiceState.RECORDING.name))
                .fetchSemanticsNodes().isNotEmpty()
        }
        capture("19-guardian-recording-permission-retry-active")

        composeRule.onNode(hasContentDescription("녹음 정지하고 저장")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasStateDescription(GuardianVoiceState.READY.name))
                .fetchSemanticsNodes().isNotEmpty()
        }
        capture("11-guardian-recording-ready")

        val damagedRecording = GuardianVoiceStorage.finalFile(
            InstrumentationRegistry.getInstrumentation().targetContext.noBackupFilesDir,
            LessonId.GIEOK,
        )
        val completedHash = sha256(damagedRecording)
        composeRule.onNode(hasContentDescription("기존 녹음을 보존하고 새로 녹음")).performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasStateDescription(GuardianVoiceState.RECORDING.name))
                .fetchSemanticsNodes().isNotEmpty()
        }
        SystemClock.sleep(300)
        val recordingDirectory = damagedRecording.parentFile!!
        try {
            Os.chmod(recordingDirectory.absolutePath, 0b101101101)
            composeRule.onNode(hasContentDescription("녹음 정지하고 저장")).performClick()
            composeRule.waitUntil(timeoutMillis = 3_000) {
                composeRule.onAllNodes(hasStateDescription(GuardianVoiceState.READY.name))
                    .fetchSemanticsNodes().isNotEmpty()
            }
        } finally {
            Os.chmod(recordingDirectory.absolutePath, 0b111000000)
        }
        check(sha256(damagedRecording) == completedHash)
        check(GuardianVoiceStorage.tempFile(damagedRecording).delete())
        capture("28-guardian-recording-save-failed-original-preserved")

        damagedRecording.writeBytes(byteArrayOf(0, 0, 0, 12, 'f'.code.toByte(), 't'.code.toByte(), 'y'.code.toByte(), 'p'.code.toByte(), 0, 0, 0, 0))
        val damagedHash = sha256(damagedRecording)
        composeRule.onNode(hasContentDescription("녹음 미리 듣기")).performClick()
        composeRule.waitForIdle()
        check(damagedRecording.isFile)
        check(sha256(damagedRecording) == damagedHash)
        composeRule.onNode(hasStateDescription(GuardianVoiceState.READY.name)).assertExists()
        capture("20-guardian-damaged-playback-preserved-ready")

        composeRule.onNode(hasContentDescription("ㄱ 녹음 삭제 확인")).performClick()
        capture("12-guardian-delete-confirmation")
        composeRule.onNode(hasContentDescription("ㄱ 녹음 보존")).performClick()
        composeRule.onNode(hasStateDescription(GuardianVoiceState.READY.name)).assertExists()
        capture("13-guardian-delete-cancelled-ready")

        composeRule.onNode(hasContentDescription("녹음 화면 닫기")).performClick()
        composeRule.onNode(hasContentDescription("목록 추가")).performClick()
        composeRule.onNode(hasContentDescription("lesson ㄱ, 목록에 추가")).performClick()
        composeRule.onNode(hasContentDescription("목록 편집")).performClick()
        composeRule.onNode(hasContentDescription("처음부터")).performClick()
        composeRule.onNode(
            hasContentDescription("0/1획 완료", substring = true) and hasClickAction(),
        ).performSemanticsAction(SemanticsActions.OnClick)
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("학습 목록을 다 했어요"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        capture("17-guardian-learning-complete")
    }

    @Test
    fun guardianPagedListStatesAreCapturedThroughProductionCallbacks() {
        val listStorage = GuardianLearningListStorage(
            InstrumentationRegistry.getInstrumentation().targetContext.noBackupFilesDir,
        )
        check(!listStorage.storageFile().exists() || listStorage.storageFile().delete())
        check(!listStorage.currentIndexStorageFile().exists() || listStorage.currentIndexStorageFile().delete())
        composeRule.activityRule.scenario.recreate()
        composeRule.waitForIdle()

        composeRule.onNode(hasContentDescription("\uBCF4\uD638\uC790 \uBA54\uB274"))
            .performSemanticsAction(SemanticsActions.OnLongClick)
        composeRule.onNode(hasContentDescription("\uBAA9\uB85D \uCD94\uAC00")).performClick()
        val lessonCards = composeRule.onAllNodes(hasContentDescription("lesson", substring = true))
        lessonCards[0].performClick()
        lessonCards[0].performClick()
        repeat(9) { index -> lessonCards[index + 1].performClick() }
        composeRule.onNode(hasContentDescription("\uBAA9\uB85D \uD3B8\uC9D1")).performClick()

        capture("21-guardian-list-first-page-duplicates")
        composeRule.onNode(hasContentDescription("2\uBC88 lesson \u3131")).performClick()
        capture("22-guardian-list-first-page-selected")
        composeRule.onNode(hasContentDescription("\uB4A4\uB85C")).performClick()
        capture("23-guardian-list-duplicate-moved")
        composeRule.onNode(hasContentDescription("\uB2E4\uC74C \uCABD")).performClick()
        capture("24-guardian-list-middle-page")
        composeRule.onNode(hasContentDescription("\uB2E4\uC74C \uCABD")).performClick()
        capture("25-guardian-list-last-page")
        composeRule.onNode(hasContentDescription("11\uBC88 lesson", substring = true)).performClick()
        capture("26-guardian-list-last-selected")
        composeRule.onNode(hasContentDescription("\uC0AD\uC81C")).performClick()
        capture("27-guardian-list-last-deleted")
    }

    private fun openMenu(index: Int) {
        composeRule.onAllNodes(hasContentDescription("학습", substring = true))[index].performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(hasContentDescription("쓰기 시작", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun acceptRecordingPermissionIfShown() {
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val deadline = SystemClock.uptimeMillis() + 3_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = uiAutomation.rootInActiveWindow
            val allow = root?.findAccessibilityNodeInfosByViewId(
                "com.android.permissioncontroller:id/permission_allow_foreground_only_button",
            )?.firstOrNull()
            if (allow != null) {
                check(allow.performAction(AccessibilityNodeInfo.ACTION_CLICK))
                return
            }
            SystemClock.sleep(100)
        }
    }

    private fun denyRecordingPermission() {
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val deadline = SystemClock.uptimeMillis() + 3_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = uiAutomation.rootInActiveWindow
            val deny = root?.findAccessibilityNodeInfosByViewId(
                "com.android.permissioncontroller:id/permission_deny_button",
            )?.firstOrNull()
            if (deny != null) {
                check(deny.performAction(AccessibilityNodeInfo.ACTION_CLICK))
                composeRule.waitForIdle()
                return
            }
            SystemClock.sleep(100)
        }
        error("녹음 권한 거부 버튼을 찾지 못함")
    }

    private fun sha256(file: File): String = MessageDigest.getInstance("SHA-256")
        .digest(file.readBytes())
        .joinToString("") { "%02x".format(it) }

    private fun capture(name: String, settle: Boolean = true) {
        if (settle) composeRule.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        SystemClock.sleep(if (settle) 600 else 80)
        if (settle) instrumentation.uiAutomation.waitForIdle(500, 2_000)
        val directory = File(
            instrumentation.targetContext.getExternalFilesDir(null),
            "loop237-iteration7-inventory",
        ).apply { mkdirs() }
        val screenshot = File(directory, "$name.png")
        val hierarchy = File(directory, "$name-hierarchy.txt")
        val focus = File(directory, "$name-focus.txt")
        FileOutputStream(screenshot).use {
            instrumentation.uiAutomation.takeScreenshot()
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        hierarchy.writeText(captureAccessibilityHierarchy())
        instrumentation.uiAutomation.executeShellCommand("dumpsys window").use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).use { input ->
                focus.outputStream().use { input.copyTo(it) }
            }
        }
        listOf(screenshot, hierarchy, focus).forEach { file ->
            instrumentation.uiAutomation.executeShellCommand(
                "cp ${file.absolutePath} /data/local/tmp/loop237-iteration7-${file.name}",
            ).use { descriptor ->
                FileInputStream(descriptor.fileDescriptor).use(FileInputStream::readBytes)
            }
        }
    }

    private fun captureAccessibilityHierarchy(): String {
        val root = InstrumentationRegistry.getInstrumentation().uiAutomation.rootInActiveWindow
            ?: return "root=null"
        return buildString { appendNode(root, depth = 0) }
    }

    private fun StringBuilder.appendNode(node: AccessibilityNodeInfo, depth: Int) {
        val bounds = Rect().also(node::getBoundsInScreen)
        append("  ".repeat(depth))
        append("class=").append(node.className)
        append(" text=").append(node.text)
        append(" contentDescription=").append(node.contentDescription)
        append(" bounds=").append(bounds.toShortString())
        append(" enabled=").append(node.isEnabled)
        append(" clickable=").append(node.isClickable)
        append('\n')
        repeat(node.childCount) { index ->
            node.getChild(index)?.let { appendNode(it, depth + 1) }
        }
    }
}
