package dev.ky3he4ik.composeweather.repository

import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.data.remote.dto.ForecastContainer
import dev.ky3he4ik.composeweather.data.remote.dto.Search
import dev.ky3he4ik.composeweather.data.remote.dto.WeatherContainer
import dev.ky3he4ik.composeweather.model.WeatherDomainObject

interface WeatherRepository {
    suspend fun getWeather(location: String): NetworkResult<WeatherContainer>

    suspend fun getForecast(location: String): NetworkResult<ForecastContainer>

    suspend fun getSearchResults(location: String): NetworkResult<List<Search>>

    suspend fun getWeatherList(
        locations: List<String>
    ): List<WeatherDomainObject>

}
