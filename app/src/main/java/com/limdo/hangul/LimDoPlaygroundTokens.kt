package com.limdo.hangul

import androidx.compose.ui.graphics.Color

internal object LimDoPlaygroundTokens {
    // D0 art bible의 공통 표현 계약. D1 이후에서 화면별 임의 수치 대신 이 계층을 쓴다.
    const val SCREEN_PADDING_DP = 32f
    const val SPACING_COMPACT_DP = 12f
    const val SPACING_RELATED_DP = 20f
    const val SPACING_SECTION_DP = 32f
    const val SPACING_SCENE_DP = 48f
    const val CARD_GAP_DP = 20f
    const val CORNER_CONTROL_DP = 20f
    const val CARD_CORNER_DP = 32f
    const val CORNER_HERO_DP = 40f
    const val CARD_BORDER_DP = 4f
    const val ELEVATION_FLAT_DP = 0f
    const val ELEVATION_CONTROL_DP = 4f
    const val CARD_SHADOW_DP = 8f
    const val ELEVATION_HERO_DP = 12f
    const val TYPE_SUPPORT_SP = 16f
    const val TYPE_ACTION_SP = 22f
    const val TYPE_SECTION_SP = 30f
    const val TYPE_HERO_SP = 48f
    const val ICON_CONTROL_DP = 56f
    const val ICON_PRIMARY_DP = 72f
    const val CHILD_TOUCH_MIN_DP = 64f
    const val GUARDIAN_TOUCH_MIN_DP = 64f
    const val WRITING_CANVAS_MIN_WIDTH_PX = 1962
    const val WRITING_CANVAS_MIN_HEIGHT_PX = 954
    const val WRITING_ACTION_PX = 168
    const val BACKGROUND_CHILD_MAX_EDGE_OCCUPANCY = 0.22f
    const val BACKGROUND_GUARDIAN_MAX_DECORATION_OCCUPANCY = 0.10f
    const val MOTION_FEEDBACK_DURATION_MS = 180
    const val HOME_ENTRANCE_DURATION_MS = 360
    const val HOME_ENTRANCE_STAGGER_MS = 90
    const val HOME_ENTRANCE_OFFSET_DP = 28f
    const val HOME_CARD_PRESSED_SCALE = 0.94f
    const val HOME_CARD_GLOW_BORDER_DP = 10f
    const val MENU_TRANSITION_OVERLAY_ALPHA = 0.84f
    const val MENU_TRANSITION_DURATION_MS = 480
    const val MOTION_RETRY_MAX_DURATION_MS = 500
    const val MOTION_SUCCESS_MAX_DURATION_MS = 1000
    const val REDUCED_MOTION_PARTICLE_COUNT = 0

    val warmCream = Color(0xFFFFF8EC)
    val deepTeal = Color(0xFF3F725E)
    val coral = Color(0xFFD95D4F)
    val sunYellow = Color(0xFFFFD85A)
    val ink = Color(0xFF26332D)
    val playgroundBackground = Color(0xFFFFD85A)
    val cardSurface = Color(0xFFFFFEFA)
    val cardOutline = Color(0xFF6B4A24)

    const val MATERIAL_NAME = "soft toy clay + warm paper"
    const val LIGHT_DIRECTION_NAME = "upper-left"
    const val ICON_STYLE_NAME = "rounded tactile silhouette"
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
