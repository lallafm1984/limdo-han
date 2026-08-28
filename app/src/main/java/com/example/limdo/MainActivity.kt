package com.example.limdo

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        setContent {
            LimDoApp()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

private val LimDoColorScheme = lightColorScheme(
    primary = Color(0xFF3F725E),
    onPrimary = Color.White,
    secondary = Color(0xFFF0A660),
    background = Color(0xFFFFF8EC),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF26332D),
)

@Composable
private fun LimDoApp() {
    MaterialTheme(colorScheme = LimDoColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LearningShell()
        }
    }
}

@Composable
private fun LearningShell() {
    var destination by remember { mutableStateOf<LearningDestination>(LearningDestination.Home) }
    var nextWritingSessionId by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = destination != LearningDestination.Home) {
        destination = LearningNavigation.back(destination)
    }

    when (val current = destination) {
        LearningDestination.Home -> LearningMenuHome(
            onSelect = {
                destination = LearningDestination.MenuTransition(it)
            },
        )
        is LearningDestination.MenuTransition -> MenuSelectionTransition(
            menu = current.menu,
            onFinished = { destination = LearningDestination.Selection(current.menu) },
        )
        is LearningDestination.Selection -> LessonSelection(
            menu = current.menu,
            onSelect = { lesson ->
                nextWritingSessionId += 1
                destination = LearningDestination.Writing(
                    menu = current.menu,
                    lessonId = lesson.id,
                    sessionId = nextWritingSessionId,
                )
            },
            onHome = { destination = LearningDestination.Home },
        )
        is LearningDestination.Writing -> key(current.sessionId) {
            WritingLesson(
                initialLessonId = current.lessonId,
                menu = current.menu,
                onHome = { destination = LearningDestination.Home },
            )
        }
    }
}

@Composable
private fun MenuSelectionTransition(
    menu: LearningMenu,
    onFinished: () -> Unit,
) {
    val progress = remember(menu) { Animatable(0f) }
    LaunchedEffect(menu) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(LimDoPlaygroundTokens.MENU_TRANSITION_DURATION_MS),
        )
        onFinished()
    }
    val transition = menuTransitionVisuals(progress.value)
    val visuals = menu.visuals()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(visuals.softSurface)
            .semantics(mergeDescendants = true) {
                contentDescription = "${menu.label} 선택 이동, ${menu.symbol}"
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = menu.symbol,
            color = visuals.accent,
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.graphicsLayer {
                alpha = transition.symbolAlpha
                scaleX = transition.symbolScale
                scaleY = transition.symbolScale
            },
        )
    }
}

@Composable
private fun LearningMenuHome(onSelect: (LearningMenu) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(LimDoPlaygroundTokens.playgroundBackground)
            .padding(LimDoPlaygroundTokens.SCREEN_PADDING_DP.dp),
        horizontalArrangement = Arrangement.spacedBy(LimDoPlaygroundTokens.CARD_GAP_DP.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LearningMenu.entries.forEachIndexed { index, menu ->
            val visuals = menu.visuals()
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val press = homeCardPressVisuals(isPressed)
            val entranceProgress = remember(menu) { Animatable(0f) }
            LaunchedEffect(menu) {
                delay(index * LimDoPlaygroundTokens.HOME_ENTRANCE_STAGGER_MS.toLong())
                entranceProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(LimDoPlaygroundTokens.HOME_ENTRANCE_DURATION_MS),
                )
            }
            val entrance = homeEntranceVisuals(entranceProgress.value)
            Surface(
                onClick = { onSelect(menu) },
                interactionSource = interactionSource,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha = entrance.alpha
                        scaleX = entrance.scale * press.scale
                        scaleY = entrance.scale * press.scale
                        translationY = entrance.offsetDp.dp.toPx()
                    }
                    .border(
                        press.glowBorderDp.dp,
                        Color.White,
                        RoundedCornerShape(LimDoPlaygroundTokens.CARD_CORNER_DP.dp),
                    )
                    .border(
                        LimDoPlaygroundTokens.CARD_BORDER_DP.dp,
                        visuals.accent,
                        RoundedCornerShape(LimDoPlaygroundTokens.CARD_CORNER_DP.dp),
                    )
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${menu.label} 학습, ${menu.symbol}"
                    },
                color = visuals.softSurface,
                shape = RoundedCornerShape(LimDoPlaygroundTokens.CARD_CORNER_DP.dp),
                shadowElevation = LimDoPlaygroundTokens.CARD_SHADOW_DP.dp,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(menu.symbol, fontSize = 76.sp, fontWeight = FontWeight.Bold)
                    Text(menu.label, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LessonSelection(
    menu: LearningMenu,
    onSelect: (LessonSpec) -> Unit,
    onHome: () -> Unit,
) {
    val lessons = LearningNavigation.lessons(menu)
    val visuals = menu.visuals()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(visuals.softSurface)
            .padding(LimDoPlaygroundTokens.SCREEN_PADDING_DP.dp),
        verticalArrangement = Arrangement.spacedBy(LimDoPlaygroundTokens.CARD_GAP_DP.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LimDoPlaygroundTokens.CARD_GAP_DP.dp),
        ) {
            Surface(
                onClick = onHome,
                modifier = Modifier.size(72.dp),
                color = Color.White,
                shape = CircleShape,
            ) { Box(contentAlignment = Alignment.Center) { Text("⌂", fontSize = 36.sp) } }
            Text(menu.symbol, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        lessons.chunked(7).forEach { rowLessons ->
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                rowLessons.forEach { lesson ->
                    val available = true
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val cardState = when {
                        !available -> LessonCardVisualState.DISABLED
                        isPressed -> LessonCardVisualState.SELECTED
                        else -> LessonCardVisualState.DEFAULT
                    }
                    val cardVisuals = menu.lessonCardVisuals(cardState)
                    val cardShape = RoundedCornerShape(cardVisuals.cornerDp.dp)
                    Surface(
                        onClick = { onSelect(lesson) },
                        enabled = available,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(
                                cardVisuals.outlineWidthDp.dp,
                                cardVisuals.outline,
                                cardShape,
                            )
                            .semantics {
                                contentDescription = "${lesson.glyph} 쓰기 시작"
                                stateDescription = when (cardState) {
                                    LessonCardVisualState.DEFAULT -> "선택 가능"
                                    LessonCardVisualState.SELECTED -> "선택됨"
                                    LessonCardVisualState.DISABLED -> "아직 사용할 수 없음"
                                }
                            },
                        color = cardVisuals.surface,
                        shape = cardShape,
                        shadowElevation = cardVisuals.shadowDp.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(lesson.glyph, fontSize = 58.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                repeat(7 - rowLessons.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun WritingLesson(
    initialLessonId: LessonId,
    menu: LearningMenu,
    onHome: () -> Unit,
) {
    var clearRequest by rememberSaveable { mutableIntStateOf(0) }
    var traceResult by rememberSaveable { mutableStateOf<GieokTraceResult?>(null) }
    var lessonIndex by rememberSaveable {
        mutableIntStateOf(KoreanCurriculum.lessons.indexOfFirst { it.id == initialLessonId })
    }
    val celebrationProgress = remember { Animatable(0f) }
    val retryProgress = remember { Animatable(1f) }
    var retryEvent by rememberSaveable { mutableIntStateOf(0) }
    var retryLessonIndex by rememberSaveable { mutableIntStateOf(-1) }
    val currentLesson = KoreanCurriculum.lessons[lessonIndex]
    val retryVisuals = retryAnimationVisuals(retryProgress.value)

    LaunchedEffect(retryEvent, currentLesson.id, retryLessonIndex) {
        if (retryEvent > 0 && retryLessonIndex == lessonIndex) {
            retryProgress.snapTo(0f)
            retryProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(RetryAnimationSpec.DURATION_MS),
            )
            if (traceResult.isRetryResult()) {
                clearRequest += 1
                traceResult = null
            }
            retryLessonIndex = -1
        } else {
            retryProgress.snapTo(1f)
        }
    }

    LaunchedEffect(traceResult) {
        celebrationProgress.snapTo(0f)
        if (traceResult == GieokTraceResult.SUCCESS) {
            celebrationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(SuccessCelebrationSpec.DURATION_MS),
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(menu.visuals().softSurface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = retryVisuals.offsetDp.dp.toPx() },
        ) {
            Image(
                painter = painterResource(R.drawable.limdo_sunny_flower_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.32f,
            )

            WritingBoardPreview(
                lesson = currentLesson,
                clearRequest = clearRequest,
                inputEnabled = traceResult == null || traceResult == GieokTraceResult.EMPTY,
                demonstrationStrokeIndex = null,
                retryStartMarkerScale = retryVisuals.startMarkerScale,
                onTraceResult = { result, _ ->
                    traceResult = result
                    if (result.isRetryResult()) {
                        retryLessonIndex = lessonIndex
                        retryEvent += 1
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = LearningShellSpec.CANVAS_HORIZONTAL_PADDING_DP.dp,
                        vertical = LearningShellSpec.CANVAS_VERTICAL_PADDING_DP.dp,
                    ),
            )

            if (traceResult == GieokTraceResult.SUCCESS) {
                SuccessFeedbackOverlay(
                    progress = celebrationProgress.value,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            EdgeActionColumns(
                onHome = onHome,
                onClear = {
                    retryLessonIndex = -1
                    clearRequest += 1
                    traceResult = null
                },
                onPrevious = {
                    retryLessonIndex = -1
                    clearRequest += 1
                    traceResult = null
                    val previousLesson = LearningNavigation.previousLesson(menu, currentLesson)
                    lessonIndex = KoreanCurriculum.lessons.indexOfFirst { it.id == previousLesson.id }
                },
                onNext = {
                    retryLessonIndex = -1
                    clearRequest += 1
                    traceResult = null
                    val nextLesson = LearningNavigation.nextLesson(menu, currentLesson)
                    lessonIndex = KoreanCurriculum.lessons.indexOfFirst { it.id == nextLesson.id }
                },
                modifier = Modifier.fillMaxSize(),
            )

            if (retryVisuals.flashAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFF4C7).copy(alpha = retryVisuals.flashAlpha)),
                )
            }
        }
    }
}

@Composable
private fun SuccessFeedbackOverlay(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val visuals = successCelebrationVisuals(progress)
    Image(
        painter = painterResource(R.drawable.limdo_success_fullscreen_feedback),
        contentDescription = stringResource(R.string.success_feedback_description),
        modifier = modifier
            .graphicsLayer {
                alpha = visuals.alpha
                scaleX = visuals.scale
                scaleY = visuals.scale
            },
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun LessonHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        color = Color(0xFFE4F1EA),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.journey_label),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.lesson_prompt),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = stringResource(R.string.lesson_progress),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun WritingBoardPreview(
    lesson: LessonSpec,
    clearRequest: Int,
    inputEnabled: Boolean,
    demonstrationStrokeIndex: Int?,
    retryStartMarkerScale: Float,
    onTraceResult: (GieokTraceResult?, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
    ) {
        WritingCanvas(
            lesson = lesson,
            contentDescription = writingCanvasDescription(lesson.glyph, lesson.strokeCount),
            clearRequest = clearRequest,
            inputEnabled = inputEnabled,
            demonstrationStrokeIndex = demonstrationStrokeIndex,
            retryStartMarkerScale = retryStartMarkerScale,
            onTraceResult = onTraceResult,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun GieokTraceResult?.isRetryResult(): Boolean = when (this) {
    GieokTraceResult.WRONG_START,
    GieokTraceResult.WRONG_DIRECTION,
    GieokTraceResult.OFF_GUIDE,
    GieokTraceResult.INCOMPLETE,
    -> true
    GieokTraceResult.SUCCESS, GieokTraceResult.EMPTY, null -> false
}

@Composable
private fun EdgeActionColumns(
    onHome: () -> Unit,
    onClear: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(LearningShellSpec.ACTION_COLUMN_SPACING_DP.dp),
        ) {
            HomeAction(
                onClick = onHome,
                modifier = Modifier.width(LearningShellSpec.ACTION_COLUMN_WIDTH_DP.dp),
            )
            ClearAction(
                contentDescription = stringResource(R.string.action_rewrite_description),
                onClick = onClear,
                modifier = Modifier.width(LearningShellSpec.ACTION_COLUMN_WIDTH_DP.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(LearningShellSpec.ACTION_COLUMN_SPACING_DP.dp),
        ) {
            LessonNavigationAction(
                contentDescription = stringResource(R.string.action_previous_description),
                previous = true,
                onClick = onPrevious,
                modifier = Modifier.width(LearningShellSpec.ACTION_COLUMN_WIDTH_DP.dp),
            )
            LessonNavigationAction(
                contentDescription = stringResource(R.string.action_next_description),
                previous = false,
                onClick = onNext,
                modifier = Modifier.width(LearningShellSpec.ACTION_COLUMN_WIDTH_DP.dp),
            )
        }
    }
}

@Composable
private fun HomeAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics { contentDescription = "홈으로 돌아가기" },
        color = Color(0xFFFFFEFA),
        shape = RoundedCornerShape(20.dp),
    ) {
        ActionAtlasIcon(ActionButtonAtlasSpec.HOME_COLUMN, isPressed)
    }
}

@Composable
private fun ClearAction(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics { this.contentDescription = contentDescription },
        color = Color(0xFFFFF3E6),
        shape = RoundedCornerShape(20.dp),
    ) {
        ActionAtlasIcon(ActionButtonAtlasSpec.CLEAR_COLUMN, isPressed)
    }
}

@Composable
private fun LessonNavigationAction(
    contentDescription: String,
    previous: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics { this.contentDescription = contentDescription },
        color = Color(0xFFDCEDE5),
        shape = RoundedCornerShape(20.dp),
    ) {
        ActionAtlasIcon(
            column = ActionButtonAtlasSpec.NEXT_COLUMN,
            isPressed = isPressed,
            mirrorHorizontal = previous,
        )
    }
}

@Composable
private fun ActionAtlasIcon(
    column: Int,
    isPressed: Boolean,
    mirrorHorizontal: Boolean = false,
) {
    val atlas = ImageBitmap.imageResource(R.drawable.limdo_action_button_atlas)
    val row = if (isPressed) ActionButtonAtlasSpec.PRESSED_ROW else ActionButtonAtlasSpec.DEFAULT_ROW
    Canvas(
        modifier = Modifier
            .size(ActionButtonAtlasSpec.ICON_DP.dp)
            .graphicsLayer {
                val scale = if (isPressed) ActionButtonAtlasSpec.PRESSED_SCALE else 1f
                scaleX = if (mirrorHorizontal) -scale else scale
                scaleY = scale
            },
    ) {
        val side = minOf(size.width, size.height)
        drawImage(
            image = atlas,
            srcOffset = IntOffset(column * ActionButtonAtlasSpec.CELL_SIZE_PX, row * ActionButtonAtlasSpec.CELL_SIZE_PX),
            srcSize = IntSize(ActionButtonAtlasSpec.CELL_SIZE_PX, ActionButtonAtlasSpec.CELL_SIZE_PX),
            dstOffset = IntOffset(((size.width - side) / 2f).roundToInt(), ((size.height - side) / 2f).roundToInt()),
            dstSize = IntSize(side.roundToInt(), side.roundToInt()),
        )
    }
}
