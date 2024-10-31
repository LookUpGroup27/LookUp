package com.github.lookupgroup27.lookup.ui.calendar

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.model.calendar.MockIcalRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Summary
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalendarKtTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var calendarViewModel: CalendarViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var navController: TestNavHostController
  private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

  @Before
  fun setUp() {

    val mockIcalData =
        """
            BEGIN:VCALENDAR
            BEGIN:VEVENT
            DTSTART:20251001T120000Z
            DTEND:20251001T130000Z
            SUMMARY:Test Event
            END:VEVENT
            END:VCALENDAR
        """
            .trimIndent()

    val mockIcalRepository = MockIcalRepository(mockIcalData)
    calendarViewModel = CalendarViewModel(mockIcalRepository)

    val event = VEvent(DateTime("20251001T120000Z"), "Test Event v2")
    event.getProperties().add(Summary("Test Event v2"))

    composeTestRule.setContent {
      EventItem(event = event, onClick = {})
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      navigationActions = NavigationActions(navController)
      CalendarScreen(calendarViewModel = calendarViewModel, navigationActions = navigationActions)
    }
  }

  @Test
  fun testCalendarHeaderDisplaysCurrentMonth() = runTest {
    val currentDate = Calendar.getInstance().time
    val formattedMonth = dateFormat.format(currentDate)

    composeTestRule.onNodeWithText(formattedMonth).assertIsDisplayed()
  }

  @Test
  fun testPreviousMonthButton() = runTest {
    val currentDate = Calendar.getInstance().time
    val previousMonthDate =
        Calendar.getInstance()
            .apply {
              time = currentDate
              add(Calendar.MONTH, -1)
            }
            .time
    val formattedPreviousMonth = dateFormat.format(previousMonthDate)

    composeTestRule.onNodeWithContentDescription("Previous Month").performClick()
    composeTestRule.onNodeWithText(formattedPreviousMonth).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Previous_month").assertIsDisplayed()
  }

  @Test
  fun testNextMonthButton() = runTest {
    val currentDate = Calendar.getInstance().time
    val nextMonthDate =
        Calendar.getInstance()
            .apply {
              time = currentDate
              add(Calendar.MONTH, 1)
            }
            .time
    val formattedNextMonth = dateFormat.format(nextMonthDate)

    composeTestRule.onNodeWithContentDescription("Next Month").performClick()
    composeTestRule.onNodeWithText(formattedNextMonth).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Next_month").assertIsDisplayed()
  }

  @Test
  fun testLookUpEventButtonExists() = runTest {
    composeTestRule.onNodeWithContentDescription("Look Up Event").assertIsDisplayed()
    composeTestRule.onNodeWithTag("look_up_event").assertIsDisplayed()
  }

  @Test
  fun testSearchDialogOpensAndSearchesForEvents() = runTest {
    advanceUntilIdle()

    composeTestRule.onNodeWithContentDescription("Look Up Event").performClick()
    composeTestRule.onNodeWithText("Look Up Event").assertIsDisplayed()

    composeTestRule.onNodeWithText("Enter event name").performTextInput("Test Event")
    composeTestRule.onNodeWithText("Search").performClick()

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Test Event").assertIsDisplayed()
  }

  @Test
  fun testSearchDialogCancelButtonClosesDialog() = runTest {
    composeTestRule.onNodeWithContentDescription("Look Up Event").performClick()
    composeTestRule.onNodeWithText("Look Up Event").assertIsDisplayed()

    composeTestRule.onNodeWithText("Cancel").performClick()
    composeTestRule.onNodeWithText("Look Up Event").assertDoesNotExist()
  }

  @Test
  fun testEventItemDisplaysCorrectly() = runTest {
    composeTestRule.onNodeWithText("Test Event v2").assertIsDisplayed()
    composeTestRule.onNodeWithText("Date: October 1, 2025").assertIsDisplayed()
  }

  @Test
  fun testSelectingDayUpdatesSelectedDate() = runTest {
    val calendar = Calendar.getInstance()
    val dayToSelect = calendar.get(Calendar.DAY_OF_MONTH)

    composeTestRule.onNodeWithText(dayToSelect.toString()).performClick()

    composeTestRule.onNodeWithText(dayToSelect.toString()).assertIsDisplayed()
  }

  @Test
  fun testEventListDiplays() = runTest {
    composeTestRule.onNodeWithTag("list_event").assertIsDisplayed()
  }

  @Test
  fun calendarGridIsDiplays() = runTest {
    composeTestRule.onNodeWithTag("calendar_grid").assertIsDisplayed()
  }

  @Test
  fun calendarHeaderIsDiplays() = runTest {
    composeTestRule.onNodeWithTag("calendar_header").assertIsDisplayed()
  }
}
