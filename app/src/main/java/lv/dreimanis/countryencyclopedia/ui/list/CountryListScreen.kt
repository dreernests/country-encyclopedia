package lv.dreimanis.countryencyclopedia.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lv.dreimanis.countryencyclopedia.R
import lv.dreimanis.countryencyclopedia.data.local.CountryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountryListViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val countries by viewModel.countries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasError by viewModel.hasError.collectAsStateWithLifecycle()
    val favoriteUuids by viewModel.favoriteUuids.collectAsStateWithLifecycle()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = viewModel::toggleFavoritesOnly) {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(
                                if (showFavoritesOnly) R.string.show_all_countries else R.string.show_favourites,
                            ),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        CountryListContent(
            query = query,
            countries = countries,
            isLoading = isLoading,
            hasError = hasError,
            favoriteUuids = favoriteUuids,
            showFavoritesOnly = showFavoritesOnly,
            onQueryChange = viewModel::onQueryChange,
            onRetry = viewModel::loadCountries,
            onCountryClick = onCountryClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun CountryListContent(
    query: String,
    countries: List<CountryEntity>,
    isLoading: Boolean,
    hasError: Boolean,
    favoriteUuids: Set<String>,
    showFavoritesOnly: Boolean,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(stringResource(R.string.search_label)) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.clear_search),
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            hasError -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.load_error))
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }

            showFavoritesOnly && favoriteUuids.isEmpty() -> {
                EmptyMessage(stringResource(R.string.no_favourites))
            }

            countries.isEmpty() && query.isNotEmpty() -> {
                EmptyMessage(stringResource(R.string.no_results, query))
            }

            else -> {
                LazyColumn {
                    items(countries, key = { it.uuid }) { country ->
                        ListItem(
                            modifier = Modifier.clickable { onCountryClick(country.uuid) },
                            leadingContent = {
                                Text(country.flagEmoji.ifEmpty { "🏳️" }, style = MaterialTheme.typography.headlineSmall)
                            },
                            headlineContent = { Text(country.commonName) },
                            supportingContent = { Text(country.officialName) },
                            trailingContent = {
                                if (country.uuid in favoriteUuids) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = stringResource(R.string.favourite),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMessage(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
