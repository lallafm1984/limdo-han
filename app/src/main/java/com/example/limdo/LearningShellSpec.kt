package com.example.limdo

internal object LearningShellSpec {
    const val GUIDE_WEIGHT = 3f
    const val WRITING_BOARD_WEIGHT = 7f

    val writingBoardFraction: Float
        get() = WRITING_BOARD_WEIGHT / (GUIDE_WEIGHT + WRITING_BOARD_WEIGHT)
}
