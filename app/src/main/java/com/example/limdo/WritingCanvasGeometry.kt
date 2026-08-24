package com.example.limdo

import kotlin.math.min

internal data class CanvasPoint(
    val x: Float,
    val y: Float,
)

internal object WritingCanvasGeometry {
    private const val CONTENT_PADDING_FRACTION = 0.18f

    fun gieokPoints(width: Float, height: Float): List<CanvasPoint> {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val shortestSide = min(width, height)
        val contentPadding = shortestSide * CONTENT_PADDING_FRACTION
        val contentSide = shortestSide - (contentPadding * 2f)
        val left = (width - contentSide) / 2f
        val top = (height - contentSide) / 2f
        val right = left + contentSide
        val bottom = top + contentSide

        return listOf(
            CanvasPoint(left, top),
            CanvasPoint(right, top),
            CanvasPoint(right, bottom),
        )
    }
}
