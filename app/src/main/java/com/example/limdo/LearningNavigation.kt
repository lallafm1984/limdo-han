package com.example.limdo

internal enum class LearningMenu(
    val label: String,
    val symbol: String,
    val icon: String,
    val spokenCue: SpokenCue,
) {
    CONSONANTS("자음", "ㄱ ㄴ ㄷ", "🚓", SpokenCue.MENU_CONSONANTS),
    VOWELS("모음", "ㅏ ㅑ ㅓ", "🚒", SpokenCue.MENU_VOWELS),
    GANADA("가나다", "가 나 다", "🚌", SpokenCue.MENU_GANADA),
}

internal sealed interface LearningDestination {
    data object Home : LearningDestination
    data class Selection(val menu: LearningMenu) : LearningDestination
    data class Writing(
        val menu: LearningMenu,
        val lessonId: LessonId,
        val sessionId: Int,
    ) : LearningDestination
}

internal object LearningNavigation {
    private val ganadoIds = listOf(
        LessonId.GA, LessonId.NA, LessonId.DA, LessonId.RA, LessonId.MA, LessonId.BA,
        LessonId.SA, LessonId.AH, LessonId.JA, LessonId.CHA, LessonId.KA, LessonId.TA,
        LessonId.PA, LessonId.HA,
    )

    fun lessons(menu: LearningMenu): List<LessonSpec> = when (menu) {
        LearningMenu.CONSONANTS -> KoreanCurriculum.lessons.filter {
            it.stage == CurriculumStage.CONSONANTS
        }
        LearningMenu.VOWELS -> KoreanCurriculum.lessons.filter {
            it.stage == CurriculumStage.VOWELS
        }
        LearningMenu.GANADA -> ganadoIds.map { id ->
            KoreanCurriculum.lessons.single { it.id == id }
        }
    }

    fun back(destination: LearningDestination): LearningDestination = when (destination) {
        LearningDestination.Home -> LearningDestination.Home
        is LearningDestination.Selection -> LearningDestination.Home
        is LearningDestination.Writing -> LearningDestination.Selection(destination.menu)
    }
}
