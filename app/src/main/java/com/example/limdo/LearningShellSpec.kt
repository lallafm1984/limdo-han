package com.example.limdo

internal object LearningShellSpec {
    const val CANVAS_HORIZONTAL_PADDING_DP = 72f
    const val CANVAS_VERTICAL_PADDING_DP = 24f
    const val ACTION_COLUMN_WIDTH_DP = 64f
    const val ACTION_COLUMN_SPACING_DP = 12f

    fun writingCanvasSizePx(screenWidthPx: Int, screenHeightPx: Int, density: Float): Pair<Int, Int> {
        val horizontalInset = 2f * CANVAS_HORIZONTAL_PADDING_DP * density
        val verticalInset = 2f * CANVAS_VERTICAL_PADDING_DP * density
        return (screenWidthPx - horizontalInset).toInt() to
            (screenHeightPx - verticalInset).toInt()
    }
}
