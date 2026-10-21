package lv.dreimanis.countryencyclopedia.data

import kotlinx.coroutines.flow.Flow
import lv.dreimanis.countryencyclopedia.data.local.CountryDao
import lv.dreimanis.countryencyclopedia.data.local.CountryEntity
import lv.dreimanis.countryencyclopedia.data.local.FavoriteEntity
import lv.dreimanis.countryencyclopedia.data.remote.CountriesApi
import lv.dreimanis.countryencyclopedia.data.remote.CountryDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryRepository @Inject constructor(
    private val api: CountriesApi,
    private val dao: CountryDao,
) {

    fun search(query: String): Flow<List<CountryEntity>> =
        dao.search(query.trim().normalizeForSearch())

    suspend fun refreshIfEmpty() {
        if (dao.count() > 0) return

        val countries = mutableListOf<CountryDto>()
        var offset = 0
        do {
            val page = api.getCountries(offset = offset).data
            countries += page.objects
            offset += page.objects.size
        } while (page.meta.more)

        dao.upsertAll(countries.map { it.toEntity() })
    }

    suspend fun getCountry(uuid: String): CountryEntity? = dao.getByUuid(uuid)

    suspend fun getPopulationRank(population: Long): Int = dao.populationRank(population)

    suspend fun getCountriesByCodes(alpha3Codes: List<String>): List<CountryEntity> =
        dao.getByAlpha3(alpha3Codes)

    suspend fun getCountriesSpeaking(
        languageCode: String,
        excludeUuid: String
    ): List<CountryEntity> =
        dao.getAll().filter { country ->
            country.uuid != excludeUuid && country.languages.any { it.code == languageCode }
        }

    fun isFavorite(uuid: String): Flow<Boolean> = dao.isFavorite(uuid)

    fun favoriteUuids(): Flow<List<String>> = dao.favoriteUuids()

    suspend fun setFavorite(uuid: String, favorite: Boolean) {
        if (favorite) {
            dao.addFavorite(FavoriteEntity(uuid))
        } else {
            dao.removeFavorite(uuid)
        }
    }
}