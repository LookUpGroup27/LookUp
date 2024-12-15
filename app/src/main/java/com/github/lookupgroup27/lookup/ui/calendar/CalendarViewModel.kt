package com.github.lookupgroup27.lookup.ui.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.lookupgroup27.lookup.model.calendar.CalendarRepository
import com.github.lookupgroup27.lookup.model.calendar.CalendarRepositoryFirestore
import com.github.lookupgroup27.lookup.model.calendar.CalendarUseCase
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.util.MapTimeZoneCache
import okhttp3.OkHttpClient

/**
 * ViewModel responsible for managing calendar events and exposing them to the UI.
 *
 * @property calendarRepository Repository for fetching raw calendar data.
 * @property calendarUseCase UseCase for parsing and processing calendar data.
 */
class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val calendarUseCase: CalendarUseCase = CalendarUseCase()
) : ViewModel() {

  companion object {
    private const val CALENDAR_URL =
        "https://p127-caldav.icloud.com/published/2/MTE0OTM4OTk2MTExNDkzOIzDRaBjEGa9_1mlmgjzcdlka5HK6EzMiIdOswU-0rZBZMDBibtH_M7CDyMpDQRQJPdGOSM0hTsS2qFNGOObsTc"

    /** Factory for creating an instance of CalendarViewModel. */
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalendarViewModel(CalendarRepositoryFirestore(OkHttpClient(), CALENDAR_URL)) as T
          }
        }
  }

  private val _icalEvents = MutableStateFlow<List<VEvent>>(emptyList())
  val icalEvents: StateFlow<List<VEvent>> = _icalEvents

  init {
    System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache::class.java.name)
    getData()
  }

  /** Fetches calendar data from the repository and processes it using the CalendarUseCase. */
  fun getData() {
    viewModelScope.launch {
      try {
        val icalData = calendarRepository.getData()
        if (icalData != null) {
          val events = calendarUseCase.parseICalData(icalData)
          _icalEvents.value = events
        } else {
          _icalEvents.value = emptyList()
        }
      } catch (e: IOException) {
        Log.e("CalendarViewModel", "Error reading iCal data: ${e.localizedMessage}", e)
        _icalEvents.value = emptyList()
      } catch (e: Exception) {
        Log.e(
            "CalendarViewModel", "General error while fetching iCal data: ${e.localizedMessage}", e)
        _icalEvents.value = emptyList()
      }
    }
  }
}
