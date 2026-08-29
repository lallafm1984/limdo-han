package com.limdo.hangul

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal enum class GuardianVoiceState { EMPTY, RECORDING, READY, PLAYING }
internal enum class GuardianVoicePlaybackSource { USER_RECORDING, DEFAULT_ASSET, NONE }
internal enum class GuardianVoiceEvent(val fileSuffix: String, val label: String) {
    START("start", "쓰기 전"),
    SUCCESS("success", "정답 후"),
}

internal data class GuardianVoiceKey(
    val lessonId: LessonId,
)

internal object GuardianVoiceCatalog {
    val keys: List<GuardianVoiceKey> = GuardianLessonCatalog.lessons.map { GuardianVoiceKey(it.id) }
}

internal object WritingVoiceCatalog {
    val keys: List<GuardianVoiceKey> = KoreanCurriculum.lessons
        .map { GuardianVoiceKey(it.id) }
        .distinct()
}

internal object DefaultGuardianVoiceCatalog {
    fun resourceId(lessonId: LessonId): Int? = when (lessonId) {
        LessonId.GIEOK -> R.raw.limdo_voice_gieok
        LessonId.NIEUN -> R.raw.limdo_voice_nieun
        LessonId.DIGEUT -> R.raw.limdo_voice_digeut
        LessonId.RIEUL -> R.raw.limdo_voice_rieul
        LessonId.MIEUM -> R.raw.limdo_voice_mieum
        LessonId.BIEUP -> R.raw.limdo_voice_bieup
        LessonId.SIOT -> R.raw.limdo_voice_siot
        LessonId.IEUNG -> R.raw.limdo_voice_ieung
        LessonId.JIEUT -> R.raw.limdo_voice_jieut
        LessonId.CHIEUT -> R.raw.limdo_voice_chieut
        LessonId.KIEUK -> R.raw.limdo_voice_kieuk
        LessonId.TIEUT -> R.raw.limdo_voice_tieut
        LessonId.PIEUP -> R.raw.limdo_voice_pieup
        LessonId.HIEUH -> R.raw.limdo_voice_hieuh
        LessonId.A -> R.raw.limdo_voice_a
        LessonId.YA -> R.raw.limdo_voice_ya
        LessonId.EO -> R.raw.limdo_voice_eo
        LessonId.YEO -> R.raw.limdo_voice_yeo
        LessonId.O -> R.raw.limdo_voice_o
        LessonId.YO -> R.raw.limdo_voice_yo
        LessonId.U -> R.raw.limdo_voice_u
        LessonId.YU -> R.raw.limdo_voice_yu
        LessonId.EU -> R.raw.limdo_voice_eu
        LessonId.I -> R.raw.limdo_voice_i
        LessonId.GA -> R.raw.limdo_voice_ga
        LessonId.NA -> R.raw.limdo_voice_na
        LessonId.DA -> R.raw.limdo_voice_da
        LessonId.RA -> R.raw.limdo_voice_ra
        LessonId.MA -> R.raw.limdo_voice_ma
        LessonId.BA -> R.raw.limdo_voice_ba
        LessonId.SA -> R.raw.limdo_voice_sa
        LessonId.AH -> R.raw.limdo_voice_ah
        LessonId.JA -> R.raw.limdo_voice_ja
        LessonId.CHA -> R.raw.limdo_voice_cha
        LessonId.KA -> R.raw.limdo_voice_ka
        LessonId.TA -> R.raw.limdo_voice_ta
        LessonId.PA -> R.raw.limdo_voice_pa
        LessonId.HA -> R.raw.limdo_voice_ha
        else -> null
    }
}

internal fun resolveGuardianVoicePlaybackSource(
    useUserRecording: Boolean,
    hasValidUserRecording: Boolean,
    hasDefaultAsset: Boolean,
): GuardianVoicePlaybackSource = when {
    useUserRecording && hasValidUserRecording -> GuardianVoicePlaybackSource.USER_RECORDING
    hasDefaultAsset -> GuardianVoicePlaybackSource.DEFAULT_ASSET
    else -> GuardianVoicePlaybackSource.NONE
}

internal class GuardianVoicePreferenceStorage(
    private val noBackupFilesDir: File,
) {
    companion object {
        const val DEFAULT_USE_USER_RECORDING = true
        private const val FILE_NAME = "use_user_recording"
    }

    private val preferenceFile: File
        get() = File(File(noBackupFilesDir, GuardianVoiceStorage.DIRECTORY), FILE_NAME)

    fun loadUseUserRecording(): Boolean = when (preferenceFile.takeIf(File::isFile)?.readText()?.trim()) {
        "true" -> true
        "false" -> false
        else -> DEFAULT_USE_USER_RECORDING
    }

    fun saveUseUserRecording(enabled: Boolean): Boolean = runCatching {
        preferenceFile.parentFile?.mkdirs()
        val temporaryFile = File(preferenceFile.parentFile, ".${preferenceFile.name}.saving")
        temporaryFile.writeText(enabled.toString())
        Files.move(
            temporaryFile.toPath(),
            preferenceFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
        )
    }.isSuccess
}

internal object GuardianVoiceStorage {
    const val DIRECTORY = "guardian_voice"
    const val MAX_DURATION_MILLIS = 8_000

    fun finalFile(
        noBackupFilesDir: File,
        lessonId: LessonId,
    ): File = File(
        File(noBackupFilesDir, DIRECTORY),
        "${lessonId.name.lowercase()}.m4a",
    )

    fun legacyFile(noBackupFilesDir: File, lessonId: LessonId, event: GuardianVoiceEvent): File =
        File(File(noBackupFilesDir, DIRECTORY), "${lessonId.name.lowercase()}_${event.fileSuffix}.m4a")

    fun migrateLegacyRecording(noBackupFilesDir: File, lessonId: LessonId): File {
        val unified = finalFile(noBackupFilesDir, lessonId)
        if (isSupportedM4a(unified)) return unified
        if (unified.exists()) unified.delete()
        val source = GuardianVoiceEvent.entries
            .map { legacyFile(noBackupFilesDir, lessonId, it) }
            .firstOrNull(::isSupportedM4a)
            ?: return unified
        unified.parentFile?.mkdirs()
        val migrationFile = File(unified.parentFile, ".${unified.name}.migration")
        source.copyTo(migrationFile, overwrite = true)
        commit(migrationFile, unified)
        return unified
    }

    fun tempFile(finalFile: File): File = File(finalFile.parentFile, ".${finalFile.name}.recording")

    fun discardTemporaryFile(finalFile: File) {
        tempFile(finalFile).delete()
    }

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
    private val finalFile get() = GuardianVoiceStorage.migrateLegacyRecording(context.noBackupFilesDir, lessonId)
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
        GuardianVoiceStorage.discardTemporaryFile(finalFile)
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

    fun play(
        useUserRecording: Boolean,
        onFinished: () -> Unit = {},
    ): Boolean {
        val defaultResourceId = DefaultGuardianVoiceCatalog.resourceId(lessonId)
        val source = resolveGuardianVoicePlaybackSource(
            useUserRecording = useUserRecording,
            hasValidUserRecording = validFinalFile(),
            hasDefaultAsset = defaultResourceId != null,
        )
        return playSource(source, defaultResourceId, onFinished)
    }

    fun playUserRecording(onFinished: () -> Unit = {}): Boolean = playSource(
        source = if (validFinalFile()) GuardianVoicePlaybackSource.USER_RECORDING
        else GuardianVoicePlaybackSource.NONE,
        defaultResourceId = null,
        onFinished = onFinished,
    )

    private fun playSource(
        source: GuardianVoicePlaybackSource,
        defaultResourceId: Int?,
        onFinished: () -> Unit,
    ): Boolean {
        stopRecording(save = false)
        stopPlayback()
        if (source == GuardianVoicePlaybackSource.NONE) return false
        val nextPlayer = MediaPlayer()
        return runCatching {
            when (source) {
                GuardianVoicePlaybackSource.USER_RECORDING -> {
                    nextPlayer.setDataSource(finalFile.absolutePath)
                }
                GuardianVoicePlaybackSource.DEFAULT_ASSET -> {
                    val resourceId = requireNotNull(defaultResourceId)
                    context.resources.openRawResourceFd(resourceId).use { asset ->
                        nextPlayer.setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
                    }
                }
                GuardianVoicePlaybackSource.NONE -> error("재생할 음성이 없습니다.")
            }
            nextPlayer.setOnCompletionListener {
                stopPlayback()
                onFinished()
            }
            nextPlayer.setOnErrorListener { _, _, _ ->
                stopPlayback()
                onFinished()
                true
            }
            nextPlayer.prepare()
            nextPlayer.start()
            player = nextPlayer
            notifyState()
            true
        }.getOrElse {
            nextPlayer.release()
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
        GuardianVoiceStorage.discardTemporaryFile(finalFile)
    }

    private fun notifyState() = onStateChanged?.invoke(currentState())

    private fun validFinalFile(): Boolean {
        return GuardianVoiceStorage.isSupportedM4a(finalFile)
    }
}
