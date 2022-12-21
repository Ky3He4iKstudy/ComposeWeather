package dev.ky3he4ik.composeweather.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.*
import dev.ky3he4ik.composeweather.data.local.WeatherDao
import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.data.remote.dto.asDomainModel
import dev.ky3he4ik.composeweather.model.ForecastDomainObject
import dev.ky3he4ik.composeweather.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

sealed class ForecastViewData {
    object Loading : ForecastViewData()
    class Error(val code: Int, val message: String?) : ForecastViewData()
    class Done(val forecastDomainObject: ForecastDomainObject) : ForecastViewData()
}

/**
 * [ViewModel] to provide data to the WeatherLocationDetailFragment
 */

// Pass an application as a parameter to the viewmodel constructor which is the contect passed to the singleton database object
class DailyForecastViewModel(
    private val weatherRepository: WeatherRepository,
    private val weatherDao: WeatherDao,
    application: Application
) :
    AndroidViewModel(application) {

    //The data source this viewmodel will fetch results from

    private val refreshFlow = MutableSharedFlow<Unit>(1, 1, BufferOverflow.DROP_OLDEST)
        .apply {
            tryEmit(Unit)
        }

    fun refresh() {
        refreshFlow.tryEmit(Unit)
    }

    fun getWeather(zipcode: String) = weatherDao.getWeatherByName(zipcode)


    @OptIn(ExperimentalCoroutinesApi::class)
    fun getForecast(
        location: String,
    )
            : StateFlow<ForecastViewData> {
        return refreshFlow
            .flatMapLatest {
                flow {
                    emit(ForecastViewData.Loading)
                    when (val response = weatherRepository.getForecast(location)) {
                        is NetworkResult.Success -> emit(
                            ForecastViewData.Done(
                                response.data.asDomainModel()
                            )
                        )
                        is NetworkResult.Failure -> emit(
                            ForecastViewData.Error(
                                code = response.code,
                                message = response.message
                            )
                        )
                        is NetworkResult.Exception -> emit(
                            ForecastViewData.Error(
                                code = 0,
                                message = response.e.message
                            )
                        )
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, ForecastViewData.Loading)
    }


// create a view model factory that takes a WeatherDao as a property and
//  creates a WeatherViewModel

    class DailyForecastViewModelFactory
        (
        private val weatherRepository: WeatherRepository,
        private val weatherDao: WeatherDao,
        val app: Application
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DailyForecastViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DailyForecastViewModel(
                    weatherRepository,
                    weatherDao,
                    app
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}




