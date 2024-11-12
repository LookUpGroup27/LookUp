package com.github.lookupgroup27.lookup.ui.calendar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.util.DateUtils.isSameDay
import com.github.lookupgroup27.lookup.util.DateUtils.updateMonth
import java.text.SimpleDateFormat
import java.util.*
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    navigationActions: NavigationActions
) {
  var selectedDate by remember { mutableStateOf(Date()) }
  var searchQuery by remember { mutableStateOf("") }
  var showDialog by remember { mutableStateOf(false) }
  var searchResults by remember { mutableStateOf<List<VEvent>>(emptyList()) }

  val icalEvents by calendarViewModel.icalEvents.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("calendar_screen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = Route.CALENDAR)
      },
      content = { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
          CalendarHeader(
              selectedDate = selectedDate,
              onPreviousMonth = { selectedDate = updateMonth(selectedDate, -1) },
              onNextMonth = { selectedDate = updateMonth(selectedDate, 1) })

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(
                modifier = Modifier.testTag("look_up_event"), onClick = { showDialog = true }) {
                  Icon(imageVector = Icons.Default.Search, contentDescription = "Look Up Event")
                }
          }

          Spacer(modifier = Modifier.height(16.dp))

          CalendarGrid(
              selectedDate = selectedDate,
              icalEvents = icalEvents,
              onDateSelected = { newDate ->
                searchQuery = ""
                selectedDate = newDate
              })

          Spacer(modifier = Modifier.height(16.dp))

          if (searchQuery.isNotEmpty()) {
            EventList(
                title = "Search Results",
                events = searchResults,
                onEventClick = { eventDate ->
                  selectedDate = eventDate
                  searchQuery = ""
                })
          } else {
            val eventsForDay =
                icalEvents.filter { event ->
                  val dtStart = event.getProperty<DtStart>(Property.DTSTART)?.date
                  dtStart != null && isSameDay(dtStart, selectedDate)
                }
            EventList(
                title =
                    "Events for ${SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(selectedDate)}",
                events = eventsForDay,
                onEventClick = {})
          }
        }

        if (showDialog) {
          SearchDialog(
              searchQuery = searchQuery,
              onQueryChange = { searchQuery = it },
              onSearch = {
                searchResults =
                    icalEvents
                        .filter { event ->
                          val eventTitle = event.getProperty<Summary>(Property.SUMMARY)?.value ?: ""
                          eventTitle.contains(searchQuery, ignoreCase = true)
                        }
                        .sortedBy { event ->
                          (event.getProperty<DtStart>(Property.DTSTART)?.date as? Date)?.time
                              ?: Long.MAX_VALUE
                        }
                showDialog = false
              },
              onDismiss = { showDialog = false })
        }
      })
}

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
          OutlinedTextField(
              value = searchQuery,
              onValueChange = onQueryChange,
              label = { Text("Enter event name") },
              modifier = Modifier.fillMaxWidth())
        }
      },
      confirmButton = { Button(onClick = onSearch) { Text("Search") } },
      dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
fun CalendarHeader(selectedDate: Date, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
  val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        IconButton(modifier = Modifier.testTag("Previous_month"), onClick = onPreviousMonth) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
              contentDescription = "Previous Month",
              tint = MaterialTheme.colorScheme.onSurface)
        }

        Text(
            modifier = Modifier.weight(1f).testTag("calendar_header"),
            text = monthFormat.format(selectedDate),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge)

        IconButton(modifier = Modifier.testTag("Next_month"), onClick = onNextMonth) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
              contentDescription = "Next Month",
              tint = MaterialTheme.colorScheme.onSurface)
        }
      }
}

@Composable
fun CalendarGrid(selectedDate: Date, icalEvents: List<VEvent>, onDateSelected: (Date) -> Unit) {
  val calendar = Calendar.getInstance().apply { time = selectedDate }
  calendar.set(Calendar.DAY_OF_MONTH, 1)
  val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

  LazyVerticalGrid(
      columns = GridCells.Fixed(7),
      contentPadding = PaddingValues(4.dp),
      modifier = Modifier.fillMaxWidth().testTag("calendar_grid")) {
        items(daysInMonth) { day ->
          val date =
              Calendar.getInstance()
                  .apply {
                    time = selectedDate
                    set(Calendar.DAY_OF_MONTH, day + 1)
                  }
                  .time

          val hasEvents =
              icalEvents.any { event ->
                val dtStart = event.getProperty<DtStart>(Property.DTSTART)?.date
                dtStart != null && isSameDay(dtStart, date)
              }

          val isSelected = isSameDay(date, selectedDate)

          DayCell(
              day = day + 1,
              date = date,
              isSelected = isSelected,
              hasEvents = hasEvents,
              onDateSelected = onDateSelected)
        }
      }
}

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
          Text(
              text = day.toString(),
              textAlign = TextAlign.Center,
          )

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

@Composable
fun EventList(title: String, events: List<VEvent>, onEventClick: (Date) -> Unit) {
  Column(modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("list_event")) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp))

    if (events.isNotEmpty()) {
      LazyColumn(modifier = Modifier.fillMaxHeight().padding(bottom = 8.dp)) {
        items(events) { event ->
          val eventDate = event.getProperty<DtStart>(Property.DTSTART)?.date as? Date
          if (eventDate != null) {
            EventItem(event = event, onClick = { onEventClick(eventDate) })
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    } else {
      Text(
          text = "No events.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun EventItem(event: VEvent, onClick: () -> Unit) {
  val eventTitle = event.getProperty<Summary>(Property.SUMMARY)?.value ?: "Unnamed Event"
  val eventDate = event.getProperty<DtStart>(Property.DTSTART)?.date as? Date
  val formattedDate =
      eventDate?.let { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it) }
          ?: "Unknown date"

  Card(
      modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
      elevation = CardDefaults.cardElevation(4.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Text(
              text = eventTitle,
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(4.dp))

          Text(
              text = "Date: $formattedDate",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
}
