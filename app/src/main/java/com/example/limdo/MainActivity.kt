package com.example.limdo

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var localSpeech: LocalKoreanSpeech
    private var speechState by mutableStateOf<SpeechPlaybackState>(SpeechPlaybackState.Initializing)
    private var demonstrationStrokeIndex by mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localSpeech = LocalKoreanSpeech(
            context = this,
            onDemonstrationStrokeChanged = { strokeIndex ->
                runOnUiThread { demonstrationStrokeIndex = strokeIndex }
            },
            onStateChanged = { newState -> runOnUiThread { speechState = newState } },
        )
        setContent {
            LimDoApp(
                speechState = speechState,
                demonstrationStrokeIndex = demonstrationStrokeIndex,
                speak = localSpeech::speakLatest,
                stopSpeech = localSpeech::stop,
            )
        }
    }

    override fun onDestroy() {
        localSpeech.release()
        super.onDestroy()
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

private val LessonRewardStateSaver = listSaver<LessonRewardState, Any>(
    save = {
        listOf(it.completedSteps, it.targetSteps, it.successConsumed, it.phase.name)
    },
    restore = {
        LessonRewardState(
            completedSteps = it[0] as Int,
            targetSteps = it[1] as Int,
            successConsumed = it[2] as Boolean,
            phase = RewardMovePhase.valueOf(it[3] as String),
        )
    },
)

private val RewardOffsetSaver = Saver<Animatable<Float, *>, Float>(
    save = { it.value },
    restore = { Animatable(it) },
)

@Composable
private fun LimDoApp(
    speechState: SpeechPlaybackState,
    demonstrationStrokeIndex: Int?,
    speak: (SpokenCue) -> Boolean,
    stopSpeech: () -> Unit,
) {
    MaterialTheme(colorScheme = LimDoColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LearningShell(
                speechState = speechState,
                demonstrationStrokeIndex = demonstrationStrokeIndex,
                speak = speak,
                stopSpeech = stopSpeech,
            )
        }
    }
}

@Composable
private fun LearningShell(
    speechState: SpeechPlaybackState,
    demonstrationStrokeIndex: Int?,
    speak: (SpokenCue) -> Boolean,
    stopSpeech: () -> Unit,
) {
    var destination by remember { mutableStateOf<LearningDestination>(LearningDestination.Home) }
    var nextWritingSessionId by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(enabled = destination != LearningDestination.Home) {
        stopSpeech()
        destination = LearningNavigation.back(destination)
    }

    when (val current = destination) {
        LearningDestination.Home -> LearningMenuHome(
            onSelect = {
                speak(it.spokenCue)
                destination = LearningDestination.Selection(it)
            },
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
                speechState = speechState,
                demonstrationStrokeIndex = demonstrationStrokeIndex,
                speak = speak,
                stopSpeech = stopSpeech,
                onHome = {
                    stopSpeech()
                    destination = LearningDestination.Home
                },
            )
        }
    }
}

@Composable
private fun LearningMenuHome(onSelect: (LearningMenu) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD85A))
            .padding(48.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LearningMenu.entries.forEach { menu ->
            Surface(
                onClick = { onSelect(menu) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${menu.label} 학습, ${menu.symbol}"
                    },
                color = when (menu) {
                    LearningMenu.CONSONANTS -> Color(0xFFE5F3FF)
                    LearningMenu.VOWELS -> Color(0xFFFFE8D2)
                    LearningMenu.GANADA -> Color(0xFFE4F4DE)
                },
                shape = RoundedCornerShape(36.dp),
                shadowElevation = 8.dp,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(menu.icon, fontSize = 72.sp)
                    Text(menu.symbol, fontSize = 54.sp, fontWeight = FontWeight.Bold)
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD85A))
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Surface(
                onClick = onHome,
                modifier = Modifier.size(72.dp),
                color = Color.White,
                shape = CircleShape,
            ) { Box(contentAlignment = Alignment.Center) { Text("⌂", fontSize = 36.sp) } }
            Text("${menu.icon}  ${menu.symbol}", fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        lessons.chunked(7).forEach { rowLessons ->
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                rowLessons.forEach { lesson ->
                    Surface(
                        onClick = { onSelect(lesson) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .semantics { contentDescription = "${lesson.glyph} 쓰기 시작" },
                        color = Color(0xFFFFFEFA),
                        shape = RoundedCornerShape(28.dp),
                        shadowElevation = 5.dp,
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
    speechState: SpeechPlaybackState,
    demonstrationStrokeIndex: Int?,
    speak: (SpokenCue) -> Boolean,
    stopSpeech: () -> Unit,
    onHome: () -> Unit,
) {
    var clearRequest by rememberSaveable { mutableIntStateOf(0) }
    var traceResult by rememberSaveable { mutableStateOf<GieokTraceResult?>(null) }
    var traceStrokeIndex by rememberSaveable { mutableIntStateOf(0) }
    var lessonIndex by rememberSaveable {
        mutableIntStateOf(KoreanCurriculum.lessons.indexOfFirst { it.id == initialLessonId })
    }
    var vehicleIndex by rememberSaveable { mutableIntStateOf(0) }
    var vehicleSuccessArmed by rememberSaveable { mutableStateOf(true) }
    var nextVehiclePending by rememberSaveable { mutableStateOf(false) }
    var rewardState by rememberSaveable(stateSaver = LessonRewardStateSaver) {
        mutableStateOf(LessonRewardState())
    }
    val rewardOffset = rememberSaveable(saver = RewardOffsetSaver) { Animatable(0f) }
    var initialSpeechRequested by rememberSaveable { mutableStateOf(false) }
    var initialSpeechPending by rememberSaveable { mutableStateOf(false) }
    var successSpeechPending by rememberSaveable { mutableStateOf(false) }
    var restoredInitialSpeechHandled by remember {
        mutableStateOf(!initialSpeechPending)
    }
    var restoredSuccessSpeechHandled by remember {
        mutableStateOf(traceResult != GieokTraceResult.SUCCESS)
    }
    val currentLesson = KoreanCurriculum.lessons[lessonIndex]
    val currentCue = SpokenCueModel.forResult(traceResult, traceStrokeIndex, currentLesson)
    val currentVehicle = VehicleCarousel.vehicles[vehicleIndex]

    LaunchedEffect(rewardState.targetSteps) {
        if (rewardState.phase == RewardMovePhase.START) {
            delay(120)
            rewardState = rewardState.moving()
        }
        if (rewardState.phase == RewardMovePhase.MOVING) {
            for (step in (rewardState.completedSteps + 1)..rewardState.targetSteps) {
                rewardOffset.animateTo(
                    targetValue = step.toFloat(),
                    animationSpec = tween(durationMillis = 280),
                )
                if (step < rewardState.targetSteps) delay(100)
            }
            rewardState = rewardState.complete()
        } else if (rewardState.targetSteps == 0) {
            rewardOffset.snapTo(0f)
        }
    }

    LaunchedEffect(speechState, initialSpeechRequested) {
        if (speechState == SpeechPlaybackState.Ready && !initialSpeechRequested) {
            initialSpeechRequested = true
            speak(currentLesson.initialCue)
        }
    }

    LaunchedEffect(speechState, initialSpeechPending, restoredInitialSpeechHandled) {
        if (speechState == SpeechPlaybackState.Completed(currentLesson.initialCue) ||
            speechState == SpeechPlaybackState.Error(currentLesson.initialCue)
        ) {
            initialSpeechPending = false
        } else if (shouldResumeInitialCue(
                speechState = speechState,
                initialSpeechPending = initialSpeechPending,
                alreadyHandled = restoredInitialSpeechHandled,
            )
        ) {
            restoredInitialSpeechHandled = true
            if (!speak(currentLesson.initialCue)) initialSpeechPending = false
        }
    }

    LaunchedEffect(speechState, traceResult, successSpeechPending, restoredSuccessSpeechHandled) {
        if (speechState == SpeechPlaybackState.Completed(currentLesson.successCue)) {
            successSpeechPending = false
        } else if (shouldResumeSuccessCue(
                speechState = speechState,
                traceResult = traceResult,
                successSpeechPending = successSpeechPending,
                alreadyHandled = restoredSuccessSpeechHandled,
            )
        ) {
            restoredSuccessSpeechHandled = true
            if (!speak(currentLesson.successCue)) successSpeechPending = false
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD85A)),
    ) {
        val rewardVehicleCenterX = RewardPathGeometry.vehicleCenterX(
            containerWidth = maxWidth.value,
            containerHeight = maxHeight.value,
            completedSteps = rewardOffset.value,
            targetSteps = currentLesson.strokeCount,
            lesson = currentLesson,
        )
        val successMarkerCenter = SuccessMarkerGeometry.center(
            containerWidth = maxWidth.value,
            containerHeight = maxHeight.value,
            lesson = currentLesson,
        )
        Image(
            painter = painterResource(R.drawable.limdo_sunny_flower_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.32f,
        )

        WritingBoardPreview(
            lesson = currentLesson,
            clearRequest = clearRequest,
            inputEnabled = !rewardState.inputLocked,
            demonstrationStrokeIndex = demonstrationStrokeIndex,
            onTraceResult = { result, strokeIndex ->
                restoredSuccessSpeechHandled = true
                successSpeechPending = result == GieokTraceResult.SUCCESS
                val vehicleState = VehicleCarouselState(
                    index = vehicleIndex,
                    successArmed = vehicleSuccessArmed,
                    nextVehiclePending = nextVehiclePending,
                ).onTraceResult(result)
                vehicleIndex = vehicleState.index
                vehicleSuccessArmed = vehicleState.successArmed
                nextVehiclePending = vehicleState.nextVehiclePending
                traceResult = result
                traceStrokeIndex = strokeIndex
                rewardState = rewardState.onTraceResult(result, currentLesson)
                if (result != null) {
                    speak(SpokenCueModel.forResult(result, strokeIndex, currentLesson))
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = LearningShellSpec.CANVAS_HORIZONTAL_PADDING_DP.dp,
                    vertical = LearningShellSpec.CANVAS_VERTICAL_PADDING_DP.dp,
                ),
        )

        Image(
            painter = painterResource(currentVehicle.drawableRes),
            contentDescription = "${currentVehicle.koreanName} 안내",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (rewardVehicleCenterX - maxWidth.value / 2f).dp, y = (-139).dp)
                .size(width = 128.dp, height = 86.dp),
            contentScale = ContentScale.Fit,
        )

        if (traceResult != null && traceResult != GieokTraceResult.EMPTY) {
            Text(
                text = if (traceResult == GieokTraceResult.SUCCESS) "★  ✓" else "↻",
                modifier = if (traceResult == GieokTraceResult.SUCCESS) {
                    Modifier
                        .align(Alignment.Center)
                        .offset(
                            x = (successMarkerCenter.x - maxWidth.value / 2f).dp,
                            y = (successMarkerCenter.y - maxHeight.value / 2f).dp,
                        )
                        .size(
                            width = SuccessMarkerGeometry.WIDTH.dp,
                            height = SuccessMarkerGeometry.HEIGHT.dp,
                        )
                } else {
                    Modifier.align(Alignment.TopCenter)
                },
                textAlign = TextAlign.Center,
                color = if (traceResult == GieokTraceResult.SUCCESS) {
                    Color(0xFF276B50)
                } else {
                    Color(0xFF9A5527)
                },
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        EdgeActionColumns(
            replayVisualState = speechState.replayVisualState,
            nextAvailable = rewardState.phase == RewardMovePhase.COMPLETE,
            onHome = onHome,
            onReplay = {
                restoredInitialSpeechHandled = true
                initialSpeechPending = currentCue == currentLesson.initialCue
                if (!speak(currentCue)) initialSpeechPending = false
            },
            onClear = {
                initialSpeechPending = false
                restoredSuccessSpeechHandled = true
                successSpeechPending = false
                stopSpeech()
                clearRequest += 1
                val vehicleState = VehicleCarouselState(
                    index = vehicleIndex,
                    successArmed = vehicleSuccessArmed,
                    nextVehiclePending = nextVehiclePending,
                ).clearCurrentInput()
                vehicleIndex = vehicleState.index
                vehicleSuccessArmed = vehicleState.successArmed
                nextVehiclePending = vehicleState.nextVehiclePending
                rewardState = LessonRewardState()
                traceResult = null
                traceStrokeIndex = 0
            },
            onNext = next@{
                if (!shouldStartNextInitialCue(
                        moveCompleted = rewardState.phase == RewardMovePhase.COMPLETE,
                        nextVehiclePending = nextVehiclePending,
                    )
                ) return@next

                initialSpeechPending = true
                restoredInitialSpeechHandled = true
                restoredSuccessSpeechHandled = true
                successSpeechPending = false
                stopSpeech()
                clearRequest += 1
                val vehicleState = VehicleCarouselState(
                    index = vehicleIndex,
                    successArmed = vehicleSuccessArmed,
                    nextVehiclePending = nextVehiclePending,
                ).prepareNextInput(moveCompleted = true)
                vehicleIndex = vehicleState.index
                vehicleSuccessArmed = vehicleState.successArmed
                nextVehiclePending = vehicleState.nextVehiclePending
                rewardState = LessonRewardState()
                traceResult = null
                traceStrokeIndex = 0
                val nextLessonIndex = KoreanCurriculum.nextIndex(lessonIndex)
                val nextLesson = KoreanCurriculum.lessons[nextLessonIndex]
                lessonIndex = nextLessonIndex
                if (!speak(nextLesson.initialCue)) initialSpeechPending = false
            },
            modifier = Modifier
                .fillMaxSize(),
        )
    }
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
private fun GuideCharacterCard(
    traceResult: GieokTraceResult?,
    modifier: Modifier = Modifier,
) {
    val feedback = when (traceResult) {
        null, GieokTraceResult.EMPTY -> ChildFeedback(
            symbol = "🐰",
            visualCue = "●  →  ↓",
            message = stringResource(R.string.guide_message),
            backgroundColor = Color(0xFFFFEBCB),
        )
        GieokTraceResult.SUCCESS -> ChildFeedback(
            symbol = "★",
            visualCue = "✓",
            message = stringResource(R.string.trace_success),
            backgroundColor = Color(0xFFDDF2E5),
        )
        GieokTraceResult.WRONG_START -> ChildFeedback(
            symbol = "↻",
            visualCue = "●  →  ↓",
            message = stringResource(R.string.trace_retry_start),
            backgroundColor = Color(0xFFFFEBCB),
        )
        GieokTraceResult.WRONG_DIRECTION -> ChildFeedback(
            symbol = "↻",
            visualCue = "→  ↓",
            message = stringResource(R.string.trace_retry_direction),
            backgroundColor = Color(0xFFFFEBCB),
        )
        GieokTraceResult.OFF_GUIDE -> ChildFeedback(
            symbol = "↻",
            visualCue = "ㄱ",
            message = stringResource(R.string.trace_retry_guide),
            backgroundColor = Color(0xFFFFEBCB),
        )
        GieokTraceResult.INCOMPLETE -> ChildFeedback(
            symbol = "↻",
            visualCue = "…  ↓",
            message = stringResource(R.string.trace_retry_finish),
            backgroundColor = Color(0xFFFFEBCB),
        )
    }

    Surface(
        modifier = modifier.semantics { stateDescription = feedback.message },
        color = feedback.backgroundColor,
        shape = RoundedCornerShape(30.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFFFF8EC), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = feedback.symbol,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = stringResource(R.string.guide_character),
                color = Color(0xFF7A4A22),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = feedback.visualCue,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = feedback.message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
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
            onTraceResult = onTraceResult,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private data class ChildFeedback(
    val symbol: String,
    val visualCue: String,
    val message: String,
    val backgroundColor: Color,
)

@Composable
private fun EdgeActionColumns(
    replayVisualState: ReplayVisualState,
    nextAvailable: Boolean,
    onHome: () -> Unit,
    onReplay: () -> Unit,
    onClear: () -> Unit,
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
            ReplayAction(
                label = stringResource(R.string.action_replay),
                contentDescription = stringResource(R.string.action_replay_description),
                visualState = replayVisualState,
                onClick = onReplay,
                modifier = Modifier.width(LearningShellSpec.ACTION_COLUMN_WIDTH_DP.dp),
            )
            ClearAction(
                label = stringResource(R.string.action_clear),
                contentDescription = stringResource(R.string.action_clear_description),
                onClick = onClear,
                modifier = Modifier.width(LearningShellSpec.ACTION_COLUMN_WIDTH_DP.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
        ) {
            NextAction(
                label = stringResource(R.string.action_next),
                available = nextAvailable,
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
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics { contentDescription = "홈으로 돌아가기" },
        color = Color(0xFFFFFEFA),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("⌂", color = Color(0xFF7A4A22), fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReplayAction(
    label: String,
    contentDescription: String,
    visualState: ReplayVisualState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val available = visualState == ReplayVisualState.AVAILABLE
    val playing = visualState == ReplayVisualState.PLAYING
    Surface(
        onClick = onClick,
        enabled = available,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics(mergeDescendants = true) {
                this.contentDescription = when (visualState) {
                    ReplayVisualState.PLAYING -> "$contentDescription, 안내 재생 중, 사용 불가"
                    ReplayVisualState.AVAILABLE -> "$contentDescription, 음성 안내 사용 가능"
                    ReplayVisualState.UNAVAILABLE -> "$contentDescription, 음성 안내 사용 불가"
                }
                stateDescription = when (visualState) {
                    ReplayVisualState.PLAYING -> "안내 재생 중, 사용 불가"
                    ReplayVisualState.AVAILABLE -> "음성 안내 사용 가능"
                    ReplayVisualState.UNAVAILABLE -> "음성 안내 사용 불가"
                }
            },
        color = when (visualState) {
            ReplayVisualState.PLAYING -> Color(0xFFDDEEFF)
            ReplayVisualState.AVAILABLE -> Color(0xFFDCEDE5)
            ReplayVisualState.UNAVAILABLE -> Color(0xFFD8D4CC)
        },
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = when (visualState) {
                    ReplayVisualState.PLAYING -> "🔊 )))"
                    ReplayVisualState.AVAILABLE -> "🔊"
                    ReplayVisualState.UNAVAILABLE -> "🔇"
                },
                fontSize = 24.sp,
            )
            Text(
                text = label,
                color = when {
                    playing -> Color(0xFF24577A)
                    available -> Color(0xFF285A46)
                    else -> Color(0xFF68645E)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ClearAction(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics { this.contentDescription = contentDescription },
        color = Color(0xFFFFF3E6),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "⌫",
                color = Color(0xFF7A4A22),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                color = Color(0xFF7A4A22),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun NextAction(
    label: String,
    available: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        enabled = available,
        modifier = modifier
            .heightIn(min = 64.dp)
            .semantics {
                contentDescription = if (available) {
                    "다음 쓰기, 사용 가능"
                } else {
                    "다음 쓰기, 사용 불가"
                }
                stateDescription = if (available) "다음 쓰기 사용 가능" else "다음 쓰기 사용 불가"
            },
        color = if (available) Color(0xFFDCEDE5) else Color(0xFFD8D4CC),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "▶",
                color = if (available) Color(0xFF285A46) else Color(0xFF68645E),
                fontSize = 24.sp,
            )
            Text(
                text = label,
                color = if (available) Color(0xFF285A46) else Color(0xFF68645E),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
