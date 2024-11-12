package com.github.lookupgroup27.lookup.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Displays the header of the calendar, showing the current month and year, with controls to
 * navigate to the previous and next months.
 *
 * @param selectedDate The date currently selected, used to display the month and year.
 * @param onPreviousMonth Callback function to invoke when navigating to the previous month.
 * @param onNextMonth Callback function to invoke when navigating to the next month.
 */
@Composable
fun CalendarHeader(selectedDate: Date, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
  val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        // Button to navigate to the previous month
        IconButton(modifier = Modifier.testTag("Previous_month"), onClick = onPreviousMonth) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
              contentDescription = "Previous Month",
              tint = MaterialTheme.colorScheme.onSurface)
        }

        // Display of the current month and year
        Text(
            modifier = Modifier.weight(1f).testTag("calendar_header"),
            text = monthFormat.format(selectedDate),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge)

        // Button to navigate to the next month
        IconButton(modifier = Modifier.testTag("Next_month"), onClick = onNextMonth) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
              contentDescription = "Next Month",
              tint = MaterialTheme.colorScheme.onSurface)
        }
      }
}
