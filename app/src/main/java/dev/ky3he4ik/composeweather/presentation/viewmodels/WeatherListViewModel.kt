package dev.ky3he4ik.composeweather.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.ky3he4ik.composeweather.data.local.WeatherDao
import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.model.WeatherDomainObject
import dev.ky3he4ik.composeweather.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

/**
 * UI state for the Home screen
 */
sealed interface WeatherListState {
    data class Success(val weatherDomainObjects: List<WeatherDomainObject>) : WeatherListState
    data class Error(val message: String?) : WeatherListState
    object Loading : WeatherListState
    object Empty : WeatherListState
}

class WeatherListViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao,
    application: Application
) : AndroidViewModel(application) {

    private val refreshFlow = MutableSharedFlow<Unit>(1, 1, BufferOverflow.DROP_OLDEST).apply {
        tryEmit(Unit)
    }

    fun getLocationsFromDatabase() = weatherDao.getLocationsFlow()

    fun deleteWeather(loc: String) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherDao.delete(loc)
        }
    }


    fun refresh() {
        refreshFlow.tryEmit(Unit)
    }

    /**
     * Gets Weather info for a list of zipcodes
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllWeather(): StateFlow<WeatherListState> {
        return refreshFlow
            .flatMapLatest {
                getLocationsFromDatabase()
                    .flatMapLatest { locs ->
                        flow {
                            if (locs.isNotEmpty()) {
                                emit(WeatherListState.Loading)
                                when (val response =
                                    weatherRepository.getWeather(locs.first())) {
                                    is NetworkResult.Success -> emit(
                                        WeatherListState.Success(
                                            weatherRepository.getWeatherList(
                                                locs,
                                            )
                                        )
                                    )
                                    is NetworkResult.Failure -> emit(
                                        WeatherListState.Error(
                                            message = response.message
                                        )
                                    )
                                    is NetworkResult.Exception -> emit(
                                        WeatherListState.Error(
                                            message = response.e.message
                                        )
                                    )
                                }
                            } else emit(WeatherListState.Empty)
                        }
                    }
            }.stateIn(viewModelScope, SharingStarted.Lazily, WeatherListState.Loading)
    }

}
