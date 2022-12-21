package dev.ky3he4ik.composeweather.repository

import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.data.remote.dto.ForecastContainer
import dev.ky3he4ik.composeweather.data.remote.dto.Search
import dev.ky3he4ik.composeweather.data.remote.dto.WeatherContainer
import dev.ky3he4ik.composeweather.model.WeatherDomainObject

interface WeatherRepository {

    // The only thing we should be storing into the database is zipcode and city name, everything
    // else is dynamic

    suspend fun getWeather(zipcode: String): NetworkResult<WeatherContainer>

    suspend fun getForecast(zipcode: String): NetworkResult<ForecastContainer>

    suspend fun getSearchResults(location: String): NetworkResult<List<Search>>

    suspend fun getWeatherList(
        zipcodes: List<String>
    ): List<WeatherDomainObject>

}