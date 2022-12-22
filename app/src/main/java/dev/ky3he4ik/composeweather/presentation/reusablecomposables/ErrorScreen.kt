package dev.ky3he4ik.composeweather.presentation.reusablecomposables

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    message: String?,
) {
    Log.e("WeatherApp", message ?: "Unknown error")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = "Fail"
        )
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = message ?: "Unknown error"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = retryAction
        ) {
            Text("Retry")
        }
    }


}