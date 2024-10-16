package com.github.lookupgroup27.lookup.model.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.io.IOException
import java.io.StringReader
import java.util.Calendar as JavaCalendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.ParserException
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Date
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.Period
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.RRule
import net.fortuna.ical4j.util.MapTimeZoneCache
import okhttp3.OkHttpClient

class CalendarViewModel(private val icalRepository: IcalRepository) : ViewModel() {

  companion object {

    private const val ICAL_URL =
        "https://p127-caldav.icloud.com/published/2/MTE0OTM4OTk2MTExNDkzOIzDRaBjEGa9_1mlmgjzcdlka5HK6EzMiIdOswU-0rZBZMDBibtH_M7CDyMpDQRQJPdGOSM0hTsS2qFNGOObsTc"

    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(HttpIcalRepository(OkHttpClient())) as T
          }
        }
  }

  private val _icalEvents = MutableStateFlow<List<VEvent>>(emptyList())
  val icalEvents: StateFlow<List<VEvent>> = _icalEvents

  init {
    System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)
    fetchICalData()
  }

  fun fetchICalData() {
    viewModelScope.launch {
      val icalData = icalRepository.fetchIcalData(ICAL_URL)
      icalData?.let { parseICalData(it) }
    }
  }

  private fun parseICalData(icalData: String) {
    try {
      val reader = StringReader(icalData)
      val calendar: Calendar = CalendarBuilder().build(reader)
      val start = DateTime(System.currentTimeMillis())
      val end =
          DateTime(
              System.currentTimeMillis() +
                  365L *
                      24 *
                      60 *
                      60 *
                      1000) // This is a year from now, change as needed. (365 to 730 for 2
      // years...)
      val period = Period(start, end)

      val allEvents = mutableListOf<VEvent>()

      for (component in calendar.getComponents<VEvent>(VEvent.VEVENT)) {
        val dtStart = component.getProperty<DtStart>(Property.DTSTART)
        val dtEnd = component.getProperty<DtEnd>(Property.DTEND)

        dtStart?.also { startDate ->
          val endDate = dtEnd?.date ?: startDate.date
          val rrule = component.getProperty<RRule>(Property.RRULE)

          try {
            if (rrule != null) {
              val recurringEvents = handleRecurringEvents(component, period, startDate.date, endDate)
              allEvents.addAll(recurringEvents)
            } else if (startDate.date.before(end) && endDate.after(start)) {
              val nonRecurringEvents = handleNonRecurringEvents(component, startDate.date, endDate)
              allEvents.addAll(nonRecurringEvents)
            } else {
              Log.d("CalendarViewModel", "Event does not meet conditions for handling.")
            }
          } catch (e: IllegalArgumentException) {
            Log.e("CalendarViewModel", "Error processing event recurrence rules: ${e.localizedMessage}", e)
          }
        }

      }

      _icalEvents.value = allEvents

      Log.d("CalendarViewModel", "Total events parsed: ${_icalEvents.value.size}")
    } catch (e: IOException) {
      Log.e("CalendarViewModel", "Error reading iCal data: ${e.localizedMessage}", e)
    } catch (e: ParserException) {
      Log.e("CalendarViewModel", "Error parsing iCal data: ${e.localizedMessage}", e)
    } catch (e: Exception) {
      Log.e("CalendarViewModel", "General error while parsing iCal data: ${e.localizedMessage}", e)
    }
  }

  /**
   * Handles recurring events by calculating their recurrence set within a given period. For each
   * recurrence, an event instance is created and, if the event spans multiple days, it is broken
   * into daily events. This method returns a list of generated event instances.
   *
   * @param component The VEvent component representing the original event.
   * @param period The period within which the events are considered.
   * @param eventStartDate The start date of the event.
   * @param eventEndDate The end date of the event.
   * @return A list of VEvent instances representing the recurring event occurrences.
   */
  private fun handleRecurringEvents(
      component: VEvent,
      period: Period,
      eventStartDate: Date,
      eventEndDate: Date
  ): List<VEvent> {
    val events = mutableListOf<VEvent>()
    val recurrenceSet = component.calculateRecurrenceSet(period)

    for (recurrence in recurrenceSet) {

      // We create a copy of the event to ensure that the original event definition remains
      // unchanged.
      // Each occurrence of a recurring event may have a different start date (DTSTART), so we
      // update the copied instance to reflect the specific occurrence. This also allows us to
      // handle multi-day
      // events by creating individual instances for each day the event spans, while keeping the
      // original
      // event intact.
      val eventInstance = component.copy() as VEvent
      eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = recurrence.start

      if (!eventEndDate.after(recurrence.start)) {
        // If the event does not span multiple days, we add it directly
        events.add(eventInstance)
        continue
      }

      var current = recurrence.start
      while (current.before(eventEndDate)) {
        // We create an instance for each day the event spans
        val multiDayEventInstance = eventInstance.copy() as VEvent
        multiDayEventInstance.getProperty<DtStart>(Property.DTSTART)?.date = DateTime(current.time)
        events.add(multiDayEventInstance)

        // Move to the next day, breaking the multi-day event into individual daily events
        val calendarInstance =
            JavaCalendar.getInstance().apply {
              time = current
              add(JavaCalendar.DATE, 1)
            }
        current = DateTime(calendarInstance.time)
      }
    }

    return events
  }

  /**
   * Handles non-recurring events by generating a list of event instances for each day the event
   * spans.
   *
   * @param component The VEvent component representing the event.
   * @param eventStartDate The start date of the event.
   * @param eventEndDate The end date of the event.
   * @return A list of VEvent instances for each day of the event.
   */
  private fun handleNonRecurringEvents(
      component: VEvent,
      eventStartDate: Date,
      eventEndDate: Date
  ): List<VEvent> {
    val events = mutableListOf<VEvent>()

    // Check if the event spans multiple days
    if (eventEndDate.after(eventStartDate)) {
      var current = eventStartDate
      while (current.before(eventEndDate)) {
        // Create a new instance for each day the event spans
        val eventInstance = component.copy() as VEvent
        eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = DateTime(current.time)
        events.add(eventInstance)

        // Move to the next day
        val calendarInstance =
            JavaCalendar.getInstance().apply {
              time = current
              add(JavaCalendar.DATE, 1)
            }
        current = DateTime(calendarInstance.time)
      }
    } else {
      // Single-day event
      events.add(component)
    }

    return events
  }
}
