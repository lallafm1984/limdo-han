package com.example.limdo

internal object SuccessCelebrationSpec {
    const val DURATION_MS = 1_200
}

internal object FullScreenFeedbackSpec {
    const val SOURCE_SIZE_PX = 1_024
    const val SUCCESS_VISIBLE_FRACTION = 0.92f
    const val RETRY_VISIBLE_FRACTION = 0.82f
    const val MIN_WRITING_BOARD_HEIGHT_FRACTION = 0.8f
}

internal data class SuccessCelebrationVisuals(
    val scale: Float,
    val alpha: Float,
)

internal fun successCelebrationVisuals(progress: Float): SuccessCelebrationVisuals {
    val bounded = progress.coerceIn(0f, 1f)
    val scale = when {
        bounded < 0.22f -> 0.72f + (bounded / 0.22f) * 0.36f
        bounded < 0.48f -> 1.08f - ((bounded - 0.22f) / 0.26f) * 0.08f
        else -> 1f
    }
    val alpha = (bounded / 0.16f).coerceIn(0f, 1f)
    return SuccessCelebrationVisuals(
        scale = scale.coerceIn(0.72f, 1.08f),
        alpha = alpha,
    )
}
