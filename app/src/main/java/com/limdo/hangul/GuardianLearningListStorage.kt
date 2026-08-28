package com.limdo.hangul

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal class GuardianLearningListStorage(
    private val noBackupRoot: File,
) {
    private val supportedIds = GuardianLessonCatalog.lessons.mapTo(mutableSetOf(), LessonSpec::id)
    private val listFile = File(noBackupRoot, FILE_NAME)
    private val currentIndexFile = File(noBackupRoot, CURRENT_INDEX_FILE_NAME)

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
        if (current.isEmpty()) saveCurrentIndex(0)
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
        val currentIndex = loadCurrentIndex(current)
        val movedCurrentIndex = when {
            currentIndex == fromIndex -> toIndex
            fromIndex < currentIndex && toIndex >= currentIndex -> currentIndex - 1
            fromIndex > currentIndex && toIndex <= currentIndex -> currentIndex + 1
            else -> currentIndex
        }
        saveCurrentIndex(movedCurrentIndex)
        return updated
    }

    fun removeAt(current: List<LessonId>, index: Int): List<LessonId> {
        require(index in current.indices)
        val updated = current.toMutableList().apply { removeAt(index) }
        save(updated)
        val currentIndex = loadCurrentIndex(current)
        val updatedCurrentIndex = when {
            updated.isEmpty() -> EMPTY_INDEX
            index < currentIndex -> currentIndex - 1
            index == currentIndex -> currentIndex.coerceAtMost(updated.lastIndex)
            else -> currentIndex
        }
        saveCurrentIndex(updatedCurrentIndex)
        return updated
    }

    fun loadCurrentIndex(lessonIds: List<LessonId>): Int {
        if (lessonIds.isEmpty()) return EMPTY_INDEX
        val stored = currentIndexFile.takeIf(File::isFile)?.readText()?.trim()?.toIntOrNull()
        return (stored ?: 0).coerceIn(lessonIds.indices)
    }

    fun continueFromCurrent(lessonIds: List<LessonId>): Int {
        val currentIndex = loadCurrentIndex(lessonIds)
        saveCurrentIndex(currentIndex)
        return currentIndex
    }

    fun restartFromBeginning(lessonIds: List<LessonId>): Int {
        val currentIndex = if (lessonIds.isEmpty()) EMPTY_INDEX else 0
        saveCurrentIndex(currentIndex)
        return currentIndex
    }

    fun saveCurrentPosition(lessonIds: List<LessonId>, index: Int): Int {
        require(lessonIds.isNotEmpty())
        require(index in lessonIds.indices)
        saveCurrentIndex(index)
        return index
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

    private fun saveCurrentIndex(index: Int) {
        noBackupRoot.mkdirs()
        val temporary = File(noBackupRoot, "$CURRENT_INDEX_FILE_NAME.tmp")
        temporary.writeText(index.toString())
        Files.move(
            temporary.toPath(),
            currentIndexFile.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
        )
    }

    internal fun storageFile(): File = listFile
    internal fun currentIndexStorageFile(): File = currentIndexFile

    private companion object {
        const val FILE_NAME = "guardian-learning-list.txt"
        const val CURRENT_INDEX_FILE_NAME = "guardian-learning-current-index.txt"
        const val EMPTY_INDEX = -1
    }
}
