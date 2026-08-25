package com.example.limdo

internal data class TraceAttempt(
    val stroke: StrokePath = StrokePath(),
    val completedStrokes: List<StrokePath> = emptyList(),
    val result: GieokTraceResult? = null,
) {
    fun start(
        point: CanvasPoint,
        width: Float,
        height: Float,
        safeInset: Float,
    ): TraceAttempt = copy(
        stroke = stroke.start(point, width, height, safeInset),
        result = null,
    )

    fun append(
        point: CanvasPoint,
        width: Float,
        height: Float,
        safeInset: Float,
    ): TraceAttempt = copy(
        stroke = stroke.append(point, width, height, safeInset),
    )

    fun finish(width: Float, height: Float): TraceAttempt {
        val strokeResult = GaTraceEvaluator.evaluateStroke(
            width = width,
            height = height,
            strokeIndex = completedStrokes.size,
            stroke = stroke,
        )
        if (strokeResult != GieokTraceResult.SUCCESS) return copy(result = strokeResult)

        val finished = completedStrokes + stroke
        return if (finished.size == WritingCanvasGeometry.ga(width, height).strokes.size) {
            copy(completedStrokes = finished, result = GieokTraceResult.SUCCESS)
        } else {
            copy(stroke = StrokePath(), completedStrokes = finished, result = null)
        }
    }

    fun clear(): TraceAttempt = TraceAttempt()
}
