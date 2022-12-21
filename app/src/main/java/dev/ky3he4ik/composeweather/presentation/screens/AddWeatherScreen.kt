package dev.ky3he4ik.composeweather.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ky3he4ik.composeweather.R
import dev.ky3he4ik.composeweather.presentation.reusablecomposables.AutoCompleteTextView
import dev.ky3he4ik.composeweather.presentation.viewmodels.AddWeatherLocationViewModel
import dev.ky3he4ik.composeweather.presentation.viewmodels.SearchViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddWeatherScreen(
    navAction: () -> Unit
) {
    val addWeatherLocationViewModel = getViewModel<AddWeatherLocationViewModel>()
    val coroutineScope = rememberCoroutineScope()
    var location by remember { mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchResults by addWeatherLocationViewModel.getSearchResults.collectAsState()
    var itemClicked by remember { mutableStateOf(false) }
    var isCoord by remember { mutableStateOf(false) }
    var lat by remember { mutableStateOf("") }
    var long by remember { mutableStateOf("") }
    val coordRegex = "^.*?(?<num>-?\\d+\\.?\\d*).*?\$".toRegex() // I hope this works

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(32.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Get by city name", modifier = Modifier.weight(1f))
                Switch(
                    checked = isCoord,
                    onCheckedChange = {
                        isCoord = it
                        itemClicked = isCoord
                    },
                    modifier = Modifier.weight(.5f)
                )
                Text(text = "Get by coordinates", modifier = Modifier.weight(1f))
            }

            AnimatedVisibility(visible = !isCoord) {
                Column(

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    AutoCompleteTextView(
                        query = location,
                        queryLabel = "Search",
                        searchResults =
                        // This will clear results if query is cleared with backspace key
                        if (location.isBlank()) {
                            emptyList()
                        } else {
                            when (searchResults) {
                                is SearchViewData.Done -> (searchResults as SearchViewData.Done).searchResults
                                is SearchViewData.Loading -> emptyList()
                                is SearchViewData.Error -> emptyList()
                            }
                        },
                        onClearClick = {
                            itemClicked = false
                            location = ""
                            addWeatherLocationViewModel.clearQueryResults()
                        },
                        onDoneActionClick = { keyboardController?.hide() },
                        onItemClick = {
                            itemClicked = true
                            location = it
                            addWeatherLocationViewModel.clearQueryResults()
                        },
                        onQueryChanged = { updatedSearch ->
                            if (updatedSearch.length >= 3) {
                                addWeatherLocationViewModel.setQuery(updatedSearch)
                            } else if (updatedSearch.isBlank()) {
                                itemClicked = false
                                addWeatherLocationViewModel.clearQueryResults()
                            }
                            location = updatedSearch
                        },
                        itemContent = {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                modifier = Modifier.animateContentSize()
                            )
                        }
                    )
                }
            }
            AnimatedVisibility(visible = isCoord) {
                Column(

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    TextField(value = lat, onValueChange = {
                        lat = coordRegex.find(it)?.groupValues?.get(1) ?: ""
                    }, label = {
                        Text("Latitude")
                    })
                    TextField(value = long, onValueChange = {
                        long = coordRegex.find(it)?.groupValues?.get(1) ?: ""
                    }, label = { Text("Longitude") })
                    Button(onClick = {
                        val loc =
                            addWeatherLocationViewModel.getCurrentLocation(context) ?: return@Button
                        lat = loc.latitude.toString()
                        long = loc.longitude.toString()
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_baseline_gps_fixed_24),
                            contentDescription = "GPS location"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(120.dp))

            Button(
                onClick = {
                    if (itemClicked) {
                        if (isCoord)
                            location = "${lat},${long}"
                        coroutineScope.launch(Dispatchers.IO) {
                            addWeather(
                                navAction,
                                addWeatherLocationViewModel,
                                location,
                                context
                            )
                        }
                    }
                },
            ) {
                Text("Save")
            }

        }
    }
}


private suspend fun addWeather(
    navAction: () -> Unit,
    viewModel: AddWeatherLocationViewModel,
    location: String,
    context: Context
) {
    if (location.isNotBlank()) {
        if (!viewModel.storeNetworkDataInDatabase(location)) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Cannot add location, check network or location",
                    Toast.LENGTH_SHORT
                )
            }
        }

        //Navigate back to main screen
        withContext(Dispatchers.Main) {
            run(navAction)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddWeatherPreview() {
    AddWeatherScreen {

    }
}
