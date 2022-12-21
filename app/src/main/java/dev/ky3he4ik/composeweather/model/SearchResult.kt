package dev.ky3he4ik.composeweather.model

data class SearchResult(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String,
)
