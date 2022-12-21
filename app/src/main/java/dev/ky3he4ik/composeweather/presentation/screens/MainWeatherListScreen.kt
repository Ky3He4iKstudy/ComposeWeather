package dev.ky3he4ik.composeweather.presentation.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ky3he4ik.composeweather.R
import dev.ky3he4ik.composeweather.model.WeatherDomainObject
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.ErrorScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.LoadingScreen
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.WeatherConditionIcon
import dev.ky3he4ik.composeweather.presentation.viewmodels.MainViewModel
import dev.ky3he4ik.composeweather.presentation.viewmodels.WeatherListState
import dev.ky3he4ik.composeweather.presentation.viewmodels.WeatherListViewModel
import kotlinx.coroutines.launch

@Composable
fun MainWeatherListScreen(
    weatherUiState: WeatherListState,
    retryAction: () -> Unit,
    onClick: (String) -> Unit,
    addWeatherFabAction: () -> Unit,
    weatherListViewModel: WeatherListViewModel,
    mainViewModel: MainViewModel
) {

    LaunchedEffect(Unit) {
        mainViewModel.updateActionBarTitle("places")
    }
    when (weatherUiState) {
        is WeatherListState.Empty -> WeatherListScreen(
            emptyList(),
            onClick,
            addWeatherFabAction,
            weatherListViewModel
        )
        is WeatherListState.Loading -> LoadingScreen()
        is WeatherListState.Success -> WeatherListScreen(
            weatherUiState.weatherDomainObjects,
            onClick,
            addWeatherFabAction,
            weatherListViewModel
        )
        is WeatherListState.Error -> ErrorScreen(
            retryAction,
        )
    }
}


/**
 * The home screen displaying list of weather objects
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun WeatherListScreen(
    weatherDomainObjectList: List<WeatherDomainObject>,
    onClick: (String) -> Unit,
    addWeatherFabAction: () -> Unit,
    weatherListViewModel: WeatherListViewModel,
) {

    Log.e("Weather", weatherDomainObjectList.joinToString { it.toString() })
    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val showScrollToTopButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val showAddWeatherFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    Scaffold(
        floatingActionButton =
        {
            AnimatedVisibility(
                visible = showAddWeatherFab,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                AddWeatherFab(
                    onClick = addWeatherFabAction
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->

        Box {

            if (weatherDomainObjectList.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Press Add",
                        fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(4.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(weatherDomainObjectList) { item ->

                    WeatherListItem(
                        weatherDomainObject = item,
                        onClick = onClick,
                        viewModel = weatherListViewModel
                    )
                }
            }

            AnimatedVisibility(
                visible = showScrollToTopButton,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = scaleIn(),
                exit = scaleOut()
            ) {

                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0, 0)
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_expand_less_24),
                        contentDescription = "to top"
                    )
                }
            }
        }
    }


}

@Composable
fun AddWeatherFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(size = 18.dp),
        modifier = Modifier
            .size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add weather"
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListItem(
    weatherDomainObject: WeatherDomainObject,
    onClick: (String) -> Unit,
    viewModel: WeatherListViewModel
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(175.dp)
            .fillMaxWidth()
                onClick = { onClick(weatherDomainObject.location_coord) },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Row {
                    Button(onClick = { viewModel.deleteWeather(weatherDomainObject.location_coord) }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_baseline_delete_forever_24),
                            contentDescription = "Remove"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)

                ) {
                    Column(modifier = Modifier.weight(10f)) {
                        Text(
                            text = weatherDomainObject.location,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                        )
                        Text(
                            text = weatherDomainObject.country,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = weatherDomainObject.conditionText,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(.1f))

                    Column(modifier = Modifier.weight(7.5f)) {
                        Text(
                            text = "${weatherDomainObject.temp}\u00B0",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = weatherDomainObject.time,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = "Feels like: ${weatherDomainObject.feelsLikeTemp}\u00B0",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Wind: ${weatherDomainObject.windSpeed} km/h",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Humidity: ${weatherDomainObject.humidity}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    WeatherConditionIcon(iconUrl = weatherDomainObject.imgSrcUrl, iconSize = 64)
                }
            }
        }
    }
}
