package com.limdo.hangul

import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardianVoiceRecordingTest {
    @Test
    fun onlyIsoBmffM4aHeaderIsAcceptedAsACompletedRecording() {
        val root = Files.createTempDirectory("limdo-guardian-voice-validation").toFile()
        val recording = GuardianVoiceStorage.finalFile(root, LessonId.GIEOK)
        recording.parentFile!!.mkdirs()

        recording.writeBytes(byteArrayOf(1, 2, 3))
        assertFalse(GuardianVoiceStorage.isSupportedM4a(recording))

        recording.writeBytes(ByteArray(16).also { bytes ->
            "ftyp".encodeToByteArray().copyInto(bytes, destinationOffset = 4)
        })
        assertTrue(GuardianVoiceStorage.isSupportedM4a(recording))

        recording.writeBytes(ByteArray(16))
        assertFalse(GuardianVoiceStorage.isSupportedM4a(recording))
    }

    @Test
    fun startRecordingUsesLessonSpecificNoBackupPathAndEightSecondLimit() {
        val noBackupRoot = File("/private/app/no_backup")
        val finalFile = GuardianVoiceStorage.finalFile(noBackupRoot, LessonId.GIEOK)
        val tempFile = GuardianVoiceStorage.tempFile(finalFile)

        assertEquals(File(noBackupRoot, "guardian_voice/gieok.m4a"), finalFile)
        assertEquals(File(noBackupRoot, "guardian_voice/.gieok.m4a.recording"), tempFile)
        assertEquals(8_000, GuardianVoiceStorage.MAX_DURATION_MILLIS)
    }

    @Test
    fun legacyStartIsPreferredAndCopiedWithoutDeletingEitherLegacyFile() {
        val root = Files.createTempDirectory("limdo-guardian-events").toFile()
        val start = GuardianVoiceStorage.legacyFile(root, LessonId.GIEOK, GuardianVoiceEvent.START)
        val success = GuardianVoiceStorage.legacyFile(root, LessonId.GIEOK, GuardianVoiceEvent.SUCCESS)
        start.parentFile!!.mkdirs()
        writeM4a(start, 1)
        writeM4a(success, 2)

        val unified = GuardianVoiceStorage.migrateLegacyRecording(root, LessonId.GIEOK)

        assertTrue(start.readBytes().contentEquals(unified.readBytes()))
        assertTrue(start.exists())
        assertTrue(success.exists())
    }

    @Test
    fun damagedLegacyStartFallsBackToSuccessWithoutDeletingSources() {
        val root = Files.createTempDirectory("limdo-guardian-fallback").toFile()
        val start = GuardianVoiceStorage.legacyFile(root, LessonId.GIEOK, GuardianVoiceEvent.START)
        val success = GuardianVoiceStorage.legacyFile(root, LessonId.GIEOK, GuardianVoiceEvent.SUCCESS)
        start.parentFile!!.mkdirs()
        start.writeBytes(byteArrayOf(1, 2, 3))
        writeM4a(success, 7)

        val unified = GuardianVoiceStorage.migrateLegacyRecording(root, LessonId.GIEOK)

        assertTrue(success.readBytes().contentEquals(unified.readBytes()))
        assertTrue(start.exists())
        assertTrue(success.exists())
    }

    @Test
    fun allThirtyEightLessonsHaveOneUniqueRecordingPath() {
        val root = File("/private/app/no_backup")
        val expectedKeys = GuardianLessonCatalog.lessons.map { GuardianVoiceKey(it.id) }
        val paths = GuardianVoiceCatalog.keys.map { key ->
            GuardianVoiceStorage.finalFile(root, key.lessonId).path
        }

        assertEquals(38, GuardianLessonCatalog.lessons.size)
        assertEquals(38, GuardianVoiceCatalog.keys.size)
        assertEquals(expectedKeys, GuardianVoiceCatalog.keys)
        assertEquals(38, paths.toSet().size)
    }

    @Test
    fun firstMiddleAndLastLessonEventsPreserveEveryOtherRecording() {
        val root = Files.createTempDirectory("limdo-guardian-all-lessons").toFile()
        val representativeKeys = listOf(
            GuardianVoiceCatalog.keys.first(),
            GuardianVoiceCatalog.keys[GuardianVoiceCatalog.keys.size / 2],
            GuardianVoiceCatalog.keys.last(),
        )
        GuardianVoiceCatalog.keys.forEach { key ->
            GuardianVoiceStorage.finalFile(root, key.lessonId).apply {
                parentFile!!.mkdirs()
                writeText("original-${key.lessonId}")
            }
        }

        representativeKeys.forEach { changedKey ->
            val before = GuardianVoiceCatalog.keys.associateWith { key ->
                GuardianVoiceStorage.finalFile(root, key.lessonId).readBytes()
            }
            val changed = GuardianVoiceStorage.finalFile(root, changedKey.lessonId)
            GuardianVoiceStorage.tempFile(changed).writeText("changed-$changedKey")
            GuardianVoiceStorage.commit(GuardianVoiceStorage.tempFile(changed), changed)
            GuardianVoiceCatalog.keys.filter { it != changedKey }.forEach { preservedKey ->
                val preserved = GuardianVoiceStorage.finalFile(root, preservedKey.lessonId)
                assertTrue(before.getValue(preservedKey).contentEquals(preserved.readBytes()))
            }
        }
    }

    @Test
    fun completedTemporaryFileAtomicallyReplacesExistingRecording() {
        val root = Files.createTempDirectory("limdo-guardian-voice").toFile()
        val finalFile = GuardianVoiceStorage.finalFile(root, LessonId.GIEOK)
        finalFile.parentFile!!.mkdirs()
        finalFile.writeText("old")
        val tempFile = GuardianVoiceStorage.tempFile(finalFile)
        tempFile.writeText("new recording")

        GuardianVoiceStorage.commit(tempFile, finalFile)

        assertEquals("new recording", finalFile.readText())
        assertFalse(tempFile.exists())
    }

    @Test
    fun backgroundReleaseDiscardsTemporaryFileWithoutChangingCompletedBytes() {
        val root = Files.createTempDirectory("limdo-guardian-background").toFile()
        val completed = GuardianVoiceStorage.finalFile(root, LessonId.GIEOK)
        completed.parentFile!!.mkdirs()
        val originalBytes = "completed".encodeToByteArray()
        completed.writeBytes(originalBytes)
        val temporary = GuardianVoiceStorage.tempFile(completed)
        temporary.writeText("unfinished")

        GuardianVoiceStorage.discardTemporaryFile(completed)

        assertFalse(temporary.exists())
        assertTrue(originalBytes.contentEquals(completed.readBytes()))
        val source = File(rootProject(), "app/src/main/java/com/limdo/hangul/GuardianVoiceRecording.kt").readText()
        assertTrue(source.contains("fun release()"))
        assertTrue(source.contains("stopRecording(save = false)\n        stopPlayback()\n        GuardianVoiceStorage.discardTemporaryFile(finalFile)"))
    }

    @Test
    fun playbackLifecycleUsesTheSameReleasePathForBackgroundAndScreenExit() {
        val project = rootProject()
        val controllerSource = File(
            project,
            "app/src/main/java/com/limdo/hangul/GuardianVoiceRecording.kt",
        ).readText()
        val activitySource = File(
            project,
            "app/src/main/java/com/limdo/hangul/MainActivity.kt",
        ).readText()

        assertTrue(controllerSource.contains("fun release()"))
        assertTrue(controllerSource.contains("stopRecording(save = false)\n        stopPlayback()"))
        assertTrue(activitySource.contains("override fun onStop()"))
        assertTrue(activitySource.contains("guardianVoiceControllers.values.forEach(GuardianVoiceController::release)"))
        assertTrue(activitySource.contains("onDispose { controller.release() }"))
    }

    @Test
    fun productionHasNoSyntheticSpeechEngineOrLegacyPlaybackStateModel() {
        val sourceRoot = File(rootProject(), "app/src/main/java")
        val productionSource = sourceRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .joinToString("\n") { it.readText() }

        assertFalse(productionSource.contains("android.speech.tts.TextToSpeech"))
        assertFalse(productionSource.contains("SpeechPlaybackState"))
        assertFalse(productionSource.contains("SpeechPlaybackTracker"))
        assertFalse(productionSource.contains("shouldResumeSuccessCue"))
        assertFalse(productionSource.contains("shouldResumeInitialCue"))
        assertTrue(productionSource.contains("START(\"start\", \"쓰기 전\")"))
        assertTrue(productionSource.contains("SUCCESS(\"success\", \"정답 후\")"))
        assertTrue(productionSource.contains("GuardianVoiceState.EMPTY"))
        assertFalse(File(rootProject(), "app/src/main/java/com/limdo/hangul/MainActivity.kt").readText().contains("GuardianEventAction("))
    }

    @Test
    fun writingFlowUsesTheSameLessonRecordingOnEntryAndAfterSuccess() {
        val source = File(rootProject(), "app/src/main/java/com/limdo/hangul/MainActivity.kt").readText()

        assertTrue(source.contains("guardianVoiceControllers[GuardianVoiceKey(currentLesson.id)]"))
        assertTrue(source.contains("LaunchedEffect(currentLesson.id, guardianIndex) {"))
        assertTrue(source.contains("guardianLessonProgressStorage.markPracticed(currentLesson.id)"))
        assertTrue(source.contains("currentVoiceController.play()"))
        assertTrue(source.contains("if (traceResult == GieokTraceResult.SUCCESS)"))
        assertEquals(1, Regex("currentVoiceController\\.play\\(\\)").findAll(source).count())
        assertEquals(1, Regex("currentVoiceController\\.play \\{").findAll(source).count())
        assertTrue(source.contains("onDispose { currentVoiceController.stopPlayback() }"))
        assertEquals(5, Regex("currentVoiceController\\.stopPlayback\\(\\)").findAll(source).count())
        assertTrue(source.contains("val playbackFinished = CompletableDeferred<Unit>()"))
        assertTrue(source.contains("val minimumSuccessVisibility = async"))
        assertTrue(source.contains("delay(SuccessCelebrationSpec.DURATION_MS.toLong())"))
        assertTrue(source.contains("minimumSuccessVisibility.await()"))
        assertTrue(source.contains("if (playbackStarted) playbackFinished.await()"))
        assertTrue(source.contains("currentLesson.id == successfulLessonId"))
        assertTrue(source.contains("LearningNavigation.nextLesson(menu, currentLesson)"))
    }

    @Test
    fun completedAndFailedSuccessPlaybackBothReleaseTheFlowWithoutDeletingRecording() {
        val source = File(rootProject(), "app/src/main/java/com/limdo/hangul/GuardianVoiceRecording.kt").readText()
        val playBody = source.substringAfter("fun play(onFinished: () -> Unit = {}): Boolean")
            .substringBefore("fun stopPlayback()")

        assertEquals(2, Regex("onFinished\\(\\)").findAll(playBody).count())
        assertTrue(playBody.contains("setOnCompletionListener"))
        assertTrue(playBody.contains("setOnErrorListener"))
        assertFalse(playBody.contains("finalFile.delete()"))
    }

    @Test
    fun playbackFailureNeverDeletesTheCompletedRecording() {
        val source = File(rootProject(), "app/src/main/java/com/limdo/hangul/GuardianVoiceRecording.kt").readText()
        val playBody = source.substringAfter("fun play(): Boolean").substringBefore("fun stopPlayback()")
        val validationBody = source.substringAfter("private fun validFinalFile()").substringBefore("\n    }")

        assertFalse(playBody.contains("finalFile.delete()"))
        assertFalse(validationBody.contains("finalFile.delete()"))
        assertEquals(1, Regex("finalFile\\.delete\\(\\)").findAll(source).count())
    }

    @Test
    fun recordingPermissionIsTheOnlyRuntimePermissionAndIsRequestedFromRecordingAction() {
        val project = rootProject()
        val manifest = File(project, "app/src/main/AndroidManifest.xml").readText()
        val source = File(project, "app/src/main/java/com/limdo/hangul/MainActivity.kt").readText()

        assertEquals(1, Regex("<uses-permission").findAll(manifest).count())
        assertTrue(manifest.contains("android.permission.RECORD_AUDIO"))
        assertFalse(manifest.contains("INTERNET"))
        assertFalse(manifest.contains("WRITE_EXTERNAL_STORAGE"))
        assertTrue(source.contains("permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)"))
        assertTrue(source.contains("LaunchedEffect(controller)"))
        assertTrue(source.contains("permissionDenied = false"))
        assertTrue(source.contains("권한이 없어\\n녹음하지 않았어요.\\n학습은 그대로예요."))
        assertTrue(source.contains("lineHeight = 21.sp"))
        assertTrue(source.contains("maxLines = 3"))
        assertTrue(source.contains("GuardianVoiceState.EMPTY -> GuardianTextAction(\"녹음\""))
    }

    @Test
    fun guardianRecordingBackReturnsToGuardianList() {
        assertEquals(
            LearningDestination.GuardianLessons,
            LearningNavigation.back(LearningDestination.GuardianStartRecording(LessonId.GIEOK)),
        )
    }

    private fun rootProject(): File {
        val workingDirectory = File(requireNotNull(System.getProperty("user.dir")))
        return if (File(workingDirectory, "app").isDirectory) workingDirectory else workingDirectory.parentFile
    }

    private fun writeM4a(file: File, marker: Byte) {
        file.writeBytes(ByteArray(16).also { bytes ->
            "ftyp".encodeToByteArray().copyInto(bytes, destinationOffset = 4)
            bytes[12] = marker
        })
    }
}
