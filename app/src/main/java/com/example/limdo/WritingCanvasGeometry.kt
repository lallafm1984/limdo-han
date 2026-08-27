package com.example.limdo

import kotlin.math.min
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
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
    private const val FINAL_CONSONANT_GUIDE_STROKE_EM_FRACTION = 0.12f
    private const val CHILD_STROKE_GUIDE_FRACTION = 0.60f

    fun evenlySpacedGuideDots(
        stroke: List<CanvasPoint>,
        targetSpacing: Float,
    ): List<CanvasPoint> {
        if (stroke.size < 2 || targetSpacing <= 0f) return stroke
        val segmentLengths = stroke.zipWithNext().map { (start, end) ->
            sqrt((end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y))
        }
        val totalLength = segmentLengths.sum()
        if (totalLength == 0f) return listOf(stroke.first())
        val intervalCount = (totalLength / targetSpacing).toInt().coerceAtLeast(1)
        val spacing = totalLength / intervalCount
        return List(intervalCount + 1) { dotIndex ->
            val distance = spacing * dotIndex
            var traversed = 0f
            var segmentIndex = 0
            while (
                segmentIndex < segmentLengths.lastIndex &&
                traversed + segmentLengths[segmentIndex] < distance
            ) {
                traversed += segmentLengths[segmentIndex]
                segmentIndex++
            }
            val start = stroke[segmentIndex]
            val end = stroke[segmentIndex + 1]
            val segmentLength = segmentLengths[segmentIndex]
            val fraction = if (segmentLength == 0f) 0f else (distance - traversed) / segmentLength
            CanvasPoint(
                x = start.x + (end.x - start.x) * fraction,
                y = start.y + (end.y - start.y) * fraction,
            )
        }
    }

    /**
     * 받침 있는 세로 모음 음절은 받침 없는 `가`를 그대로 축소해 붙이지 않는다.
     * 종성의 높이와 복잡도에 따라 초성·중성이 차지하는 윗칸 비율도 함께 조정한다.
     */
    private data class ClosedSyllableLayout(
        val initialBottom: Float,
        val medialBottom: Float,
        val medialBranchY: Float,
    )

    private val standardClosedSyllable = ClosedSyllableLayout(
        initialBottom = 0.50f,
        medialBottom = 0.52f,
        medialBranchY = 0.29f,
    )

    private val lowFinalClosedSyllable = ClosedSyllableLayout(
        initialBottom = 0.54f,
        medialBottom = 0.56f,
        medialBranchY = 0.31f,
    )

    private val tallFinalClosedSyllable = ClosedSyllableLayout(
        initialBottom = 0.48f,
        medialBottom = 0.48f,
        medialBranchY = 0.27f,
    )

    private val gieokTemplate = listOf(
        listOf(
            CanvasPoint(0.12f, 0.12f),
            CanvasPoint(0.88f, 0.12f),
            CanvasPoint(0.88f, 0.88f),
        ),
    )

    private val nieunTemplate = listOf(
        listOf(
            CanvasPoint(0.12f, 0.12f),
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

    private fun rieulTemplate(left: Float, right: Float): List<List<CanvasPoint>> = listOf(
        listOf(
            CanvasPoint(left, 0.10f),
            CanvasPoint(right, 0.10f),
            CanvasPoint(right, 0.40f),
        ),
        listOf(CanvasPoint(left, 0.40f), CanvasPoint(right, 0.40f)),
        listOf(CanvasPoint(left, 0.40f), CanvasPoint(left, 0.90f), CanvasPoint(right, 0.90f)),
    )

    private val rieulTemplate = rieulTemplate(left = 0.12f, right = 0.88f)

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

    private fun siotLegs(
        apexX: Float,
        apexY: Float,
        left: Float,
        right: Float,
        bottom: Float,
    ): List<List<CanvasPoint>> {
        val junction = CanvasPoint(apexX, apexY + (bottom - apexY) * 0.42f)
        val leftLeg = listOf(
            CanvasPoint(apexX, apexY),
            CanvasPoint(apexX, apexY + (bottom - apexY) * 0.20f),
            junction,
            CanvasPoint(apexX - 0.05f, apexY + (bottom - apexY) * 0.62f),
            CanvasPoint(apexX - 0.16f, apexY + (bottom - apexY) * 0.80f),
            CanvasPoint(left, bottom),
        )
        val rightLeg = listOf(
            junction,
            CanvasPoint(junction.x + (right - junction.x) * 0.18f, junction.y + (bottom - junction.y) * 0.34f),
            CanvasPoint(junction.x + (right - junction.x) * 0.48f, junction.y + (bottom - junction.y) * 0.62f),
            CanvasPoint(right, bottom),
        )
        return listOf(leftLeg, rightLeg)
    }

    private fun jieutBody(
        left: Float,
        right: Float,
        apexX: Float,
        barY: Float,
        bottom: Float,
    ): List<List<CanvasPoint>> {
        val junction = CanvasPoint(apexX, (barY + bottom) / 2f)
        val rightBottom = CanvasPoint(right, bottom)
        return listOf(
            listOf(
                CanvasPoint(left, barY),
                CanvasPoint(right, barY),
                junction,
                CanvasPoint(left, bottom),
            ),
            listOf(junction, rightBottom),
        )
    }

    private val siotTemplate = siotLegs(0.50f, 0.12f, 0.16f, 0.84f, 0.82f)

    private val ieungTemplate = listOf(circleTemplate(0.50f, 0.50f, 0.42f))

    private val jieutTemplate = jieutBody(0.14f, 0.86f, 0.50f, 0.18f, 0.84f)

    private val chieutTemplate = listOf(
        listOf(CanvasPoint(0.34f, 0.07f), CanvasPoint(0.66f, 0.07f)),
    ) + jieutBody(0.14f, 0.86f, 0.50f, 0.28f, 0.88f)

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

    private fun pieupTemplate(left: Float, right: Float): List<List<CanvasPoint>> {
        val width = right - left
        return listOf(
            listOf(CanvasPoint(left, 0.20f), CanvasPoint(right, 0.20f)),
            listOf(CanvasPoint(left + width * 0.23f, 0.25f), CanvasPoint(left + width * 0.23f, 0.75f)),
            listOf(CanvasPoint(left + width * 0.77f, 0.25f), CanvasPoint(left + width * 0.77f, 0.75f)),
            listOf(CanvasPoint(left, 0.80f), CanvasPoint(right, 0.80f)),
        )
    }

    private val pieupTemplate = pieupTemplate(left = 0.14f, right = 0.86f)

    private val hieuhTemplate = listOf(
        listOf(CanvasPoint(0.36f, 0.06f), CanvasPoint(0.64f, 0.06f)),
        listOf(CanvasPoint(0.20f, 0.30f), CanvasPoint(0.80f, 0.30f)),
        circleTemplate(0.50f, 0.71f, 0.19f),
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

    private val verticalGieok = gaTemplate.first()
    private val horizontalGieok = listOf(
        CanvasPoint(0.12f, 0.08f), CanvasPoint(0.88f, 0.08f), CanvasPoint(0.88f, 0.43f),
    )
    private val gyaTemplate = listOf(verticalGieok) + listOf(
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.38f), CanvasPoint(0.94f, 0.38f)),
        listOf(CanvasPoint(0.72f, 0.62f), CanvasPoint(0.94f, 0.62f)),
    )
    private val geoTemplate = listOf(verticalGieok) + listOf(
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.52f, 0.50f)),
    )
    private val gyeoTemplate = listOf(verticalGieok) + listOf(
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.38f), CanvasPoint(0.52f, 0.38f)),
        listOf(CanvasPoint(0.72f, 0.62f), CanvasPoint(0.52f, 0.62f)),
    )
    private val goTemplate = listOf(horizontalGieok) + listOf(
        listOf(CanvasPoint(0.12f, 0.72f), CanvasPoint(0.88f, 0.72f)),
        listOf(CanvasPoint(0.50f, 0.72f), CanvasPoint(0.50f, 0.50f)),
    )
    private val gyoTemplate = listOf(horizontalGieok) + listOf(
        listOf(CanvasPoint(0.12f, 0.76f), CanvasPoint(0.88f, 0.76f)),
        listOf(CanvasPoint(0.38f, 0.76f), CanvasPoint(0.38f, 0.54f)),
        listOf(CanvasPoint(0.62f, 0.76f), CanvasPoint(0.62f, 0.54f)),
    )
    private val guTemplate = listOf(horizontalGieok) + listOf(
        listOf(CanvasPoint(0.12f, 0.62f), CanvasPoint(0.88f, 0.62f)),
        listOf(CanvasPoint(0.50f, 0.62f), CanvasPoint(0.50f, 0.88f)),
    )
    private val gyuTemplate = listOf(horizontalGieok) + listOf(
        listOf(CanvasPoint(0.12f, 0.58f), CanvasPoint(0.88f, 0.58f)),
        listOf(CanvasPoint(0.38f, 0.58f), CanvasPoint(0.38f, 0.84f)),
        listOf(CanvasPoint(0.62f, 0.58f), CanvasPoint(0.62f, 0.84f)),
    )
    private val geuTemplate = listOf(horizontalGieok) + listOf(
        listOf(CanvasPoint(0.12f, 0.72f), CanvasPoint(0.88f, 0.72f)),
    )
    private val giTemplate = listOf(verticalGieok) + listOf(
        listOf(CanvasPoint(0.76f, 0.08f), CanvasPoint(0.76f, 0.92f)),
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

    private val raTemplate = rieulTemplate(left = 0.05f, right = 0.48f) + listOf(
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

    private val saTemplate = siotLegs(0.27f, 0.12f, 0.05f, 0.49f, 0.84f) + listOf(
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val ahTemplate = listOf(
        circleTemplate(0.27f, 0.50f, 0.22f),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val jaTemplate = jieutBody(0.05f, 0.49f, 0.27f, 0.18f, 0.84f) + listOf(
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val chaTemplate = listOf(
        listOf(CanvasPoint(0.11f, 0.07f), CanvasPoint(0.43f, 0.07f)),
    ) + jieutBody(0.05f, 0.49f, 0.27f, 0.28f, 0.88f) + listOf(
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

    private val paTemplate = pieupTemplate(left = 0.05f, right = 0.49f) + listOf(
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val haTemplate = listOf(
        listOf(CanvasPoint(0.13f, 0.06f), CanvasPoint(0.41f, 0.06f)),
        listOf(CanvasPoint(-0.03f, 0.30f), CanvasPoint(0.57f, 0.30f)),
        circleTemplate(0.27f, 0.71f, 0.19f),
        listOf(CanvasPoint(0.72f, 0.08f), CanvasPoint(0.72f, 0.92f)),
        listOf(CanvasPoint(0.72f, 0.50f), CanvasPoint(0.94f, 0.50f)),
    )

    private val gakTemplate = finalConsonantTemplate(
        standardClosedSyllable,
        listOf(CanvasPoint(0.20f, 0.66f), CanvasPoint(0.80f, 0.66f), CanvasPoint(0.80f, 0.88f)),
    )

    private val ganTemplate = finalConsonantTemplate(
        lowFinalClosedSyllable,
        listOf(CanvasPoint(0.20f, 0.70f), CanvasPoint(0.20f, 0.88f), CanvasPoint(0.80f, 0.88f)),
    )

    private val gatTemplate = finalConsonantTemplate(
        standardClosedSyllable,
        listOf(CanvasPoint(0.20f, 0.66f), CanvasPoint(0.80f, 0.66f)),
        listOf(CanvasPoint(0.20f, 0.66f), CanvasPoint(0.20f, 0.88f), CanvasPoint(0.80f, 0.88f)),
    )

    private val galTemplate = finalConsonantTemplate(
        tallFinalClosedSyllable,
        listOf(CanvasPoint(0.20f, 0.62f), CanvasPoint(0.80f, 0.62f), CanvasPoint(0.80f, 0.75f)),
        listOf(CanvasPoint(0.80f, 0.75f), CanvasPoint(0.20f, 0.75f), CanvasPoint(0.20f, 0.88f)),
        listOf(CanvasPoint(0.20f, 0.88f), CanvasPoint(0.80f, 0.88f)),
    )

    private val gamTemplate = finalConsonantTemplate(
        standardClosedSyllable,
        listOf(CanvasPoint(0.20f, 0.66f), CanvasPoint(0.20f, 0.88f)),
        listOf(CanvasPoint(0.20f, 0.66f), CanvasPoint(0.80f, 0.66f), CanvasPoint(0.80f, 0.88f)),
        listOf(CanvasPoint(0.20f, 0.88f), CanvasPoint(0.80f, 0.88f)),
    )

    private val gapTemplate = finalConsonantTemplate(
        tallFinalClosedSyllable,
        listOf(CanvasPoint(0.30f, 0.63f), CanvasPoint(0.30f, 0.88f)),
        listOf(CanvasPoint(0.70f, 0.63f), CanvasPoint(0.70f, 0.88f)),
        listOf(CanvasPoint(0.30f, 0.76f), CanvasPoint(0.70f, 0.76f)),
        listOf(CanvasPoint(0.30f, 0.88f), CanvasPoint(0.70f, 0.88f)),
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
        transform(
            template = templateFor(lesson),
            width = width,
            height = height,
            guideStrokeEmFraction = if (lesson.stage == CurriculumStage.FINAL_CONSONANTS) {
                FINAL_CONSONANT_GUIDE_STROKE_EM_FRACTION
            } else {
                GUIDE_STROKE_EM_FRACTION
            },
        )

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

    fun startMarkerRadius(pathStroke: Float): Float = pathStroke * 0.20f

    fun finishMarkerOuterRadius(pathStroke: Float): Float = pathStroke * 0.20f

    fun finishMarkerColorRadius(pathStroke: Float): Float = pathStroke * 0.13f

    fun finishMarkerCenterRadius(pathStroke: Float): Float = pathStroke * 0.055f

    fun demonstrationMarkerOuterRadius(pathStroke: Float): Float = pathStroke * 0.14f

    fun demonstrationMarkerTravelProgress(progress: Float): Float =
        0.08f + progress.coerceIn(0f, 1f) * 0.84f

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
        LessonId.GYA -> gyaTemplate
        LessonId.GEO -> geoTemplate
        LessonId.GYEO -> gyeoTemplate
        LessonId.GO -> goTemplate
        LessonId.GYO -> gyoTemplate
        LessonId.GU -> guTemplate
        LessonId.GYU -> gyuTemplate
        LessonId.GEU -> geuTemplate
        LessonId.GI -> giTemplate
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
        LessonId.GAK -> gakTemplate
        LessonId.GAN -> ganTemplate
        LessonId.GAT -> gatTemplate
        LessonId.GAL -> galTemplate
        LessonId.GAM -> gamTemplate
        LessonId.GAP -> gapTemplate
    }

    private fun finalConsonantTemplate(
        layout: ClosedSyllableLayout,
        vararg finalStrokes: List<CanvasPoint>,
    ): List<List<CanvasPoint>> = listOf(
        listOf(
            CanvasPoint(0.12f, 0.12f),
            CanvasPoint(0.50f, 0.12f),
            CanvasPoint(0.50f, layout.initialBottom),
        ),
        listOf(CanvasPoint(0.70f, 0.12f), CanvasPoint(0.70f, layout.medialBottom)),
        listOf(CanvasPoint(0.70f, layout.medialBranchY), CanvasPoint(0.86f, layout.medialBranchY)),
    ) + finalStrokes

    private fun transform(
        template: List<List<CanvasPoint>>,
        width: Float,
        height: Float,
        guideStrokeEmFraction: Float = GUIDE_STROKE_EM_FRACTION,
    ): GlyphGeometry {
        require(width > 0f) { "width must be positive" }
        require(height > 0f) { "height must be positive" }

        val board = learningBoard(width, height)
        val emSize = board.width
        val originX = board.left
        val originY = board.top
        return GlyphGeometry(
            emSize = emSize,
            strokeWidth = emSize * guideStrokeEmFraction,
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
    private fun circleTemplate(
        centerX: Float,
        centerY: Float,
        radius: Float,
        sampleCount: Int = 48,
    ): List<CanvasPoint> = (0..sampleCount).map { index ->
        val angle = -PI / 2.0 - (2.0 * PI * index / sampleCount)
        CanvasPoint(
            x = centerX + radius * cos(angle).toFloat(),
            y = centerY + radius * sin(angle).toFloat(),
        )
    }
