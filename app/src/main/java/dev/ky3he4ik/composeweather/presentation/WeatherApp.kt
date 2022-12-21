package dev.ky3he4ik.composeweather.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ky3he4ik.composeweather.presentation.navigation.AddLocation
import dev.ky3he4ik.composeweather.presentation.navigation.DailyForecast
import dev.ky3he4ik.composeweather.presentation.navigation.HourlyForecast
import dev.ky3he4ik.composeweather.presentation.navigation.MainWeatherList
import dev.ky3he4ik.composeweather.presentation.screens.AddWeatherScreen
import dev.ky3he4ik.composeweather.presentation.screens.DailyForecastScreen
import dev.ky3he4ik.composeweather.presentation.screens.HourlyForecastScreen
import dev.ky3he4ik.composeweather.presentation.screens.MainWeatherListScreen
import dev.ky3he4ik.composeweather.presentation.viewmodels.MainViewModel
import dev.ky3he4ik.composeweather.presentation.viewmodels.WeatherListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(
    weatherListViewModel: WeatherListViewModel,
    mainViewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainWeatherList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = MainWeatherList.route,
                arguments = MainWeatherList.arguments
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val weatherUiState by remember { weatherListViewModel.getAllWeather() }
                        .collectAsState()

                    MainWeatherListScreen(
                        weatherUiState = weatherUiState,
                        retryAction = { weatherListViewModel.refresh() },
                        onClick = { location -> navController.navigateToDailyForecast(location) },
                        addWeatherFabAction = { navController.navigate(AddLocation.route) },
                        weatherListViewModel = weatherListViewModel,
                        mainViewModel = mainViewModel
                    )
                }
            }

            composable(route = AddLocation.route) {
                AddWeatherScreen(
                    navAction = { navController.popBackStack() }
                )
            }

            composable(
                route = DailyForecast.routeWithArgs,
            ) { navBackStackEntry ->
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val location =
                        navBackStackEntry.arguments?.getString(MainWeatherList.locationArg)
                    location?.let {
                        DailyForecastScreen(
                            onClick = { date ->
                                navController.navigateToHourlyForecast(
                                    location,
                                    date
                                )
                            },
                            location = location,
                            mainViewModel = mainViewModel
                        )
                    }
                }
            }

            composable(route = HourlyForecast.routeWithArgs) { navBackStackEntry ->
                val location =
                    navBackStackEntry.arguments?.getString(MainWeatherList.locationArg)
                val date = navBackStackEntry.arguments?.getString(MainWeatherList.dateArg)
                if (date != null && location != null) {
                    HourlyForecastScreen(
                        date = date,
                        location = location,
                        mainViewModel = mainViewModel
                    )
                }

            }
        }
    }


}

private fun NavHostController.navigateToDailyForecast(location: String) {
    this.navigate("${DailyForecast.route}/$location")
}

private fun NavHostController.navigateToHourlyForecast(location: String, date: String) {
    this.navigate("${HourlyForecast.route}/$location/$date")
}

