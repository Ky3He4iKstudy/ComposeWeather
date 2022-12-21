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
    val database: WeatherDatabase by lazy { WeatherDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()


        val appModule = module {
            // Singleton for the Weather Repository implementation passed to all viewmodels
            single<WeatherRepository> {
                WeatherRepositoryImpl(get())
            }

            single {
                WeatherApi
            }

            single {
                database.getWeatherDao()
            }


            // Use factory to create multiple instances for each viewmodel
            viewModel {
                MainViewModel()
            }
            viewModel {
                WeatherListViewModel(get(), get(), get())
            }
            viewModel {
                DailyForecastViewModel(get(), get(), get())
            }
            viewModel {
                HourlyForecastViewModel(get(), get(), get())
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