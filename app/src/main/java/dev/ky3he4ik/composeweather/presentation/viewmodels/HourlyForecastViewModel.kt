package dev.ky3he4ik.composeweather.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.*
import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.data.remote.dto.asDomainModel
import dev.ky3he4ik.composeweather.model.ForecastDomainObject
import dev.ky3he4ik.composeweather.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*


sealed class HourlyForecastViewData {
    class Done(val forecastDomainObject: ForecastDomainObject) : HourlyForecastViewData()
    class Error(val code: Int) : HourlyForecastViewData()
    object Loading : HourlyForecastViewData()
}

class HourlyForecastViewModel(
    private val weatherRepository: WeatherRepository,
    application: Application
) :
    AndroidViewModel(application) {

    private val refreshFlow = MutableSharedFlow<Unit>(1, 1, BufferOverflow.DROP_OLDEST).apply {
        tryEmit(Unit)
    }

    fun refresh() {
        refreshFlow.tryEmit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getHourlyForecast(
        zipcode: String
    ): StateFlow<HourlyForecastViewData> {
        return refreshFlow
            .flatMapLatest {
                flow {
                    emit(HourlyForecastViewData.Loading)
                    when (val response = weatherRepository.getForecast(zipcode)) {
                        is NetworkResult.Success -> emit(
                            HourlyForecastViewData.Done(
                                response.data.asDomainModel()
                            )
                        )
                        is NetworkResult.Failure -> emit(
                            HourlyForecastViewData.Error(
                                code = response.code
                            )
                        )
                        is NetworkResult.Exception -> emit(
                            HourlyForecastViewData.Error(
                                code = response.e.hashCode()
                            )
                        )
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, HourlyForecastViewData.Loading)
    }
}






