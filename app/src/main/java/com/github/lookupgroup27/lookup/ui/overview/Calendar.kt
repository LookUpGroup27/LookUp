package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import java.text.SimpleDateFormat
import java.util.*
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    navigationActions: NavigationActions
) {

  var selectedDate by remember { mutableStateOf(Date()) }
  var searchQuery by remember { mutableStateOf("") }
  var showDialog by remember { mutableStateOf(false) }
  var searchResults by remember { mutableStateOf<List<VEvent>>(emptyList()) }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    CalendarHeader(
        selectedDate = selectedDate,
        onPreviousMonth = { selectedDate = updateMonth(selectedDate, -1) },
        onNextMonth = { selectedDate = updateMonth(selectedDate, 1) })

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { showDialog = true }) { Text("Look Up Event") }

    Spacer(modifier = Modifier.height(16.dp))

    CalendarGrid(
        selectedDate = selectedDate,
        onDateSelected = { newDate ->
          searchQuery = ""
          selectedDate = newDate
        },
        calendarViewModel = calendarViewModel)

    Spacer(modifier = Modifier.height(16.dp))

    if (searchQuery.isNotEmpty()) {
      EventListWithResults(
          searchResults = searchResults,
          onEventClick = { eventDate ->
            selectedDate = eventDate
            searchQuery = ""
          })
    } else {
      EventList(calendarViewModel = calendarViewModel, selectedDate = selectedDate)
    }
  }

  if (showDialog) {
    SearchDialog(
        searchQuery = searchQuery,
        onQueryChange = { searchQuery = it },
        onSearch = {
          searchResults =
              calendarViewModel.icalEvents
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
fun EventListWithResults(searchResults: List<VEvent>, onEventClick: (Date) -> Unit) {
  if (searchResults.isNotEmpty()) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Text(
          text = "Search Results:",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 8.dp))

      LazyColumn(modifier = Modifier.fillMaxHeight().padding(bottom = 8.dp)) {
        items(searchResults) { event ->
          val eventDate = event.getProperty<DtStart>(Property.DTSTART)?.date as? Date
          if (eventDate != null) {
            EventItem(event = event, onClick = { onEventClick(eventDate) })
          }
          Spacer(modifier = Modifier.height(10.dp))
        }
      }
    }
  } else {
    Text(
        text = "No matching events found.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
fun CalendarHeader(selectedDate: Date, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
  val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        BasicText(
            text = "<",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.clickable { onPreviousMonth() })

        Text(
            text = monthFormat.format(selectedDate),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.weight(1f))

        BasicText(
            text = ">",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.clickable { onNextMonth() })
      }
}

@Composable
fun CalendarGrid(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit,
    calendarViewModel: CalendarViewModel
) {
  val calendar = Calendar.getInstance().apply { time = selectedDate }
  calendar.set(Calendar.DAY_OF_MONTH, 1)
  val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

  LazyVerticalGrid(
      columns = GridCells.Fixed(7),
      contentPadding = PaddingValues(4.dp),
      modifier = Modifier.fillMaxWidth()) {
        items(daysInMonth) { day ->
          val date =
              Calendar.getInstance()
                  .apply {
                    time = selectedDate
                    set(Calendar.DAY_OF_MONTH, day + 1)
                  }
                  .time

          val hasEvents =
              calendarViewModel.icalEvents.any { event ->
                val dtStart = event.getProperty<DtStart>(Property.DTSTART)?.date
                dtStart != null && isSameDay(dtStart, date)
              }

          val isSelected = isSameDay(date, selectedDate)

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
                      text = (day + 1).toString(),
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
      }
}

@Composable
fun EventList(calendarViewModel: CalendarViewModel, selectedDate: Date) {
  val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
  val formattedDate = dateFormat.format(selectedDate)

  val eventsForDay =
      calendarViewModel.icalEvents.filter { event ->
        val dtStart = event.getProperty<DtStart>(Property.DTSTART)?.date
        dtStart != null && isSameDay(dtStart, selectedDate)
      }

  Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
    Text(
        text = "Events ($formattedDate):",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp))

    if (eventsForDay.isNotEmpty()) {
      eventsForDay.forEach { event ->
        EventItem(event = event, onClick = {})
        Spacer(modifier = Modifier.height(8.dp))
      }
    } else {
      Text(
          text = "No events for today.",
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

fun isSameDay(date1: Date, date2: Date): Boolean {
  val cal1 = Calendar.getInstance().apply { time = date1 }
  val cal2 = Calendar.getInstance().apply { time = date2 }
  return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
      cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun updateMonth(date: Date, months: Int): Date {
  return Calendar.getInstance()
      .apply {
        time = date
        add(Calendar.MONTH, months)
      }
      .time
}
