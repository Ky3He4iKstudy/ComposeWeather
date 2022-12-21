package dev.ky3he4ik.composeweather.repository

import dev.ky3he4ik.composeweather.data.remote.*
import dev.ky3he4ik.composeweather.data.remote.dto.ForecastContainer
import dev.ky3he4ik.composeweather.data.remote.dto.Search
import dev.ky3he4ik.composeweather.data.remote.dto.WeatherContainer
import dev.ky3he4ik.composeweather.data.remote.dto.asDomainModel
import dev.ky3he4ik.composeweather.model.WeatherDomainObject


class WeatherRepositoryImpl(private val weatherApi: WeatherApi) : WeatherRepository {

    override suspend fun getWeather(location: String): NetworkResult<WeatherContainer> =
        weatherApi.retrofitService.getWeather(location)

    override suspend fun getForecast(location: String): NetworkResult<ForecastContainer> =
        try {
            weatherApi.retrofitService.getForecast(location, days = 7)
        } catch (e: Exception) {
            weatherApi.retrofitService.getForecast(location, days = 7)
        }

    override suspend fun getSearchResults(location: String): NetworkResult<List<Search>> =
        weatherApi.retrofitService.locationSearch(location)

    override suspend fun getWeatherList(
        locations: List<String>,
    ): List<WeatherDomainObject> {
        val weatherDomainObjects = mutableListOf<WeatherDomainObject>()
        locations.forEach { location ->
            val response = getWeather(location)
            response.onSuccess {
                weatherDomainObjects.add(it.asDomainModel(location))
            }.onError { code, message ->
                println(message)
            }.onException {
                println(it.message)
            }
        }
        return weatherDomainObjects
    }
}