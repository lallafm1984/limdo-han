package com.example.limdo

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.min

internal enum class GieokTraceResult {
    EMPTY,
    WRONG_START,
    WRONG_DIRECTION,
    OFF_GUIDE,
    INCOMPLETE,
    SUCCESS,
}

internal object GieokTraceEvaluator {
    private const val START_TOLERANCE_FRACTION = 0.18f
    private const val CORRIDOR_TOLERANCE_FRACTION = 0.16f
    private const val FINISH_TOLERANCE_FRACTION = 0.20f
    private const val MEANINGFUL_MOVEMENT_FRACTION = 0.12f
    private const val MAX_OFF_GUIDE_FRACTION = 0.25f
    private const val MAX_CONSECUTIVE_OFF_GUIDE_FRACTION = 0.12f
    private const val MAX_BACKTRACK_FRACTION = 0.20f
    private const val BACKTRACK_COMPARISON_EPSILON_FRACTION = 0.0001f
    private const val CORNER_PROGRESS = 0.72f
    private const val FINISH_PROGRESS = 1.78f

    fun evaluate(
        width: Float,
        height: Float,
        stroke: StrokePath,
    ): GieokTraceResult {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val points = stroke.points
        if (points.isEmpty()) return GieokTraceResult.EMPTY

        val guide = WritingCanvasGeometry.gieokPoints(width, height)
        val start = guide[0]
        val corner = guide[1]
        val end = guide[2]
        val shortestSide = min(width, height)
        val horizontalLength = corner.x - start.x
        val verticalLength = end.y - corner.y

        if (points.first().distanceTo(start) > shortestSide * START_TOLERANCE_FRACTION) {
            return GieokTraceResult.WRONG_START
        }

        val meaningfulPoint = points.firstOrNull {
            it.distanceTo(start) >= shortestSide * MEANINGFUL_MOVEMENT_FRACTION
        }
        if (meaningfulPoint != null) {
            val dx = meaningfulPoint.x - start.x
            val dy = meaningfulPoint.y - start.y
            if (dx <= 0f || abs(dx) <= abs(dy)) {
                return GieokTraceResult.WRONG_DIRECTION
            }
        }

        val corridorTolerance = shortestSide * CORRIDOR_TOLERANCE_FRACTION
        val sampledPoints = sampleSegments(points, corridorTolerance / 2f)
        val projections = sampledPoints.map { point ->
            projectOntoGuide(point, start, corner, end, horizontalLength, verticalLength)
        }
        val offGuideFraction = projections.count { it.distance > corridorTolerance }.toFloat() /
            projections.size
        if (offGuideFraction > MAX_OFF_GUIDE_FRACTION) {
            return GieokTraceResult.OFF_GUIDE
        }
        val maximumConsecutiveOffGuideDistance = projections.zipWithNext()
            .fold(0f to 0f) { (currentDistance, maximumDistance), (first, second) ->
                val nextDistance = if (
                    first.distance > corridorTolerance && second.distance > corridorTolerance
                ) {
                    currentDistance + first.point.distanceTo(second.point)
                } else {
                    0f
                }
                nextDistance to maxOf(maximumDistance, nextDistance)
            }.second
        if (maximumConsecutiveOffGuideDistance >
            shortestSide * MAX_CONSECUTIVE_OFF_GUIDE_FRACTION
        ) {
            return GieokTraceResult.OFF_GUIDE
        }

        val backtrack = projections.zipWithNext().sumOf { (first, second) ->
            if (first.segment == second.segment) {
                (first.progress - second.progress).coerceAtLeast(0f).toDouble()
            } else {
                0.0
            }
        }.toFloat()
        if (
            backtrack > shortestSide *
            (MAX_BACKTRACK_FRACTION + BACKTRACK_COMPARISON_EPSILON_FRACTION)
        ) {
            return GieokTraceResult.WRONG_DIRECTION
        }

        val reachedCorner = projections.any {
            it.progress >= horizontalLength * CORNER_PROGRESS && it.distance <= corridorTolerance
        }
        val reachedFinishProgress = projections.maxOf { it.progress } >=
            horizontalLength + (verticalLength * (FINISH_PROGRESS - 1f))
        val finishedNearEnd = points.last().distanceTo(end) <=
            shortestSide * FINISH_TOLERANCE_FRACTION

        return if (reachedCorner && reachedFinishProgress && finishedNearEnd) {
            GieokTraceResult.SUCCESS
        } else {
            GieokTraceResult.INCOMPLETE
        }
    }

    private fun sampleSegments(points: List<CanvasPoint>, maximumSpacing: Float): List<CanvasPoint> {
        if (points.size < 2) return points

        return buildList {
            add(points.first())
            points.zipWithNext().forEach { (start, end) ->
                val steps = ceil(start.distanceTo(end) / maximumSpacing).toInt().coerceAtLeast(1)
                for (step in 1..steps) {
                    val ratio = step.toFloat() / steps
                    add(
                        CanvasPoint(
                            x = start.x + ((end.x - start.x) * ratio),
                            y = start.y + ((end.y - start.y) * ratio),
                        ),
                    )
                }
            }
        }
    }

    private fun projectOntoGuide(
        point: CanvasPoint,
        start: CanvasPoint,
        corner: CanvasPoint,
        end: CanvasPoint,
        horizontalLength: Float,
        verticalLength: Float,
    ): GuideProjection {
        val horizontalRatio = ((point.x - start.x) / horizontalLength).coerceIn(0f, 1f)
        val horizontalPoint = CanvasPoint(
            x = start.x + (horizontalLength * horizontalRatio),
            y = start.y,
        )
        val verticalRatio = ((point.y - corner.y) / verticalLength).coerceIn(0f, 1f)
        val verticalPoint = CanvasPoint(
            x = corner.x,
            y = corner.y + (verticalLength * verticalRatio),
        )
        val horizontalDistance = point.distanceTo(horizontalPoint)
        val verticalDistance = point.distanceTo(verticalPoint)

        return if (horizontalDistance <= verticalDistance) {
            GuideProjection(
                point = point,
                segment = GuideSegment.HORIZONTAL,
                progress = horizontalLength * horizontalRatio,
                distance = horizontalDistance,
            )
        } else {
            GuideProjection(
                point = point,
                segment = GuideSegment.VERTICAL,
                progress = horizontalLength + (verticalLength * verticalRatio),
                distance = verticalDistance,
            )
        }
    }
}

private enum class GuideSegment {
    HORIZONTAL,
    VERTICAL,
}

private data class GuideProjection(
    val point: CanvasPoint,
    val segment: GuideSegment,
    val progress: Float,
    val distance: Float,
)

private fun CanvasPoint.distanceTo(other: CanvasPoint): Float =
    hypot(x - other.x, y - other.y)
