package com.example.limdo

internal data class LessonSpec(
    val glyph: String,
    val strokeCount: Int,
)

internal val GieokLesson = LessonSpec(glyph = "ㄱ", strokeCount = 1)
internal val GaLesson = LessonSpec(glyph = "가", strokeCount = 3)

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
