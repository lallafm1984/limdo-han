package com.example.limdo

import kotlin.math.PI
import kotlin.math.sin

internal object RetryAnimationSpec {
    const val DURATION_MS = 420
    const val MAX_OFFSET_DP = 12f
    const val START_MARKER_MAX_SCALE = 1.45f
}

internal object RetryFeedbackAtlasSpec {
    const val COLUMNS = 2
    const val ROWS = 1
    const val SOURCE_WIDTH_PX = 1_774
    const val SOURCE_HEIGHT_PX = 887
    const val CELL_SIZE_PX = 887
    const val RETURN_ARROW_CELL = 0
    const val SPARKLES_CELL = 1

    const val CONTAINER_WIDTH_DP = 116f
    const val CONTAINER_HEIGHT_DP = 76f
    const val RETURN_ARROW_SIZE_DP = 68f
    const val SPARKLES_SIZE_DP = 70f
}

internal data class RetryAnimationVisuals(
    val offsetDp: Float,
    val startMarkerScale: Float,
    val feedbackScale: Float,
    val feedbackAlpha: Float,
    val sparkleAlpha: Float,
)

internal fun retryAnimationVisuals(progress: Float): RetryAnimationVisuals {
    val bounded = progress.coerceIn(0f, 1f)
    val decay = 1f - bounded
    val wave = sin(bounded * 4f * PI).toFloat()
    val emphasis = sin(bounded * PI).toFloat().coerceAtLeast(0f)
    return RetryAnimationVisuals(
        offsetDp = RetryAnimationSpec.MAX_OFFSET_DP * wave * decay,
        startMarkerScale = 1f +
            ((RetryAnimationSpec.START_MARKER_MAX_SCALE - 1f) * emphasis),
        feedbackScale = (0.78f + emphasis * 0.22f).coerceIn(0.78f, 1f),
        feedbackAlpha = if (bounded < 0.8f) 1f else (1f - bounded) / 0.2f,
        sparkleAlpha = (emphasis * (1f - bounded)).coerceIn(0f, 1f),
    )
}
