package dev.ky3he4ik.composeweather.presentation.reusablecomposables

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.ky3he4ik.composeweather.R

@Composable
fun WeatherConditionIcon(
    iconUrl: String,
    iconSize: Int
) {
    AsyncImage(
        modifier = Modifier.size(iconSize.dp),
        model = ImageRequest.Builder(context = LocalContext.current)
            .data("https:$iconUrl")
            .crossfade(true)
            .build(),
        error = painterResource(R.drawable.ic_broken_image),
        placeholder = painterResource(R.drawable.loading_img),
        contentDescription = "weather condition",
        contentScale = ContentScale.FillBounds,
    )
}
