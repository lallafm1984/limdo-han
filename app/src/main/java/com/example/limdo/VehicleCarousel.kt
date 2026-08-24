package com.example.limdo

import androidx.annotation.DrawableRes

internal data class VehicleSpec(
    val koreanName: String,
    @DrawableRes val drawableRes: Int,
)

internal data class VehicleCarouselState(
    val index: Int = 0,
    val successArmed: Boolean = true,
) {
    val current: VehicleSpec
        get() = VehicleCarousel.vehicles[index]

    fun onTraceResult(result: GieokTraceResult?): VehicleCarouselState = when {
        result == GieokTraceResult.SUCCESS && successArmed -> copy(
            index = (index + 1) % VehicleCarousel.vehicles.size,
            successArmed = false,
        )
        result == GieokTraceResult.SUCCESS -> this
        else -> copy(successArmed = true)
    }
}

internal object VehicleCarousel {
    val vehicles = listOf(
        VehicleSpec("경찰차", R.drawable.limdo_vehicle_police),
        VehicleSpec("소방차", R.drawable.limdo_vehicle_fire_truck),
        VehicleSpec("구급차", R.drawable.limdo_vehicle_ambulance),
        VehicleSpec("스포츠카", R.drawable.limdo_vehicle_sports_car),
        VehicleSpec("택시", R.drawable.limdo_vehicle_taxi),
        VehicleSpec("버스", R.drawable.limdo_vehicle_bus),
        VehicleSpec("쓰레기차", R.drawable.limdo_vehicle_garbage_truck),
        VehicleSpec("화물트럭", R.drawable.limdo_vehicle_cargo_truck),
        VehicleSpec("지게차", R.drawable.limdo_vehicle_forklift),
    )
}
