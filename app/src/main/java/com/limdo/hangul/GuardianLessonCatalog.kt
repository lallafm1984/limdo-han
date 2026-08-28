package com.limdo.hangul

internal data class GuardianLessonGroup(
    val label: String,
    val lessons: List<LessonSpec>,
)

internal object GuardianLessonCatalog {
    val groups: List<GuardianLessonGroup> = listOf(
        GuardianLessonGroup("자음 14개", LearningNavigation.lessons(LearningMenu.CONSONANTS)),
        GuardianLessonGroup("모음 10개", LearningNavigation.lessons(LearningMenu.VOWELS)),
        GuardianLessonGroup("글자 14개", LearningNavigation.lessons(LearningMenu.GANADA)),
    )

    val lessons: List<LessonSpec> = groups.flatMap(GuardianLessonGroup::lessons)
}
