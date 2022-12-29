package dev.ky3he4ik.composeweather.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.ky3he4ik.composeweather.data.remote.dto.ForecastContainer
import dev.ky3he4ik.composeweather.data.remote.dto.Search
import dev.ky3he4ik.composeweather.data.remote.dto.WeatherContainer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

val json = Json {
    ignoreUnknownKeys = true
}

val logging = HttpLoggingInterceptor().also {
    it.setLevel(HttpLoggingInterceptor.Level.BODY)
}

val httpClient = OkHttpClient.Builder().also {
    it.addInterceptor(logging)
}


@OptIn(ExperimentalSerializationApi::class)
val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl("https://api.weatherapi.com/v1/")
    .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
    .client(httpClient.build())
    .build()

const val APIKEY = "API KEY HERE"

interface WeatherApiService {
    @GET("current.json?key=$APIKEY")
    suspend fun getWeather(
        @Query("q") name: String
    ): NetworkResult<WeatherContainer>

    @GET("search.json?key=$APIKEY")
    suspend fun locationSearch(
        @Query("Q") location: String
    ): NetworkResult<List<Search>>

    @GET("forecast.json?key=$APIKEY")
    suspend fun getForecast(
        @Query("q") name: String,
        @Query("days") days: Int = 3,
        @Query("alerts") alerts: String = "no"
    ): NetworkResult<ForecastContainer>
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}


sealed interface NetworkResult<T : Any> {
    class Success<T : Any>(val data: T) : NetworkResult<T>
    class Failure<T : Any>(val code: Int, val message: String?) : NetworkResult<T>
    class Exception<T : Any>(val e: Throwable) : NetworkResult<T>
}


