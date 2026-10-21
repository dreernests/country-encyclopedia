package lv.dreimanis.countryencyclopedia.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lv.dreimanis.countryencyclopedia.data.CountryRepository
import lv.dreimanis.countryencyclopedia.data.local.CountryEntity
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val repository: CountryRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    val favoriteUuids: StateFlow<Set<String>> = repository.favoriteUuids()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    val countries: StateFlow<List<CountryEntity>> = combine(
        _query.flatMapLatest { repository.search(it) },
        favoriteUuids,
        _showFavoritesOnly,
    ) { results, favorites, favoritesOnly ->
        if (favoritesOnly) results.filter { it.uuid in favorites } else results
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadCountries()
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun loadCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            try {
                repository.refreshIfEmpty()
            } catch (e: IOException) {
                _hasError.value = true
            } catch (e: HttpException) {
                _hasError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}