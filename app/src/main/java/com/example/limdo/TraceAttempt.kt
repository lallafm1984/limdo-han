package com.example.limdo

internal data class TraceAttempt(
    val stroke: StrokePath = StrokePath(),
    val result: GieokTraceResult? = null,
) {
    fun start(
        point: CanvasPoint,
        width: Float,
        height: Float,
        safeInset: Float,
    ): TraceAttempt = TraceAttempt(
        stroke = stroke.start(point, width, height, safeInset),
    )

    fun append(
        point: CanvasPoint,
        width: Float,
        height: Float,
        safeInset: Float,
    ): TraceAttempt = copy(
        stroke = stroke.append(point, width, height, safeInset),
    )

    fun finish(width: Float, height: Float): TraceAttempt = copy(
        result = GieokTraceEvaluator.evaluate(width, height, stroke),
    )

    fun clear(): TraceAttempt = TraceAttempt()
}
