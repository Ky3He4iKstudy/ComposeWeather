package dev.ky3he4ik.composeweather.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.ky3he4ik.composeweather.presentation.navigation.MainWeatherList.dateArg
import dev.ky3he4ik.composeweather.presentation.navigation.MainWeatherList.locationArg


/**
 * Contract for information needed on every navigation destination
 */
interface NavDestinations {
    val route: String
}

object MainWeatherList: NavDestinations {
    override val route = "weatherList"
    const val locationArg = "location"
    const val dateArg = "date"
    val arguments = listOf(
        navArgument(locationArg) { type = NavType.StringType },
        navArgument(dateArg) { type = NavType.StringType }
    )
}

object AddLocation: NavDestinations {
    override val route = "addLocation"
    val routeWithArgs = "$route/{${locationArg}}"
}

object DailyForecast: NavDestinations {
    override val route = "dailyForecast"
    val routeWithArgs = "$route/{${locationArg}}"
}

object HourlyForecast: NavDestinations {
    override val route = "hourlyForecast"
    val routeWithArgs = "$route/{${locationArg}}/{${dateArg}}"
}

object Alerts: NavDestinations {
    override val route = "alerts"
    val routeWithArgs = "$route/{${locationArg}}"
}


//val screens = listOf(
//    MainWeatherList.route,
//    DailyForecast.route,
//    HourlyForecast.route,
//    AddLocation.route,
//    InterfaceMenu.route,
//    Alerts.route,
//    NotificationsMenu.route
//)