package com.example.limdo

internal data class MarkerCenter(
    val x: Float,
    val y: Float,
)

internal object SuccessMarkerGeometry {
    const val WIDTH = 144f
    const val HEIGHT = 64f
    const val GLYPH_SAFETY_MARGIN = 24f

    private const val WRITING_HORIZONTAL_PADDING = 72f
    private const val WRITING_TOP_PADDING = 24f
    private const val WRITING_BOTTOM_PADDING = 92f

    fun center(
        containerWidth: Float,
        containerHeight: Float,
        lesson: LessonSpec = GaLesson,
    ): MarkerCenter {
        require(containerWidth > WRITING_HORIZONTAL_PADDING * 2f)
        require(containerHeight > WRITING_TOP_PADDING + WRITING_BOTTOM_PADDING)

        val canvasWidth = containerWidth - WRITING_HORIZONTAL_PADDING * 2f
        val canvasHeight = containerHeight - WRITING_TOP_PADDING - WRITING_BOTTOM_PADDING
        val glyph = WritingCanvasGeometry.visibleLessonGlyph(canvasWidth, canvasHeight, lesson)
        val points = glyph.strokes.flatten()
        val guideLeft = WRITING_HORIZONTAL_PADDING + points.minOf { it.x } - glyph.strokeWidth / 2f
        val guideRight = WRITING_HORIZONTAL_PADDING + points.maxOf { it.x } + glyph.strokeWidth / 2f
        val guideTop = WRITING_TOP_PADDING + points.minOf { it.y } - glyph.strokeWidth / 2f
        val guideBottom = WRITING_TOP_PADDING + points.maxOf { it.y } + glyph.strokeWidth / 2f
        val rightCenter = guideRight + GLYPH_SAFETY_MARGIN + WIDTH / 2f
        val leftCenter = guideLeft - GLYPH_SAFETY_MARGIN - WIDTH / 2f
        val centerX = if (rightCenter + WIDTH / 2f <= containerWidth) rightCenter else leftCenter
        return MarkerCenter(
            x = centerX.coerceIn(WIDTH / 2f, containerWidth - WIDTH / 2f),
            y = ((guideTop + guideBottom) / 2f)
                .coerceIn(HEIGHT / 2f, containerHeight - HEIGHT / 2f),
        )
    }
}
