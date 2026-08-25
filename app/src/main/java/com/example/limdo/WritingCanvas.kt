package com.example.limdo

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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
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
private val StartMarker = Color(0xFF35A77B)
private val FinishMarker = Color(0xFFFFA93A)
private val ChildStroke = Color(0xFF174F73)
private val DirectionArrow = Color(0xFF176B52)
private val DemonstrationMarker = Color(0xFF0B6F88)

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
    contentDescription: String,
    clearRequest: Int,
    inputEnabled: Boolean,
    demonstrationStrokeIndex: Int?,
    onTraceResult: (GieokTraceResult?, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    var attempt by rememberSaveable(stateSaver = TraceAttemptSaver) {
        mutableStateOf(TraceAttempt())
    }
    var handledClearRequest by rememberSaveable { mutableIntStateOf(clearRequest) }
    val currentOnTraceResult by rememberUpdatedState(onTraceResult)
    val emptyStateDescription = "아직 그린 선이 없어요"
    val drawingStateDescription = "선을 그리고 있어요"
    val demonstrationProgress = if (attempt.stroke.points.isEmpty() && attempt.result == null) {
        val transition = rememberInfiniteTransition(label = "가 세 획 시범")
        val progress by transition.animateFloat(
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
        demonstrationStrokeIndex?.let { strokeIndex ->
            WritingCanvasGeometry.gaStrokeDemonstrationProgress(strokeIndex, progress)
        } ?: WritingCanvasGeometry.gaCurrentStrokeDemonstrationProgress(
            completedStrokeCount = attempt.completedStrokes.size,
            progress = progress,
        )
    } else {
        null
    }
    val inputGuideMotion = if (attempt.stroke.points.isNotEmpty() && attempt.result == null) {
        val transition = rememberInfiniteTransition(label = "입력 중 다음 방향")
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes { durationMillis = 650 },
                repeatMode = RepeatMode.Reverse,
            ),
            label = "손가락 앞 짧은 움직임",
        )
        progress
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
            .pointerInput(inputEnabled) {
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
                    )
                    attempt = finishedAttempt
                    currentOnTraceResult(finishedAttempt.result, finishedAttempt.completedStrokes.size)
                }
            }
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = when (attempt.result) {
                    GieokTraceResult.SUCCESS -> "가를 완성했어요"
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

        val glyph = WritingCanvasGeometry.visibleLessonGlyph(size.width, size.height)
        val pathStroke = glyph.strokeWidth
        glyph.strokes.forEach { stroke ->
            stroke.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = GieokGuide,
                    start = Offset(start.x, start.y),
                    end = Offset(end.x, end.y),
                    strokeWidth = pathStroke,
                    cap = StrokeCap.Round,
                )
            }
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
        )?.let { currentStart ->
            drawCircle(
                color = StartMarker,
                radius = pathStroke * 0.62f,
                center = Offset(currentStart.x, currentStart.y),
            )
        }
        WritingCanvasGeometry.currentVisibleStrokeEnd(
            width = size.width,
            height = size.height,
            completedStrokeCount = attempt.completedStrokes.size,
        )?.let { currentEnd ->
            drawCircle(
                color = GieokGuide,
                radius = pathStroke * 0.76f,
                center = Offset(currentEnd.x, currentEnd.y),
            )
            drawCircle(
                color = FinishMarker,
                radius = pathStroke * 0.44f,
                center = Offset(currentEnd.x, currentEnd.y),
            )
            drawCircle(
                color = GieokGuide,
                radius = pathStroke * 0.18f,
                center = Offset(currentEnd.x, currentEnd.y),
            )
        }

        demonstrationProgress?.let { progress ->
            val marker = WritingCanvasGeometry.gaDemonstrationGuide(
                width = size.width,
                height = size.height,
                progress = progress,
            )
            drawCircle(
                color = GieokGuide,
                radius = pathStroke * 0.27f,
                center = Offset(marker.center.x, marker.center.y),
                style = Stroke(width = pathStroke * 0.12f),
            )
            drawCircle(
                color = DemonstrationMarker,
                radius = pathStroke * 0.20f,
                center = Offset(marker.center.x, marker.center.y),
                style = Stroke(width = pathStroke * 0.08f),
            )
            drawDirectionArrow(
                center = Offset(marker.center.x, marker.center.y),
                direction = Offset(marker.direction.x, marker.direction.y),
                length = arrowLength * 0.58f,
                strokeWidth = arrowStroke,
            )
        }

        inputGuideMotion?.let { motion ->
            val guide = WritingCanvasGeometry.gaInputDirectionGuide(
                width = size.width,
                height = size.height,
                strokeIndex = attempt.completedStrokes.size,
                input = attempt.stroke.points.last(),
                motionProgress = motion,
            )
            drawDirectionArrow(
                center = Offset(guide.center.x, guide.center.y),
                direction = Offset(guide.direction.x, guide.direction.y),
                length = arrowLength * 0.58f,
                strokeWidth = arrowStroke,
            )
        }

    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDirectionArrow(
    center: Offset,
    direction: Offset,
    length: Float,
    strokeWidth: Float,
) {
    val halfLength = length / 2f
    val tail = Offset(
        x = center.x - (direction.x * halfLength),
        y = center.y - (direction.y * halfLength),
    )
    val tip = Offset(
        x = center.x + (direction.x * halfLength),
        y = center.y + (direction.y * halfLength),
    )
    val headLength = length * 0.30f
    val perpendicular = Offset(-direction.y, direction.x)
    val headBase = Offset(
        x = tip.x - (direction.x * headLength),
        y = tip.y - (direction.y * headLength),
    )

    drawLine(
        color = DirectionArrow,
        start = tail,
        end = tip,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
    listOf(-1f, 1f).forEach { side ->
        drawLine(
            color = DirectionArrow,
            start = tip,
            end = Offset(
                x = headBase.x + (perpendicular.x * headLength * side),
                y = headBase.y + (perpendicular.y * headLength * side),
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}
