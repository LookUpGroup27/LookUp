package com.github.lookupgroup27.lookup.ui.calendar

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.lookupgroup27.lookup.ui.calendar.components.CalendarGrid
import com.github.lookupgroup27.lookup.ui.calendar.components.CalendarHeader
import com.github.lookupgroup27.lookup.ui.calendar.components.EventList
import com.github.lookupgroup27.lookup.ui.calendar.components.SearchDialog
import com.github.lookupgroup27.lookup.ui.navigation.BottomNavigationMenu
import com.github.lookupgroup27.lookup.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.navigation.Route
import com.github.lookupgroup27.lookup.util.DateUtils.isSameDay
import com.github.lookupgroup27.lookup.util.DateUtils.updateMonth
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.*

/**
 * CalendarScreen is the main calendar interface, providing a monthly view with navigation options,
 * an event list for selected dates, and a search feature to locate specific events by name.
 *
 * This composable displays the calendar's month view, allows users to navigate between months, and
 * view events associated with specific dates. It also includes a search functionality to quickly
 * find events by title. The screen is structured with a header, navigation controls, and an event
 * list that updates based on user interactions.
 *
 * @param calendarViewModel ViewModel that provides access to calendar events and manages the state
 *   of the calendar, including the currently selected date and list of events.
 * @param navigationActions Interface for navigation between screens.
 */
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    navigationActions: NavigationActions
) {
  val context = LocalContext.current
  // Lock the screen orientation to portrait mode.
  DisposableEffect(Unit) {
    val activity = context as? ComponentActivity
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose { activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
  }

  // State variables for selected date, search query, and visibility of the search dialog.
  var selectedDate by remember { mutableStateOf(Date()) }
  var searchQuery by remember { mutableStateOf("") }
  var showDialog by remember { mutableStateOf(false) }
  var searchResults by remember { mutableStateOf<List<VEvent>>(emptyList()) }
  val user = FirebaseAuth.getInstance().currentUser
  val isUserLoggedIn = user != null

  // Observing events from ViewModel.
  val icalEvents by calendarViewModel.icalEvents.collectAsState()

  Scaffold(
      modifier = Modifier.testTag("calendar_screen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            isUserLoggedIn = isUserLoggedIn,
            selectedItem = Route.CALENDAR)
      },
      content = { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
          // Displays the month/year header and month navigation controls.
          CalendarHeader(
              selectedDate = selectedDate,
              onPreviousMonth = { selectedDate = updateMonth(selectedDate, -1) },
              onNextMonth = { selectedDate = updateMonth(selectedDate, 1) })

          Spacer(modifier = Modifier.height(16.dp))

          // Search button to trigger event lookup dialog.
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(
                modifier = Modifier.testTag("look_up_event"), onClick = { showDialog = true }) {
                  Icon(imageVector = Icons.Default.Search, contentDescription = "Look Up Event")
                }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Calendar grid for displaying individual days in the month, with event indicators.
          CalendarGrid(
              selectedDate = selectedDate,
              icalEvents = icalEvents,
              onDateSelected = { newDate ->
                searchQuery = ""
                selectedDate = newDate
              })

          Spacer(modifier = Modifier.height(16.dp))

          // Display either search results or events for the selected day.
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

        // Dialog for searching events by title.
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
