package dev.ky3he4ik.composeweather.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Location(val location: LocationData)

@Serializable
data class LocationData(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

fun LocationData.toDomainModel(): dev.ky3he4ik.composeweather.model.LocationData {
    return dev.ky3he4ik.composeweather.model.LocationData(
        name = name,
        region = region,
        country = country,
        lat = lat,
        lon = lon,
        tz_id = tz_id,
        localtime_epoch = localtime_epoch,
        localtime = localtime
    )
}