package dev.ky3he4ik.composeweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.ky3he4ik.composeweather.presentation.WeatherApp
import dev.ky3he4ik.composeweather.presentation.screens.ShowPermissionComposable
import dev.ky3he4ik.composeweather.presentation.viewmodels.MainViewModel
import dev.ky3he4ik.composeweather.presentation.viewmodels.WeatherListViewModel
import dev.ky3he4ik.composeweather.ui.theme.ComposeWeatherTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeWeatherTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowPermissionComposable()

                    val mainViewModel = getViewModel<MainViewModel>()
                    val weatherListViewModel = getViewModel<WeatherListViewModel>()
                    WeatherApp(weatherListViewModel, mainViewModel)
                }
            }
        }
    }
}
