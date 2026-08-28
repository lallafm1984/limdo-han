package com.nullplaying.limdo

import androidx.compose.ui.graphics.Color

internal object LimDoPlaygroundTokens {
    const val SCREEN_PADDING_DP = 32f
    const val CARD_GAP_DP = 20f
    const val CARD_CORNER_DP = 32f
    const val CARD_BORDER_DP = 4f
    const val CARD_SHADOW_DP = 8f
    const val HOME_ENTRANCE_DURATION_MS = 360
    const val HOME_ENTRANCE_STAGGER_MS = 90
    const val HOME_ENTRANCE_OFFSET_DP = 28f
    const val HOME_CARD_PRESSED_SCALE = 0.94f
    const val HOME_CARD_GLOW_BORDER_DP = 10f
    const val MENU_TRANSITION_DURATION_MS = 480

    val playgroundBackground = Color(0xFFFFD85A)
    val cardSurface = Color(0xFFFFFEFA)
    val cardOutline = Color(0xFF6B4A24)
}

internal data class MenuTransitionVisuals(
    val symbolScale: Float,
    val symbolAlpha: Float,
)

internal fun menuTransitionVisuals(progress: Float): MenuTransitionVisuals {
    val bounded = progress.coerceIn(0f, 1f)
    return MenuTransitionVisuals(
        symbolScale = 0.82f + (0.18f * bounded),
        symbolAlpha = bounded,
    )
}

internal data class HomeCardPressVisuals(
    val scale: Float,
    val glowBorderDp: Float,
)

internal fun homeCardPressVisuals(isPressed: Boolean) = HomeCardPressVisuals(
    scale = if (isPressed) LimDoPlaygroundTokens.HOME_CARD_PRESSED_SCALE else 1f,
    glowBorderDp = if (isPressed) LimDoPlaygroundTokens.HOME_CARD_GLOW_BORDER_DP else 0f,
)

internal data class HomeEntranceVisuals(
    val alpha: Float,
    val scale: Float,
    val offsetDp: Float,
)

internal fun homeEntranceVisuals(progress: Float): HomeEntranceVisuals {
    val bounded = progress.coerceIn(0f, 1f)
    return HomeEntranceVisuals(
        alpha = bounded,
        scale = 0.92f + (0.08f * bounded),
        offsetDp = LimDoPlaygroundTokens.HOME_ENTRANCE_OFFSET_DP * (1f - bounded),
    )
}

internal data class LearningMenuVisuals(
    val accent: Color,
    val softSurface: Color,
)

internal enum class LessonCardVisualState {
    DEFAULT,
    SELECTED,
    DISABLED,
}

internal data class LessonCardVisuals(
    val surface: Color,
    val outline: Color,
    val outlineWidthDp: Float,
    val cornerDp: Float,
    val shadowDp: Float,
)

internal fun LearningMenu.lessonCardVisuals(state: LessonCardVisualState): LessonCardVisuals {
    val menuVisuals = visuals()
    return when (state) {
        LessonCardVisualState.DEFAULT -> LessonCardVisuals(
            surface = LimDoPlaygroundTokens.cardSurface,
            outline = menuVisuals.accent,
            outlineWidthDp = LimDoPlaygroundTokens.CARD_BORDER_DP,
            cornerDp = LimDoPlaygroundTokens.CARD_CORNER_DP,
            shadowDp = LimDoPlaygroundTokens.CARD_SHADOW_DP,
        )
        LessonCardVisualState.SELECTED -> LessonCardVisuals(
            surface = menuVisuals.accent,
            outline = LimDoPlaygroundTokens.cardOutline,
            outlineWidthDp = 8f,
            cornerDp = 20f,
            shadowDp = 2f,
        )
        LessonCardVisualState.DISABLED -> LessonCardVisuals(
            surface = Color(0xFFE2DDD4),
            outline = Color(0xFF9B9489),
            outlineWidthDp = 2f,
            cornerDp = 16f,
            shadowDp = 0f,
        )
    }
}

internal fun LearningMenu.visuals(): LearningMenuVisuals = when (this) {
    LearningMenu.CONSONANTS -> LearningMenuVisuals(
        accent = Color(0xFF2878B8),
        softSurface = Color(0xFFE5F3FF),
    )
    LearningMenu.VOWELS -> LearningMenuVisuals(
        accent = Color(0xFFE86B2D),
        softSurface = Color(0xFFFFE8D2),
    )
    LearningMenu.GANADA -> LearningMenuVisuals(
        accent = Color(0xFF3F8A45),
        softSurface = Color(0xFFE4F4DE),
    )
}
