package com.example.limdo

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
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

@Composable
internal fun WritingCanvas(
    contentDescription: String,
    clearRequest: Int,
    onTraceResult: (GieokTraceResult?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    var attempt by remember { mutableStateOf(TraceAttempt()) }
    val currentOnTraceResult by rememberUpdatedState(onTraceResult)
    val emptyStateDescription = "아직 그린 선이 없어요"
    val drawingStateDescription = "선을 그리고 있어요"

    LaunchedEffect(clearRequest) {
        attempt = attempt.clear()
        currentOnTraceResult(null)
    }

    Canvas(
        modifier = modifier
            .background(PracticeSurface, shape)
            .border(2.dp, PracticeBorder, shape)
            .pointerInput(Unit) {
                val safeInset = 24.dp.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    attempt = attempt.start(
                        point = CanvasPoint(down.position.x, down.position.y),
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        safeInset = safeInset,
                    )
                    currentOnTraceResult(null)
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
                    currentOnTraceResult(finishedAttempt.result)
                }
            }
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = when (attempt.result) {
                    GieokTraceResult.SUCCESS -> "기역을 완성했어요"
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

        drawLine(
            color = PracticeGuide,
            start = Offset(size.width / 2f, 0f),
            end = Offset(size.width / 2f, size.height),
            strokeWidth = guideStroke,
            pathEffect = dashEffect,
        )
        drawLine(
            color = PracticeGuide,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = guideStroke,
            pathEffect = dashEffect,
        )

        val glyph = WritingCanvasGeometry.gieok(size.width, size.height)
        val points = glyph.strokes.single()
        val pathStroke = glyph.strokeWidth
        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = GieokGuide,
                start = Offset(start.x, start.y),
                end = Offset(end.x, end.y),
                strokeWidth = pathStroke,
                cap = StrokeCap.Round,
            )
        }
        val arrowLength = min(size.width, size.height) * 0.10f
        val arrowStroke = pathStroke * 0.18f
        drawDirectionArrow(
            center = Offset(
                x = (points[0].x + points[1].x) / 2f,
                y = points[0].y,
            ),
            direction = Offset(1f, 0f),
            length = arrowLength,
            strokeWidth = arrowStroke,
        )
        drawDirectionArrow(
            center = Offset(
                x = points[1].x,
                y = (points[1].y + points[2].y) / 2f,
            ),
            direction = Offset(0f, 1f),
            length = arrowLength,
            strokeWidth = arrowStroke,
        )
        drawCircle(
            color = StartMarker,
            radius = pathStroke * 0.62f,
            center = Offset(points.first().x, points.first().y),
        )
        drawCircle(
            color = GieokGuide,
            radius = pathStroke * 0.76f,
            center = Offset(points.last().x, points.last().y),
        )
        drawCircle(
            color = FinishMarker,
            radius = pathStroke * 0.48f,
            center = Offset(points.last().x, points.last().y),
        )
        drawCircle(
            color = GieokGuide,
            radius = pathStroke * 0.18f,
            center = Offset(points.last().x, points.last().y),
        )

        val childStrokeWidth = min(size.width, size.height) * 0.045f
        if (attempt.stroke.points.size == 1) {
            drawCircle(
                color = ChildStroke,
                radius = childStrokeWidth / 2f,
                center = Offset(attempt.stroke.points.first().x, attempt.stroke.points.first().y),
            )
        } else {
            attempt.stroke.points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = ChildStroke,
                    start = Offset(start.x, start.y),
                    end = Offset(end.x, end.y),
                    strokeWidth = childStrokeWidth,
                    cap = StrokeCap.Round,
                )
            }
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
