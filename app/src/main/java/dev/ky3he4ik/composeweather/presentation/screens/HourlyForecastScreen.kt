package dev.ky3he4ik.composeweather.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ky3he4ik.composeweather.model.Hours
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.ErrorScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.LoadingScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.WeatherConditionIcon
import dev.ky3he4ik.composeweather.presentation.viewmodels.*
import org.koin.androidx.compose.getViewModel

@Composable
fun HourlyForecastScreen(
    location: String,
    date: String,
    mainViewModel: MainViewModel
) {
    val hourlyForecastViewModel = getViewModel<HourlyForecastViewModel>()
    LaunchedEffect(Unit) {
        mainViewModel.updateActionBarTitle(date)
    }
    val context = LocalContext.current
    val uiState = remember {
        hourlyForecastViewModel.getHourlyForecast(
            location
        )
    }.collectAsState()

    when (uiState.value) {
        is HourlyForecastViewData.Loading -> LoadingScreen()
        is HourlyForecastViewData.Done -> HourlyForecastList(
            (uiState.value as HourlyForecastViewData.Done).forecastDomainObject.days.first { it.dayOfWeek == date }.hours,
        )
        is HourlyForecastViewData.Error -> ErrorScreen { hourlyForecastViewModel.refresh() }
    }
}


@Composable
fun HourlyForecastList(
    hoursList: List<Hours>,
) {
    val listState = rememberLazyListState()

    Box {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp),
            state = listState
        ) {
            items(hoursList) { hourDomainObject ->
                HourlyForecastListItem(
                    hourDomainObject,
                )
            }
        }
    }
}


@Composable
fun HourlyForecastListItem(
    hour: Hours,
) {
    Card(
        modifier = Modifier
            .padding(8.dp),
    ) {
        Box {

            Column(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.weight(3f)) {
                        Text(
                            text = hour.time,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = hour.condition.text,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(.5f))

                    Row {
                        Text(
                            text = "${hour.temp_c.toInt()}\u00B0",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )


                    }
                    Spacer(modifier = Modifier.weight(1f))
                    WeatherConditionIcon(iconUrl = hour.condition.icon, iconSize = 64)
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Wind: ${hour.wind_kph} km/h (${hour.wind_dir})")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Pressure: ${hour.pressure_mb} mmHg")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Precipitations: ${hour.precip_mm} mm")
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

