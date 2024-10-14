package com.github.lookupgroup27.lookup.model.calendar

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.StringReader
import java.util.Calendar as JavaCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.fortuna.ical4j.data.CalendarBuilder
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
import okhttp3.Request

class CalendarViewModel : ViewModel() {

  private val client = OkHttpClient()
  private val _icalEvents = mutableStateListOf<VEvent>()
  val icalEvents: List<VEvent> = _icalEvents

  init {
    System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)
    fetchICalData()
  }

  fun fetchICalData() {
    viewModelScope.launch {
      val icalData =
          fetchIcalFromUrl(
              "https://p127-caldav.icloud.com/published/2/MTE0OTM4OTk2MTExNDkzOIzDRaBjEGa9_1mlmgjzcdlka5HK6EzMiIdOswU-0rZBZMDBibtH_M7CDyMpDQRQJPdGOSM0hTsS2qFNGOObsTc")
      icalData?.let { parseICalData(it) }
    }
  }

  private suspend fun fetchIcalFromUrl(url: String): String? =
      withContext(Dispatchers.IO) {
        return@withContext try {
          val request = Request.Builder().url(url).build()
          val response = client.newCall(request).execute()
          response.body?.string()
        } catch (e: Exception) {
          Log.e("CalendarViewModel", "Error fetching iCal data: ${e.localizedMessage}", e)
          null
        }
      }

  private fun parseICalData(icalData: String) {
    try {
      val reader = StringReader(icalData)
      val calendar: Calendar = CalendarBuilder().build(reader)
      val start = DateTime(System.currentTimeMillis())
      val end = DateTime(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)
      val period = Period(start, end)

      val allEvents = mutableListOf<VEvent>()

      for (component in calendar.getComponents<VEvent>(VEvent.VEVENT)) {
        val dtStart = component.getProperty<DtStart>(Property.DTSTART)
        val dtEnd = component.getProperty<DtEnd>(Property.DTEND)

        dtStart?.let { startDate ->
          val endDate = dtEnd?.date ?: startDate.date
          val rrule = component.getProperty<RRule>(Property.RRULE)

          if (rrule != null) {
            handleRecurringEvents(component, period, startDate.date, endDate, allEvents)
          } else if (startDate.date.before(end) && endDate.after(start)) {
            handleNonRecurringEvents(component, startDate.date, endDate, allEvents)
          }
        }
      }

      _icalEvents.clear()
      _icalEvents.addAll(allEvents)
      Log.d("CalendarViewModel", "Total events parsed: ${_icalEvents.size}")
    } catch (e: Exception) {
      Log.e("CalendarViewModel", "Error parsing iCal data: ${e.localizedMessage}", e)
    }
  }

  private fun handleRecurringEvents(
      component: VEvent,
      period: Period,
      eventStartDate: Date,
      eventEndDate: Date,
      allEvents: MutableList<VEvent>
  ) {
    val recurrenceSet = component.calculateRecurrenceSet(period)
    for (recurrence in recurrenceSet) {
      val eventInstance = component.copy() as VEvent
      eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = recurrence.start
      if (eventEndDate.after(recurrence.start)) {
        var current = recurrence.start
        while (current.before(eventEndDate)) {
          val multiDayEventInstance = eventInstance.copy() as VEvent
          multiDayEventInstance.getProperty<DtStart>(Property.DTSTART)?.date =
              DateTime(current.time)
          allEvents.add(multiDayEventInstance)

          val calendarInstance =
              JavaCalendar.getInstance().apply {
                time = current
                add(JavaCalendar.DATE, 1)
              }
          current = DateTime(calendarInstance.time)
        }
      } else {
        allEvents.add(eventInstance)
      }
    }
  }

  private fun handleNonRecurringEvents(
      component: VEvent,
      eventStartDate: Date,
      eventEndDate: Date,
      allEvents: MutableList<VEvent>
  ) {
    if (eventEndDate.after(eventStartDate)) {
      var current = eventStartDate
      while (current.before(eventEndDate)) {
        val eventInstance = component.copy() as VEvent
        eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = DateTime(current.time)
        allEvents.add(eventInstance)

        val calendarInstance =
            JavaCalendar.getInstance().apply {
              time = current
              add(JavaCalendar.DATE, 1)
            }
        current = DateTime(calendarInstance.time)
      }
    } else {
      allEvents.add(component)
    }
  }
}
