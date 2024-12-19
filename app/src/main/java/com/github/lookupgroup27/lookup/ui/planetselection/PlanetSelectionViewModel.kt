package com.github.lookupgroup27.lookup.ui.planetselection

import PlanetsRepository
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.lookupgroup27.lookup.model.location.LocationProviderSingleton
import com.github.lookupgroup27.lookup.model.map.planets.PlanetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlanetSelectionViewModel(planetsRepository: PlanetsRepository) : ViewModel() {

  private val _selectedPlanet = MutableStateFlow(planetsRepository.planets.first())
  val selectedPlanet: StateFlow<PlanetData> = _selectedPlanet

  val planets = planetsRepository.planets // Expose the list of planets

  fun selectPlanet(planet: PlanetData) {
    _selectedPlanet.value = planet
  }

  companion object {
    fun createFactory(context: Context): ViewModelProvider.Factory {
      val locationProvider = LocationProviderSingleton.getInstance(context)
      val planetsRepository = PlanetsRepository(locationProvider)
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return PlanetSelectionViewModel(planetsRepository) as T
        }
      }
    }
  }
}
