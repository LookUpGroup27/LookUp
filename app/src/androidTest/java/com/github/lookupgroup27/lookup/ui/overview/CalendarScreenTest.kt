package com.github.lookupgroup27.lookup.ui.overview

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.model.calendar.MockIcalRepository
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalendarScreenTest {

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

    val mockIcalRepository = MockIcalRepository(mockIcalData)
    calendarViewModel = CalendarViewModel(mockIcalRepository)

    composeTestRule.setContent {
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
  }

  @Test
  fun testLookUpEventButtonExists() = runTest {
    composeTestRule.onNodeWithContentDescription("Look Up Event").assertIsDisplayed()
  }

  @Test
  fun testSelectingDayUpdatesSelectedDate() = runTest {
    val calendar = Calendar.getInstance()
    val dayToSelect = calendar.get(Calendar.DAY_OF_MONTH) + 1

    composeTestRule.onNodeWithText(dayToSelect.toString()).performClick()

    composeTestRule.onNodeWithText(dayToSelect.toString()).assertIsDisplayed()
  }
}
