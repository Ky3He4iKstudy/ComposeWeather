package dev.ky3he4ik.composeweather.presentation.reusablecomposables

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AutoCompleteTextView(
    query: String,
    queryLabel: String,
    onQueryChanged: (String) -> Unit,
    searchResults: List<T>,
    onDoneActionClick: () -> Unit,
    onClearClick: () -> Unit,
    onItemClick: (T) -> Unit,
    itemContent: @Composable (T) -> Unit
) {

    val view = LocalView.current
    val lazyListState = rememberLazyListState()
    QuerySearch(
        query = query,
        label = queryLabel,
        onQueryChanged = onQueryChanged,
        onDoneActionClick = {
            onDoneActionClick()
        },
        onClearClick = {
            onClearClick()
        }
    )
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxWidth()
            .heightIn(max = TextFieldDefaults.MinHeight * 6)
            .animateContentSize()
    ) {
        if (searchResults.isNotEmpty()) {
            items(searchResults) { place ->
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(place)
                            view.clearFocus()
                            view.hideKeyboard()
                        }
                ) {
                    itemContent(place)
                }
                Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
