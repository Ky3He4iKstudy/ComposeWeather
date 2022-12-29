package dev.ky3he4ik.composeweather.data.remote.dto

import dev.ky3he4ik.composeweather.data.local.WeatherEntity
import dev.ky3he4ik.composeweather.data.local.model.WeatherDomainObject
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class WeatherContainer(
    val location: LocationData,
    val current: CurrentWeatherData
)

fun WeatherContainer.asDatabaseModel(
    name: String,
): WeatherEntity {
    return WeatherEntity(
        loc = name,
        cityName = location.name,
    )
}

fun WeatherContainer.asDomainModel(loc: String): WeatherDomainObject {
    val locationDomain = location.toDomainModel()
    val localTime = Instant
        .ofEpochSecond(location.localtime_epoch)
        .atZone(ZoneId.of(location.tz_id))
        .format(DateTimeFormatter.ofPattern("MMM dd"))

    return WeatherDomainObject(
        time = localTime,
        location = locationDomain.name,
        location_coord = loc,
        temp = current.temp_c.toInt().toString(),
        imgSrcUrl = current.condition.icon,
        conditionText = current.condition.text,
        windSpeed = current.wind_kph,
        windDirection = current.wind_dir,
        code = current.condition.code,
        country = locationDomain.country,
        feelsLikeTemp = current.feelslike_c.toInt().toString(),
        humidity = current.humidity
    )
}







