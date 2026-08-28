package com.limdo.hangul

internal enum class GaAssemblyPiece { GIEOK, A }

internal data class GaAssemblyState(
    val gieokPlaced: Boolean = false,
    val aPlaced: Boolean = false,
    val retryPiece: GaAssemblyPiece? = null,
) {
    val complete: Boolean get() = gieokPlaced && aPlaced

    fun place(piece: GaAssemblyPiece): GaAssemblyState = when (piece) {
        GaAssemblyPiece.GIEOK -> if (gieokPlaced) this else copy(gieokPlaced = true, retryPiece = null)
        GaAssemblyPiece.A -> if (!gieokPlaced || aPlaced) copy(retryPiece = piece)
        else copy(aPlaced = true, retryPiece = null)
    }
}
