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
          val recurrenceSet = component.calculateRecurrenceSet(period)
          val recurrenceDates = recurrenceSet.map { it.start }
          val recurringEvents = createEventInstances(component, recurrenceDates, endDate)
          allEvents.addAll(recurringEvents)
        } else if (startDate.date.before(end) && endDate.after(start)) {
          val nonRecurringDates = listOf(startDate.date)
          val nonRecurringEvents = createEventInstances(component, nonRecurringDates, endDate)
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
   * Creates event instances for each start date and handles multi-day events.
   *
   * @param component The original VEvent component.
   * @param startDates A list of start dates for the event occurrences.
   * @param originalEndDate The original end date of the event.
   * @return A list of VEvent instances.
   */
  private fun createEventInstances(
      component: VEvent,
      startDates: List<Date>,
      originalEndDate: Date
  ): List<VEvent> {
    val events = mutableListOf<VEvent>()

    for (startDate in startDates) {
      val eventInstance = component.copy() as VEvent
      eventInstance.getProperty<DtStart>(Property.DTSTART)?.date = startDate

      val endDate = if (originalEndDate.after(startDate)) originalEndDate else startDate
      if (endDate.after(startDate)) {
        var current = startDate

        while (current.before(endDate)) {
          // Add the event instance for the current day
          val multiDayEventInstance = component.copy() as VEvent
          multiDayEventInstance.getProperty<DtStart>(Property.DTSTART)?.date =
              DateTime(current.time)
          events.add(multiDayEventInstance)

          // Move to the next day
          val calendarInstance =
              JavaCalendar.getInstance().apply {
                time = current
                add(JavaCalendar.DATE, 1)
              }
          current = DateTime(calendarInstance.time)

          // **Breaking Condition**: Stop if current time is beyond the event's end time
          if (current.time >= originalEndDate.time) {
            Log.d(
                "CalendarUseCase",
                "Breaking loop as current time (${current.time}) >= eventEndDate (${originalEndDate.time})")
            break
          }
        }
      } else {
        events.add(eventInstance)
      }
    }

    return events
  }
}
