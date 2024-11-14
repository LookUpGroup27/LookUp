package com.github.lookupgroup27.lookup.ui.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.Summary

/**
 * Displays a single event item within a card format, showing the event's title and date.
 *
 * @param event The VEvent instance containing event data such as title and date.
 * @param onClick Lambda function triggered when the item is clicked.
 */
@Composable
fun EventItem(event: VEvent, onClick: () -> Unit) {
  // Extracts the event title or sets a default if not found
  val eventTitle = event.getProperty<Summary>(Property.SUMMARY)?.value ?: "Unnamed Event"
  // Extracts and formats the event date or sets a default if not found
  val eventDate = event.getProperty<DtStart>(Property.DTSTART)?.date as? Date
  val formattedDate =
      eventDate?.let { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it) }
          ?: "Unknown date"

  // Card container for displaying event details
  Card(
      modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
      elevation = CardDefaults.cardElevation(4.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          // Display event title
          Text(
              text = eventTitle,
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(4.dp))

          // Display formatted event date
          Text(
              text = "Date: $formattedDate",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
}
