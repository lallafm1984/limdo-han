package com.example.limdo

internal data class CanvasPoint(
    val x: Float,
    val y: Float,
)

internal object WritingCanvasGeometry {
    private const val HORIZONTAL_PADDING_FRACTION = 0.12f
    private const val VERTICAL_PADDING_FRACTION = 0.10f

    fun gieokPoints(width: Float, height: Float): List<CanvasPoint> {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val left = width * HORIZONTAL_PADDING_FRACTION
        val top = height * VERTICAL_PADDING_FRACTION
        val right = width - left
        val bottom = height - top

        return listOf(
            CanvasPoint(left, top),
            CanvasPoint(right, top),
            CanvasPoint(right, bottom),
        )
    }
}
