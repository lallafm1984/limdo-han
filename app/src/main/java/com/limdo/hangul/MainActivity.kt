package com.limdo.hangul

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private lateinit var guardianVoiceControllers: Map<GuardianVoiceKey, GuardianVoiceController>
    private lateinit var guardianLearningListStorage: GuardianLearningListStorage
    private lateinit var guardianLessonProgressStorage: GuardianLessonProgressStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guardianVoiceControllers = GuardianVoiceCatalog.keys.associateWith { key ->
            GuardianVoiceController(applicationContext, key.lessonId)
        }
        guardianLearningListStorage = GuardianLearningListStorage(noBackupFilesDir)
        guardianLessonProgressStorage = GuardianLessonProgressStorage(noBackupFilesDir)
        hideSystemBars()
        setContent {
            LimDoApp(
                guardianVoiceControllers,
                guardianLearningListStorage,
                guardianLessonProgressStorage,
            )
        }
    }

    override fun onStop() {
        guardianVoiceControllers.values.forEach(GuardianVoiceController::release)
        super.onStop()
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
private fun LimDoApp(
    guardianVoiceControllers: Map<GuardianVoiceKey, GuardianVoiceController>,
    guardianLearningListStorage: GuardianLearningListStorage,
    guardianLessonProgressStorage: GuardianLessonProgressStorage,
) {
    MaterialTheme(colorScheme = LimDoColorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            LearningShell(
                guardianVoiceControllers,
                guardianLearningListStorage,
                guardianLessonProgressStorage,
            )
        }
    }
}

@Composable
private fun LearningShell(
    guardianVoiceControllers: Map<GuardianVoiceKey, GuardianVoiceController>,
    guardianLearningListStorage: GuardianLearningListStorage,
    guardianLessonProgressStorage: GuardianLessonProgressStorage,
) {
    var destination by remember { mutableStateOf<LearningDestination>(LearningDestination.Home) }
    var nextWritingSessionId by rememberSaveable { mutableIntStateOf(0) }
    var guardianLearningList by remember {
        mutableStateOf(guardianLearningListStorage.load())
    }
    var guardianLearningCurrentIndex by remember {
        mutableIntStateOf(guardianLearningListStorage.loadCurrentIndex(guardianLearningList))
    }

    BackHandler(enabled = destination != LearningDestination.Home) {
        destination = LearningNavigation.back(destination)
    }

    when (val current = destination) {
        LearningDestination.Home -> LearningMenuHome(
            onSelect = {
                destination = LearningDestination.MenuTransition(it)
            },
            onOpenGuardian = { destination = LearningDestination.GuardianLessons },
        )
        LearningDestination.GuardianLessons -> GuardianLessonScreen(
            onClose = { destination = LearningDestination.Home },
            onOpenStartRecording = { destination = LearningDestination.GuardianStartRecording(it) },
            progressByLesson = guardianLessonProgressStorage.load(),
            learningList = guardianLearningList,
            learningCurrentIndex = guardianLearningCurrentIndex,
            onAddLesson = { lessonId ->
                guardianLearningList = guardianLearningListStorage.append(
                    guardianLearningList,
                    lessonId,
                )
                guardianLearningCurrentIndex = guardianLearningListStorage.loadCurrentIndex(guardianLearningList)
            },
            onMoveLesson = { fromIndex, toIndex ->
                guardianLearningList = guardianLearningListStorage.move(
                    guardianLearningList,
                    fromIndex,
                    toIndex,
                )
                guardianLearningCurrentIndex = guardianLearningListStorage.loadCurrentIndex(guardianLearningList)
            },
            onRemoveLesson = { index ->
                guardianLearningList = guardianLearningListStorage.removeAt(
                    guardianLearningList,
                    index,
                )
                guardianLearningCurrentIndex = guardianLearningListStorage.loadCurrentIndex(guardianLearningList)
            },
            onContinueLearning = {
                guardianLearningCurrentIndex = guardianLearningListStorage.continueFromCurrent(guardianLearningList)
                val lessonId = guardianLearningList[guardianLearningCurrentIndex]
                nextWritingSessionId += 1
                destination = LearningDestination.Writing(
                    LearningNavigation.menuFor(lessonId), lessonId, nextWritingSessionId,
                )
            },
            onRestartLearning = {
                guardianLearningCurrentIndex = guardianLearningListStorage.restartFromBeginning(guardianLearningList)
                val lessonId = guardianLearningList[guardianLearningCurrentIndex]
                nextWritingSessionId += 1
                destination = LearningDestination.Writing(
                    LearningNavigation.menuFor(lessonId), lessonId, nextWritingSessionId,
                )
            },
        )
        is LearningDestination.GuardianStartRecording -> GuardianStartRecordingScreen(
            lesson = GuardianLessonCatalog.lessons.single { it.id == current.lessonId },
            controller = requireNotNull(
                guardianVoiceControllers[GuardianVoiceKey(current.lessonId)],
            ),
            onClose = {
                guardianVoiceControllers.values.forEach(GuardianVoiceController::release)
                destination = LearningDestination.GuardianLessons
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
                guardianVoiceControllers = guardianVoiceControllers,
                guardianLessonProgressStorage = guardianLessonProgressStorage,
                onHome = { destination = LearningDestination.Home },
                guardianLearningList = guardianLearningList,
                guardianLearningCurrentIndex = guardianLearningCurrentIndex,
                onGuardianLearningCurrentIndexChange = { index ->
                    guardianLearningCurrentIndex = guardianLearningListStorage.saveCurrentPosition(
                        guardianLearningList,
                        index,
                    )
                },
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
private fun LearningMenuHome(
    onSelect: (LearningMenu) -> Unit,
    onOpenGuardian: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LimDoPlaygroundTokens.playgroundBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = LimDoPlaygroundTokens.SCREEN_PADDING_DP.dp,
                    top = 112.dp,
                    end = LimDoPlaygroundTokens.SCREEN_PADDING_DP.dp,
                    bottom = LimDoPlaygroundTokens.SCREEN_PADDING_DP.dp,
                ),
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
        GuardianEntry(
            onOpen = onOpenGuardian,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 28.dp, top = 28.dp),
        )
    }
}

private const val GUARDIAN_HOLD_MILLIS = 2_000L

@Composable
private fun GuardianEntry(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(74.dp)
            .pointerInput(onOpen) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    val releasedEarly = withTimeoutOrNull(GUARDIAN_HOLD_MILLIS) {
                        waitForUpOrCancellation()
                    }
                    if (releasedEarly == null) {
                        onOpen()
                        waitForUpOrCancellation()
                    }
                }
            }
            .semantics {
                contentDescription = "보호자 메뉴"
                onLongClick("2초간 길게 눌러 보호자 메뉴 열기") {
                    onOpen()
                    true
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            color = Color(0xFFF7E7B6),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF806D42)),
        ) {
            Canvas(Modifier.padding(11.dp).fillMaxSize()) {
                val strokeWidth = size.minDimension * 0.12f
                drawArc(
                    color = Color(0xFF665638),
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.23f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.54f, size.height * 0.58f),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
                drawRoundRect(
                    color = Color(0xFF665638),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, size.height * 0.42f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.58f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.16f),
                )
                drawCircle(
                    color = Color(0xFFF7E7B6),
                    radius = size.minDimension * 0.09f,
                    center = center.copy(y = size.height * 0.68f),
                )
            }
        }
    }
}

@Composable
private fun GuardianLessonScreen(
    onClose: () -> Unit,
    onOpenStartRecording: (LessonId) -> Unit,
    progressByLesson: Map<LessonId, GuardianLessonProgress>,
    learningList: List<LessonId>,
    learningCurrentIndex: Int,
    onAddLesson: (LessonId) -> Unit,
    onMoveLesson: (Int, Int) -> Unit,
    onRemoveLesson: (Int) -> Unit,
    onContinueLearning: () -> Unit,
    onRestartLearning: () -> Unit,
) {
    var selectedGroupIndex by remember { mutableStateOf(0) }
    var guardianMode by remember { mutableIntStateOf(0) }
    val selectedGroup = GuardianLessonCatalog.groups[selectedGroupIndex]
    val selectedVisuals = LearningMenu.entries[selectedGroupIndex].visuals()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8EC))
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Surface(
                onClick = onClose,
                modifier = Modifier
                    .size(width = 112.dp, height = 68.dp)
                    .semantics { contentDescription = "보호자 화면 닫기" },
                color = Color(0xFF3F725E),
                shape = RoundedCornerShape(22.dp),
                shadowElevation = 4.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("닫기", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column {
                Text("보호자 설정", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text("자유 학습 목록 ${learningList.size}개 · 같은 글자 반복 가능", fontSize = 18.sp)
            }
            Spacer(Modifier.weight(1f))
            listOf("녹음 관리", "목록 추가", "목록 편집").forEachIndexed { mode, label ->
                val selected = guardianMode == mode
                Surface(
                    onClick = { guardianMode = mode },
                    modifier = Modifier
                        .size(width = 88.dp, height = 74.dp)
                        .semantics {
                            contentDescription = label
                            stateDescription = if (selected) "선택됨" else "선택 안 됨"
                        },
                    color = if (selected) Color(0xFF3F725E) else Color(0xFFE7F2EC),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(3.dp, Color(0xFF3F725E)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            label,
                            color = if (selected) Color.White else Color(0xFF315C4B),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        if (guardianMode == 2) {
            GuardianLearningListEditor(
                learningList = learningList,
                currentIndex = learningCurrentIndex,
                modifier = Modifier.fillMaxSize(),
                onMoveLesson = onMoveLesson,
                onRemoveLesson = onRemoveLesson,
                onContinueLearning = onContinueLearning,
                onRestartLearning = onRestartLearning,
            )
            return@Column
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            GuardianLessonCatalog.groups.forEachIndexed { index, group ->
                val visuals = LearningMenu.entries[index].visuals()
                val selected = index == selectedGroupIndex
                Surface(
                    onClick = { selectedGroupIndex = index },
                    modifier = Modifier
                        .weight(1f)
                        .height(74.dp)
                        .semantics {
                            contentDescription = "${group.label} 분류"
                            stateDescription = if (selected) "선택됨" else "선택 안 됨"
                        },
                    color = if (selected) visuals.accent else visuals.softSurface,
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(3.dp, visuals.accent),
                    shadowElevation = if (selected) 5.dp else 0.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            group.label,
                            color = if (selected) Color.White else visuals.accent,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        GuardianLessonGroupCard(
            group = selectedGroup,
            accent = selectedVisuals.accent,
            surface = selectedVisuals.softSurface,
            modifier = Modifier.fillMaxSize(),
            onOpenStartRecording = onOpenStartRecording,
            addingToLearningList = guardianMode == 1,
            learningList = learningList,
            progressByLesson = progressByLesson,
            onAddLesson = onAddLesson,
        )
    }
}

@Composable
private fun GuardianLearningListEditor(
    learningList: List<LessonId>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    onMoveLesson: (Int, Int) -> Unit,
    onRemoveLesson: (Int) -> Unit,
    onContinueLearning: () -> Unit,
    onRestartLearning: () -> Unit,
) {
    val pageSize = 5
    val pageCount = maxOf(1, (learningList.size + pageSize - 1) / pageSize)
    var pageIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    if (pageIndex >= pageCount) pageIndex = pageCount - 1
    if (selectedIndex !in learningList.indices) selectedIndex = -1
    val pageStart = pageIndex * pageSize
    val pageItems = learningList.drop(pageStart).take(pageSize)
    val accent = Color(0xFF3F725E)

    Surface(
        modifier = modifier.border(3.dp, accent, RoundedCornerShape(30.dp)),
        color = Color(0xFFE7F2EC),
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 5.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (pageItems.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("목록이 비어 있어요", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    pageItems.forEachIndexed { offset, lessonId ->
                        val absoluteIndex = pageStart + offset
                        val lesson = GuardianLessonCatalog.lessons.single { it.id == lessonId }
                        val selected = absoluteIndex == selectedIndex
                        val current = absoluteIndex == currentIndex
                        Surface(
                            onClick = { selectedIndex = absoluteIndex },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(
                                    width = if (current) 7.dp else 0.dp,
                                    color = if (current) Color(0xFFF0A660) else Color.Transparent,
                                    shape = RoundedCornerShape(22.dp),
                                )
                                .semantics {
                                    contentDescription = "${absoluteIndex + 1}번 lesson ${lesson.glyph}"
                                    stateDescription = listOfNotNull(
                                        if (current) "현재 시작 위치" else null,
                                        if (selected) "선택됨" else "선택 안 됨",
                                    ).joinToString(", ")
                                },
                            color = if (selected) accent else Color.White,
                            shape = RoundedCornerShape(22.dp),
                            border = BorderStroke(3.dp, accent),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text("${absoluteIndex + 1}", fontSize = 18.sp, color = if (selected) Color.White else accent)
                                Text(lesson.glyph, fontSize = 46.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Color(0xFF26332E))
                                if (current) {
                                    Text("현재", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Color(0xFF9A5B18))
                                }
                            }
                        }
                    }
                    repeat(pageSize - pageItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                GuardianEditorAction("이전 쪽", pageIndex > 0, Modifier.weight(1f)) {
                    pageIndex -= 1
                    selectedIndex = -1
                }
                Box(Modifier.weight(1f).height(74.dp), contentAlignment = Alignment.Center) {
                    Text("${pageIndex + 1} / $pageCount", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                GuardianEditorAction("다음 쪽", pageIndex < pageCount - 1, Modifier.weight(1f)) {
                    pageIndex += 1
                    selectedIndex = -1
                }
                GuardianEditorAction("앞으로", selectedIndex > 0, Modifier.weight(1f)) {
                    val from = selectedIndex
                    onMoveLesson(from, from - 1)
                    selectedIndex = from - 1
                    pageIndex = selectedIndex / pageSize
                }
                GuardianEditorAction("뒤로", selectedIndex in 0 until learningList.lastIndex, Modifier.weight(1f)) {
                    val from = selectedIndex
                    onMoveLesson(from, from + 1)
                    selectedIndex = from + 1
                    pageIndex = selectedIndex / pageSize
                }
                GuardianEditorAction("삭제", selectedIndex in learningList.indices, Modifier.weight(1f), Color(0xFF9A4B43)) {
                    onRemoveLesson(selectedIndex)
                    selectedIndex = -1
                }
                GuardianEditorAction("이어하기", learningList.isNotEmpty(), Modifier.weight(1f)) {
                    onContinueLearning()
                }
                GuardianEditorAction("처음부터", learningList.isNotEmpty(), Modifier.weight(1f), Color(0xFFF0A660)) {
                    onRestartLearning()
                    pageIndex = 0
                }
            }
        }
    }
}

@Composable
private fun GuardianEditorAction(
    label: String,
    enabled: Boolean,
    modifier: Modifier,
    color: Color = Color(0xFF3F725E),
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(74.dp)
            .semantics {
                contentDescription = label
                stateDescription = if (enabled) "사용 가능" else "사용 불가"
            },
        color = if (enabled) color else Color(0xFFD5D8D6),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(3.dp, if (enabled) color else Color(0xFF9EA5A1)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = if (enabled) Color.White else Color(0xFF6D7470), fontSize = 21.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GuardianLessonGroupCard(
    group: GuardianLessonGroup,
    accent: Color,
    surface: Color,
    modifier: Modifier = Modifier,
    onOpenStartRecording: (LessonId) -> Unit,
    addingToLearningList: Boolean,
    learningList: List<LessonId>,
    progressByLesson: Map<LessonId, GuardianLessonProgress>,
    onAddLesson: (LessonId) -> Unit,
) {
    Surface(
        modifier = modifier.border(3.dp, accent, RoundedCornerShape(30.dp)),
        color = surface,
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 5.dp,
    ) {
        val columnCount = if (group.lessons.size == 10) 5 else 7
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            group.lessons.chunked(columnCount).forEach { lessonRow ->
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    lessonRow.forEach { lesson ->
                        val progress = progressByLesson[lesson.id]
                        val progressSummary = guardianProgressSummary(progress)
                        Surface(
                            onClick = {
                                if (addingToLearningList) onAddLesson(lesson.id)
                                else onOpenStartRecording(lesson.id)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .semantics {
                                    contentDescription = if (addingToLearningList) {
                                        "lesson ${lesson.glyph}, 목록에 추가"
                                    } else {
                                        "lesson ${lesson.glyph}, 녹음 열기"
                                    }
                                    val count = learningList.count { it == lesson.id }
                                    stateDescription = buildList {
                                        if (addingToLearningList) add("현재 목록에 ${count}개")
                                        add(progressSummary.semantics)
                                    }.joinToString(", ")
                                },
                            color = if (addingToLearningList && lesson.id in learningList) {
                                accent.copy(alpha = 0.16f)
                            } else Color.White.copy(alpha = 0.96f),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(2.dp, accent.copy(alpha = 0.45f)),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                Text(lesson.glyph, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Column(
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    ) {
                                        GuardianProgressSymbol(progress, Modifier.size(15.dp))
                                        Text(
                                            progressSummary.status,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = progressSummary.color,
                                        )
                                    }
                                    progressSummary.time?.let {
                                        Text(it, fontSize = 8.sp, color = Color(0xFF52615A))
                                    }
                                }
                            }
                        }
                    }
                    repeat(columnCount - lessonRow.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

private data class GuardianProgressSummary(
    val status: String,
    val time: String?,
    val semantics: String,
    val color: Color,
)

private fun guardianProgressSummary(progress: GuardianLessonProgress?): GuardianProgressSummary {
    if (progress == null) return GuardianProgressSummary(
        status = "연습 전",
        time = null,
        semantics = "연습 전",
        color = Color(0xFF6D7470),
    )
    val practiced = guardianProgressTime(progress.lastPracticedAtMillis)
    val completed = progress.lastCompletedAtMillis?.let(::guardianProgressTime)
    if (completed == null) return GuardianProgressSummary(
        status = "연습 중",
        time = "연 $practiced",
        semantics = "연습 중, 최근 연습 $practiced",
        color = Color(0xFF9A5B18),
    )
    val status = if (progress.lastAssistance == GuardianLessonAssistance.AFTER_HELP) {
        "도움 후 완성"
    } else {
        "혼자 완성"
    }
    return GuardianProgressSummary(
        status = status,
        time = "연 $practiced\n완 $completed",
        semantics = "$status, 최근 연습 $practiced, 최근 완료 $completed",
        color = Color(0xFF2F7655),
    )
}

private fun guardianProgressTime(millis: Long): String =
    SimpleDateFormat("M/d HH:mm", Locale.KOREA).format(Date(millis))

@Composable
private fun GuardianProgressSymbol(
    progress: GuardianLessonProgress?,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val strokeWidth = size.minDimension * 0.13f
        when {
            progress?.lastCompletedAtMillis != null -> {
                drawCircle(Color(0xFFE0F2E8))
                drawCircle(Color(0xFF2F7655), style = Stroke(strokeWidth))
                val check = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width * 0.25f, size.height * 0.52f)
                    lineTo(size.width * 0.44f, size.height * 0.70f)
                    lineTo(size.width * 0.76f, size.height * 0.32f)
                }
                drawPath(check, Color(0xFF2F7655), style = Stroke(strokeWidth, cap = StrokeCap.Round))
            }
            progress != null -> {
                drawCircle(Color(0xFFFFE8C5))
                drawArc(
                    color = Color(0xFF9A5B18),
                    startAngle = -90f,
                    sweepAngle = 250f,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round),
                )
            }
            else -> drawCircle(Color(0xFF8A918D), style = Stroke(strokeWidth))
        }
    }
}

@Composable
private fun GuardianStartRecordingScreen(
    lesson: LessonSpec,
    controller: GuardianVoiceController,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    var voiceState by remember { mutableStateOf(controller.currentState()) }
    var permissionDenied by remember { mutableStateOf(false) }
    LaunchedEffect(controller) {
        permissionDenied = false
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionDenied = !granted
        if (granted) controller.startRecording()
    }
    DisposableEffect(controller) {
        controller.observe { voiceState = it }
        onDispose { controller.release() }
    }
    val stateLabel = when (voiceState) {
        GuardianVoiceState.EMPTY -> "녹음 없음"
        GuardianVoiceState.RECORDING -> "녹음 중 · 최대 8초"
        GuardianVoiceState.READY -> "녹음 완료"
        GuardianVoiceState.PLAYING -> "미리 듣는 중"
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8EC)).padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            GuardianTextAction("목록", "녹음 화면 닫기", Color(0xFF3F725E), onClose)
            Text(lesson.glyph, fontSize = 54.sp, fontWeight = FontWeight.Bold)
            Column {
                Text("보호자 녹음", fontSize = 31.sp, fontWeight = FontWeight.Bold)
                Text(stateLabel, fontSize = 21.sp)
            }
        }
        Surface(
            modifier = Modifier.fillMaxSize().border(3.dp, Color(0xFF3F725E), RoundedCornerShape(30.dp)),
            color = Color(0xFFEAF5EF),
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 5.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RecordingStateSymbol(voiceState, Modifier.size(80.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(stateLabel, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                        if (permissionDenied) {
                            Text(
                                "권한이 없어\n녹음하지 않았어요.\n학습은 그대로예요.",
                                fontSize = 16.sp,
                                lineHeight = 21.sp,
                                maxLines = 3,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.weight(2f),
                    horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (voiceState) {
                        GuardianVoiceState.EMPTY -> GuardianTextAction("녹음", "${lesson.glyph} 녹음 시작", Color(0xFFD95D4F)) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                controller.startRecording()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        GuardianVoiceState.RECORDING -> GuardianTextAction("정지", "녹음 정지하고 저장", Color(0xFFD95D4F)) { controller.stopRecording() }
                        GuardianVoiceState.READY -> {
                            GuardianTextAction("듣기", "녹음 미리 듣기", Color(0xFF3F725E)) { controller.play() }
                            GuardianTextAction("다시 녹음", "기존 녹음을 보존하고 새로 녹음", Color(0xFFD9873A)) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) controller.startRecording()
                                else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                            GuardianTextAction("삭제", "${lesson.glyph} 녹음 삭제", Color(0xFF765B50)) { controller.delete() }
                        }
                        GuardianVoiceState.PLAYING -> GuardianTextAction("듣기 정지", "미리 듣기 정지", Color(0xFF3F725E)) { controller.stopPlayback() }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuardianTextAction(
    label: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(width = 148.dp, height = 74.dp).semantics { contentDescription = description },
        color = color,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecordingStateSymbol(state: GuardianVoiceState, modifier: Modifier = Modifier) {
    Canvas(modifier.semantics { stateDescription = state.name }) {
        when (state) {
            GuardianVoiceState.EMPTY -> drawCircle(Color(0xFFD95D4F), radius = size.minDimension * 0.34f)
            GuardianVoiceState.RECORDING -> drawRoundRect(Color(0xFFD95D4F), size = size.copy(width = size.width * 0.62f, height = size.height * 0.62f), topLeft = center.copy(x = size.width * 0.19f, y = size.height * 0.19f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f))
            GuardianVoiceState.READY -> {
                drawCircle(Color(0xFF3F725E), radius = size.minDimension * 0.46f)
                val p = androidx.compose.ui.graphics.Path().apply { moveTo(size.width * 0.40f, size.height * 0.28f); lineTo(size.width * 0.76f, size.height * 0.50f); lineTo(size.width * 0.40f, size.height * 0.72f); close() }
                drawPath(p, Color.White)
            }
            GuardianVoiceState.PLAYING -> repeat(3) { index ->
                val x = size.width * (0.30f + index * 0.20f)
                drawLine(Color(0xFF3F725E), androidx.compose.ui.geometry.Offset(x, size.height * (0.38f - index * 0.08f)), androidx.compose.ui.geometry.Offset(x, size.height * (0.62f + index * 0.08f)), strokeWidth = size.width * 0.08f, cap = StrokeCap.Round)
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
            HomeAction(
                onClick = onHome,
                modifier = Modifier.size(84.dp),
            )
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
    guardianVoiceControllers: Map<GuardianVoiceKey, GuardianVoiceController>,
    guardianLessonProgressStorage: GuardianLessonProgressStorage,
    onHome: () -> Unit,
    guardianLearningList: List<LessonId>,
    guardianLearningCurrentIndex: Int,
    onGuardianLearningCurrentIndexChange: (Int) -> Unit,
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
    val followsGuardianList = guardianLearningList.isNotEmpty() &&
        initialLessonId == guardianLearningList.getOrNull(guardianLearningCurrentIndex)
    var guardianIndex by rememberSaveable { mutableIntStateOf(guardianLearningCurrentIndex) }
    var guardianListComplete by rememberSaveable { mutableStateOf(false) }
    var receivedHelp by rememberSaveable { mutableStateOf(false) }
    var retryCount by remember { mutableIntStateOf(0) }
    val currentLesson = KoreanCurriculum.lessons[lessonIndex]
    val currentVoiceController = requireNotNull(
        guardianVoiceControllers[GuardianVoiceKey(currentLesson.id)],
    )
    val retryVisuals = retryAnimationVisuals(retryProgress.value)
    val assistanceLevel = RetryAssistanceSpec.level(retryCount)

    LaunchedEffect(currentLesson.id, guardianIndex) {
        guardianLessonProgressStorage.markPracticed(currentLesson.id)
        receivedHelp = false
        retryCount = 0
        currentVoiceController.play()
    }

    DisposableEffect(currentVoiceController) {
        onDispose { currentVoiceController.stopPlayback() }
    }

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
            retryCount = 0
            val successfulLessonId = currentLesson.id
            guardianLessonProgressStorage.markCompleted(
                successfulLessonId,
                if (receivedHelp) GuardianLessonAssistance.AFTER_HELP
                else GuardianLessonAssistance.INDEPENDENT,
            )
            val minimumSuccessVisibility = async {
                delay(SuccessCelebrationSpec.DURATION_MS.toLong())
            }
            celebrationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(SuccessCelebrationSpec.DURATION_MS),
            )
            minimumSuccessVisibility.await()
            val playbackFinished = CompletableDeferred<Unit>()
            val playbackStarted = currentVoiceController.play {
                playbackFinished.complete(Unit)
            }
            if (playbackStarted) playbackFinished.await()
            if (traceResult == GieokTraceResult.SUCCESS && currentLesson.id == successfulLessonId) {
                if (followsGuardianList && guardianIndex == guardianLearningList.lastIndex) {
                    guardianListComplete = true
                    return@LaunchedEffect
                }
                clearRequest += 1
                traceResult = null
                val nextLesson = if (followsGuardianList) {
                    guardianIndex += 1
                    onGuardianLearningCurrentIndexChange(guardianIndex)
                    KoreanCurriculum.lessons.single { it.id == guardianLearningList[guardianIndex] }
                } else LearningNavigation.nextLesson(menu, currentLesson)
                lessonIndex = KoreanCurriculum.lessons.indexOfFirst { it.id == nextLesson.id }
            }
        }
    }

    if (guardianListComplete) {
        GuardianLearningComplete(
            onHome = onHome,
            onRestart = {
                guardianIndex = 0
                onGuardianLearningCurrentIndexChange(0)
                lessonIndex = KoreanCurriculum.lessons.indexOfFirst {
                    it.id == guardianLearningList.first()
                }
                clearRequest += 1
                traceResult = null
                guardianListComplete = false
            },
        )
        return
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

            if (traceResult == GieokTraceResult.SUCCESS) {
                SuccessFeedbackOverlay(
                    progress = celebrationProgress.value,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            WritingBoardPreview(
                lesson = currentLesson,
                clearRequest = clearRequest,
                inputEnabled = traceResult == null || traceResult == GieokTraceResult.EMPTY,
                demonstrationStrokeIndex = null,
                demonstrationDurationMs = RetryAssistanceSpec.demonstrationDurationMs(assistanceLevel),
                retryStartMarkerScale = retryVisuals.startMarkerScale *
                    RetryAssistanceSpec.startMarkerScale(assistanceLevel),
                guideDotScale = RetryAssistanceSpec.guideDotScale(assistanceLevel),
                onTraceResult = { result, _ ->
                    traceResult = result
                    if (result.isRetryResult()) {
                        receivedHelp = true
                        retryCount = (retryCount + 1).coerceAtMost(RetryAssistanceSpec.MAX_LEVEL)
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

            EdgeActionColumns(
                onHome = {
                    currentVoiceController.stopPlayback()
                    onHome()
                },
                onClear = {
                    currentVoiceController.stopPlayback()
                    retryLessonIndex = -1
                    clearRequest += 1
                    traceResult = null
                    retryCount = 0
                },
                onPrevious = {
                    currentVoiceController.stopPlayback()
                    retryLessonIndex = -1
                    clearRequest += 1
                    traceResult = null
                    retryCount = 0
                    val previousLesson = if (followsGuardianList) {
                        guardianIndex = (guardianIndex - 1).coerceAtLeast(0)
                        onGuardianLearningCurrentIndexChange(guardianIndex)
                        KoreanCurriculum.lessons.single { it.id == guardianLearningList[guardianIndex] }
                    } else LearningNavigation.previousLesson(menu, currentLesson)
                    lessonIndex = KoreanCurriculum.lessons.indexOfFirst { it.id == previousLesson.id }
                },
                onNext = {
                    currentVoiceController.stopPlayback()
                    retryLessonIndex = -1
                    clearRequest += 1
                    traceResult = null
                    retryCount = 0
                    val nextLesson = if (followsGuardianList) {
                        guardianIndex = (guardianIndex + 1).coerceAtMost(guardianLearningList.lastIndex)
                        onGuardianLearningCurrentIndexChange(guardianIndex)
                        KoreanCurriculum.lessons.single { it.id == guardianLearningList[guardianIndex] }
                    } else LearningNavigation.nextLesson(menu, currentLesson)
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
private fun GuardianLearningComplete(
    onHome: () -> Unit,
    onRestart: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4D7))
            .semantics { contentDescription = "학습 목록을 다 했어요" },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("★", fontSize = 96.sp, color = Color(0xFFF0A660))
            Text("다 했어요!", fontSize = 48.sp, fontWeight = FontWeight.Black)
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 37.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
                GuardianCompleteAction("홈", ActionButtonAtlasSpec.HOME_COLUMN, Color(0xFF3F725E), onHome)
                GuardianCompleteAction("처음부터", ActionButtonAtlasSpec.CLEAR_COLUMN, Color(0xFFF0A660), onRestart)
        }
    }
}

@Composable
private fun GuardianCompleteAction(label: String, atlasColumn: Int, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(width = 240.dp, height = 80.dp),
        color = color,
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionAtlasIcon(atlasColumn, false)
                Text(label, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
    demonstrationDurationMs: Int,
    retryStartMarkerScale: Float,
    guideDotScale: Float,
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
            demonstrationDurationMs = demonstrationDurationMs,
            retryStartMarkerScale = retryStartMarkerScale,
            guideDotScale = guideDotScale,
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
