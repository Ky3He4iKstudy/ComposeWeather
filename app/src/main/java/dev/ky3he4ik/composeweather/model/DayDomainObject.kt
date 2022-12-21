package dev.ky3he4ik.composeweather.model

import androidx.compose.ui.graphics.Color
import dev.ky3he4ik.composeweather.data.remote.dto.Condition

data class DayDomainObject(
    val condition: Condition,
    val avgtemp_f: Double,
    val maxtemp_f: Double,
    val mintemp_f: Double,
    val avgtemp_c: Double,
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val daily_chance_of_rain: Double,
    val daily_chance_of_snow: Double,
    val totalprecip_in: Double,
    val totalprecip_mm: Double,
    val avghumidity: Double
)

