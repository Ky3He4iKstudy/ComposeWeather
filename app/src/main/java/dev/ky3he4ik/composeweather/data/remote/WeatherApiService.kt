package dev.ky3he4ik.composeweather.data.remote

import dev.ky3he4ik.composeweather.data.remote.dto.WeatherContainer
import dev.ky3he4ik.composeweather.data.remote.dto.ForecastContainer
import dev.ky3he4ik.composeweather.data.remote.dto.Search
import dev.ky3he4ik.composeweather.util.Constants.BASE_URL
import dev.ky3he4ik.composeweather.util.Constants.CURRENT
import dev.ky3he4ik.composeweather.util.Constants.FORECAST
import dev.ky3he4ik.composeweather.util.Constants.SEARCH
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * A retrofit service to fetch the weather data from the API
 */

val json = Json {
    ignoreUnknownKeys = true
}

//val logging = HttpLoggingInterceptor().also {
//    it.setLevel(HttpLoggingInterceptor.Level.BODY)
//}

//val httpClient = OkHttpClient.Builder().also {
//    it.addInterceptor(logging)
//}


// Configure retrofit to parse JSON and use coroutines
@OptIn(ExperimentalSerializationApi::class)
val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
//    .client(httpClient.build())
    .build()


interface WeatherApiService {
    @GET(CURRENT)
    suspend fun getWeather(
        @Query("q") name: String
    ): NetworkResult<WeatherContainer>

    @GET(SEARCH)
    suspend fun locationSearch(
        @Query("Q") location: String
    ): NetworkResult<List<Search>>

    @GET(FORECAST)
    suspend fun getForecast(
        @Query("q") name: String,
        @Query("days") days: Int = 3,
        @Query("alerts") alerts: String = "yes"
    ): NetworkResult<ForecastContainer>
}


/**
 * Main entry point for network access
 */

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}


/**
 * Sealed class to handle API responses
 */
sealed interface NetworkResult<T : Any> {
    class Success<T : Any>(val data: T) : NetworkResult<T>
    class Failure<T : Any>(val code: Int, val message: String?) : NetworkResult<T>
    class Exception<T : Any>(val e: Throwable) : NetworkResult<T>
}


