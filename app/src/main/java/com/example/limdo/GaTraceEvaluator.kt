package com.example.limdo

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

internal object LessonTraceEvaluator {
    private const val START_TOLERANCE_EM = 0.18f
    private const val CORRIDOR_TOLERANCE_EM = 0.16f
    private const val FINISH_TOLERANCE_EM = 0.20f
    private const val MEANINGFUL_MOVEMENT_EM = 0.12f
    private const val MAX_OFF_GUIDE_FRACTION = 0.25f

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
        if (points.first().distanceTo(start) > glyph.emSize * START_TOLERANCE_EM) {
            return GieokTraceResult.WRONG_START
        }

        val targetDirection = target.first().directionTo(target[1])
        val meaningful = points.firstOrNull {
            it.distanceTo(start) >= glyph.emSize * MEANINGFUL_MOVEMENT_EM
        }
        if (meaningful != null) {
            val movement = start.directionTo(meaningful)
            if ((movement.x * targetDirection.x) + (movement.y * targetDirection.y) <= 0f) {
                return GieokTraceResult.WRONG_DIRECTION
            }
        }

        val tolerance = glyph.emSize * CORRIDOR_TOLERANCE_EM
        val sampled = sample(points, tolerance / 2f)
        val offGuideFraction = sampled.count { point ->
            target.zipWithNext().minOf { (lineStart, lineEnd) ->
                distanceToSegment(point, lineStart, lineEnd)
            } > tolerance
        }.toFloat() / sampled.size
        if (offGuideFraction > MAX_OFF_GUIDE_FRACTION) return GieokTraceResult.OFF_GUIDE

        return if (points.last().distanceTo(end) <= glyph.emSize * FINISH_TOLERANCE_EM) {
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
