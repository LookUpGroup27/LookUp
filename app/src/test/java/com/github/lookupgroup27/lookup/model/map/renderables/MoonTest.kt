import com.github.lookupgroup27.lookup.R
import com.github.lookupgroup27.lookup.model.map.renderables.Moon
import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class MoonTest {

  @Test
  fun testGetMoonPhaseTextureId() {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.set(2024, Calendar.JULY, 6)
    var textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.new_moon, textureId)

    calendar.set(2024, Calendar.AUGUST, 9)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.waxing_crescent, textureId)

    calendar.set(2024, Calendar.MAY, 15)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.first_quarter, textureId)

    calendar.set(2021, Calendar.AUGUST, 19)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.waxing_gibbous, textureId)

    calendar.set(2025, Calendar.JANUARY, 13)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.full_moon, textureId)

    calendar.set(2023, Calendar.OCTOBER, 2)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.waning_gibbous, textureId)

    calendar.set(2024, Calendar.JANUARY, 3)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.last_quarter, textureId)

    calendar.set(2021, Calendar.MAY, 8)
    textureId = Moon.getMoonPhaseTextureId(calendar)
    assertEquals(R.drawable.waning_crescent, textureId)
  }
}
