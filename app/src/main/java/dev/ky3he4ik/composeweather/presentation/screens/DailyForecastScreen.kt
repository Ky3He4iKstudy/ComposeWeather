package dev.ky3he4ik.composeweather.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ky3he4ik.composeweather.model.Days
import dev.ky3he4ik.composeweather.model.ForecastDomainObject
import dev.ky3he4ik.composeweather.presentation.animations.pressClickEffect
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.ErrorScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.LoadingScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.WeatherConditionIcon
import dev.ky3he4ik.composeweather.presentation.viewmodels.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.getViewModel


@Composable
fun DailyForecastScreen(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    location: String,
    mainViewModel: MainViewModel
) {
    val dailyForecastViewModel = getViewModel<DailyForecastViewModel>()
    // update title bar

    // This only seems to work if I pass the viewmodel all the way down from main activity and only have one instance of main view model, grabbing it from Koin doesnt work
    LaunchedEffect(Unit) {
        mainViewModel.updateActionBarTitle(
            dailyForecastViewModel.getWeather(location).firstOrNull()?.cityName ?: "ERROR"
        )
    }
    val state by remember {
        dailyForecastViewModel.getForecast(
            location,
        )
    }.collectAsState()

    when (state) {
        is ForecastViewData.Loading -> LoadingScreen(modifier)
        is ForecastViewData.Done -> {
            ForecastList(
                (state as ForecastViewData.Done).forecastDomainObject,
                modifier,
                onClick,
            )
        }
        is ForecastViewData.Error -> ErrorScreen({ dailyForecastViewModel.refresh() }, modifier)
    }
}


/**
 * Screen displaying Daily Forecast
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastList(
    forecast: ForecastDomainObject,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    Scaffold { innerPadding ->

        Box {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentPadding = innerPadding
            ) {
                items(forecast.days) {
                    ForecastListItem(
                        it,
                        onClick = onClick,
                    )
                }
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastListItem(
    days: Days,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    val date = days.dayOfWeek
    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(125.dp)
            .pressClickEffect(),
        onClick = { onClick(date) },
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.Center)
            ) {
                Column(modifier = modifier.weight(6.5f)) {
                    Text(
                        text = days.dayOfWeek,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = days.date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = days.day.condition.text,
                        fontSize = 18.sp,
                        //maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.weight(.5f))

                Column(modifier = Modifier.weight(8f)) {

                    Row(
                        modifier = modifier.padding(top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "${days.day.mintemp_c.toInt()}\u00B0 ·",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " ${days.day.maxtemp_c.toInt()}\u00B0",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }

                    if (days.day.daily_chance_of_rain > 0)
                        Text(
                            text = "Rain: ${days.day.daily_chance_of_rain}%",
                            textAlign = TextAlign.Center
                        )
                    if (days.day.daily_chance_of_snow > 0)
                        Text(
                            text = "Snow: ${days.day.daily_chance_of_snow}%",
                            textAlign = TextAlign.Center
                        )
                    Text(
                        text = "Temp: ${days.day.avgtemp_f}\u00B0",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Humidity: ${days.day.avghumidity}%",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Sun day: ${days.astroData.sunrise}-${days.astroData.sunset}",
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(.5f))
                WeatherConditionIcon(iconUrl = days.day.condition.icon, iconSize = 64)
            }
        }


    }
}
