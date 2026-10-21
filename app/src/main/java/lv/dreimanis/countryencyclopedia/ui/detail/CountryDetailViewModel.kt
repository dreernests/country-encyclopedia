package lv.dreimanis.countryencyclopedia.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lv.dreimanis.countryencyclopedia.data.CountryRepository
import lv.dreimanis.countryencyclopedia.data.local.CountryEntity
import lv.dreimanis.countryencyclopedia.data.local.LanguageEntity
import lv.dreimanis.countryencyclopedia.ui.navigation.CountryDetailRoute
import javax.inject.Inject

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CountryRepository,
) : ViewModel() {

    private val uuid = savedStateHandle.toRoute<CountryDetailRoute>().uuid

    private val _country = MutableStateFlow<CountryEntity?>(null)
    val country: StateFlow<CountryEntity?> = _country.asStateFlow()

    private val _rank = MutableStateFlow<Int?>(null)
    val rank: StateFlow<Int?> = _rank.asStateFlow()

    private val _neighbours = MutableStateFlow<List<CountryEntity>>(emptyList())
    val neighbours: StateFlow<List<CountryEntity>> = _neighbours.asStateFlow()

    private val _sameLanguage =
        MutableStateFlow<Map<LanguageEntity, List<CountryEntity>>>(emptyMap())
    val sameLanguage: StateFlow<Map<LanguageEntity, List<CountryEntity>>> =
        _sameLanguage.asStateFlow()

    val isFavorite: StateFlow<Boolean> = repository.isFavorite(uuid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            val country = repository.getCountry(uuid) ?: return@launch
            _country.value = country
            _rank.value = country.population?.let { repository.getPopulationRank(it) }
            _neighbours.value = repository.getCountriesByCodes(country.borders)
            _sameLanguage.value = country.languages.associateWith { language ->
                repository.getCountriesSpeaking(language.code, excludeUuid = country.uuid)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.setFavorite(uuid, !isFavorite.value)
        }
    }
}