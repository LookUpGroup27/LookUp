package com.github.lookupgroup27.lookup.ui.calendar.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A dialog component to search for events by name.
 *
 * @param searchQuery The current text input for the search query.
 * @param onQueryChange A lambda to handle updates to the search query text.
 * @param onSearch A lambda triggered when the user confirms the search action.
 * @param onDismiss A lambda triggered to close the dialog without performing a search.
 */
@Composable
fun SearchDialog(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Look Up Event") },
      text = {
        Column {
          // Input field for entering the event name
          OutlinedTextField(
              value = searchQuery,
              onValueChange = onQueryChange,
              label = { Text("Enter event name") },
              modifier = Modifier.fillMaxWidth())
        }
      },
      // Button to confirm the search action
      confirmButton = { Button(onClick = onSearch) { Text("Search") } },
      // Button to cancel and close the dialog
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
