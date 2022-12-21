package dev.ky3he4ik.composeweather.model

data class DayDomainObject(
    val condition: WeatherCondition,
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

