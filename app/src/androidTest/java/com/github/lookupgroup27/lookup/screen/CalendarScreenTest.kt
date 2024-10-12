package com.github.lookupgroup27.lookup.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.lookupgroup27.lookup.model.calendar.CalendarViewModel
import com.github.lookupgroup27.lookup.ui.navigation.NavigationActions
import com.github.lookupgroup27.lookup.ui.overview.CalendarScreen
import io.mockk.mockk
import java.text.SimpleDateFormat
import java.util.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalendarScreenTest {

  private lateinit var calendarViewModel: CalendarViewModel
  private lateinit var navigationActions: NavigationActions
  private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {

    calendarViewModel = mockk(relaxed = true)
    navigationActions = mockk(relaxed = true)

    composeTestRule.setContent {
      CalendarScreen(calendarViewModel = calendarViewModel, navigationActions = navigationActions)
    }
  }

  @Test
  fun testCalendarHeaderDisplaysCurrentMonth() {
    val currentDate = Calendar.getInstance().time
    val formattedMonth = dateFormat.format(currentDate)

    composeTestRule.onNodeWithText(formattedMonth).assertIsDisplayed()
  }

  @Test
  fun testPreviousMonthButton() {
    val currentDate = Calendar.getInstance().time
    val previousMonthDate =
        Calendar.getInstance()
            .apply {
              time = currentDate
              add(Calendar.MONTH, -1)
            }
            .time
    val formattedPreviousMonth = dateFormat.format(previousMonthDate)

    composeTestRule.onNodeWithText("<").performClick()
    composeTestRule.onNodeWithText(formattedPreviousMonth).assertIsDisplayed()
  }

  @Test
  fun testNextMonthButton() {
    val currentDate = Calendar.getInstance().time
    val nextMonthDate =
        Calendar.getInstance()
            .apply {
              time = currentDate
              add(Calendar.MONTH, 1)
            }
            .time
    val formattedNextMonth = dateFormat.format(nextMonthDate)

    composeTestRule.onNodeWithText(">").performClick()
    composeTestRule.onNodeWithText(formattedNextMonth).assertIsDisplayed()
  }

  @Test
  fun testLookUpEventButtonExists() {
    composeTestRule.onNodeWithText("Look Up Event").assertIsDisplayed()
  }

  // Add more tests when figma is updated with all the components and testTags

  @Test
  fun testSelectingDayUpdatesSelectedDate() {
    val calendar = Calendar.getInstance()
    val dayToSelect = calendar.get(Calendar.DAY_OF_MONTH) + 1

    composeTestRule.onNodeWithText(dayToSelect.toString()).performClick()

    composeTestRule.onNodeWithText(dayToSelect.toString()).assertIsDisplayed()
  }
}
