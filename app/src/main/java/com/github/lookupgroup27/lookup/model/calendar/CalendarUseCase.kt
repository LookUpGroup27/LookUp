package com.github.lookupgroup27.lookup.model.calendar

import android.util.Log
import java.io.IOException
import java.io.StringReader
import java.util.Calendar as JavaCalendar
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

/** Use case responsible for processing and parsing iCal data to extract calendar events. */
class CalendarUseCase {

  /**
   * Parses raw iCal data and extracts a list of events.
   *
   * @param icalData Raw iCal data as a string.
   * @return A list of VEvent objects representing calendar events.
   * @throws IOException if an error occurs during parsing.
   */
  @Throws(IOException::class)
  fun parseICalData(icalData: String): List<VEvent> {
    val reader = StringReader(icalData)
    val calendar: Calendar = CalendarBuilder().build(reader)

    val start = DateTime(System.currentTimeMillis())
    val end = DateTime(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000) // 1 year
    val period = Period(start, end)

    val allEvents = mutableListOf<VEvent>()

    for (component in calendar.getComponents<VEvent>(VEvent.VEVENT)) {
      val dtStart = component.getProperty<DtStart>(Property.DTSTART)
      val dtEnd = component.getProperty<DtEnd>(Property.DTEND)

      dtStart?.also { startDate ->
        val endDate = dtEnd?.date ?: startDate.date
        val rrule = component.getProperty<RRule>(Property.RRULE)

        if (rrule != null) {
          val recurringEvents = handleRecurringEvents(component, period, startDate.date, endDate)
          allEvents.addAll(recurringEvents)
        } else if (startDate.date.before(end) && endDate.after(start)) {
          val nonRecurringEvents = handleNonRecurringEvents(component, startDate.date, endDate)
          allEvents.addAll(nonRecurringEvents)
        } else {
          Log.d("CalendarUseCase", "Event does not meet conditions for handling.")
        }
      }
    }

    Log.d("CalendarUseCase", "Total events parsed: ${allEvents.size}")
    return allEvents
  }

  /**
   * Handles recurring events by calculating their recurrence set within a given period.
   *
   * @param component The VEvent component representing the event.
   * @param period The period during which events are extracted.
   * @param eventStartDate The event's start date.
   * @param eventEndDate The event's end date.
   * @return A list of recurring VEvent instances.
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
      val eventInstance = component.copy() as VEvent
      eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = recurrence.start

      if (!eventEndDate.after(recurrence.start)) {
        events.add(eventInstance)
        continue
      }

      var current = recurrence.start
      while (current.before(eventEndDate)) {
        val multiDayEventInstance = eventInstance.copy() as VEvent
        multiDayEventInstance.getProperty<DtStart>(Property.DTSTART)?.date = DateTime(current.time)
        events.add(multiDayEventInstance)

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
   * @param eventStartDate The event's start date.
   * @param eventEndDate The event's end date.
   * @return A list of VEvent instances for each day of the event.
   */
  private fun handleNonRecurringEvents(
      component: VEvent,
      eventStartDate: Date,
      eventEndDate: Date
  ): List<VEvent> {
    val events = mutableListOf<VEvent>()

    if (eventEndDate.after(eventStartDate)) {
      var current = eventStartDate
      while (current.before(eventEndDate)) {
        val eventInstance = component.copy() as VEvent
        eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = DateTime(current.time)
        events.add(eventInstance)

        val calendarInstance =
            JavaCalendar.getInstance().apply {
              time = current
              add(JavaCalendar.DATE, 1)
            }
        current = DateTime(calendarInstance.time)
      }
    } else {
      events.add(component)
    }

    return events
  }
}
