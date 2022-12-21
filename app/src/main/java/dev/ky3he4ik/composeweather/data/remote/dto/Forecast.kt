package dev.ky3he4ik.composeweather.data.remote.dto

import dev.ky3he4ik.composeweather.model.*
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


@Serializable
data class ForecastContainer(
    val location: LocationData,
    val forecast: ForecastDay,
    val alerts: AlertList
)

@Serializable
data class ForecastDay(
    val forecastday: List<Day>
)

@Serializable
data class AlertList(
    val alert: List<Alert>
)

@Serializable
data class Alert(
    val headline: String,
    val category: String,
    val severity: String,
    val event: String,
    val effective: String,
    val expires: String,
    val desc: String
)

@Serializable
data class Day(
    val date: String,
    val day: ForecastForDay,
    val hour: List<Hour>,
    val astro: Astro
)

@Serializable
data class Astro(
    val sunrise: String,
    val sunset: String,
    val moon_phase: String
)

@Serializable
data class ForecastForDay(
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

@Serializable
data class Hour(
    val time_epoch: Int,
    val time: String,
    val temp_f: Double,
    val temp_c: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_dir: String,
    val chance_of_rain: Int,
    val pressure_mb: Double,
    val pressure_in: Double,
    val will_it_rain: Int,
    val chance_of_snow: Double,
    val will_it_snow: Int,
    val precip_mm: Double,
    val precip_in: Double,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val windchill_c: Double,
    val windchill_f: Double
)

fun ForecastContainer.asDomainModel(): ForecastDomainObject {
    return ForecastDomainObject(
        days = forecast.forecastday.map { it.toDomainModel() },
    )
}


fun Day.toDomainModel(): Days {

    val currentEpochTime = System.currentTimeMillis() / 1000 - 3600
    val today = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayOfWeek = LocalDate.parse(date).dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val month = LocalDate.parse(date).month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    val dayOfMonth = LocalDate.parse(date).dayOfMonth.toString()
    val shortDate = "$dayOfMonth $month"

    return Days(
        dayOfWeek = if (dayOfWeek == today) "Today"
        else dayOfWeek,
        date = shortDate,
        day = day
            .toDomainModel(),
        hours = hour
            .filter { it.time_epoch > currentEpochTime }
            .map { it.toDomainModel() },
        astroData = astro
            .toDomainModel()
    )
}

fun Condition.toDomainModel(): WeatherCondition {
    return WeatherCondition(
        text = text,
        icon = icon,
        code = code
    )
}

fun ForecastForDay.toDomainModel(): DayDomainObject {
    return DayDomainObject(
        condition = condition.toDomainModel(),
        avgtemp_c = avgtemp_c,
        avgtemp_f = avgtemp_f,
        maxtemp_c = maxtemp_c,
        maxtemp_f = maxtemp_f,
        mintemp_c = mintemp_c,
        mintemp_f = mintemp_f,
        daily_chance_of_rain = daily_chance_of_rain,
        daily_chance_of_snow = daily_chance_of_snow,
        totalprecip_in = totalprecip_in,
        totalprecip_mm = totalprecip_mm,
        avghumidity = avghumidity
    )
}

fun Astro.toDomainModel(): AstroDataDomainObject {
    // These need to observe the clock format setting
    val sunrise = LocalTime
        .parse(sunrise, DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
        .format(DateTimeFormatter.ofPattern("kk:mm"))
    val sunset = LocalTime
        .parse(sunset, DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
        .format(DateTimeFormatter.ofPattern("kk:mm"))


    return AstroDataDomainObject(
        moon_phase = moon_phase,
        sunrise = sunrise,
        sunset = sunset
    )
}

fun Hour.toDomainModel(): Hours {
    return Hours(
        time_epoch = time_epoch,
        time = time.substring(11) /* Remove date from time */,
        temp_f = temp_f,
        temp_c = temp_c,
        is_day = is_day,
        condition = condition.toDomainModel(),
        wind_mph = wind_mph,
        wind_kph = wind_kph,
        wind_dir = wind_dir,
        chance_of_rain = chance_of_rain,
        pressure_mb = pressure_mb,
        pressure_in = pressure_in,
        will_it_rain = will_it_rain,
        chance_of_snow = chance_of_snow,
        will_it_snow = will_it_snow,
        precip_mm = precip_mm,
        precip_in = precip_in,
        feelslike_c = feelslike_c,
        feelslike_f = feelslike_f,
        windchill_c = windchill_c,
        windchill_f = windchill_f,
    )
}
