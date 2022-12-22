package dev.ky3he4ik.composeweather

import android.app.Application
import dev.ky3he4ik.composeweather.data.local.WeatherDatabase
import dev.ky3he4ik.composeweather.data.remote.WeatherApi
import dev.ky3he4ik.composeweather.presentation.viewmodels.*
import dev.ky3he4ik.composeweather.repository.WeatherRepository
import dev.ky3he4ik.composeweather.repository.WeatherRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    private val database: WeatherDatabase by lazy { WeatherDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()


        val appModule = module {
            single<WeatherRepository> {
                WeatherRepositoryImpl(get())
            }

            single {
                WeatherApi
            }

            single {
                database.getWeatherDao()
            }
            viewModel {
                WeatherListViewModel(get(), get(), get())
            }
            viewModel {
                DailyForecastViewModel(get(), get())
            }
            viewModel {
                HourlyForecastViewModel(get(), get())
            }
            viewModel {
                AddWeatherLocationViewModel(get(), get(), get())
            }
        }
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}