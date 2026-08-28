package com.limdo.hangul

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal enum class GuardianVoiceState { EMPTY, RECORDING, READY, PLAYING }

internal object GuardianVoiceStorage {
    const val DIRECTORY = "guardian_voice"
    const val MAX_DURATION_MILLIS = 8_000

    fun finalFile(noBackupFilesDir: File, lessonId: LessonId): File =
        File(File(noBackupFilesDir, DIRECTORY), "${lessonId.name.lowercase()}_start.m4a")

    fun tempFile(finalFile: File): File = File(finalFile.parentFile, ".${finalFile.name}.recording")

    fun isSupportedM4a(file: File): Boolean {
        if (!file.isFile || file.length() < 12L) return false
        return runCatching {
            file.inputStream().use { input ->
                val header = ByteArray(12)
                input.read(header) == header.size &&
                    header.copyOfRange(4, 8).contentEquals(byteArrayOf('f'.code.toByte(), 't'.code.toByte(), 'y'.code.toByte(), 'p'.code.toByte()))
            }
        }.getOrDefault(false)
    }

    fun commit(tempFile: File, finalFile: File) {
        check(tempFile.parentFile == finalFile.parentFile)
        Files.move(
            tempFile.toPath(),
            finalFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
        )
    }
}

@Suppress("DEPRECATION")
internal class GuardianVoiceController(
    private val context: Context,
    private val lessonId: LessonId = LessonId.GIEOK,
) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var onStateChanged: ((GuardianVoiceState) -> Unit)? = null
    private val finalFile get() = GuardianVoiceStorage.finalFile(context.noBackupFilesDir, lessonId)
    private val tempFile get() = GuardianVoiceStorage.tempFile(finalFile)

    fun observe(callback: (GuardianVoiceState) -> Unit) {
        onStateChanged = callback
        callback(currentState())
    }

    fun currentState(): GuardianVoiceState = when {
        recorder != null -> GuardianVoiceState.RECORDING
        player != null -> GuardianVoiceState.PLAYING
        validFinalFile() -> GuardianVoiceState.READY
        else -> GuardianVoiceState.EMPTY
    }

    fun startRecording(): Boolean {
        stopPlayback()
        stopRecording(save = false)
        finalFile.parentFile?.mkdirs()
        tempFile.delete()
        val nextRecorder = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(context) else MediaRecorder()
        return runCatching {
            nextRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            nextRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            nextRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            nextRecorder.setAudioEncodingBitRate(96_000)
            nextRecorder.setAudioSamplingRate(44_100)
            nextRecorder.setMaxDuration(GuardianVoiceStorage.MAX_DURATION_MILLIS)
            nextRecorder.setOutputFile(tempFile.absolutePath)
            nextRecorder.setOnInfoListener { _, what, _ ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) stopRecording(save = true)
            }
            nextRecorder.prepare()
            nextRecorder.start()
            recorder = nextRecorder
            notifyState()
            true
        }.getOrElse {
            nextRecorder.release()
            tempFile.delete()
            notifyState()
            false
        }
    }

    fun stopRecording(save: Boolean = true) {
        val active = recorder ?: return
        recorder = null
        val stopped = runCatching { active.stop() }.isSuccess
        active.reset()
        active.release()
        if (save && stopped && tempFile.length() > 0L) {
            runCatching { GuardianVoiceStorage.commit(tempFile, finalFile) }
                .onFailure { tempFile.delete() }
        } else {
            tempFile.delete()
        }
        notifyState()
    }

    fun play(): Boolean {
        stopRecording(save = false)
        stopPlayback()
        if (!validFinalFile()) return false
        val nextPlayer = MediaPlayer()
        return runCatching {
            nextPlayer.setDataSource(finalFile.absolutePath)
            nextPlayer.setOnCompletionListener { stopPlayback() }
            nextPlayer.setOnErrorListener { _, _, _ ->
                discardFailedPlayback()
                true
            }
            nextPlayer.prepare()
            nextPlayer.start()
            player = nextPlayer
            notifyState()
            true
        }.getOrElse {
            nextPlayer.release()
            finalFile.delete()
            notifyState()
            false
        }
    }

    fun stopPlayback() {
        val active = player ?: return
        player = null
        runCatching { active.stop() }
        active.reset()
        active.release()
        notifyState()
    }

    fun delete() {
        release()
        finalFile.delete()
        tempFile.delete()
        notifyState()
    }

    fun release() {
        stopRecording(save = false)
        stopPlayback()
    }

    private fun notifyState() = onStateChanged?.invoke(currentState())

    private fun validFinalFile(): Boolean {
        if (GuardianVoiceStorage.isSupportedM4a(finalFile)) return true
        if (finalFile.exists()) finalFile.delete()
        return false
    }

    private fun discardFailedPlayback() {
        stopPlayback()
        finalFile.delete()
        notifyState()
    }
}
