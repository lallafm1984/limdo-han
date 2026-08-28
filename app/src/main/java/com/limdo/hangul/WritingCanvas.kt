package com.limdo.hangul

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.min

private val PracticeSurface = Color(0x5CFFD84F)
private val PracticeBorder = Color(0x66FFF4C2)
private val PracticeGuide = Color(0x4D9B7620)
private val GieokGuide = Color(0xFFFFFEF6)
private val TraceCenterGuide = Color(0xFF176B52)
private val StartMarker = Color(0xFF35A77B)
private val FinishMarker = Color(0xFFFFA93A)
private val ChildStroke = Color(0xFF174F73)
private val DemonstrationMarker = Color(0xFF36BFAF)
private val DemonstrationMarkerOutline = Color(0xFF0E5862)
private val DemonstrationMarkerGlyph = Color(0xFFFFF3C4)
private val MarkerOutline = Color(0xFF5B3A1C)

private val TraceAttemptSaver = listSaver<TraceAttempt, Any>(
    save = { attempt ->
        buildList {
            add(attempt.result?.name.orEmpty())
            add(attempt.completedStrokes.size)
            attempt.completedStrokes.forEach { stroke ->
                add(stroke.points.size)
                stroke.points.forEach { point ->
                    add(point.x)
                    add(point.y)
                }
            }
            add(attempt.stroke.points.size)
            attempt.stroke.points.forEach { point ->
                add(point.x)
                add(point.y)
            }
        }
    },
    restore = { saved ->
        var index = 1
        val completedCount = (saved[index++] as Number).toInt()
        val completed = List(completedCount) {
            val pointCount = (saved[index++] as Number).toInt()
            StrokePath(List(pointCount) {
                CanvasPoint(
                    (saved[index++] as Number).toFloat(),
                    (saved[index++] as Number).toFloat(),
                )
            })
        }
        val currentPointCount = (saved[index++] as Number).toInt()
        TraceAttempt(
            stroke = StrokePath(
                List(currentPointCount) {
                    CanvasPoint(
                        (saved[index++] as Number).toFloat(),
                        (saved[index++] as Number).toFloat(),
                    )
                },
            ),
            completedStrokes = completed,
            result = (saved[0] as String).takeIf(String::isNotEmpty)?.let(
                GieokTraceResult::valueOf,
            ),
        )
    },
)

@Composable
internal fun WritingCanvas(
    lesson: LessonSpec,
    contentDescription: String,
    clearRequest: Int,
    inputEnabled: Boolean,
    demonstrationStrokeIndex: Int?,
    retryStartMarkerScale: Float,
    onTraceResult: (GieokTraceResult?, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    var attempt by rememberSaveable(lesson.id, stateSaver = TraceAttemptSaver) {
        mutableStateOf(TraceAttempt())
    }
    var handledClearRequest by rememberSaveable { mutableIntStateOf(clearRequest) }
    val currentOnTraceResult by rememberUpdatedState(onTraceResult)
    val emptyStateDescription = "아직 그린 선이 없어요"
    val drawingStateDescription = "선을 그리고 있어요"
    val demonstrationProgress = if (attempt.stroke.points.isEmpty() && attempt.result == null) {
        val transition = rememberInfiniteTransition(label = "${lesson.glyph} 현재 획 시범")
        val rawProgress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3_000
                    0f at 0
                    0.5f at 1_100
                    1f at 2_200
                    1f at 2_600
                },
                repeatMode = RepeatMode.Restart,
            ),
            label = "시작에서 끝까지",
        )
        val progress = WritingCanvasGeometry.demonstrationMarkerTravelProgress(rawProgress)
        demonstrationStrokeIndex?.let { strokeIndex ->
            WritingCanvasGeometry.strokeDemonstrationProgress(lesson, strokeIndex, progress)
        } ?: WritingCanvasGeometry.currentStrokeDemonstrationProgress(
            lesson = lesson,
            completedStrokeCount = attempt.completedStrokes.size,
            progress = progress,
        )
    } else {
        null
    }
    LaunchedEffect(clearRequest) {
        if (clearRequest != handledClearRequest) {
            handledClearRequest = clearRequest
            attempt = attempt.clear()
            currentOnTraceResult(null, 0)
        }
    }

    Canvas(
        modifier = modifier
            .background(PracticeSurface, shape)
            .border(2.dp, PracticeBorder, shape)
            .pointerInput(inputEnabled, lesson.id) {
                if (!inputEnabled) return@pointerInput
                val safeInset = 24.dp.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    attempt = attempt.start(
                        point = CanvasPoint(down.position.x, down.position.y),
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        safeInset = safeInset,
                    )
                    currentOnTraceResult(null, attempt.completedStrokes.size)
                    down.consume()

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break

                        attempt = attempt.append(
                            point = CanvasPoint(change.position.x, change.position.y),
                            width = size.width.toFloat(),
                            height = size.height.toFloat(),
                            safeInset = safeInset,
                        )
                        change.consume()
                    }

                    val finishedAttempt = attempt.finish(
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        lesson = lesson,
                    )
                    attempt = finishedAttempt
                    currentOnTraceResult(finishedAttempt.result, finishedAttempt.completedStrokes.size)
                }
            }
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = when (attempt.result) {
                    GieokTraceResult.SUCCESS -> "${lesson.glyph}를 완성했어요"
                    GieokTraceResult.WRONG_START,
                    GieokTraceResult.WRONG_DIRECTION,
                    GieokTraceResult.OFF_GUIDE,
                    GieokTraceResult.INCOMPLETE,
                    GieokTraceResult.EMPTY,
                    -> "다시 해볼 수 있어요"
                    null -> if (attempt.stroke.points.isEmpty()) {
                        emptyStateDescription
                    } else {
                        drawingStateDescription
                    }
                }
            },
    ) {
        val guideStroke = min(size.width, size.height) * 0.005f
        val dashEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(guideStroke * 4f, guideStroke * 4f),
        )
        val learningBoard = WritingCanvasGeometry.learningBoard(size.width, size.height)

        drawLine(
            color = PracticeGuide,
            start = Offset(size.width / 2f, learningBoard.top),
            end = Offset(size.width / 2f, learningBoard.bottom),
            strokeWidth = guideStroke,
            pathEffect = dashEffect,
        )
        drawLine(
            color = PracticeGuide,
            start = Offset(learningBoard.left, size.height / 2f),
            end = Offset(learningBoard.right, size.height / 2f),
            strokeWidth = guideStroke,
            pathEffect = dashEffect,
        )

        val glyph = WritingCanvasGeometry.visibleLessonGlyph(size.width, size.height, lesson)
        val pathStroke = glyph.strokeWidth
        glyph.strokes.forEach { stroke ->
            val glyphPath = Path().apply {
                moveTo(stroke.first().x, stroke.first().y)
                stroke.drop(1).forEach { point -> lineTo(point.x, point.y) }
            }
            drawPath(
                path = glyphPath,
                color = GieokGuide,
                style = Stroke(
                    width = pathStroke,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
        glyph.strokes.getOrNull(attempt.completedStrokes.size)?.let { currentStroke ->
            val dotRadius = pathStroke * 0.04f
            WritingCanvasGeometry.evenlySpacedGuideDots(
                stroke = currentStroke,
                targetSpacing = pathStroke * 0.20f,
            ).forEach { dot ->
                drawCircle(
                    color = TraceCenterGuide,
                    radius = dotRadius,
                    center = Offset(dot.x, dot.y),
                )
            }
        }
        val strokeEndpoints = glyph.strokes.flatMap { listOf(it.first(), it.last()) }
        strokeEndpoints.distinct().filter { endpoint ->
            strokeEndpoints.count { it == endpoint } > 1
        }.forEach { junction ->
            drawCircle(
                color = GieokGuide,
                radius = pathStroke / 2f,
                center = Offset(junction.x, junction.y),
            )
        }
        val childStrokeWidth = WritingCanvasGeometry.childStrokeWidth(size.width, size.height)
        val childStrokes = attempt.completedStrokes + listOf(attempt.stroke)
        childStrokes.filter { it.points.size == 1 }.forEach { childStroke ->
            drawCircle(
                color = ChildStroke,
                radius = childStrokeWidth / 2f,
                center = Offset(childStroke.points.first().x, childStroke.points.first().y),
            )
        }
        childStrokes.forEach { childStroke ->
            childStroke.points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = ChildStroke,
                    start = Offset(start.x, start.y),
                    end = Offset(end.x, end.y),
                    strokeWidth = childStrokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
        val arrowLength = min(size.width, size.height) * 0.10f
        val arrowStroke = pathStroke * 0.18f
        WritingCanvasGeometry.currentVisibleStrokeStart(
            width = size.width,
            height = size.height,
            completedStrokeCount = attempt.completedStrokes.size,
            lesson = lesson,
        )?.let { currentStart ->
            drawCircle(
                color = StartMarker,
                radius = WritingCanvasGeometry.startMarkerRadius(pathStroke) * retryStartMarkerScale,
                center = Offset(currentStart.x, currentStart.y),
            )
        }
        WritingCanvasGeometry.currentVisibleStrokeEnd(
            width = size.width,
            height = size.height,
            completedStrokeCount = attempt.completedStrokes.size,
            lesson = lesson,
        )?.let { currentEnd ->
            drawCircle(
                color = MarkerOutline,
                radius = WritingCanvasGeometry.finishMarkerOuterRadius(pathStroke),
                center = Offset(currentEnd.x, currentEnd.y),
            )
            drawCircle(
                color = FinishMarker,
                radius = WritingCanvasGeometry.finishMarkerColorRadius(pathStroke),
                center = Offset(currentEnd.x, currentEnd.y),
            )
            drawCircle(
                color = MarkerOutline,
                radius = WritingCanvasGeometry.finishMarkerCenterRadius(pathStroke),
                center = Offset(currentEnd.x, currentEnd.y),
            )
        }

        demonstrationProgress?.let { progress ->
            val marker = WritingCanvasGeometry.demonstrationGuide(
                lesson = lesson,
                width = size.width,
                height = size.height,
                progress = progress,
            )
            val markerOuterRadius =
                WritingCanvasGeometry.demonstrationMarkerOuterRadius(pathStroke) * marker.visualScale
            drawCircle(
                color = DemonstrationMarkerOutline,
                radius = markerOuterRadius,
                center = Offset(marker.center.x, marker.center.y),
            )
            drawCircle(
                color = DemonstrationMarker,
                radius = markerOuterRadius * 0.72f,
                center = Offset(marker.center.x, marker.center.y),
            )
            val direction = Offset(marker.direction.x, marker.direction.y)
            val perpendicular = Offset(-direction.y, direction.x)
            val glyphTip = Offset(
                x = marker.center.x + direction.x * markerOuterRadius * 0.62f,
                y = marker.center.y + direction.y * markerOuterRadius * 0.62f,
            )
            val glyphBack = Offset(
                x = marker.center.x - direction.x * markerOuterRadius * 0.36f,
                y = marker.center.y - direction.y * markerOuterRadius * 0.36f,
            )
            val glyphHalfWidth = markerOuterRadius * 0.34f
            val markerGlyph = Path().apply {
                moveTo(glyphTip.x, glyphTip.y)
                lineTo(
                    glyphBack.x + perpendicular.x * glyphHalfWidth,
                    glyphBack.y + perpendicular.y * glyphHalfWidth,
                )
                lineTo(
                    glyphBack.x - perpendicular.x * glyphHalfWidth,
                    glyphBack.y - perpendicular.y * glyphHalfWidth,
                )
                close()
            }
            drawPath(
                path = markerGlyph,
                color = DemonstrationMarkerGlyph,
            )
        }

    }
}
