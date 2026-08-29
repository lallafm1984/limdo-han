package com.limdo.hangul

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningNavigationTest {
    @Test
    fun menuTransitionKeepsSelectedColorAndFiniteSymbolStates() {
        val start = menuTransitionVisuals(0f)
        val middle = menuTransitionVisuals(0.5f)
        val end = menuTransitionVisuals(1f)

        assertTrue(start.symbolScale < middle.symbolScale)
        assertTrue(middle.symbolScale < end.symbolScale)
        assertEquals(0f, start.symbolAlpha)
        assertTrue(middle.symbolAlpha in 0.4f..0.6f)
        assertEquals(1f, end.symbolAlpha)
        LearningMenu.entries.forEach { menu ->
            assertTrue(menu.symbol.isNotBlank())
            assertNotEquals(menu.visuals().accent, menu.visuals().softSurface)
            assertEquals(
                LearningDestination.Home,
                LearningNavigation.back(LearningDestination.MenuTransition(menu)),
            )
        }
    }

    @Test
    fun homeCardsKeepLargePressAndEntranceFeedback() {
        val rest = homeCardPressVisuals(isPressed = false)
        val pressed = homeCardPressVisuals(isPressed = true)
        val start = homeEntranceVisuals(0f)
        val middle = homeEntranceVisuals(0.5f)
        val end = homeEntranceVisuals(1f)

        assertEquals(1f, rest.scale)
        assertEquals(0f, rest.glowBorderDp)
        assertTrue(pressed.scale in 0.92f..0.97f)
        assertTrue(pressed.glowBorderDp >= 8f)
        assertEquals(0f, start.alpha)
        assertTrue(start.scale < middle.scale)
        assertTrue(middle.scale < end.scale)
        assertTrue(start.offsetDp > middle.offsetDp)
        assertTrue(middle.offsetDp > end.offsetDp)
        assertEquals(1f, end.alpha)
        assertEquals(1f, end.scale)
        assertEquals(0f, end.offsetDp)
    }

    @Test
    fun playgroundTokensKeepSharedCardLanguageAndDistinctMenuColors() {
        assertTrue(LimDoPlaygroundTokens.CARD_CORNER_DP >= 28f)
        assertTrue(LimDoPlaygroundTokens.CARD_BORDER_DP >= 3f)
        assertTrue(LimDoPlaygroundTokens.CARD_SHADOW_DP >= 5f)
        assertTrue(LimDoPlaygroundTokens.CARD_GAP_DP >= 12f)
        assertEquals(3, LearningMenu.entries.map { it.visuals().accent }.distinct().size)
        assertEquals(3, LearningMenu.entries.map { it.visuals().softSurface }.distinct().size)
    }

    @Test
    fun premiumArtBibleTokensKeepSharedSafetyAndExpressionScales() {
        assertEquals(listOf(12f, 20f, 32f, 48f), listOf(
            LimDoPlaygroundTokens.SPACING_COMPACT_DP,
            LimDoPlaygroundTokens.SPACING_RELATED_DP,
            LimDoPlaygroundTokens.SPACING_SECTION_DP,
            LimDoPlaygroundTokens.SPACING_SCENE_DP,
        ))
        assertEquals(listOf(20f, 32f, 40f), listOf(
            LimDoPlaygroundTokens.CORNER_CONTROL_DP,
            LimDoPlaygroundTokens.CARD_CORNER_DP,
            LimDoPlaygroundTokens.CORNER_HERO_DP,
        ))
        assertTrue(LimDoPlaygroundTokens.CHILD_TOUCH_MIN_DP >= 64f)
        assertTrue(LimDoPlaygroundTokens.GUARDIAN_TOUCH_MIN_DP >= 64f)
        assertTrue(LimDoPlaygroundTokens.WRITING_CANVAS_MIN_WIDTH_PX >= 1962)
        assertTrue(LimDoPlaygroundTokens.WRITING_CANVAS_MIN_HEIGHT_PX >= 954)
        assertEquals(168, LimDoPlaygroundTokens.WRITING_ACTION_PX)
        assertEquals(0, LimDoPlaygroundTokens.REDUCED_MOTION_PARTICLE_COUNT)
        assertTrue(LimDoPlaygroundTokens.MOTION_RETRY_MAX_DURATION_MS <= 500)
        assertTrue(LimDoPlaygroundTokens.MOTION_SUCCESS_MAX_DURATION_MS <= 1000)
        assertEquals(5, listOf(
            LimDoPlaygroundTokens.warmCream,
            LimDoPlaygroundTokens.deepTeal,
            LimDoPlaygroundTokens.coral,
            LimDoPlaygroundTokens.sunYellow,
            LimDoPlaygroundTokens.ink,
        ).distinct().size)
    }

    @Test
    fun lessonCardsDistinguishDefaultSelectedAndDisabledWithoutColorAlone() {
        LearningMenu.entries.forEach { menu ->
            val default = menu.lessonCardVisuals(LessonCardVisualState.DEFAULT)
            val selected = menu.lessonCardVisuals(LessonCardVisualState.SELECTED)
            val disabled = menu.lessonCardVisuals(LessonCardVisualState.DISABLED)

            assertNotEquals(default.surface, selected.surface)
            assertNotEquals(default.outlineWidthDp, selected.outlineWidthDp)
            assertNotEquals(default.cornerDp, selected.cornerDp)
            assertNotEquals(default.surface, disabled.surface)
            assertTrue(selected.outlineWidthDp > default.outlineWidthDp)
            assertTrue(disabled.shadowDp < default.shadowDp)
        }
    }

    @Test
    fun menusExposeShortCuesAndTheRequiredCurriculumLessons() {
        assertEquals(
            listOf(SpokenCue.MENU_CONSONANTS, SpokenCue.MENU_VOWELS, SpokenCue.MENU_GANADA),
            LearningMenu.entries.map(LearningMenu::spokenCue),
        )
        assertEquals(
            KoreanCurriculum.lessons.filter { it.stage == CurriculumStage.CONSONANTS },
            LearningNavigation.lessons(LearningMenu.CONSONANTS),
        )
        assertEquals(
            listOf(
                LessonId.A, LessonId.YA, LessonId.EO, LessonId.YEO, LessonId.O,
                LessonId.YO, LessonId.U, LessonId.YU, LessonId.EU, LessonId.I,
            ),
            LearningNavigation.lessons(LearningMenu.VOWELS).map(LessonSpec::id),
        )
        assertEquals(
            listOf(
                LessonId.GA, LessonId.NA, LessonId.DA, LessonId.RA, LessonId.MA, LessonId.BA,
                LessonId.SA, LessonId.AH, LessonId.JA, LessonId.CHA, LessonId.KA, LessonId.TA,
                LessonId.PA, LessonId.HA,
            ),
            LearningNavigation.lessons(LearningMenu.GANADA).map(LessonSpec::id),
        )
    }

    @Test
    fun guardianCatalogShowsExactlyTheThirtyEightProductionLessonsInThreeGroups() {
        assertEquals(listOf("자음 14개", "모음 10개", "글자 14개"), GuardianLessonCatalog.groups.map { it.label })
        assertEquals(listOf(14, 10, 14), GuardianLessonCatalog.groups.map { it.lessons.size })
        assertEquals(38, GuardianLessonCatalog.lessons.size)
        assertEquals(38, GuardianLessonCatalog.lessons.map { it.id }.distinct().size)
        assertEquals(
            LearningMenu.entries.flatMap(LearningNavigation::lessons).map { it.id },
            GuardianLessonCatalog.lessons.map { it.id },
        )
        assertEquals(
            LearningDestination.Home,
            LearningNavigation.back(LearningDestination.GuardianLessons),
        )
    }

    @Test
    fun previousAndNextLessonsStayInsideEachMenuAndWrapBothDirections() {
        val expected = listOf(
            LessonId.NA, LessonId.DA, LessonId.RA, LessonId.MA, LessonId.BA, LessonId.SA,
            LessonId.AH, LessonId.JA, LessonId.CHA, LessonId.KA, LessonId.TA, LessonId.PA,
            LessonId.HA, LessonId.GA,
        )
        val actual = LearningNavigation.lessons(LearningMenu.GANADA)
            .map { LearningNavigation.nextLesson(LearningMenu.GANADA, it).id }
        assertEquals(expected, actual)

        val vowelCycle = LearningNavigation.lessons(LearningMenu.VOWELS)
            .map { LearningNavigation.nextLesson(LearningMenu.VOWELS, it).id }
        assertEquals(
            listOf(
                LessonId.YA, LessonId.EO, LessonId.YEO, LessonId.O, LessonId.YO,
                LessonId.U, LessonId.YU, LessonId.EU, LessonId.I, LessonId.A,
            ),
            vowelCycle,
        )

        LearningMenu.entries.forEach { menu ->
            val lessons = LearningNavigation.lessons(menu)
            val lessonIds = lessons.map(LessonSpec::id).toSet()
            lessons.forEach { lesson ->
                assertTrue(LearningNavigation.nextLesson(menu, lesson).id in lessonIds)
                assertTrue(LearningNavigation.previousLesson(menu, lesson).id in lessonIds)
                assertEquals(
                    lesson,
                    LearningNavigation.nextLesson(
                        menu,
                        LearningNavigation.previousLesson(menu, lesson),
                    ),
                )
                assertEquals(
                    lesson,
                    LearningNavigation.previousLesson(
                        menu,
                        LearningNavigation.nextLesson(menu, lesson),
                    ),
                )
            }
            assertEquals(lessons.first(), LearningNavigation.nextLesson(menu, lessons.last()))
            assertEquals(lessons.last(), LearningNavigation.previousLesson(menu, lessons.first()))
        }
    }

    @Test
    fun everySelectableLessonKeepsWritingGeometryAndNavigationIntegrated() {
        LearningMenu.entries.forEachIndexed { menuIndex, menu ->
            LearningNavigation.lessons(menu).forEachIndexed { lessonIndex, lesson ->
                val writing = LearningDestination.Writing(
                    menu = menu,
                    lessonId = lesson.id,
                    sessionId = menuIndex * 100 + lessonIndex + 1,
                )
                val geometry = WritingCanvasGeometry.glyph(lesson, width = 1962f, height = 954f)

                assertEquals(lesson.strokeCount, geometry.strokes.size)
                assertEquals(lesson.strokeCount, lesson.strokeDirections.size)
                assertTrue(lesson.initialCue.utterance.isNotBlank())
                assertTrue(lesson.successCue.utterance.isNotBlank())
                assertEquals(
                    LearningDestination.Selection(menu),
                    LearningNavigation.back(writing),
                )
            }
        }
    }

    @Test
    fun backAndReopeningSameLessonKeepNavigationStateIndependent() {
        val first = LearningDestination.Writing(LearningMenu.GANADA, LessonId.GA, sessionId = 1)
        val reopened = LearningDestination.Writing(LearningMenu.GANADA, LessonId.GA, sessionId = 2)
        val selection = LearningNavigation.back(first)

        assertEquals(LearningDestination.Selection(LearningMenu.GANADA), selection)
        assertEquals(LearningDestination.Home, LearningNavigation.back(selection))
        assertEquals(first.lessonId, reopened.lessonId)
        assertNotEquals(first, reopened)
    }
}
