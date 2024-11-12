package com.github.lookupgroup27.lookup.ui.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Date

/**
 * Represents a single day cell within a calendar grid. Displays the day number and, if applicable,
 * a marker for days with events. The cell also indicates if it is selected.
 *
 * @param day The numeric day of the month to display.
 * @param date The full date corresponding to this day cell.
 * @param isSelected Boolean flag indicating if the day cell is selected.
 * @param hasEvents Boolean flag indicating if there are events on this date.
 * @param onDateSelected Callback function to invoke when the day cell is clicked, passing the
 *   selected date.
 */
@Composable
fun DayCell(
    day: Int,
    date: Date,
    isSelected: Boolean,
    hasEvents: Boolean,
    onDateSelected: (Date) -> Unit
) {
  Box(
      modifier =
          Modifier.size(48.dp)
              .background(
                  if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                  else MaterialTheme.colorScheme.surface)
              .clickable { onDateSelected(date) },
      contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          // Display the numeric day of the month
          Text(
              text = day.toString(),
              textAlign = TextAlign.Center,
          )

          // Display an indicator if the day has events
          if (hasEvents) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier =
                    Modifier.size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape))
          }
        }
      }
}
