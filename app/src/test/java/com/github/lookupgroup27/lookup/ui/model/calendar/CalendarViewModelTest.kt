package com.github.lookupgroup27.lookup.model.calendar

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Summary
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

  private lateinit var viewModel: CalendarViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `handle multi-day non-recurring event`() = runTest {
    val mockIcalData =
        """
            BEGIN:VCALENDAR
            BEGIN:VEVENT
            DTSTART:20251001T120000Z
            DTEND:20251004T120000Z
            SUMMARY:Multi-Day Event
            END:VEVENT
            END:VCALENDAR
        """
            .trimIndent()

    val icalRepository: IcalRepository = MockIcalRepository(mockIcalData)
    viewModel = CalendarViewModel(icalRepository)

    viewModel.fetchICalData()
    advanceUntilIdle()

    val icalEvents = viewModel.icalEvents.first()
    assertEquals(3, icalEvents.size)
    assertEquals("Multi-Day Event", icalEvents[0].getProperty<Summary>(Property.SUMMARY)?.value)
  }

  @Test
  fun `handle event with no end date`() = runTest {
    val mockIcalData =
        """
            BEGIN:VCALENDAR
            BEGIN:VEVENT
            DTSTART:20251001T120000Z
            SUMMARY:Event without End Date
            END:VEVENT
            END:VCALENDAR
        """
            .trimIndent()

    val icalRepository: IcalRepository = MockIcalRepository(mockIcalData)
    viewModel = CalendarViewModel(icalRepository)

    viewModel.fetchICalData()
    advanceUntilIdle()

    val icalEvents = viewModel.icalEvents.first()
    assertEquals(1, icalEvents.size)
    assertEquals(
        "Event without End Date", icalEvents[0].getProperty<Summary>(Property.SUMMARY)?.value)
  }

  @Test
  fun `ignore events outside the date range`() = runTest {
    val mockIcalData =
        """
            BEGIN:VCALENDAR
            BEGIN:VEVENT
            DTSTART:20301001T120000Z
            DTEND:20301001T130000Z
            SUMMARY:Outside Date Range
            END:VEVENT
            END:VCALENDAR
        """
            .trimIndent()

    val icalRepository: IcalRepository = MockIcalRepository(mockIcalData)
    viewModel = CalendarViewModel(icalRepository)

    viewModel.fetchICalData()
    advanceUntilIdle()

    val icalEvents = viewModel.icalEvents.first()
    assertEquals(0, icalEvents.size)
  }

  @Test
  fun `recurring event generates multiple instances`() = runTest {
    val mockIcalData =
        """
            BEGIN:VCALENDAR
            BEGIN:VEVENT
            DTSTART:20251001T120000Z
            DTEND:20251001T130000Z
            SUMMARY:Recurring Event
            RRULE:FREQ=DAILY;COUNT=3
            END:VEVENT
            END:VCALENDAR
        """
            .trimIndent()

    val icalRepository: IcalRepository = MockIcalRepository(mockIcalData)
    viewModel = CalendarViewModel(icalRepository)

    viewModel.fetchICalData()
    advanceUntilIdle()

    val icalEvents = viewModel.icalEvents.first()
    assertEquals(3, icalEvents.size)
    assertEquals("Recurring Event", icalEvents[0].getProperty<Summary>(Property.SUMMARY)?.value)
  }

  @Test
  fun `empty calendar returns no events`() = runTest {
    val mockIcalData =
        """
            BEGIN:VCALENDAR
            END:VCALENDAR
        """
            .trimIndent()

    val icalRepository: IcalRepository = MockIcalRepository(mockIcalData)
    viewModel = CalendarViewModel(icalRepository)

    viewModel.fetchICalData()
    advanceUntilIdle()

    val icalEvents = viewModel.icalEvents.first()
    assertEquals(0, icalEvents.size)
  }

  @Test
  fun `malformed calendar data is handle correctly and doesnt crash the app`() = runTest {
    val mockIcalData = "MALFORMED DATA"

    val icalRepository: IcalRepository = MockIcalRepository(mockIcalData)
    viewModel = CalendarViewModel(icalRepository)

    viewModel.fetchICalData()
    advanceUntilIdle()

    val icalEvents = viewModel.icalEvents.first()
    assertEquals(0, icalEvents.size)
  }
}
