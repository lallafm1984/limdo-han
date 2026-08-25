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

internal enum class LessonId {
    GIEOK, NIEUN, DIGEUT, RIEUL, MIEUM, BIEUP, SIOT, IEUNG, JIEUT, CHIEUT, KIEUK, TIEUT,
    PIEUP, HIEUH,
    A, YA, EO, YEO, O, YO, U, YU, EU, I, GA, NA, DA, RA, MA, BA, SA, AH, JA,
}

internal enum class StrokeDirection { RIGHT, LEFT, UP, DOWN }

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
            id = LessonId.RIEUL,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㄹ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_RIEUL,
            successCue = SpokenCue.SUCCESS_RIEUL,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.LEFT, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.MIEUM,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅁ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_MIEUM,
            successCue = SpokenCue.SUCCESS_MIEUM,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT, StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.BIEUP,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅂ",
            strokeCount = 4,
            initialCue = SpokenCue.INITIAL_BIEUP,
            successCue = SpokenCue.SUCCESS_BIEUP,
            strokeDirections = listOf(
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.SIOT,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅅ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_SIOT,
            successCue = SpokenCue.SUCCESS_SIOT,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.IEUNG,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅇ",
            strokeCount = 1,
            initialCue = SpokenCue.INITIAL_IEUNG,
            successCue = SpokenCue.SUCCESS_IEUNG,
            strokeDirections = listOf(StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.JIEUT,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅈ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_JIEUT,
            successCue = SpokenCue.SUCCESS_JIEUT,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.CHIEUT,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅊ",
            strokeCount = 4,
            initialCue = SpokenCue.INITIAL_CHIEUT,
            successCue = SpokenCue.SUCCESS_CHIEUT,
            strokeDirections = listOf(
                StrokeDirection.RIGHT,
                StrokeDirection.RIGHT,
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
            ),
        ),
        LessonSpec(
            id = LessonId.KIEUK,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅋ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_KIEUK,
            successCue = SpokenCue.SUCCESS_KIEUK,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.TIEUT,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅌ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_TIEUT,
            successCue = SpokenCue.SUCCESS_TIEUT,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.PIEUP,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅍ",
            strokeCount = 4,
            initialCue = SpokenCue.INITIAL_PIEUP,
            successCue = SpokenCue.SUCCESS_PIEUP,
            strokeDirections = listOf(
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.HIEUH,
            stage = CurriculumStage.CONSONANTS,
            glyph = "ㅎ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_HIEUH,
            successCue = SpokenCue.SUCCESS_HIEUH,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.RIGHT, StrokeDirection.RIGHT),
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
            id = LessonId.YA,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅑ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_YA,
            successCue = SpokenCue.SUCCESS_YA,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.RIGHT, StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.EO,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅓ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_EO,
            successCue = SpokenCue.SUCCESS_EO,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.LEFT),
        ),
        LessonSpec(
            id = LessonId.YEO,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅕ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_YEO,
            successCue = SpokenCue.SUCCESS_YEO,
            strokeDirections = listOf(StrokeDirection.DOWN, StrokeDirection.LEFT, StrokeDirection.LEFT),
        ),
        LessonSpec(
            id = LessonId.O,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅗ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_O,
            successCue = SpokenCue.SUCCESS_O,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.UP),
        ),
        LessonSpec(
            id = LessonId.YO,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅛ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_YO,
            successCue = SpokenCue.SUCCESS_YO,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.UP, StrokeDirection.UP),
        ),
        LessonSpec(
            id = LessonId.U,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅜ",
            strokeCount = 2,
            initialCue = SpokenCue.INITIAL_U,
            successCue = SpokenCue.SUCCESS_U,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.YU,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅠ",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_YU,
            successCue = SpokenCue.SUCCESS_YU,
            strokeDirections = listOf(StrokeDirection.RIGHT, StrokeDirection.DOWN, StrokeDirection.DOWN),
        ),
        LessonSpec(
            id = LessonId.EU,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅡ",
            strokeCount = 1,
            initialCue = SpokenCue.INITIAL_EU,
            successCue = SpokenCue.SUCCESS_EU,
            strokeDirections = listOf(StrokeDirection.RIGHT),
        ),
        LessonSpec(
            id = LessonId.I,
            stage = CurriculumStage.VOWELS,
            glyph = "ㅣ",
            strokeCount = 1,
            initialCue = SpokenCue.INITIAL_I,
            successCue = SpokenCue.SUCCESS_I,
            strokeDirections = listOf(StrokeDirection.DOWN),
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
        LessonSpec(
            id = LessonId.RA,
            stage = CurriculumStage.OPEN_SYLLABLES,
            glyph = "라",
            strokeCount = 5,
            initialCue = SpokenCue.INITIAL_RA,
            successCue = SpokenCue.SUCCESS_RA,
            strokeDirections = listOf(
                StrokeDirection.RIGHT,
                StrokeDirection.LEFT,
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.MA,
            stage = CurriculumStage.OPEN_SYLLABLES,
            glyph = "마",
            strokeCount = 5,
            initialCue = SpokenCue.INITIAL_MA,
            successCue = SpokenCue.SUCCESS_MA,
            strokeDirections = listOf(
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
                StrokeDirection.RIGHT,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.BA,
            stage = CurriculumStage.OPEN_SYLLABLES,
            glyph = "바",
            strokeCount = 6,
            initialCue = SpokenCue.INITIAL_BA,
            successCue = SpokenCue.SUCCESS_BA,
            strokeDirections = listOf(
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
                StrokeDirection.RIGHT,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.SA,
            stage = CurriculumStage.OPEN_SYLLABLES,
            glyph = "사",
            strokeCount = 4,
            initialCue = SpokenCue.INITIAL_SA,
            successCue = SpokenCue.SUCCESS_SA,
            strokeDirections = listOf(
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.AH,
            stage = CurriculumStage.OPEN_SYLLABLES,
            glyph = "아",
            strokeCount = 3,
            initialCue = SpokenCue.INITIAL_AH,
            successCue = SpokenCue.SUCCESS_AH,
            strokeDirections = listOf(
                StrokeDirection.RIGHT,
                StrokeDirection.DOWN,
                StrokeDirection.RIGHT,
            ),
        ),
        LessonSpec(
            id = LessonId.JA,
            stage = CurriculumStage.OPEN_SYLLABLES,
            glyph = "자",
            strokeCount = 5,
            initialCue = SpokenCue.INITIAL_JA,
            successCue = SpokenCue.SUCCESS_JA,
            strokeDirections = listOf(
                StrokeDirection.RIGHT,
                StrokeDirection.DOWN,
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
internal val RieulLesson = KoreanCurriculum.lessons.first { it.id == LessonId.RIEUL }
internal val MieumLesson = KoreanCurriculum.lessons.first { it.id == LessonId.MIEUM }
internal val BieupLesson = KoreanCurriculum.lessons.first { it.id == LessonId.BIEUP }
internal val SiotLesson = KoreanCurriculum.lessons.first { it.id == LessonId.SIOT }
internal val IeungLesson = KoreanCurriculum.lessons.first { it.id == LessonId.IEUNG }
internal val JieutLesson = KoreanCurriculum.lessons.first { it.id == LessonId.JIEUT }
internal val ChieutLesson = KoreanCurriculum.lessons.first { it.id == LessonId.CHIEUT }
internal val KieukLesson = KoreanCurriculum.lessons.first { it.id == LessonId.KIEUK }
internal val TieutLesson = KoreanCurriculum.lessons.first { it.id == LessonId.TIEUT }
internal val PieupLesson = KoreanCurriculum.lessons.first { it.id == LessonId.PIEUP }
internal val HieuhLesson = KoreanCurriculum.lessons.first { it.id == LessonId.HIEUH }
internal val ALesson = KoreanCurriculum.lessons.first { it.id == LessonId.A }
internal val YaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.YA }
internal val EoLesson = KoreanCurriculum.lessons.first { it.id == LessonId.EO }
internal val YeoLesson = KoreanCurriculum.lessons.first { it.id == LessonId.YEO }
internal val OLesson = KoreanCurriculum.lessons.first { it.id == LessonId.O }
internal val YoLesson = KoreanCurriculum.lessons.first { it.id == LessonId.YO }
internal val ULesson = KoreanCurriculum.lessons.first { it.id == LessonId.U }
internal val YuLesson = KoreanCurriculum.lessons.first { it.id == LessonId.YU }
internal val EuLesson = KoreanCurriculum.lessons.first { it.id == LessonId.EU }
internal val ILesson = KoreanCurriculum.lessons.first { it.id == LessonId.I }
internal val GaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.GA }
internal val NaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.NA }
internal val DaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.DA }
internal val RaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.RA }
internal val MaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.MA }
internal val BaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.BA }
internal val SaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.SA }
internal val AhLesson = KoreanCurriculum.lessons.first { it.id == LessonId.AH }
internal val JaLesson = KoreanCurriculum.lessons.first { it.id == LessonId.JA }

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
