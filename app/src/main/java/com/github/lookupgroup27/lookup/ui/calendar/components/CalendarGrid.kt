package com.github.lookupgroup27.lookup.ui.calendar.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.lookupgroup27.lookup.util.DateUtils.isSameDay
import java.util.Calendar
import java.util.Date
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStart

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
