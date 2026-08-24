package com.example.limdo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.saveable.rememberSaveable
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localSpeech = LocalKoreanSpeech(this) { newState ->
            runOnUiThread { speechState = newState }
        }
        setContent {
            LimDoApp(
                speechState = speechState,
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

@Composable
private fun LimDoApp(
    speechState: SpeechPlaybackState,
    speak: (SpokenCue) -> Boolean,
    stopSpeech: () -> Unit,
) {
    MaterialTheme(colorScheme = LimDoColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LearningShell(speechState = speechState, speak = speak, stopSpeech = stopSpeech)
        }
    }
}

@Composable
private fun LearningShell(
    speechState: SpeechPlaybackState,
    speak: (SpokenCue) -> Boolean,
    stopSpeech: () -> Unit,
) {
    var clearRequest by remember { mutableIntStateOf(0) }
    var traceResult by remember { mutableStateOf<GieokTraceResult?>(null) }
    var vehicleIndex by rememberSaveable { mutableIntStateOf(0) }
    var vehicleSuccessArmed by rememberSaveable { mutableStateOf(true) }
    var rewardState by remember { mutableStateOf(LessonRewardState()) }
    val rewardOffset = remember { Animatable(0f) }
    var initialSpeechRequested by rememberSaveable { mutableStateOf(false) }
    val currentCue = SpokenCueModel.forResult(traceResult)
    val currentVehicle = VehicleCarousel.vehicles[vehicleIndex]

    LaunchedEffect(rewardState.targetSteps) {
        if (rewardState.phase == RewardMovePhase.START) {
            delay(120)
            rewardState = rewardState.moving()
            rewardOffset.animateTo(
                targetValue = rewardState.targetSteps.toFloat(),
                animationSpec = tween(durationMillis = 650),
            )
            rewardState = rewardState.complete()
        }
    }

    LaunchedEffect(speechState, initialSpeechRequested) {
        if (speechState == SpeechPlaybackState.Ready && !initialSpeechRequested) {
            initialSpeechRequested = true
            speak(SpokenCue.INITIAL)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD85A)),
    ) {
        Image(
            painter = painterResource(R.drawable.limdo_sunny_flower_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.32f,
        )

        WritingBoardPreview(
            clearRequest = clearRequest,
            inputEnabled = !rewardState.inputLocked,
            onTraceResult = { result ->
                val vehicleState = VehicleCarouselState(
                    index = vehicleIndex,
                    successArmed = vehicleSuccessArmed,
                ).onTraceResult(result)
                vehicleIndex = vehicleState.index
                vehicleSuccessArmed = vehicleState.successArmed
                traceResult = result
                rewardState = rewardState.onTraceResult(result, GieokLesson)
                if (result != null) speak(SpokenCueModel.forResult(result))
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 72.dp, top = 24.dp, end = 72.dp, bottom = 92.dp),
        )

        Image(
            painter = painterResource(currentVehicle.drawableRes),
            contentDescription = "${currentVehicle.koreanName} 안내",
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-344 + (rewardOffset.value * 48f)).dp, y = (-139).dp)
                .size(width = 128.dp, height = 86.dp),
            contentScale = ContentScale.Fit,
        )

        if (traceResult != null && traceResult != GieokTraceResult.EMPTY) {
            Text(
                text = if (traceResult == GieokTraceResult.SUCCESS) "★  ✓" else "↻",
                modifier = if (traceResult == GieokTraceResult.SUCCESS) {
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 116.dp)
                } else {
                    Modifier.align(Alignment.TopCenter)
                },
                color = if (traceResult == GieokTraceResult.SUCCESS) {
                    Color(0xFF276B50)
                } else {
                    Color(0xFF9A5527)
                },
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        ActionShelf(
            speechAvailable = speechState.canReplay,
            onReplay = { speak(currentCue) },
            onClear = {
                stopSpeech()
                clearRequest += 1
                vehicleSuccessArmed = true
                rewardState = rewardState.onTraceResult(null, GieokLesson)
                traceResult = null
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.58f)
                .padding(bottom = 10.dp),
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
    clearRequest: Int,
    inputEnabled: Boolean,
    onTraceResult: (GieokTraceResult?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
    ) {
        WritingCanvas(
            contentDescription = stringResource(R.string.writing_canvas_description),
            clearRequest = clearRequest,
            inputEnabled = inputEnabled,
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
private fun ActionShelf(
    speechAvailable: Boolean,
    onReplay: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .heightIn(min = 72.dp),
        color = Color(0xE6FFF9E9),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReplayAction(
                label = stringResource(R.string.action_replay),
                contentDescription = stringResource(R.string.action_replay_description),
                available = speechAvailable,
                onClick = onReplay,
                modifier = Modifier.weight(1f),
            )
            ClearAction(
                label = stringResource(R.string.action_clear),
                contentDescription = stringResource(R.string.action_clear_description),
                onClick = onClear,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(8.dp))
            ActionPlaceholder(
                label = stringResource(R.string.action_next),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ReplayAction(
    label: String,
    contentDescription: String,
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
                this.contentDescription = contentDescription
                stateDescription = if (available) "음성 안내 사용 가능" else "음성 안내 사용 불가"
            },
        color = if (available) Color(0xFFDCEDE5) else Color(0xFFD8D4CC),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (available) "🔊" else "🔇",
                fontSize = 28.sp,
            )
            Text(
                text = label,
                color = if (available) Color(0xFF285A46) else Color(0xFF68645E),
                fontSize = 18.sp,
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
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "⌫",
                color = Color(0xFF7A4A22),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                color = Color(0xFF7A4A22),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ActionPlaceholder(
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = 64.dp),
        color = Color(0xFFF8F7F3),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                color = Color(0xFF68716C),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.not_available_yet),
                color = Color(0xFF7C857F),
                fontSize = 11.sp,
            )
        }
    }
}
