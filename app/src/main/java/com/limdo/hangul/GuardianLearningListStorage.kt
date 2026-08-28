package com.limdo.hangul

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal class GuardianLearningListStorage(
    private val noBackupRoot: File,
) {
    private val supportedIds = GuardianLessonCatalog.lessons.mapTo(mutableSetOf(), LessonSpec::id)
    private val listFile = File(noBackupRoot, FILE_NAME)

    fun load(): List<LessonId> {
        if (!listFile.isFile) return emptyList()
        return listFile.readLines()
            .mapNotNull { encoded ->
                LessonId.entries.firstOrNull { it.name == encoded.trim() }
            }
            .filter(supportedIds::contains)
    }

    fun append(current: List<LessonId>, lessonId: LessonId): List<LessonId> {
        require(lessonId in supportedIds) { "Unsupported guardian lesson: $lessonId" }
        val updated = current + lessonId
        save(updated)
        return updated
    }

    fun move(current: List<LessonId>, fromIndex: Int, toIndex: Int): List<LessonId> {
        require(fromIndex in current.indices)
        require(toIndex in current.indices)
        if (fromIndex == toIndex) return current
        val updated = current.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
        save(updated)
        return updated
    }

    fun removeAt(current: List<LessonId>, index: Int): List<LessonId> {
        require(index in current.indices)
        val updated = current.toMutableList().apply { removeAt(index) }
        save(updated)
        return updated
    }

    private fun save(lessonIds: List<LessonId>) {
        require(lessonIds.all(supportedIds::contains))
        noBackupRoot.mkdirs()
        val temporary = File(noBackupRoot, "$FILE_NAME.tmp")
        temporary.outputStream().bufferedWriter().use { writer ->
            lessonIds.forEach { lessonId ->
                writer.appendLine(lessonId.name)
            }
        }
        Files.move(
            temporary.toPath(),
            listFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
        )
    }

    internal fun storageFile(): File = listFile

    private companion object {
        const val FILE_NAME = "guardian-learning-list.txt"
    }
}
