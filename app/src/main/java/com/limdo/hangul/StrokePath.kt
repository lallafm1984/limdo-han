package com.limdo.hangul

internal data class StrokePath(
    val points: List<CanvasPoint> = emptyList(),
) {
    fun start(
        point: CanvasPoint,
        width: Float,
        height: Float,
        safeInset: Float,
    ): StrokePath = StrokePath(
        points = listOf(point.constrainedTo(width, height, safeInset)),
    )

    fun append(
        point: CanvasPoint,
        width: Float,
        height: Float,
        safeInset: Float,
    ): StrokePath = copy(
        points = points + point.constrainedTo(width, height, safeInset),
    )

    fun clear(): StrokePath = StrokePath()
}

private fun CanvasPoint.constrainedTo(
    width: Float,
    height: Float,
    safeInset: Float,
): CanvasPoint {
    require(width > safeInset * 2f) { "width must contain the safe inset" }
    require(height > safeInset * 2f) { "height must contain the safe inset" }

    return CanvasPoint(
        x = x.coerceIn(safeInset, width - safeInset),
        y = y.coerceIn(safeInset, height - safeInset),
    )
}
