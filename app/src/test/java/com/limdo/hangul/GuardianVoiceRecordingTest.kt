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

        assertEquals(File(noBackupRoot, "guardian_voice/gieok_start.m4a"), finalFile)
        assertEquals(File(noBackupRoot, "guardian_voice/.gieok_start.m4a.recording"), tempFile)
        assertEquals(8_000, GuardianVoiceStorage.MAX_DURATION_MILLIS)
    }

    @Test
    fun successRecordingUsesAnEventSpecificPathAndCannotReplaceStartBytes() {
        val root = Files.createTempDirectory("limdo-guardian-events").toFile()
        val start = GuardianVoiceStorage.finalFile(root, LessonId.GIEOK, GuardianVoiceEvent.START)
        val success = GuardianVoiceStorage.finalFile(root, LessonId.GIEOK, GuardianVoiceEvent.SUCCESS)
        val otherLesson = GuardianVoiceStorage.finalFile(root, LessonId.NIEUN, GuardianVoiceEvent.START)
        start.parentFile!!.mkdirs()
        start.writeBytes("start bytes".encodeToByteArray())
        otherLesson.writeBytes("other lesson bytes".encodeToByteArray())
        val originalStart = start.readBytes()
        val originalOtherLesson = otherLesson.readBytes()
        val successTemp = GuardianVoiceStorage.tempFile(success)
        successTemp.writeBytes("success bytes".encodeToByteArray())

        GuardianVoiceStorage.commit(successTemp, success)
        success.delete()

        assertEquals(File(root, "guardian_voice/gieok_success.m4a"), success)
        assertTrue(originalStart.contentEquals(start.readBytes()))
        assertTrue(originalOtherLesson.contentEquals(otherLesson.readBytes()))
        assertTrue(start.exists())
        assertFalse(success.exists())
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
    fun recordingPermissionIsTheOnlyRuntimePermissionAndIsRequestedFromRecordingAction() {
        val workingDirectory = File(requireNotNull(System.getProperty("user.dir")))
        val project = if (File(workingDirectory, "app").isDirectory) workingDirectory else workingDirectory.parentFile
        val manifest = File(project, "app/src/main/AndroidManifest.xml").readText()
        val source = File(project, "app/src/main/java/com/limdo/hangul/MainActivity.kt").readText()

        assertEquals(1, Regex("<uses-permission").findAll(manifest).count())
        assertTrue(manifest.contains("android.permission.RECORD_AUDIO"))
        assertFalse(manifest.contains("INTERNET"))
        assertFalse(manifest.contains("WRITE_EXTERNAL_STORAGE"))
        assertTrue(source.contains("permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)"))
        assertTrue(source.contains("GuardianVoiceState.EMPTY -> GuardianTextAction(\"녹음\""))
    }

    @Test
    fun guardianRecordingBackReturnsToGuardianList() {
        assertEquals(
            LearningDestination.GuardianLessons,
            LearningNavigation.back(LearningDestination.GuardianStartRecording(LessonId.GIEOK)),
        )
    }
}
