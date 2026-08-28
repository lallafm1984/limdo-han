package com.limdo.hangul

import kotlin.math.PI
import kotlin.math.sin

internal object RetryAnimationSpec {
    const val DURATION_MS = 420
    const val MAX_OFFSET_DP = 8f
    const val START_MARKER_MAX_SCALE = 1.3f
    const val MAX_FLASH_ALPHA = 0.16f
}

internal data class RetryAnimationVisuals(
    val offsetDp: Float,
    val startMarkerScale: Float,
    val flashAlpha: Float,
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
        flashAlpha = RetryAnimationSpec.MAX_FLASH_ALPHA * emphasis,
    )
}
