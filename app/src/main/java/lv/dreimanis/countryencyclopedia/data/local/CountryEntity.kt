package lv.dreimanis.countryencyclopedia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val uuid: String,
    val commonName: String,
    val officialName: String,
    val alpha2: String,
    val alpha3: String,
    val population: Long?,
    val flagEmoji: String,
    val capitalName: String?,
    val capitalLat: Double?,
    val capitalLng: Double?,
    val lat: Double?,
    val lng: Double?,
    val borders: List<String>,
    val languages: List<LanguageEntity>,
    // Every known name of the country, lowercase and without accents.
    val searchText: String,
)

@Serializable
data class LanguageEntity(
    val name: String,
    val nativeName: String,
    val code: String,
)
