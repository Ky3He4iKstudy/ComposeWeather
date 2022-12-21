package dev.ky3he4ik.composeweather.presentation.reusablecomposables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ky3he4ik.composeweather.R
import dev.ky3he4ik.composeweather.presentation.animations.pressClickEffect

/**
 * The screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = ""
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            text = "Fail"
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            modifier = Modifier.pressClickEffect(),
            onClick = retryAction) {
            Text("Retry")
        }
    }


}