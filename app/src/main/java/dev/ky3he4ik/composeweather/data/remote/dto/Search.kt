package dev.ky3he4ik.composeweather.data.remote.dto

import dev.ky3he4ik.composeweather.model.SearchResult
import kotlinx.serialization.Serializable


@Serializable
data class Search(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String,
)

fun Search.toDomainModel(): SearchResult {
    return SearchResult(
        id = id,
        country = country,
        lat = lat,
        lon = lon,
        name = name,
        region = region,
        url = url
    )
}
