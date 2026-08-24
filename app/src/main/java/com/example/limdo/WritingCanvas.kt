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

private val PracticeSurface = Color(0xFFF7FBF8)
private val PracticeBorder = Color(0xFFD4E8DE)
private val PracticeGuide = Color(0xFFCEE0D7)
private val GieokGuide = Color(0xFF4F806B)
private val StartMarker = Color(0xFFF0A660)
private val ChildStroke = Color(0xFF2D5B89)

@Composable
internal fun WritingCanvas(
    contentDescription: String,
    clearRequest: Int,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    var strokePath by remember { mutableStateOf(StrokePath()) }

    LaunchedEffect(clearRequest) {
        strokePath = strokePath.clear()
    }

    Canvas(
        modifier = modifier
            .background(PracticeSurface, shape)
            .border(2.dp, PracticeBorder, shape)
            .pointerInput(Unit) {
                val safeInset = 24.dp.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    strokePath = strokePath.start(
                        point = CanvasPoint(down.position.x, down.position.y),
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        safeInset = safeInset,
                    )
                    down.consume()

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break

                        strokePath = strokePath.append(
                            point = CanvasPoint(change.position.x, change.position.y),
                            width = size.width.toFloat(),
                            height = size.height.toFloat(),
                            safeInset = safeInset,
                        )
                        change.consume()
                    }
                }
            }
            .semantics {
                this.contentDescription = contentDescription
                stateDescription = if (strokePath.points.isEmpty()) {
                    "아직 그린 선이 없어요"
                } else {
                    "내가 그린 선이 있어요"
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

        val points = WritingCanvasGeometry.gieokPoints(size.width, size.height)
        val pathStroke = min(size.width, size.height) * 0.065f
        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = GieokGuide,
                start = Offset(start.x, start.y),
                end = Offset(end.x, end.y),
                strokeWidth = pathStroke,
                cap = StrokeCap.Round,
            )
        }
        drawCircle(
            color = StartMarker,
            radius = pathStroke * 0.62f,
            center = Offset(points.first().x, points.first().y),
        )

        val childStrokeWidth = min(size.width, size.height) * 0.045f
        if (strokePath.points.size == 1) {
            drawCircle(
                color = ChildStroke,
                radius = childStrokeWidth / 2f,
                center = Offset(strokePath.points.first().x, strokePath.points.first().y),
            )
        } else {
            strokePath.points.zipWithNext().forEach { (start, end) ->
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
