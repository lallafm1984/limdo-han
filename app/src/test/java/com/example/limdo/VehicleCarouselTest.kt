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
    fun successKeepsActiveVehicleUntilCompletedMovePreparesNextInput() {
        val pending = VehicleCarouselState().onTraceResult(GieokTraceResult.SUCCESS)
        val duplicate = pending.onTraceResult(GieokTraceResult.SUCCESS)
        val advanced = duplicate.prepareNextInput(moveCompleted = true)

        assertEquals("경찰차", pending.current.koreanName)
        assertEquals("소방차", advanced.current.koreanName)
        assertEquals(pending, duplicate)
        assertFalse(pending.successArmed)
        assertTrue(pending.nextVehiclePending)
        assertTrue(advanced.successArmed)
        assertFalse(advanced.nextVehiclePending)
    }

    @Test
    fun clearNeverAdvancesAndRearmsCurrentVehicle() {
        val afterSuccess = VehicleCarouselState().onTraceResult(GieokTraceResult.SUCCESS)
        val afterRetry = afterSuccess.onTraceResult(GieokTraceResult.OFF_GUIDE)
        val afterReplay = afterRetry.onTraceResult(null)
        val cleared = afterReplay.clearCurrentInput()

        assertEquals("경찰차", afterRetry.current.koreanName)
        assertEquals(afterRetry, afterReplay)
        assertEquals("경찰차", cleared.current.koreanName)
        assertTrue(cleared.successArmed)
        assertFalse(cleared.nextVehiclePending)
    }

    @Test
    fun nextCannotAdvanceBeforeMoveCompletion() {
        val pending = VehicleCarouselState().onTraceResult(GieokTraceResult.SUCCESS)
        val blocked = pending.prepareNextInput(moveCompleted = false)

        assertEquals("경찰차", blocked.current.koreanName)
        assertTrue(blocked.successArmed)
        assertFalse(blocked.nextVehiclePending)
    }

    @Test
    fun nineSuccessfulAttemptsWrapBackToPoliceCar() {
        var state = VehicleCarouselState()
        repeat(9) {
            state = state.onTraceResult(GieokTraceResult.SUCCESS)
            state = state.prepareNextInput(moveCompleted = true)
        }

        assertEquals("경찰차", state.current.koreanName)
        assertEquals(0, state.index)
    }
}
