package com.example.limdo

internal object RewardPathGeometry {
    const val VEHICLE_WIDTH = 128f
    const val STEP_DISTANCE = 48f
    const val GLYPH_SAFETY_MARGIN = 24f

    private const val WRITING_HORIZONTAL_PADDING = 72f
    private const val WRITING_TOP_PADDING = 24f
    private const val WRITING_BOTTOM_PADDING = 92f

    fun vehicleCenterX(
        containerWidth: Float,
        containerHeight: Float,
        completedSteps: Float,
        targetSteps: Int,
    ): Float {
        require(containerWidth > WRITING_HORIZONTAL_PADDING * 2f)
        require(containerHeight > WRITING_TOP_PADDING + WRITING_BOTTOM_PADDING)
        require(completedSteps >= 0f)
        require(targetSteps > 0)

        val canvasWidth = containerWidth - WRITING_HORIZONTAL_PADDING * 2f
        val canvasHeight = containerHeight - WRITING_TOP_PADDING - WRITING_BOTTOM_PADDING
        val glyph = WritingCanvasGeometry.visibleLessonGlyph(canvasWidth, canvasHeight)
        val glyphGuideLeft = WRITING_HORIZONTAL_PADDING +
            glyph.strokes.flatten().minOf { it.x } - glyph.strokeWidth / 2f
        val finalCenter = glyphGuideLeft - GLYPH_SAFETY_MARGIN - VEHICLE_WIDTH / 2f
        val startCenter = finalCenter - STEP_DISTANCE * targetSteps
        return startCenter + STEP_DISTANCE * completedSteps.coerceAtMost(targetSteps.toFloat())
    }
}
