package dev.ky3he4ik.composeweather.model


data class WeatherDomainObject(
    val location: String,
    val temp: String,
    val location_coord: String,
    val imgSrcUrl: String,
    val conditionText: String,
    val windSpeed: Double,
    val windDirection: String,
    val time: String,
    val code: Int,
    val country: String,
    val feelsLikeTemp: String,
    val humidity: Int
)

data class ForecastDomainObject(
    val days: List<Days>,
)
