package com.example.limdo

import kotlin.math.min
import kotlin.math.sqrt

internal data class CanvasPoint(
    val x: Float,
    val y: Float,
)

internal data class GlyphGeometry(
    val emSize: Float,
    val strokeWidth: Float,
    val strokes: List<List<CanvasPoint>>,
)

internal data class LearningBoardBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
}

internal data class InputDirectionGuide(
    val center: CanvasPoint,
    val direction: CanvasPoint,
)

internal data class DemonstrationGuide(
    val center: CanvasPoint,
    val direction: CanvasPoint,
    val strokeIndex: Int,
    val segmentIndex: Int,
    val visualScale: Float,
)

internal object WritingCanvasGeometry {
    private const val EM_CANVAS_FRACTION = 0.80f
    private const val GUIDE_STROKE_EM_FRACTION = 0.20f
    private const val CHILD_STROKE_GUIDE_FRACTION = 0.60f

    private val gieokTemplate = listOf(
        listOf(
            CanvasPoint(0.03f, 0.03f),
            CanvasPoint(0.97f, 0.03f),
            CanvasPoint(0.97f, 0.97f),
        ),
    )

    private val nieunTemplate = listOf(
        listOf(
            CanvasPoint(0.12f, 0.08f),
            CanvasPoint(0.12f, 0.88f),
            CanvasPoint(0.88f, 0.88f),
        ),
    )

    private val digeutTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.12f), CanvasPoint(0.88f, 0.12f)),
        listOf(
            CanvasPoint(0.12f, 0.12f),
            CanvasPoint(0.12f, 0.88f),
            CanvasPoint(0.88f, 0.88f),
        ),
    )

    private val rieulTemplate = listOf(
        listOf(
            CanvasPoint(0.12f, 0.10f),
            CanvasPoint(0.88f, 0.10f),
            CanvasPoint(0.88f, 0.40f),
        ),
        listOf(CanvasPoint(0.88f, 0.40f), CanvasPoint(0.12f, 0.40f)),
        listOf(
            CanvasPoint(0.12f, 0.40f),
            CanvasPoint(0.12f, 0.90f),
            CanvasPoint(0.88f, 0.90f),
        ),
    )

    private val mieumTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.12f), CanvasPoint(0.12f, 0.88f)),
        listOf(
            CanvasPoint(0.12f, 0.12f),
            CanvasPoint(0.88f, 0.12f),
            CanvasPoint(0.88f, 0.88f),
        ),
        listOf(CanvasPoint(0.12f, 0.88f), CanvasPoint(0.88f, 0.88f)),
    )

    private val bieupTemplate = listOf(
        listOf(CanvasPoint(0.24f, 0.12f), CanvasPoint(0.24f, 0.88f)),
        listOf(CanvasPoint(0.76f, 0.12f), CanvasPoint(0.76f, 0.88f)),
        listOf(CanvasPoint(0.24f, 0.50f), CanvasPoint(0.76f, 0.50f)),
        listOf(CanvasPoint(0.24f, 0.88f), CanvasPoint(0.76f, 0.88f)),
    )

    private val siotTemplate = listOf(
        listOf(
            CanvasPoint(0.50f, 0.12f),
            CanvasPoint(0.49f, 0.40f),
            CanvasPoint(0.44f, 0.54f),
            CanvasPoint(0.34f, 0.65f),
            CanvasPoint(0.16f, 0.82f),
        ),
        listOf(
            CanvasPoint(0.50f, 0.12f),
            CanvasPoint(0.51f, 0.40f),
            CanvasPoint(0.56f, 0.54f),
            CanvasPoint(0.66f, 0.65f),
            CanvasPoint(0.84f, 0.82f),
        ),
    )

    private val ieungTemplate = listOf(
        listOf(
            CanvasPoint(0.50f, 0.08f),
            CanvasPoint(0.92f, 0.22f),
            CanvasPoint(0.92f, 0.50f),
            CanvasPoint(0.78f, 0.82f),
            CanvasPoint(0.50f, 0.92f),
            CanvasPoint(0.22f, 0.82f),
            CanvasPoint(0.08f, 0.50f),
            CanvasPoint(0.22f, 0.18f),
            CanvasPoint(0.50f, 0.08f),
        ),
    )

    private val jieutTemplate = listOf(
        listOf(CanvasPoint(0.16f, 0.18f), CanvasPoint(0.84f, 0.18f)),
        listOf(
            CanvasPoint(0.50f, 0.22f),
            CanvasPoint(0.49f, 0.48f),
            CanvasPoint(0.43f, 0.58f),
            CanvasPoint(0.32f, 0.68f),
            CanvasPoint(0.14f, 0.84f),
        ),
        listOf(
            CanvasPoint(0.50f, 0.22f),
            CanvasPoint(0.51f, 0.48f),
            CanvasPoint(0.57f, 0.58f),
            CanvasPoint(0.68f, 0.68f),
            CanvasPoint(0.86f, 0.84f),
        ),
    )

    private val chieutTemplate = listOf(
        listOf(CanvasPoint(0.34f, 0.07f), CanvasPoint(0.66f, 0.07f)),
        listOf(CanvasPoint(0.16f, 0.28f), CanvasPoint(0.84f, 0.28f)),
        listOf(
            CanvasPoint(0.50f, 0.32f),
            CanvasPoint(0.49f, 0.56f),
            CanvasPoint(0.43f, 0.64f),
            CanvasPoint(0.32f, 0.74f),
            CanvasPoint(0.14f, 0.88f),
        ),
        listOf(
            CanvasPoint(0.50f, 0.32f),
            CanvasPoint(0.51f, 0.56f),
            CanvasPoint(0.57f, 0.64f),
            CanvasPoint(0.68f, 0.74f),
            CanvasPoint(0.86f, 0.88f),
        ),
    )

    private val kieukTemplate = listOf(
        listOf(
            CanvasPoint(0.14f, 0.12f),
            CanvasPoint(0.86f, 0.12f),
            CanvasPoint(0.86f, 0.88f),
        ),
        listOf(CanvasPoint(0.14f, 0.50f), CanvasPoint(0.86f, 0.50f)),
    )

    private val tieutTemplate = listOf(
        listOf(CanvasPoint(0.16f, 0.16f), CanvasPoint(0.84f, 0.16f)),
        listOf(CanvasPoint(0.16f, 0.50f), CanvasPoint(0.84f, 0.50f)),
        listOf(
            CanvasPoint(0.16f, 0.16f),
            CanvasPoint(0.16f, 0.84f),
            CanvasPoint(0.84f, 0.84f),
        ),
    )

    private val pieupTemplate = listOf(
        listOf(CanvasPoint(0.14f, 0.20f), CanvasPoint(0.86f, 0.20f)),
        listOf(CanvasPoint(0.14f, 0.80f), CanvasPoint(0.86f, 0.80f)),
        listOf(CanvasPoint(0.36f, 0.25f), CanvasPoint(0.40f, 0.75f)),
        listOf(CanvasPoint(0.64f, 0.25f), CanvasPoint(0.60f, 0.75f)),
    )

    private val hieuhTemplate = listOf(
        listOf(CanvasPoint(0.36f, 0.06f), CanvasPoint(0.64f, 0.06f)),
        listOf(CanvasPoint(0.20f, 0.30f), CanvasPoint(0.80f, 0.30f)),
        listOf(
            CanvasPoint(0.50f, 0.52f),
            CanvasPoint(0.68f, 0.55f),
            CanvasPoint(0.76f, 0.68f),
            CanvasPoint(0.70f, 0.82f),
            CanvasPoint(0.50f, 0.90f),
            CanvasPoint(0.30f, 0.82f),
            CanvasPoint(0.24f, 0.68f),
            CanvasPoint(0.32f, 0.55f),
            CanvasPoint(0.50f, 0.52f),
        ),
    )

    private val aTemplate = listOf(
        listOf(CanvasPoint(0.46f, 0.08f), CanvasPoint(0.46f, 0.92f)),
        listOf(CanvasPoint(0.46f, 0.50f), CanvasPoint(0.76f, 0.50f)),
    )

    private val aeTemplate = listOf(
        listOf(CanvasPoint(0.32f, 0.08f), CanvasPoint(0.32f, 0.92f)),
        listOf(CanvasPoint(0.32f, 0.50f), CanvasPoint(0.68f, 0.50f)),
        listOf(CanvasPoint(0.68f, 0.08f), CanvasPoint(0.68f, 0.92f)),
    )

    private val yaTemplate = listOf(
        listOf(CanvasPoint(0.46f, 0.08f), CanvasPoint(0.46f, 0.92f)),
        listOf(CanvasPoint(0.46f, 0.38f), CanvasPoint(0.76f, 0.38f)),
        listOf(CanvasPoint(0.46f, 0.62f), CanvasPoint(0.76f, 0.62f)),
    )

    private val eoTemplate = listOf(
        listOf(CanvasPoint(0.54f, 0.08f), CanvasPoint(0.54f, 0.92f)),
        listOf(CanvasPoint(0.54f, 0.50f), CanvasPoint(0.24f, 0.50f)),
    )

    private val yeoTemplate = listOf(
        listOf(CanvasPoint(0.54f, 0.08f), CanvasPoint(0.54f, 0.92f)),
        listOf(CanvasPoint(0.54f, 0.38f), CanvasPoint(0.24f, 0.38f)),
        listOf(CanvasPoint(0.54f, 0.62f), CanvasPoint(0.24f, 0.62f)),
    )

    private val oTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.62f), CanvasPoint(0.88f, 0.62f)),
        listOf(CanvasPoint(0.50f, 0.62f), CanvasPoint(0.50f, 0.18f)),
    )

    private val yoTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.68f), CanvasPoint(0.88f, 0.68f)),
        listOf(CanvasPoint(0.38f, 0.68f), CanvasPoint(0.38f, 0.28f)),
        listOf(CanvasPoint(0.62f, 0.68f), CanvasPoint(0.62f, 0.28f)),
    )

    private val uTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.38f), CanvasPoint(0.88f, 0.38f)),
        listOf(CanvasPoint(0.50f, 0.38f), CanvasPoint(0.50f, 0.82f)),
    )

    private val yuTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.32f), CanvasPoint(0.88f, 0.32f)),
        listOf(CanvasPoint(0.38f, 0.32f), CanvasPoint(0.38f, 0.72f)),
        listOf(CanvasPoint(0.62f, 0.32f), CanvasPoint(0.62f, 0.72f)),
    )

    private val euTemplate = listOf(
        listOf(CanvasPoint(0.12f, 0.50f), CanvasPoint(0.88f, 0.50f)),
    )

    private val iTemplate = listOf(
        listOf(CanvasPoint(0.50f, 0.08f), CanvasPoint(0.50f, 0.92f)),
    )

    private val gaTemplate = listOf(
        listOf(
            CanvasPoint(0.05f, 0.12f),
            CanvasPoint(0.48f, 0.12f),
            CanvasPoint(0.48f, 0.88f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val naTemplate = listOf(
        listOf(
            CanvasPoint(0.05f, 0.12f),
            CanvasPoint(0.05f, 0.88f),
            CanvasPoint(0.48f, 0.88f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val daTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.12f), CanvasPoint(0.48f, 0.12f)),
        listOf(
            CanvasPoint(0.05f, 0.12f),
            CanvasPoint(0.05f, 0.88f),
            CanvasPoint(0.48f, 0.88f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val raTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.10f), CanvasPoint(0.48f, 0.10f), CanvasPoint(0.48f, 0.38f)),
        listOf(CanvasPoint(0.48f, 0.38f), CanvasPoint(0.05f, 0.38f)),
        listOf(CanvasPoint(0.05f, 0.38f), CanvasPoint(0.05f, 0.90f), CanvasPoint(0.48f, 0.90f)),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val maTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.12f), CanvasPoint(0.05f, 0.88f)),
        listOf(CanvasPoint(0.05f, 0.12f), CanvasPoint(0.48f, 0.12f), CanvasPoint(0.48f, 0.88f)),
        listOf(CanvasPoint(0.05f, 0.88f), CanvasPoint(0.48f, 0.88f)),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val baTemplate = listOf(
        listOf(CanvasPoint(0.10f, 0.12f), CanvasPoint(0.10f, 0.88f)),
        listOf(CanvasPoint(0.44f, 0.12f), CanvasPoint(0.44f, 0.88f)),
        listOf(CanvasPoint(0.10f, 0.50f), CanvasPoint(0.44f, 0.50f)),
        listOf(CanvasPoint(0.10f, 0.88f), CanvasPoint(0.44f, 0.88f)),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val saTemplate = listOf(
        listOf(
            CanvasPoint(0.27f, 0.12f),
            CanvasPoint(0.27f, 0.40f),
            CanvasPoint(0.24f, 0.54f),
            CanvasPoint(0.16f, 0.67f),
            CanvasPoint(0.05f, 0.84f),
        ),
        listOf(
            CanvasPoint(0.27f, 0.12f),
            CanvasPoint(0.27f, 0.40f),
            CanvasPoint(0.30f, 0.54f),
            CanvasPoint(0.38f, 0.67f),
            CanvasPoint(0.49f, 0.84f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val ahTemplate = listOf(
        listOf(
            CanvasPoint(0.05f, 0.22f),
            CanvasPoint(0.49f, 0.22f),
            CanvasPoint(0.49f, 0.50f),
            CanvasPoint(0.42f, 0.82f),
            CanvasPoint(0.27f, 0.92f),
            CanvasPoint(0.12f, 0.82f),
            CanvasPoint(0.05f, 0.50f),
            CanvasPoint(0.05f, 0.22f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val jaTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.18f), CanvasPoint(0.49f, 0.18f)),
        listOf(
            CanvasPoint(0.27f, 0.22f),
            CanvasPoint(0.27f, 0.48f),
            CanvasPoint(0.23f, 0.60f),
            CanvasPoint(0.16f, 0.70f),
            CanvasPoint(0.05f, 0.84f),
        ),
        listOf(
            CanvasPoint(0.27f, 0.22f),
            CanvasPoint(0.27f, 0.48f),
            CanvasPoint(0.31f, 0.60f),
            CanvasPoint(0.38f, 0.70f),
            CanvasPoint(0.49f, 0.84f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val chaTemplate = listOf(
        listOf(CanvasPoint(0.11f, 0.07f), CanvasPoint(0.43f, 0.07f)),
        listOf(CanvasPoint(0.05f, 0.28f), CanvasPoint(0.49f, 0.28f)),
        listOf(
            CanvasPoint(0.27f, 0.32f),
            CanvasPoint(0.27f, 0.56f),
            CanvasPoint(0.23f, 0.66f),
            CanvasPoint(0.16f, 0.74f),
            CanvasPoint(0.05f, 0.88f),
        ),
        listOf(
            CanvasPoint(0.27f, 0.32f),
            CanvasPoint(0.27f, 0.56f),
            CanvasPoint(0.31f, 0.66f),
            CanvasPoint(0.38f, 0.74f),
            CanvasPoint(0.49f, 0.88f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val kaTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.12f), CanvasPoint(0.49f, 0.12f), CanvasPoint(0.49f, 0.88f)),
        listOf(CanvasPoint(0.05f, 0.50f), CanvasPoint(0.49f, 0.50f)),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val taTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.16f), CanvasPoint(0.49f, 0.16f)),
        listOf(CanvasPoint(0.05f, 0.50f), CanvasPoint(0.49f, 0.50f)),
        listOf(CanvasPoint(0.05f, 0.16f), CanvasPoint(0.05f, 0.84f), CanvasPoint(0.49f, 0.84f)),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val paTemplate = listOf(
        listOf(CanvasPoint(0.05f, 0.20f), CanvasPoint(0.49f, 0.20f)),
        listOf(CanvasPoint(0.05f, 0.80f), CanvasPoint(0.49f, 0.80f)),
        listOf(CanvasPoint(0.18f, 0.25f), CanvasPoint(0.20f, 0.75f)),
        listOf(CanvasPoint(0.36f, 0.25f), CanvasPoint(0.34f, 0.75f)),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val haTemplate = listOf(
        listOf(CanvasPoint(0.18f, 0.06f), CanvasPoint(0.36f, 0.06f)),
        listOf(CanvasPoint(0.08f, 0.30f), CanvasPoint(0.46f, 0.30f)),
        listOf(
            CanvasPoint(0.27f, 0.52f),
            CanvasPoint(0.39f, 0.55f),
            CanvasPoint(0.44f, 0.68f),
            CanvasPoint(0.40f, 0.82f),
            CanvasPoint(0.27f, 0.90f),
            CanvasPoint(0.14f, 0.82f),
            CanvasPoint(0.10f, 0.68f),
            CanvasPoint(0.15f, 0.55f),
            CanvasPoint(0.27f, 0.52f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    fun gieok(width: Float, height: Float): GlyphGeometry =
        transform(gieokTemplate, width, height)

    fun gieokPoints(width: Float, height: Float): List<CanvasPoint> =
        gieok(width, height).strokes.single()

    fun gieokDemonstrationPoint(width: Float, height: Float, progress: Float): CanvasPoint {
        require(progress in 0f..1f) { "progress must be between 0 and 1" }
        val points = gieokPoints(width, height)
        val segmentProgress = progress * 2f
        val start = if (segmentProgress <= 1f) points[0] else points[1]
        val end = if (segmentProgress <= 1f) points[1] else points[2]
        val fraction = if (segmentProgress <= 1f) segmentProgress else segmentProgress - 1f
        return CanvasPoint(
            x = start.x + ((end.x - start.x) * fraction),
            y = start.y + ((end.y - start.y) * fraction),
        )
    }

    fun gieokInputDirectionGuide(
        width: Float,
        height: Float,
        input: CanvasPoint,
        motionProgress: Float,
    ): InputDirectionGuide {
        require(motionProgress in 0f..1f) { "motionProgress must be between 0 and 1" }
        val points = gieokPoints(width, height)
        val horizontalProgress = ((input.x - points[0].x) / (points[1].x - points[0].x))
            .coerceIn(0f, 1f)
        val verticalProgress = ((input.y - points[1].y) / (points[2].y - points[1].y))
            .coerceIn(0f, 1f)
        val horizontalDistance = kotlin.math.abs(input.y - points[0].y)
        val verticalDistance = kotlin.math.abs(input.x - points[1].x)
        val inputProgress = if (horizontalDistance <= verticalDistance) {
            horizontalProgress * 0.5f
        } else {
            0.5f + (verticalProgress * 0.5f)
        }
        val guideProgress = (inputProgress + 0.06f + (motionProgress * 0.06f)).coerceAtMost(1f)
        return InputDirectionGuide(
            center = gieokDemonstrationPoint(width, height, guideProgress),
            direction = if (guideProgress < 0.5f) CanvasPoint(1f, 0f) else CanvasPoint(0f, 1f),
        )
    }

    fun childStrokeWidth(width: Float, height: Float): Float =
        gieok(width, height).strokeWidth * CHILD_STROKE_GUIDE_FRACTION

    fun learningBoard(width: Float, height: Float): LearningBoardBounds {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val emSize = min(width, height) * EM_CANVAS_FRACTION
        val originX = (width - emSize) / 2f
        val originY = (height - emSize) / 2f
        return LearningBoardBounds(
            left = originX,
            top = originY,
            right = originX + emSize,
            bottom = originY + emSize,
        )
    }

    fun ga(width: Float, height: Float): GlyphGeometry =
        transform(gaTemplate, width, height)

    fun glyph(lesson: LessonSpec, width: Float, height: Float): GlyphGeometry =
        transform(templateFor(lesson), width, height)

    fun gaInputDirectionGuide(
        width: Float,
        height: Float,
        strokeIndex: Int,
        input: CanvasPoint,
        motionProgress: Float,
    ): InputDirectionGuide = inputDirectionGuide(GaLesson, width, height, strokeIndex, input, motionProgress)

    fun inputDirectionGuide(
        lesson: LessonSpec,
        width: Float,
        height: Float,
        strokeIndex: Int,
        input: CanvasPoint,
        motionProgress: Float,
    ): InputDirectionGuide {
        require(motionProgress in 0f..1f) { "motionProgress must be between 0 and 1" }
        val stroke = glyph(lesson, width, height).strokes.getOrElse(strokeIndex) {
            throw IllegalArgumentException("strokeIndex must identify a lesson stroke")
        }
        val segments = stroke.zipWithNext().map { (start, end) ->
            val deltaX = end.x - start.x
            val deltaY = end.y - start.y
            val length = sqrt((deltaX * deltaX) + (deltaY * deltaY))
            DemonstrationSegment(0, 0, start, end, deltaX, deltaY, length)
        }
        val totalLength = segments.sumOf { it.length.toDouble() }.toFloat()
        var traversed = 0f
        var nearestDistanceSquared = Float.POSITIVE_INFINITY
        var inputDistance = 0f
        segments.forEach { segment ->
            val projection = (
                ((input.x - segment.start.x) * segment.deltaX) +
                    ((input.y - segment.start.y) * segment.deltaY)
                ) / (segment.length * segment.length)
            val fraction = projection.coerceIn(0f, 1f)
            val projectedX = segment.start.x + (segment.deltaX * fraction)
            val projectedY = segment.start.y + (segment.deltaY * fraction)
            val distanceSquared =
                ((input.x - projectedX) * (input.x - projectedX)) +
                    ((input.y - projectedY) * (input.y - projectedY))
            if (distanceSquared < nearestDistanceSquared) {
                nearestDistanceSquared = distanceSquared
                inputDistance = traversed + (segment.length * fraction)
            }
            traversed += segment.length
        }

        val guideDistance = (
            inputDistance + totalLength * (0.06f + (motionProgress * 0.06f))
            ).coerceAtMost(totalLength)
        traversed = 0f
        val segment = segments.firstOrNull { candidate ->
            val containsGuide = guideDistance <= traversed + candidate.length
            if (!containsGuide) traversed += candidate.length
            containsGuide
        } ?: segments.last()
        val fraction = ((guideDistance - traversed) / segment.length).coerceIn(0f, 1f)
        return InputDirectionGuide(
            center = CanvasPoint(
                x = segment.start.x + (segment.deltaX * fraction),
                y = segment.start.y + (segment.deltaY * fraction),
            ),
            direction = CanvasPoint(segment.deltaX / segment.length, segment.deltaY / segment.length),
        )
    }

    fun visibleLessonGlyph(
        width: Float,
        height: Float,
        lesson: LessonSpec = GaLesson,
    ): GlyphGeometry = glyph(lesson, width, height)

    fun currentVisibleStrokeStart(
        width: Float,
        height: Float,
        completedStrokeCount: Int,
        lesson: LessonSpec = GaLesson,
    ): CanvasPoint? = visibleLessonGlyph(width, height, lesson).strokes
        .getOrNull(completedStrokeCount)
        ?.first()

    fun currentVisibleStrokeEnd(
        width: Float,
        height: Float,
        completedStrokeCount: Int,
        lesson: LessonSpec = GaLesson,
    ): CanvasPoint? = visibleLessonGlyph(width, height, lesson).strokes
        .getOrNull(completedStrokeCount)
        ?.last()

    fun gaDemonstrationGuide(width: Float, height: Float, progress: Float): DemonstrationGuide =
        demonstrationGuide(GaLesson, width, height, progress)

    fun demonstrationGuide(
        lesson: LessonSpec,
        width: Float,
        height: Float,
        progress: Float,
    ): DemonstrationGuide {
        require(progress in 0f..1f) { "progress must be between 0 and 1" }
        val segments = glyph(lesson, width, height).strokes.flatMapIndexed { strokeIndex, stroke ->
            stroke.zipWithNext().mapIndexed { segmentIndex, (start, end) ->
                val deltaX = end.x - start.x
                val deltaY = end.y - start.y
                val length = sqrt((deltaX * deltaX) + (deltaY * deltaY))
                DemonstrationSegment(strokeIndex, segmentIndex, start, end, deltaX, deltaY, length)
            }
        }
        val targetDistance = segments.sumOf { it.length.toDouble() }.toFloat() * progress
        var traversed = 0f
        var segment = segments.last()
        for (candidate in segments) {
            if (targetDistance <= traversed + candidate.length) {
                segment = candidate
                break
            }
            traversed += candidate.length
        }
        val segmentProgress = if (segment.length == 0f) {
            0f
        } else {
            ((targetDistance - traversed) / segment.length).coerceIn(0f, 1f)
        }
        return DemonstrationGuide(
            center = CanvasPoint(
                x = segment.start.x + (segment.deltaX * segmentProgress),
                y = segment.start.y + (segment.deltaY * segmentProgress),
            ),
            direction = CanvasPoint(segment.deltaX / segment.length, segment.deltaY / segment.length),
            strokeIndex = segment.strokeIndex,
            segmentIndex = segment.segmentIndex,
            visualScale = strokeGuideScale(lesson, segment.strokeIndex),
        )
    }

    fun gaStrokeDemonstrationProgress(strokeIndex: Int, progress: Float): Float =
        strokeDemonstrationProgress(GaLesson, strokeIndex, progress)

    fun strokeDemonstrationProgress(
        lesson: LessonSpec,
        strokeIndex: Int,
        progress: Float,
    ): Float {
        require(progress in 0f..1f) { "progress must be between 0 and 1" }
        val strokes = templateFor(lesson).map { stroke ->
            stroke.zipWithNext().sumOf { (start, end) ->
                kotlin.math.hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble())
            }.toFloat()
        }
        require(strokeIndex in strokes.indices) { "strokeIndex must identify a lesson stroke" }
        val visualScale = strokeGuideScale(lesson, strokeIndex)
        val endpointInset = (1f - visualScale) * 0.5f
        val visibleProgress = endpointInset + progress * (1f - endpointInset * 2f)
        val distanceBefore = strokes.take(strokeIndex).sum()
        val boundaryOffset = if (strokeIndex == 0) 0f else 0.000001f
        val totalDistance = strokes.sum()
        return ((distanceBefore + strokes[strokeIndex] * visibleProgress) / totalDistance + boundaryOffset)
            .coerceAtMost(1f)
    }

    fun gaStrokeGuideScale(strokeIndex: Int): Float {
        return strokeGuideScale(GaLesson, strokeIndex)
    }

    fun strokeGuideScale(lesson: LessonSpec, strokeIndex: Int): Float {
        val strokeLength = templateFor(lesson).getOrElse(strokeIndex) {
            throw IllegalArgumentException("strokeIndex must identify a lesson stroke")
        }.zipWithNext().sumOf { (start, end) ->
            kotlin.math.hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble())
        }.toFloat()
        return (strokeLength / 0.44f).coerceIn(0.5f, 1f)
    }

    fun gaDirectionArrowHeadLength(
        width: Float,
        height: Float,
        strokeIndex: Int,
        arrowLength: Float,
    ): Float = directionArrowHeadLength(GaLesson, width, height, strokeIndex, arrowLength)

    fun directionArrowHeadLength(
        lesson: LessonSpec,
        width: Float,
        height: Float,
        strokeIndex: Int,
        arrowLength: Float,
    ): Float {
        require(arrowLength > 0f) { "arrowLength must be positive" }
        val glyph = glyph(lesson, width, height)
        val stroke = glyph.strokes.getOrElse(strokeIndex) {
            throw IllegalArgumentException("strokeIndex must identify a lesson stroke")
        }
        val strokeLength = stroke.zipWithNext().sumOf { (start, end) ->
            kotlin.math.hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble())
        }.toFloat()
        val visualScale = strokeGuideScale(lesson, strokeIndex)
        val scaledStrokeMinimum = strokeLength * (1f - visualScale) * 0.22f
        return maxOf(arrowLength * 0.30f, scaledStrokeMinimum)
            .coerceAtMost(arrowLength * 0.75f)
    }

    fun gaCurrentStrokeDemonstrationProgress(
        completedStrokeCount: Int,
        progress: Float,
    ): Float = gaStrokeDemonstrationProgress(completedStrokeCount, progress)

    fun currentStrokeDemonstrationProgress(
        lesson: LessonSpec,
        completedStrokeCount: Int,
        progress: Float,
    ): Float = strokeDemonstrationProgress(lesson, completedStrokeCount, progress)

    private fun templateFor(lesson: LessonSpec): List<List<CanvasPoint>> = when (lesson.id) {
        LessonId.GIEOK -> gieokTemplate
        LessonId.NIEUN -> nieunTemplate
        LessonId.DIGEUT -> digeutTemplate
        LessonId.RIEUL -> rieulTemplate
        LessonId.MIEUM -> mieumTemplate
        LessonId.BIEUP -> bieupTemplate
        LessonId.SIOT -> siotTemplate
        LessonId.IEUNG -> ieungTemplate
        LessonId.JIEUT -> jieutTemplate
        LessonId.CHIEUT -> chieutTemplate
        LessonId.KIEUK -> kieukTemplate
        LessonId.TIEUT -> tieutTemplate
        LessonId.PIEUP -> pieupTemplate
        LessonId.HIEUH -> hieuhTemplate
        LessonId.A -> aTemplate
        LessonId.AE -> aeTemplate
        LessonId.YA -> yaTemplate
        LessonId.EO -> eoTemplate
        LessonId.YEO -> yeoTemplate
        LessonId.O -> oTemplate
        LessonId.YO -> yoTemplate
        LessonId.U -> uTemplate
        LessonId.YU -> yuTemplate
        LessonId.EU -> euTemplate
        LessonId.I -> iTemplate
        LessonId.GA -> gaTemplate
        LessonId.NA -> naTemplate
        LessonId.DA -> daTemplate
        LessonId.RA -> raTemplate
        LessonId.MA -> maTemplate
        LessonId.BA -> baTemplate
        LessonId.SA -> saTemplate
        LessonId.AH -> ahTemplate
        LessonId.JA -> jaTemplate
        LessonId.CHA -> chaTemplate
        LessonId.KA -> kaTemplate
        LessonId.TA -> taTemplate
        LessonId.PA -> paTemplate
        LessonId.HA -> haTemplate
    }

    private fun transform(
        template: List<List<CanvasPoint>>,
        width: Float,
        height: Float,
    ): GlyphGeometry {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val board = learningBoard(width, height)
        val emSize = board.width
        val originX = board.left
        val originY = board.top
        return GlyphGeometry(
            emSize = emSize,
            strokeWidth = emSize * GUIDE_STROKE_EM_FRACTION,
            strokes = template.map { stroke ->
                stroke.map { point ->
                    CanvasPoint(originX + point.x * emSize, originY + point.y * emSize)
                }
            },
        )
    }

    private data class DemonstrationSegment(
        val strokeIndex: Int,
        val segmentIndex: Int,
        val start: CanvasPoint,
        val end: CanvasPoint,
        val deltaX: Float,
        val deltaY: Float,
        val length: Float,
    )
}
