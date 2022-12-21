package dev.ky3he4ik.composeweather.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.*
import dev.ky3he4ik.composeweather.data.local.WeatherDao
import dev.ky3he4ik.composeweather.data.local.WeatherEntity
import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.data.remote.dto.asDatabaseModel
import dev.ky3he4ik.composeweather.data.remote.dto.toDomainModel
import dev.ky3he4ik.composeweather.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*


/**
 * [ViewModel] to provide data to the  [AddWeatherLocationFragment] and allow for interaction the the [WeatherDao]
 */

// Pass an application as a parameter to the viewmodel constructor which is the context passed to the singleton database object
class AddWeatherLocationViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao,
    application: Application
) :
    AndroidViewModel(application) {

    private val queryFlow = MutableSharedFlow<String>(1, 1, BufferOverflow.DROP_OLDEST)

    private var searchJob: Job? = null


    // sort counter for database entries

    /**
     * If database is empty, initial sort value is 1
     * If not empty, find last entry in database, increment sort value by 1
     */
    private fun getLastEntrySortValue(): Int {
        var dbSortOrderValue = 1
        if (!weatherDao.isEmpty()) {
            dbSortOrderValue = weatherDao.selectLastEntry().sortOrder + 1
        }
        return dbSortOrderValue
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val getSearchResults: StateFlow<SearchViewData> =
        queryFlow
            .flatMapLatest { currentQuery ->
                flow {
                    Log.d("Search results", "begin search")
                    when (val response = weatherRepository.getSearchResults(currentQuery)) {
                        is NetworkResult.Success -> {
                            val newSearchResults =
                                response.data.map { it.toDomainModel() }
                                    .map { searchDomainObject ->
                                        searchDomainObject.name + "," + " " + searchDomainObject.region
                                    }
                            Log.d("Search results", newSearchResults.joinToString())
                            emit(SearchViewData.Done(newSearchResults))
                        }
                        is NetworkResult.Failure -> {
                            emit(
                                SearchViewData.Error(
                                    code = response.code,
                                    message = response.message
                                )
                            )
                        }
                        is NetworkResult.Exception -> {
                            emit(
                                SearchViewData.Error(
                                    code = response.e.hashCode(),
                                    message = response.e.message
                                )
                            )
                        }
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, SearchViewData.Loading)


    fun setQuery(currentQuery: String) {
        /**
         * This will cancel the job every time the query is changed in the search field, if a character
         * is not typed in 500ms, the queryflow will emit its value. This prevents the API from being
         * called on every character being typed in the search field
         */
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            queryFlow.tryEmit(currentQuery)
        }

    }

    fun clearQueryResults() {
        queryFlow.tryEmit("")
    }

    suspend fun storeNetworkDataInDatabase(location: String): Boolean {
        /**
         * This runs on a background thread by default so any value modified within this scoupe cannot
         * be returned outside of the scope
         */

        val networkError: Boolean =
            when (val response = weatherRepository.getWeather(location)) {
                is NetworkResult.Success -> {
                    weatherDao.insert(
                        response.data.asDatabaseModel(
                            location,
                            getLastEntrySortValue()
                        )
                    )
                    true
                }
                is NetworkResult.Failure -> false
                is NetworkResult.Exception -> false
            }
        return networkError

    }

    fun getWeather(location: String) = weatherDao.getWeatherByLocation(location)

    fun getCurrentLocation(context: Context): Location? {
        try {
            val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?
            return locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (ex: SecurityException) {
            Log.e("AddWeatherLocationViewModel", "Can't get location: lacking permission")
        }
        return null
    }

    fun updateWeather(
        id: Long,
        name: String,
        zipcode: String,
        sortOrder: Int
    ) {
        val weatherEntity = WeatherEntity(
            id = id,
            cityName = name,
            loc = zipcode,
            sortOrder = sortOrder
        )
        viewModelScope.launch(Dispatchers.IO) {
            // call the DAO method to update a weather object to the database here
            weatherDao.insert(weatherEntity)
        }
    }


// create a view model factory that takes a WeatherDao as a property and
//  creates a WeatherViewModel

    class AddWeatherLocationViewModelFactory(
        private val weatherRepository: WeatherRepository,
        private val weatherDao: WeatherDao,
        val app: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddWeatherLocationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddWeatherLocationViewModel(weatherRepository, weatherDao, app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class SearchViewData {
    object Loading : SearchViewData()
    data class Error(val code: Int, val message: String?) : SearchViewData()
    data class Done(val searchResults: List<String>) : SearchViewData()
}