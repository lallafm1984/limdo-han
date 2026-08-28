package com.limdo.hangul

import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardianLearningListStorageTest {
    @Test
    fun appendPreservesOrderAndDuplicatesAcrossNewStorageInstance() {
        val root = Files.createTempDirectory("limdo-list").toFile()
        val storage = GuardianLearningListStorage(root)

        var lessons = storage.append(emptyList(), LessonId.GIEOK)
        lessons = storage.append(lessons, LessonId.A)
        lessons = storage.append(lessons, LessonId.GIEOK)

        assertEquals(
            listOf(LessonId.GIEOK, LessonId.A, LessonId.GIEOK),
            GuardianLearningListStorage(root).load(),
        )
        assertTrue(storage.storageFile().canonicalPath.startsWith(root.canonicalPath))
        assertFalse(File(root, "guardian-learning-list.txt.tmp").exists())
    }

    @Test
    fun loadDropsCorruptAndUnsupportedIdsWithoutBlockingAnEmptyList() {
        val root = Files.createTempDirectory("limdo-list-corrupt").toFile()
        val storage = GuardianLearningListStorage(root)
        storage.storageFile().writeText("GIEOK\nNOT_A_LESSON\nGAK\n\nGIEOK\n")

        assertEquals(listOf(LessonId.GIEOK, LessonId.GIEOK), storage.load())
        storage.storageFile().writeText("NOT_A_LESSON\nGAK\n")
        assertTrue(storage.load().isEmpty())
    }

    @Test
    fun catalogIsTheExactThirtyEightIdAllowlist() {
        val ids = GuardianLessonCatalog.lessons.map(LessonSpec::id)
        assertEquals(38, ids.size)
        assertEquals(38, ids.distinct().size)
    }
}
