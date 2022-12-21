package dev.ky3he4ik.composeweather.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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


/**
 * Main entry point composable for app
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(
    weatherListViewModel: WeatherListViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    val ctx = LocalContext.current
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route
    val locationsInDatabase =
        weatherListViewModel.getLocationsFromDatabase().collectAsState(initial = "")

    // Get the app bar title from the main view model
    val title by mainViewModel.title.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    /**
     * These dialog states should live in the screens themeselves?
     * otherwise this seems to be like a lot states to track here.
     * Initial goal was to hoist as many states as possible to this location to keep sub
     * composables stateless
     */

    val openAboutDialog = remember { mutableStateOf(false) }
    val openTemperatureUnitDialog = remember { mutableStateOf(false) }
    val openDateUnitDialog = remember { mutableStateOf(false) }
    val openClockFormatDialog = remember { mutableStateOf(false) }
    val openWindspeedDialog = remember { mutableStateOf(false) }
    val openMeasurementDialog = remember { mutableStateOf(false) }
    val openLocationOverflowMenu = remember { mutableStateOf(false) }



    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainWeatherList.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(
                route = MainWeatherList.route,
                arguments = MainWeatherList.arguments
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val weatherUiState by remember { weatherListViewModel.getAllWeather() }
                        .collectAsState()

                    MainWeatherListScreen(
                        weatherUiState = weatherUiState,
                        retryAction = { weatherListViewModel.refresh() },
                        modifier = modifier,
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
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val location =
                        navBackStackEntry.arguments?.getString(MainWeatherList.locationArg)
                    location?.let {
                        DailyForecastScreen(
                            modifier = modifier,
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
                        modifier = modifier,
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

