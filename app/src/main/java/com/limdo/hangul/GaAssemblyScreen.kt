package com.limdo.hangul

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

@Composable
internal fun GaAssemblyScreen(onHome: () -> Unit, onWrite: (LessonId) -> Unit) {
    var target by remember { mutableStateOf<GaAssemblyTarget?>(null) }
    var state by remember { mutableStateOf(GaAssemblyState()) }
    val visuals = LearningMenu.GANADA.visuals()
    if (target == null) {
        Row(
            Modifier.fillMaxSize().background(visuals.softSurface).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeAction(onClick = onHome, modifier = Modifier.size(64.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GaAssemblyTarget.entries.toList().chunked(8).forEach { choices ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        choices.forEach { choice ->
                            Surface(
                                onClick = { target = choice; state = GaAssemblyState() },
                                shape = RoundedCornerShape(36.dp),
                                color = Color.White,
                                modifier = Modifier.size(76.dp).semantics {
                                    contentDescription = "${choice.glyph} 조립 선택"
                                },
                            ) { GaGeometry(choice, piece = null, active = true, modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
        }
        return
    }
    val selectedTarget = requireNotNull(target)
    Row(
        Modifier.fillMaxSize().background(visuals.softSurface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomeAction(onClick = onHome, modifier = Modifier.size(64.dp))
        Column(
            Modifier.width(130.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            AssemblyPiece(selectedTarget, GaAssemblyPiece.GIEOK, state.gieokPlaced, state.retryPiece == GaAssemblyPiece.GIEOK) {
                state = state.place(GaAssemblyPiece.GIEOK)
            }
            AssemblyPiece(selectedTarget, GaAssemblyPiece.VOWEL, state.vowelPlaced, state.retryPiece == GaAssemblyPiece.VOWEL) {
                state = state.place(GaAssemblyPiece.VOWEL)
            }
        }
        Box(
            Modifier.weight(1f).fillMaxHeight().border(5.dp, visuals.accent, RoundedCornerShape(36.dp))
                .background(Color(0xFFFFFEFA), RoundedCornerShape(36.dp)).padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selectedTarget.isHorizontalVowel) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    AssemblySlot(selectedTarget, GaAssemblyPiece.GIEOK, state.gieokPlaced, state.retryPiece == GaAssemblyPiece.VOWEL)
                    AssemblySlot(selectedTarget, GaAssemblyPiece.VOWEL, state.vowelPlaced, state.retryPiece == GaAssemblyPiece.VOWEL)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    AssemblySlot(selectedTarget, GaAssemblyPiece.GIEOK, state.gieokPlaced, state.retryPiece == GaAssemblyPiece.VOWEL)
                    AssemblySlot(selectedTarget, GaAssemblyPiece.VOWEL, state.vowelPlaced, state.retryPiece == GaAssemblyPiece.VOWEL)
                }
            }
        }
        Surface(
            onClick = { onWrite(selectedTarget.lessonId) },
            enabled = state.complete,
            shape = RoundedCornerShape(36.dp),
            color = if (state.complete) Color(0xFFFFD85A) else Color(0xFFE2DDD4),
            modifier = Modifier.size(140.dp).semantics {
                contentDescription = if (state.complete) "완성한 ${selectedTarget.glyph} 쓰기 시작" else "${selectedTarget.glyph} 조립 미완성"
                stateDescription = if (state.complete) "완성" else "사용할 수 없음"
            },
        ) { GaGeometry(selectedTarget, piece = null, active = state.complete, modifier = Modifier.padding(36.dp)) }
    }
}

@Composable
private fun AssemblyPiece(target: GaAssemblyTarget, piece: GaAssemblyPiece, placed: Boolean, retry: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = !placed,
        shape = RoundedCornerShape(32.dp),
        color = if (retry) Color(0xFFFFE0B2) else if (placed) Color(0xFFE2DDD4) else Color.White,
        modifier = Modifier.fillMaxWidth().height(140.dp).semantics {
            contentDescription = if (piece == GaAssemblyPiece.GIEOK) "${target.initialName} 조각" else "${target.vowelName()} 모음 조각"
            stateDescription = if (placed) "놓음" else if (retry) "${target.initialName}을 먼저 놓아요" else "선택 가능"
        },
    ) { GaGeometry(target, piece, active = !placed, modifier = Modifier.padding(16.dp)) }
}

@Composable
private fun AssemblySlot(target: GaAssemblyTarget, piece: GaAssemblyPiece, filled: Boolean, retry: Boolean) {
    Box(
        Modifier.size(140.dp).border(
            6.dp,
            if (retry && piece == GaAssemblyPiece.GIEOK) Color(0xFFF0A660) else Color(0xFF9BC99F),
            RoundedCornerShape(28.dp),
        ).background(if (filled) Color(0xFFE4F4DE) else Color(0xFFF7F2E8), RoundedCornerShape(28.dp))
            .padding(16.dp).semantics {
                contentDescription = when {
                    target.isHorizontalVowel && piece == GaAssemblyPiece.GIEOK -> "위쪽 ${target.initialName} 칸"
                    target.isHorizontalVowel -> "아래쪽 ${target.vowelName()} 모음 칸"
                    piece == GaAssemblyPiece.GIEOK -> "왼쪽 ${target.initialName} 칸"
                    else -> "오른쪽 ${target.vowelName()} 모음 칸"
                }
                stateDescription = if (filled) "채움" else "비어 있음"
            },
        contentAlignment = Alignment.Center,
    ) { if (filled) GaGeometry(target, piece, active = true, modifier = Modifier.fillMaxSize()) else Spacer(Modifier.fillMaxSize()) }
}

private fun GaAssemblyTarget.vowelName(): String = when (this) {
    GaAssemblyTarget.GA -> "아"
    GaAssemblyTarget.GEO -> "어"
    GaAssemblyTarget.GYEO -> "여"
    GaAssemblyTarget.GO -> "오"
    GaAssemblyTarget.GYO -> "요"
    GaAssemblyTarget.GU -> "우"
    GaAssemblyTarget.GYU -> "유"
    GaAssemblyTarget.GEU -> "으"
    GaAssemblyTarget.GI -> "이"
    GaAssemblyTarget.NA -> "아"
    GaAssemblyTarget.NEO -> "어"
    GaAssemblyTarget.NYEO -> "\uC5EC"
    GaAssemblyTarget.NO -> "오"
    GaAssemblyTarget.NYO -> "요"
    GaAssemblyTarget.NU -> "우"
    GaAssemblyTarget.NYU -> "유"
    GaAssemblyTarget.NEU -> "으"
    GaAssemblyTarget.NI -> "이"
    GaAssemblyTarget.DA -> "아"
    GaAssemblyTarget.DEO -> "어"
    GaAssemblyTarget.DYEO -> "여"
    GaAssemblyTarget.DO -> "오"
    GaAssemblyTarget.DYO -> "요"
    GaAssemblyTarget.DU -> "우"
    GaAssemblyTarget.DYU -> "유"
    GaAssemblyTarget.DEU -> "으"
    GaAssemblyTarget.DI -> "이"
    GaAssemblyTarget.RA -> "아"
    GaAssemblyTarget.REO -> "어"
}

@Composable
private fun GaGeometry(target: GaAssemblyTarget, piece: GaAssemblyPiece?, active: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        val lesson = KoreanCurriculum.lessons.single { it.id == target.lessonId }
        val geometry = WritingCanvasGeometry.glyph(lesson, size.width, size.height)
        val strokes = when (piece) {
            GaAssemblyPiece.GIEOK -> geometry.strokes.take(target.initialStrokeCount)
            GaAssemblyPiece.VOWEL -> geometry.strokes.drop(target.initialStrokeCount)
            null -> geometry.strokes
        }
        strokes.forEach { stroke ->
            stroke.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = if (active) Color(0xFF3F725E) else Color(0xFF9B9489),
                    start = Offset(start.x, start.y), end = Offset(end.x, end.y),
                    strokeWidth = geometry.strokeWidth, cap = StrokeCap.Round,
                )
            }
        }
    }
}
