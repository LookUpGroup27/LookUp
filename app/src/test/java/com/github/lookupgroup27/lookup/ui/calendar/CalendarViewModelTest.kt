package com.github.lookupgroup27.lookup.ui.calendar

import com.github.lookupgroup27.lookup.model.calendar.CalendarRepository
import com.github.lookupgroup27.lookup.model.calendar.MockCalendarRepository
import java.io.IOException
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

/**
 * Unit tests for [CalendarViewModel], ensuring that it correctly fetches and processes calendar
 * events under various scenarios.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

  private lateinit var viewModel: CalendarViewModel
  private lateinit var calendarRepository: CalendarRepository
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    // Set the main dispatcher to a TestDispatcher for controlling coroutine execution in tests
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    // Reset the main dispatcher to the original Main dispatcher after tests
    Dispatchers.resetMain()
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles a multi-day non-recurring event by
   * generating separate event instances for each day the event spans.
   */
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

    // Initialize the repository with mock iCal data
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    // Advance coroutines until idle to ensure all tasks are completed
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting 3 separate events for Oct 1, Oct 2, and Oct 3
    assertEquals(3, icalEvents.size)
    // Verify that the summary of the first event matches the mock data
    assertEquals("Multi-Day Event", icalEvents[0].getProperty<Summary>(Property.SUMMARY)?.value)
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles an event without an end date by
   * treating it as a single-day event.
   */
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

    // Initialize the repository with mock iCal data lacking an end date
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting a single event
    assertEquals(1, icalEvents.size)
    // Verify that the summary of the event matches the mock data
    assertEquals(
        "Event without End Date", icalEvents[0].getProperty<Summary>(Property.SUMMARY)?.value)
  }

  /**
   * Tests that [CalendarViewModel.getData] ignores events that fall outside the defined date range,
   * resulting in no events being added to the state.
   */
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

    // Initialize the repository with mock iCal data outside the date range
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting no events since the event is outside the date range
    assertEquals(0, icalEvents.size)
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles recurring events by generating
   * multiple instances based on the recurrence rule.
   */
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

    // Initialize the repository with mock iCal data containing a recurring event
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting 3 separate instances of the recurring event
    assertEquals(3, icalEvents.size)
    // Verify that the summary of the first event matches the mock data
    assertEquals("Recurring Event", icalEvents[0].getProperty<Summary>(Property.SUMMARY)?.value)
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles an empty calendar by resulting in no
   * events being added to the state.
   */
  @Test
  fun `empty calendar returns no events`() = runTest {
    val mockIcalData =
        """
                BEGIN:VCALENDAR
                END:VCALENDAR
            """
            .trimIndent()

    // Initialize the repository with an empty iCal data
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting no events
    assertEquals(0, icalEvents.size)
  }

  /**
   * Tests that [CalendarViewModel.getData] gracefully handles malformed calendar data without
   * crashing and results in no events being added to the state.
   */
  @Test
  fun `malformed calendar data is handled correctly and doesn't crash the app`() = runTest {
    val mockIcalData = "MALFORMED DATA"

    // Initialize the repository with malformed iCal data
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting no events due to malformed data
    assertEquals(0, icalEvents.size)
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles an [IllegalArgumentException] during
   * recurring event handling by resulting in no events being added to the state.
   */
  @Test
  fun `handle illegal argument exception during recurring event handling`() = runTest {
    val mockIcalData =
        """
                BEGIN:VCALENDAR
                BEGIN:VEVENT
                DTSTART:20251001T120000Z
                DTEND:20251001T130000Z
                SUMMARY:Invalid Recurrence Event
                RRULE:FREQ=INVALID;COUNT=3
                END:VEVENT
                END:VCALENDAR
            """
            .trimIndent()

    // Initialize the repository with mock iCal data containing an invalid recurrence rule
    calendarRepository = MockCalendarRepository(mockIcalData)
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting no events due to invalid recurrence rule
    assertEquals(0, icalEvents.size)
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles an [IOException] during data fetching
   * by resulting in no events being added to the state.
   */
  @Test
  fun `handle io exception during data fetch`() = runTest {
    // Create a repository that throws an IOException when getData() is called
    calendarRepository =
        object : CalendarRepository {
          override suspend fun getData(): String? {
            throw IOException("Failed to fetch iCal data")
          }
        }
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting no events due to IOException
    assertEquals(0, icalEvents.size)
  }

  /**
   * Tests that [CalendarViewModel.getData] correctly handles a general [Exception] during iCal
   * parsing by resulting in no events being added to the state.
   */
  @Test
  fun `handle general exception during ical parsing`() = runTest {
    // Create a repository that throws a general Exception when getData() is called
    calendarRepository =
        object : CalendarRepository {
          override suspend fun getData(): String? {
            throw Exception("General parsing error")
          }
        }
    viewModel = CalendarViewModel(calendarRepository)

    // Trigger data fetching
    viewModel.getData()
    advanceUntilIdle()

    // Collect the events from the ViewModel
    val icalEvents = viewModel.icalEvents.first()
    // Expecting no events due to general Exception
    assertEquals(0, icalEvents.size)
  }
}
