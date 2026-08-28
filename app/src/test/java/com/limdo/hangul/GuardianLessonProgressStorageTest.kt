package com.limdo.hangul

import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardianLessonProgressStorageTest {
    @Test
    fun progressTracksOnlyGuardianExposedLessons() {
        val storage = GuardianLessonProgressStorage(Files.createTempDirectory("limdo-progress-scope").toFile())

        assertTrue(storage.tracks(LessonId.GA))
        assertFalse(storage.tracks(LessonId.GEO))
    }

    @Test
    fun practiceAndCompletionPersistByStableLessonIdWithoutChangingOtherLessons() {
        val root = Files.createTempDirectory("limdo-progress").toFile()
        val times = ArrayDeque(listOf(100L, 200L, 300L))
        val storage = GuardianLessonProgressStorage(root) { times.removeFirst() }

        storage.markPracticed(LessonId.GIEOK)
        storage.markPracticed(LessonId.A)
        storage.markCompleted(LessonId.GIEOK, GuardianLessonAssistance.AFTER_HELP)

        assertEquals(
            GuardianLessonProgress(LessonId.GIEOK, 100L, 300L, GuardianLessonAssistance.AFTER_HELP),
            GuardianLessonProgressStorage(root).load()[LessonId.GIEOK],
        )
        assertEquals(
            GuardianLessonProgress(LessonId.A, 200L, null, null),
            GuardianLessonProgressStorage(root).load()[LessonId.A],
        )
        assertFalse(File(root, "guardian-lesson-progress-v1.txt.tmp").exists())
    }

    @Test
    fun completionWithoutStoredPracticeCreatesOrderedIndependentFacts() {
        val root = Files.createTempDirectory("limdo-progress-complete").toFile()
        val times = ArrayDeque(listOf(400L, 401L))
        val stored = GuardianLessonProgressStorage(root) { times.removeFirst() }
            .markCompleted(LessonId.HA, GuardianLessonAssistance.INDEPENDENT)

        assertEquals(400L, stored.lastPracticedAtMillis)
        assertEquals(401L, stored.lastCompletedAtMillis)
        assertEquals(GuardianLessonAssistance.INDEPENDENT, stored.lastAssistance)
    }

    @Test
    fun corruptOldAndUnsupportedFilesRestoreAsSafeEmptyState() {
        val root = Files.createTempDirectory("limdo-progress-corrupt").toFile()
        val storage = GuardianLessonProgressStorage(root)
        val candidates = listOf(
            "old-format\nGIEOK|1|2|INDEPENDENT\n",
            "LIMDO_PROGRESS_V1\nGAK|1|2|INDEPENDENT\n",
            "LIMDO_PROGRESS_V1\nGIEOK|20|10|INDEPENDENT\n",
            "LIMDO_PROGRESS_V1\nGIEOK|1|-|AFTER_HELP\n",
        )

        candidates.forEach { contents ->
            storage.storageFile().writeText(contents)
            assertTrue(storage.load().isEmpty())
        }
    }

    @Test
    fun catalogProvidesExactlyThirtyEightStableProgressKeys() {
        val ids = GuardianLessonCatalog.lessons.map(LessonSpec::id)
        assertEquals(38, ids.size)
        assertEquals(38, ids.distinct().size)
    }

    @Test
    fun guardianGridShowsEveryProgressFactWithShapeTextAndSemantics() {
        val source = File(rootProject(), "app/src/main/java/com/limdo/hangul/MainActivity.kt").readText()

        assertTrue(source.contains("progressByLesson = guardianLessonProgressStorage.load()"))
        assertTrue(source.contains("GuardianProgressSymbol(progress"))
        listOf("연습 전", "연습 중", "혼자 완성", "도움 후 완성").forEach { status ->
            assertTrue(source.contains("\"$status\""))
        }
        assertTrue(source.contains("최근 연습"))
        assertTrue(source.contains("최근 완료"))
        assertTrue(source.contains("add(progressSummary.semantics)"))
        assertEquals(0, Regex("LazyColumn|LazyRow|verticalScroll|horizontalScroll").findAll(source).count())
    }

    private fun rootProject(): File {
        var current = File(System.getProperty("user.dir")).canonicalFile
        while (!File(current, "settings.gradle.kts").isFile) {
            current = current.parentFile ?: error("project root not found")
        }
        return current
    }
}
