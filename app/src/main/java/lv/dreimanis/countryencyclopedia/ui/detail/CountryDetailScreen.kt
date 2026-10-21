package lv.dreimanis.countryencyclopedia.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import lv.dreimanis.countryencyclopedia.R
import lv.dreimanis.countryencyclopedia.data.local.CountryEntity
import lv.dreimanis.countryencyclopedia.data.local.LanguageEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    onCountryClick: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountryDetailViewModel = hiltViewModel(),
) {
    val country by viewModel.country.collectAsStateWithLifecycle()
    val rank by viewModel.rank.collectAsStateWithLifecycle()
    val neighbours by viewModel.neighbours.collectAsStateWithLifecycle()
    val sameLanguage by viewModel.sameLanguage.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(country?.commonName.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(
                                if (isFavorite) R.string.remove_from_favourites else R.string.add_to_favourites,
                            ),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        val current = country
        if (current == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            CountryDetailContent(
                country = current,
                rank = rank,
                neighbours = neighbours,
                sameLanguage = sameLanguage,
                onCountryClick = onCountryClick,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun CountryDetailContent(
    country: CountryEntity,
    rank: Int?,
    neighbours: List<CountryEntity>,
    sameLanguage: Map<LanguageEntity, List<CountryEntity>>,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (country.alpha2.isNotEmpty()) {
                AsyncImage(
                    model = "https://flagsapi.com/${country.alpha2}/flat/64.png",
                    contentDescription = stringResource(R.string.flag_of, country.commonName),
                    modifier = Modifier.size(64.dp),
                )
                Spacer(Modifier.width(16.dp))
            }
            Text(country.officialName, style = MaterialTheme.typography.titleMedium)
        }

        InfoRow(stringResource(R.string.code_label), country.alpha3.ifEmpty { "–" })
        InfoRow(
            stringResource(R.string.population_label),
            country.population?.let { "%,d".format(it) } ?: stringResource(R.string.population_unknown),
        )
        rank?.let { InfoRow(stringResource(R.string.population_rank_label), stringResource(R.string.population_rank, it)) }
        country.capitalName?.let { InfoRow(stringResource(R.string.capital_label), it) }

        val mapLat = country.capitalLat ?: country.lat
        val mapLng = country.capitalLng ?: country.lng
        if (mapLat != null && mapLng != null) {
            SectionHeader(stringResource(R.string.location))
            CountryMap(
                lat = mapLat,
                lng = mapLng,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.medium),
            )
        }

        SectionHeader(stringResource(R.string.neighbours))
        if (neighbours.isEmpty()) {
            EmptyText(stringResource(R.string.none))
        }
        neighbours.forEach { neighbour ->
            CountryRow(neighbour, onCountryClick)
        }

        SectionHeader(stringResource(R.string.languages))
        sameLanguage.forEach { (language, countries) ->
            Text(
                language.name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            )
            if (countries.isEmpty()) {
                EmptyText(stringResource(R.string.no_other_countries))
            }
            countries.forEach { other ->
                CountryRow(other, onCountryClick)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
private fun CountryRow(country: CountryEntity, onClick: (String) -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick(country.uuid) },
        leadingContent = { Text(country.flagEmoji.ifEmpty { "🏳️" }, style = MaterialTheme.typography.titleLarge) },
        headlineContent = { Text(country.commonName) },
    )
}
