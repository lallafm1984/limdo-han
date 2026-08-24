package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleCarouselTest {
    @Test
    fun vehiclesUseTheFixedNineVehicleOrderAndStartWithPoliceCar() {
        assertEquals(
            listOf("경찰차", "소방차", "구급차", "스포츠카", "택시", "버스", "쓰레기차", "화물트럭", "지게차"),
            VehicleCarousel.vehicles.map(VehicleSpec::koreanName),
        )
        assertEquals(R.drawable.limdo_vehicle_police, VehicleCarouselState().current.drawableRes)
    }

    @Test
    fun oneSuccessAdvancesOnceAndDuplicateSuccessDoesNotSkip() {
        val advanced = VehicleCarouselState().onTraceResult(GieokTraceResult.SUCCESS)
        val duplicate = advanced.onTraceResult(GieokTraceResult.SUCCESS)

        assertEquals("소방차", advanced.current.koreanName)
        assertEquals(advanced, duplicate)
        assertFalse(advanced.successArmed)
    }

    @Test
    fun retryClearAndReplayDoNotAdvanceAndAllowTheNextSuccess() {
        val afterSuccess = VehicleCarouselState().onTraceResult(GieokTraceResult.SUCCESS)
        val afterRetry = afterSuccess.onTraceResult(GieokTraceResult.OFF_GUIDE)
        val afterClear = afterRetry.onTraceResult(null)

        assertEquals("소방차", afterRetry.current.koreanName)
        assertEquals("소방차", afterClear.current.koreanName)
        assertTrue(afterClear.successArmed)
        assertEquals("구급차", afterClear.onTraceResult(GieokTraceResult.SUCCESS).current.koreanName)
    }

    @Test
    fun nineSuccessfulAttemptsWrapBackToPoliceCar() {
        var state = VehicleCarouselState()
        repeat(9) {
            state = state.onTraceResult(GieokTraceResult.SUCCESS)
            state = state.onTraceResult(null)
        }

        assertEquals("경찰차", state.current.koreanName)
        assertEquals(0, state.index)
    }
}
