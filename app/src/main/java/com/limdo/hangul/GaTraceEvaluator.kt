package com.limdo.hangul

import kotlin.math.ceil
import kotlin.math.hypot

internal object GaTraceEvaluator {
    fun evaluateStroke(
        width: Float,
        height: Float,
        strokeIndex: Int,
        stroke: StrokePath,
    ): GieokTraceResult = LessonTraceEvaluator.evaluateStroke(
        lesson = GaLesson,
        width = width,
        height = height,
        strokeIndex = strokeIndex,
        stroke = stroke,
    )
}

internal object ChildTraceToleranceSpec {
    const val START_TOLERANCE_EM = 0.22f
    const val CORRIDOR_TOLERANCE_EM = 0.20f
    const val FINISH_TOLERANCE_EM = 0.24f
    const val DIRECTION_SAMPLE_DISTANCE_EM = 0.14f
    const val STRONG_REVERSE_DOT_LIMIT = -0.25f
    const val MAX_OFF_GUIDE_FRACTION = 0.35f
}

internal object LessonTraceEvaluator {
    fun evaluateStroke(
        lesson: LessonSpec,
        width: Float,
        height: Float,
        strokeIndex: Int,
        stroke: StrokePath,
    ): GieokTraceResult {
        val glyph = WritingCanvasGeometry.glyph(lesson, width, height)
        require(strokeIndex in glyph.strokes.indices) { "strokeIndex is outside lesson strokes" }
        val target = glyph.strokes[strokeIndex]
        val points = stroke.points
        if (points.isEmpty()) return GieokTraceResult.EMPTY

        val start = target.first()
        val end = target.last()
        if (points.first().distanceTo(start) > glyph.emSize * ChildTraceToleranceSpec.START_TOLERANCE_EM) {
            return GieokTraceResult.WRONG_START
        }

        val targetDirection = target.first().directionTo(target[1])
        val actualStart = points.first()
        val meaningful = points.drop(1).firstOrNull {
            it.distanceTo(actualStart) >=
                glyph.emSize * ChildTraceToleranceSpec.DIRECTION_SAMPLE_DISTANCE_EM
        }
        if (meaningful != null) {
            val movement = actualStart.directionTo(meaningful)
            if (
                (movement.x * targetDirection.x) + (movement.y * targetDirection.y) <=
                ChildTraceToleranceSpec.STRONG_REVERSE_DOT_LIMIT
            ) {
                return GieokTraceResult.WRONG_DIRECTION
            }
        }

        val tolerance = glyph.emSize * ChildTraceToleranceSpec.CORRIDOR_TOLERANCE_EM
        val sampled = sample(points, tolerance / 2f)
        val offGuideFraction = sampled.count { point ->
            target.zipWithNext().minOf { (lineStart, lineEnd) ->
                distanceToSegment(point, lineStart, lineEnd)
            } > tolerance
        }.toFloat() / sampled.size
        if (offGuideFraction > ChildTraceToleranceSpec.MAX_OFF_GUIDE_FRACTION) {
            return GieokTraceResult.OFF_GUIDE
        }

        return if (
            points.last().distanceTo(end) <=
            glyph.emSize * ChildTraceToleranceSpec.FINISH_TOLERANCE_EM
        ) {
            GieokTraceResult.SUCCESS
        } else {
            GieokTraceResult.INCOMPLETE
        }
    }

    private fun sample(points: List<CanvasPoint>, spacing: Float): List<CanvasPoint> = buildList {
        add(points.first())
        points.zipWithNext().forEach { (start, end) ->
            val steps = ceil(start.distanceTo(end) / spacing).toInt().coerceAtLeast(1)
            for (step in 1..steps) {
                val ratio = step.toFloat() / steps
                add(CanvasPoint(start.x + (end.x - start.x) * ratio, start.y + (end.y - start.y) * ratio))
            }
        }
    }

    private fun distanceToSegment(point: CanvasPoint, start: CanvasPoint, end: CanvasPoint): Float {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val lengthSquared = (dx * dx) + (dy * dy)
        val ratio = if (lengthSquared == 0f) 0f else
            (((point.x - start.x) * dx + (point.y - start.y) * dy) / lengthSquared).coerceIn(0f, 1f)
        return point.distanceTo(CanvasPoint(start.x + dx * ratio, start.y + dy * ratio))
    }
}

private fun CanvasPoint.distanceTo(other: CanvasPoint): Float = hypot(x - other.x, y - other.y)

private fun CanvasPoint.directionTo(other: CanvasPoint): CanvasPoint {
    val dx = other.x - x
    val dy = other.y - y
    val length = hypot(dx, dy)
    return CanvasPoint(dx / length, dy / length)
}
