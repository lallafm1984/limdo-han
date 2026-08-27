package com.example.limdo

internal object SuccessCelebrationSpec {
    const val DURATION_MS = 1_200
}

internal object SuccessFeedbackAtlasSpec {
    const val COLUMNS = 2
    const val ROWS = 1
    const val SOURCE_WIDTH_PX = 1_774
    const val SOURCE_HEIGHT_PX = 887
    const val CELL_SIZE_PX = 887
    const val STAR_CELL = 0
    const val CONFETTI_CELL = 1

    const val CONTAINER_WIDTH_DP = 140f
    const val CONTAINER_HEIGHT_DP = 104f
    const val STAR_SIZE_DP = 74f
    const val CONFETTI_SIZE_DP = 96f
    const val CONFETTI_TRAVEL_DP = 10f
}

internal data class SuccessCelebrationVisuals(
    val starScale: Float,
    val glowAlpha: Float,
    val confettiAlpha: Float,
)

internal fun successCelebrationVisuals(progress: Float): SuccessCelebrationVisuals {
    val bounded = progress.coerceIn(0f, 1f)
    val starScale = when {
        bounded < 0.25f -> bounded / 0.25f
        bounded < 0.5f -> 1f + ((0.5f - bounded) * 0.4f)
        else -> 1f
    }
    val glowAlpha = when {
        bounded < 0.15f -> 0f
        bounded < 0.45f -> (bounded - 0.15f) / 0.3f
        bounded < 0.8f -> 1f
        else -> (1f - bounded) / 0.2f
    }
    val confettiAlpha = when {
        bounded < 0.4f -> 0f
        bounded < 0.65f -> (bounded - 0.4f) / 0.25f
        else -> (1f - bounded) / 0.35f
    }
    return SuccessCelebrationVisuals(
        starScale = starScale.coerceIn(0f, 1.1f),
        glowAlpha = glowAlpha.coerceIn(0f, 1f),
        confettiAlpha = confettiAlpha.coerceIn(0f, 1f),
    )
}
