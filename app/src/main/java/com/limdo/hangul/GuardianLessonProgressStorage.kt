package com.limdo.hangul

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal enum class GuardianLessonAssistance {
    INDEPENDENT,
    AFTER_HELP,
}

internal data class GuardianLessonProgress(
    val lessonId: LessonId,
    val lastPracticedAtMillis: Long,
    val lastCompletedAtMillis: Long?,
    val lastAssistance: GuardianLessonAssistance?,
)

internal class GuardianLessonProgressStorage(
    private val noBackupRoot: File,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) {
    private val supportedIds = GuardianLessonCatalog.lessons.mapTo(mutableSetOf(), LessonSpec::id)
    private val progressFile = File(noBackupRoot, FILE_NAME)

    fun tracks(lessonId: LessonId): Boolean = lessonId in supportedIds

    fun load(): Map<LessonId, GuardianLessonProgress> {
        if (!progressFile.isFile) return emptyMap()
        return runCatching {
            val lines = progressFile.readLines()
            require(lines.firstOrNull() == VERSION)
            lines.drop(1).associate { line ->
                val fields = line.split(SEPARATOR)
                require(fields.size == 4)
                val lessonId = LessonId.valueOf(fields[0])
                require(lessonId in supportedIds)
                val practicedAt = fields[1].toLong().also { require(it >= 0L) }
                val completedAt = fields[2].takeUnless { it == EMPTY }?.toLong()?.also {
                    require(it >= practicedAt)
                }
                val assistance = fields[3].takeUnless { it == EMPTY }
                    ?.let(GuardianLessonAssistance::valueOf)
                require((completedAt == null) == (assistance == null))
                lessonId to GuardianLessonProgress(
                    lessonId = lessonId,
                    lastPracticedAtMillis = practicedAt,
                    lastCompletedAtMillis = completedAt,
                    lastAssistance = assistance,
                )
            }
        }.getOrElse { emptyMap() }
    }

    fun markPracticed(lessonId: LessonId): GuardianLessonProgress {
        require(lessonId in supportedIds)
        val current = load().toMutableMap()
        val previous = current[lessonId]
        val practicedAt = maxOf(nowMillis(), previous?.lastPracticedAtMillis ?: 0L)
        val updated = GuardianLessonProgress(
            lessonId = lessonId,
            lastPracticedAtMillis = practicedAt,
            lastCompletedAtMillis = previous?.lastCompletedAtMillis,
            lastAssistance = previous?.lastAssistance,
        )
        current[lessonId] = updated
        save(current)
        return updated
    }

    fun markCompleted(
        lessonId: LessonId,
        assistance: GuardianLessonAssistance,
    ): GuardianLessonProgress {
        require(lessonId in supportedIds)
        val current = load().toMutableMap()
        val previous = current[lessonId]
        val practicedAt = previous?.lastPracticedAtMillis ?: nowMillis()
        val completedAt = maxOf(nowMillis(), practicedAt, previous?.lastCompletedAtMillis ?: 0L)
        val updated = GuardianLessonProgress(
            lessonId = lessonId,
            lastPracticedAtMillis = practicedAt,
            lastCompletedAtMillis = completedAt,
            lastAssistance = assistance,
        )
        current[lessonId] = updated
        save(current)
        return updated
    }

    private fun save(progress: Map<LessonId, GuardianLessonProgress>) {
        require(progress.keys.all(supportedIds::contains))
        noBackupRoot.mkdirs()
        val temporary = File(noBackupRoot, "$FILE_NAME.tmp")
        temporary.bufferedWriter().use { writer ->
            writer.appendLine(VERSION)
            GuardianLessonCatalog.lessons.map(LessonSpec::id).forEach { lessonId ->
                progress[lessonId]?.let { item ->
                    writer.appendLine(
                        listOf(
                            lessonId.name,
                            item.lastPracticedAtMillis,
                            item.lastCompletedAtMillis ?: EMPTY,
                            item.lastAssistance?.name ?: EMPTY,
                        ).joinToString(SEPARATOR),
                    )
                }
            }
        }
        Files.move(
            temporary.toPath(),
            progressFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
        )
    }

    internal fun storageFile(): File = progressFile

    private companion object {
        const val FILE_NAME = "guardian-lesson-progress-v1.txt"
        const val VERSION = "LIMDO_PROGRESS_V1"
        const val SEPARATOR = "|"
        const val EMPTY = "-"
    }
}
