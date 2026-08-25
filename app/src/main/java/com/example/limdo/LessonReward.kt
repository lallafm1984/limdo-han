package com.example.limdo

internal data class LessonSpec(
    val id: LessonId,
    val stage: CurriculumStage,
    val glyph: String,
    val strokeCount: Int,
    val initialCue: SpokenCue,
    val successCue: SpokenCue,
    val strokeDirections: List<StrokeDirection>,
)

internal enum class LessonId { GIEOK, NIEUN, DIGEUT, A, GA, NA, DA }

internal enum class StrokeDirection { RIGHT, DOWN }

internal enum class CurriculumStage {
    PENCIL_PREPARATION,
    CONSONANTS,
    VOWELS,
    SYLLABLE_STRUCTURE,
    OPEN_SYLLABLES,
    FINAL_CONSONANTS,
    DOUBLE_FINAL_CONSONANTS,
}

internal object KoreanCurriculum {
    val educationFlow = CurriculumStage.entries

    val lessons = listOf(
        LessonSpec(
            id = LessonId.GIEOK,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㄱ",
            strokeCount = 1,
            initialCue = SpokenCue.INITIAL_GIEOK,
            successCue = SpokenCue.SUCCESS_GIEOK,
            strokeDirections = listOf(StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.NIEUN,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㄴ",
            strokeCount = 1,
            initialCue = SpokenCue.INITIAL_NIEUN,
            successCue = SpokenCue.SUCCESS_NIEUN,
            strokeDirections = listOf(StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.DIGEUT,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㄷ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_DIGEUT,
            successCue = SpokenCue.SUCCESS_DIGEUT,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.A,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅏ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_A,
            successCue = SpokenCue.SUCCESS_A,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.GA,
            stage = CurriculumStage.SYLLABLE_STRUCTURE,
            glyph = "가",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL,
            successCue = SpokenCue.SUCCESS,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN, StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.NA,
            stage = CurriculumStage.SYLLABLE_STRUCTURE,
            glyph = "나",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_NA,
            successCue = SpokenCue.SUCCESS_NA,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.DOWN, StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.DA,
            stage = CurriculumStage.SYLLABLE_STRUCTURE,
            glyph = "다",
            strokeCount = 4,
            initialCue = SpokenCue.INITIAL_DA,
            successCue = SpokenCue.SUCCESS_DA,
            strokeDirections = listOf(
                StrokeDirection.RIGHT,
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
            ),
        ),
    )

    fun nextIndex(currentIndex: Int): Int = (currentIndex + 1) % lessons.size
}

internal val GieokLesson = KoreanCurriculum.lessons.first { it.id == LessonId.GIEOK }
internal val NieunLesson = KoreanCurriculum.lessons.first { it.id == LessonId.NIEUN }
internal val DigeutLesson = KoreanCurriculum.lessons.first { it.id == LessonId.DIGEUT }
internal val ALesson = KoreanCurriculum.lessons.first { it.id == LessonId.A }
internal val GaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.GA }
internal val NaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.NA }
internal val DaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.DA }

internal enum class RewardMovePhase { IDLE, START, MOVING, COMPLETE }

internal data class LessonRewardState(
    val completedSteps: Int = 0,
    val targetSteps: Int = 0,
    val successConsumed: Boolean = false,
    val phase: RewardMovePhase = RewardMovePhase.IDLE,
) {
    val inputLocked: Boolean
        get() = phase != RewardMovePhase.IDLE

    fun onTraceResult(result: GieokTraceResult?, lesson: LessonSpec): LessonRewardState = when {
        result == GieokTraceResult.SUCCESS && !successConsumed -> copy(
            targetSteps = completedSteps + lesson.strokeCount,
            successConsumed = true,
            phase = RewardMovePhase.START,
        )
        result == GieokTraceResult.SUCCESS -> this
        else -> copy(successConsumed = false, phase = RewardMovePhase.IDLE)
    }

    fun moving(): LessonRewardState =
        if (phase == RewardMovePhase.START) copy(phase = RewardMovePhase.MOVING) else this

    fun complete(): LessonRewardState =
        if (phase == RewardMovePhase.MOVING) copy(
            completedSteps = targetSteps,
            phase = RewardMovePhase.COMPLETE,
        ) else this
}
