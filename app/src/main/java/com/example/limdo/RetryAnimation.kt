package com.example.limdo

import kotlin.math.PI
import kotlin.math.sin

internal object RetryAnimationSpec {
    const val DURATION_MS = 420
    const val MAX_OFFSET_DP = 12f
    const val START_MARKER_MAX_SCALE = 1.45f
}

internal data class RetryAnimationVisuals(
    val offsetDp: Float,
    val startMarkerScale: Float,
    val feedbackScale: Float,
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
        feedbackScale = (1f + emphasis * 0.06f).coerceIn(1f, 1.06f),
    )
}
