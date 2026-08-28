package com.limdo.hangul

internal enum class LearningMenu(
    val label: String,
    val symbol: String,
    val spokenCue: SpokenCue,
) {
    CONSONANTS("자음", "ㄱ ㄴ ㄷ", SpokenCue.MENU_CONSONANTS),
    VOWELS("모음", "ㅏ ㅑ ㅓ", SpokenCue.MENU_VOWELS),
    GANADA("가나다", "가 나 다", SpokenCue.MENU_GANADA),
}

internal sealed interface LearningDestination {
    data object Home : LearningDestination
    data object GuardianLessons : LearningDestination
    data class GuardianStartRecording(
        val lessonId: LessonId,
    ) : LearningDestination
    data class MenuTransition(val menu: LearningMenu) : LearningDestination
    data class Selection(val menu: LearningMenu) : LearningDestination
    data object GaAssembly : LearningDestination
    data class Writing(
        val menu: LearningMenu,
        val lessonId: LessonId,
        val sessionId: Int,
    ) : LearningDestination
}

internal object LearningNavigation {
    private val basicVowelIds = listOf(
        LessonId.A, LessonId.YA, LessonId.EO, LessonId.YEO, LessonId.O,
        LessonId.YO, LessonId.U, LessonId.YU, LessonId.EU, LessonId.I,
    )

    private val ganadoIds = listOf(
        LessonId.GA, LessonId.NA, LessonId.DA, LessonId.RA, LessonId.MA, LessonId.BA,
        LessonId.SA, LessonId.AH, LessonId.JA, LessonId.CHA, LessonId.KA, LessonId.TA,
        LessonId.PA, LessonId.HA,
    )

    fun lessons(menu: LearningMenu): List<LessonSpec> = when (menu) {
        LearningMenu.CONSONANTS -> KoreanCurriculum.lessons.filter {
            it.stage == CurriculumStage.CONSONANTS
        }
        LearningMenu.VOWELS -> basicVowelIds.map { id ->
            KoreanCurriculum.lessons.single { it.id == id }
        }
        LearningMenu.GANADA -> ganadoIds.map { id ->
            KoreanCurriculum.lessons.single { it.id == id }
        }
    }

    fun menuFor(lessonId: LessonId): LearningMenu = LearningMenu.entries.single { menu ->
        lessons(menu).any { it.id == lessonId }
    }

    fun nextLesson(menu: LearningMenu, currentLesson: LessonSpec): LessonSpec {
        val menuLessons = lessons(menu)
        val currentIndex = menuLessons.indexOfFirst { it.id == currentLesson.id }
        if (currentIndex >= 0) return menuLessons[(currentIndex + 1) % menuLessons.size]
        val stageLessons = KoreanCurriculum.lessons.filter { it.stage == currentLesson.stage }
        val stageIndex = stageLessons.indexOfFirst { it.id == currentLesson.id }
        require(stageIndex >= 0) { "${currentLesson.id} is not in the curriculum" }
        return stageLessons[(stageIndex + 1) % stageLessons.size]
    }

    fun previousLesson(menu: LearningMenu, currentLesson: LessonSpec): LessonSpec {
        val menuLessons = lessons(menu)
        val currentIndex = menuLessons.indexOfFirst { it.id == currentLesson.id }
        if (currentIndex >= 0) return menuLessons[(currentIndex - 1 + menuLessons.size) % menuLessons.size]
        val stageLessons = KoreanCurriculum.lessons.filter { it.stage == currentLesson.stage }
        val stageIndex = stageLessons.indexOfFirst { it.id == currentLesson.id }
        require(stageIndex >= 0) { "${currentLesson.id} is not in the curriculum" }
        return stageLessons[(stageIndex - 1 + stageLessons.size) % stageLessons.size]
    }

    fun back(destination: LearningDestination): LearningDestination = when (destination) {
        LearningDestination.Home -> LearningDestination.Home
        LearningDestination.GuardianLessons -> LearningDestination.Home
        is LearningDestination.GuardianStartRecording -> LearningDestination.GuardianLessons
        is LearningDestination.MenuTransition -> LearningDestination.Home
        is LearningDestination.Selection -> LearningDestination.Home
        LearningDestination.GaAssembly -> LearningDestination.Selection(LearningMenu.GANADA)
        is LearningDestination.Writing -> LearningDestination.Selection(destination.menu)
    }
}
