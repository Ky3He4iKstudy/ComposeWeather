package dev.ky3he4ik.composeweather.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.*
import dev.ky3he4ik.composeweather.data.remote.NetworkResult
import dev.ky3he4ik.composeweather.data.remote.dto.asDomainModel
import dev.ky3he4ik.composeweather.data.local.model.ForecastDomainObject
import dev.ky3he4ik.composeweather.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

sealed class ForecastViewData {
    object Loading : ForecastViewData()
    class Error(val code: Int) : ForecastViewData()
    class Done(val forecastDomainObject: ForecastDomainObject) : ForecastViewData()
}

class DailyForecastViewModel(
    private val weatherRepository: WeatherRepository,
    application: Application
) :
    AndroidViewModel(application) {

    private val refreshFlow = MutableSharedFlow<Unit>(1, 1, BufferOverflow.DROP_OLDEST)
        .apply {
            tryEmit(Unit)
        }

    fun refresh() {
        refreshFlow.tryEmit(Unit)
    }

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
                            )
                        )
                        is NetworkResult.Exception -> emit(
                            ForecastViewData.Error(
                                code = 0,
                            )
                        )
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, ForecastViewData.Loading)
    }
}
