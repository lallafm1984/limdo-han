package com.example.limdo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.min

private val PracticeSurface = Color(0xFFF7FBF8)
private val PracticeBorder = Color(0xFFD4E8DE)
private val PracticeGuide = Color(0xFFCEE0D7)
private val GieokGuide = Color(0xFF4F806B)
private val StartMarker = Color(0xFFF0A660)

@Composable
internal fun WritingCanvas(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)

    Canvas(
        modifier = modifier
            .background(PracticeSurface, shape)
            .border(2.dp, PracticeBorder, shape)
            .semantics { this.contentDescription = contentDescription },
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
    }
}
