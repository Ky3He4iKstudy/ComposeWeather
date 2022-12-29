package dev.ky3he4ik.composeweather.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ky3he4ik.composeweather.data.local.model.Hours
import dev.ky3he4ik.composeweather.data.local.model.WeatherCondition
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.ErrorScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.LoadingScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.WeatherConditionIcon
import dev.ky3he4ik.composeweather.presentation.viewmodels.HourlyForecastViewData
import dev.ky3he4ik.composeweather.presentation.viewmodels.HourlyForecastViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun HourlyForecastScreen(
    location: String,
    date: String,
) {
    val hourlyForecastViewModel = getViewModel<HourlyForecastViewModel>()
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
        is HourlyForecastViewData.Error -> ErrorScreen(
            { hourlyForecastViewModel.refresh() },
            "Can't get forecast"
        )
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

@Preview()
@Composable
fun ShowHourlyPreviw() {
    HourlyForecastListItem(
        hour = Hours(
            1234567,
            "12:34",
            3.0,
            4.0,
            3,
            WeatherCondition("blabla", "//cdn.weatherapi.com/weather/64x64/day/116.png", 1003),
            5.0,
            6.0,
            "W",
            42,
            7.0,
            8.0,
            9,
            10.0,
            11,
            12.0,
            13.0,
            14.0,
            15.0,
            16.0,
            17.0
        )
    )
}
