package com.limdo.hangul

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GaAssemblyTest {
    @Test fun gieokThenACompletesButAFirstDoesNot() {
        val wrongFirst = GaAssemblyState().place(GaAssemblyPiece.A)
        assertFalse(wrongFirst.complete)
        assertFalse(wrongFirst.aPlaced)
        assertTrue(wrongFirst.retryPiece == GaAssemblyPiece.A)

        val afterGieok = wrongFirst.place(GaAssemblyPiece.GIEOK)
        assertTrue(afterGieok.gieokPlaced)
        assertFalse(afterGieok.complete)
        assertTrue(afterGieok.place(GaAssemblyPiece.A).complete)
    }
}
