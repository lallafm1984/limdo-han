package com.example.limdo

import kotlin.math.min

internal data class CanvasPoint(
    val x: Float,
    val y: Float,
)

internal data class GlyphGeometry(
    val emSize: Float,
    val strokeWidth: Float,
    val strokes: List<List<CanvasPoint>>,
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

    // 화면에 노출하지 않는 조합 글자 배치 계약용 fixture다.
    internal val gaTemplate = listOf(
        listOf(
            CanvasPoint(0.05f, 0.12f),
            CanvasPoint(0.48f, 0.12f),
            CanvasPoint(0.48f, 0.88f),
        ),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    fun gieok(width: Float, height: Float): GlyphGeometry =
        transform(gieokTemplate, width, height)

    fun gieokPoints(width: Float, height: Float): List<CanvasPoint> =
        gieok(width, height).strokes.single()

    fun childStrokeWidth(width: Float, height: Float): Float =
        gieok(width, height).strokeWidth * CHILD_STROKE_GUIDE_FRACTION

    internal fun gaFixture(width: Float, height: Float): GlyphGeometry =
        transform(gaTemplate, width, height)

    private fun transform(
        template: List<List<CanvasPoint>>,
        width: Float,
        height: Float,
    ): GlyphGeometry {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val emSize = min(width, height) * EM_CANVAS_FRACTION
        val originX = (width - emSize) / 2f
        val originY = (height - emSize) / 2f
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
}
