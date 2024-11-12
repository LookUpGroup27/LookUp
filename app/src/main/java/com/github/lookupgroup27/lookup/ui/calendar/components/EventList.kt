package com.github.lookupgroup27.lookup.ui.calendar.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Date
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart

/**
 * Displays a list of events with a title. If no events are available, shows a message.
 *
 * @param title The title displayed at the top of the list.
 * @param events A list of VEvent items to display.
 * @param onEventClick Callback for when an event is clicked, passing the event's start date.
 */
@Composable
fun EventList(title: String, events: List<VEvent>, onEventClick: (Date) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("list_event")) {
    // Displays the title for the list
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp))

    // Displays events if available; otherwise, shows a "No events" message
    if (events.isNotEmpty()) {
      LazyColumn(modifier = Modifier.fillMaxHeight().padding(bottom = 8.dp)) {
        items(events) { event ->
          val eventDate = event.getProperty<DtStart>(Property.DTSTART)?.date as? Date
          if (eventDate != null) {
            // Render each event item and trigger onEventClick with the event date
            EventItem(event = event, onClick = { onEventClick(eventDate) })
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    } else {
      // Displayed when there are no events in the list
      Text(
          text = "No events.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
