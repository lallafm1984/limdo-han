package com.limdo.hangul

internal enum class GaAssemblyTarget(val lessonId: LessonId, val glyph: String) {
    GA(LessonId.GA, "가"),
    GEO(LessonId.GEO, "거"),
    GYEO(LessonId.GYEO, "겨"),
    GO(LessonId.GO, "고"),
}

internal val GaAssemblyTarget.isHorizontalVowel: Boolean
    get() = this == GaAssemblyTarget.GO

internal enum class GaAssemblyPiece { GIEOK, VOWEL }

internal data class GaAssemblyState(
    val gieokPlaced: Boolean = false,
    val vowelPlaced: Boolean = false,
    val retryPiece: GaAssemblyPiece? = null,
) {
    val complete: Boolean get() = gieokPlaced && vowelPlaced

    fun place(piece: GaAssemblyPiece): GaAssemblyState = when (piece) {
        GaAssemblyPiece.GIEOK -> if (gieokPlaced) this else copy(gieokPlaced = true, retryPiece = null)
        GaAssemblyPiece.VOWEL -> if (!gieokPlaced || vowelPlaced) copy(retryPiece = piece)
        else copy(vowelPlaced = true, retryPiece = null)
    }
}
