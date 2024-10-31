package com.github.lookupgroup27.lookup.model.location

// toDo: try to correctly write the test for this class

/*import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class LocationProviderTest {

    private lateinit var locationProvider: LocationProvider
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Before
    fun setUp() {
        // Use the application context directly
        val context = ApplicationProvider.getApplicationContext<Application>()

        // Mock the FusedLocationProviderClient
        fusedLocationClient = mock(FusedLocationProviderClient::class.java)
        whenever(LocationServices.getFusedLocationProviderClient(context)).thenReturn(fusedLocationClient)

        // Initialize LocationProvider with the application context
        locationProvider = LocationProvider(context)
    }

    @Test
    fun `requestLocationUpdates grants permission and updates location`() {
        // Given
        val mockLocation = mock(Location::class.java)
        whenever(mockLocation.latitude).thenReturn(37.4219983)
        whenever(mockLocation.longitude).thenReturn(-122.084)

        // Simulate permission granted
        whenever(ActivityCompat.checkSelfPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION)))
            .thenReturn(PackageManager.PERMISSION_GRANTED)

        // Simulate the successful retrieval of the last known location
        val task: Task<Location> = mock()
        whenever(fusedLocationClient.lastLocation).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(true)
        whenever(task.result).thenReturn(mockLocation)

        // When
        locationProvider.requestLocationUpdates()

        // Then
        verify(fusedLocationClient).lastLocation
        locationProvider.currentLocation.value?.latitude?.let { assertEquals(37.4219983, it, 0.0) }
        locationProvider.currentLocation.value?.longitude?.let { assertEquals(-122.084, it, 0.0) }
    }

    @Test
    fun `requestLocationUpdates does not grant permission when denied`() {
        // Given
        whenever(ActivityCompat.checkSelfPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION)))
            .thenReturn(PackageManager.PERMISSION_DENIED)

        // When
        locationProvider.requestLocationUpdates()

        // Then
        verify(fusedLocationClient, never()).lastLocation
    }
}*/

/*import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Mock
import org.mockito.InjectMocks
import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.assertEquals

class LocationProviderTest {
    @Mock
    private lateinit var activity: Activity

    @Mock
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @InjectMocks
    private lateinit var locationProvider: LocationProvider

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(LocationServices.getFusedLocationProviderClient(activity)).thenReturn(fusedLocationClient)
    }

    @Test
    fun testRequestLocationUpdates_permissionNotGranted() {
        `when`(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION))
            .thenReturn(PackageManager.PERMISSION_DENIED)

        locationProvider.requestLocationUpdates()

        verify(fusedLocationClient, never()).lastLocation
    }

    @Test
    fun testRequestLocationUpdates_permissionGranted() {
        `when`(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION))
            .thenReturn(PackageManager.PERMISSION_GRANTED)

        val location = mock(Location::class.java)
        val task = mock(com.google.android.gms.tasks.Task::class.java) as com.google.android.gms.tasks.Task<Location>
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(location)
        `when`(fusedLocationClient.lastLocation).thenReturn(task)

        locationProvider.requestLocationUpdates()

        verify(fusedLocationClient).lastLocation
        assertEquals(location, locationProvider.currentLocation.value)
    }
}*/

/*import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor

@RunWith(AndroidJUnit4::class)
class LocationProviderTest {

    private lateinit var locationProvider: LocationProvider
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var context: Context

    @Before
    fun setUp() {
        // Get application context and mock the activity
        val context = ApplicationProvider.getApplicationContext<Context>()
        locationProvider = LocationProvider(context as Application)
        fusedLocationClient = mock(FusedLocationProviderClient::class.java)
    }

    @Test
    fun requestLocationUpdates_withPermission_updatesLocation() {
        `when`(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(PackageManager.PERMISSION_GRANTED)

        val mockLocation = mock(Location::class.java)
        `when`(mockLocation.latitude).thenReturn(40.7128)
        `when`(mockLocation.longitude).thenReturn(-74.0060)

        // Mock the behavior of lastLocation
        val task = mock(Task::class.java)
        `when`(fusedLocationClient.lastLocation).thenReturn(task as Task<Location>?)
        `when`(task.result).thenReturn(mockLocation)

        locationProvider.requestLocationUpdates()

        // Assert that currentLocation is updated
        assert(locationProvider.currentLocation.value?.latitude == 40.7128)
        assert(locationProvider.currentLocation.value?.longitude == -74.0060)
    }

    @Test
    fun requestLocationUpdates_withoutPermission_doesNotUpdate() {
        `when`(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(PackageManager.PERMISSION_DENIED)

        locationProvider.requestLocationUpdates()

        // Assert that currentLocation remains null
        assert(locationProvider.currentLocation.value == null)
    }
}*/
