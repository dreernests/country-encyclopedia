package lv.dreimanis.countryencyclopedia.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountriesResponseDto(
    val data: CountriesDataDto,
)

@Serializable
data class CountriesDataDto(
    val objects: List<CountryDto>,
    val meta: MetaDto,
)

@Serializable
data class MetaDto(
    val total: Int,
    val count: Int = 0,
    val limit: Int = 0,
    val offset: Int = 0,
    val more: Boolean = false,
)

@Serializable
data class CountryDto(
    val uuid: String,
    val names: NamesDto,
    // Some territories (e.g. Abkhazia) have no codes or flag.
    val codes: CodesDto = CodesDto(),
    val population: Long? = null, // Missing for uninhabited territories.
    val flag: FlagDto = FlagDto(),
    val borders: List<String> = emptyList(),
    val languages: List<LanguageDto> = emptyList(),
    val capitals: List<CapitalDto> = emptyList(),
    val coordinates: CoordinatesDto? = null,
)

@Serializable
data class NamesDto(
    val common: String,
    val official: String = "",
    // Keyed by ISO 639-3 language code, e.g. "lav", "est".
    val native: Map<String, NameDto> = emptyMap(),
    val translations: Map<String, NameDto> = emptyMap(),
    val alternates: List<String> = emptyList(),
)

@Serializable
data class NameDto(
    val common: String = "",
    val official: String = "",
)

@Serializable
data class CodesDto(
    @SerialName("alpha_2") val alpha2: String = "",
    @SerialName("alpha_3") val alpha3: String = "",
)

@Serializable
data class FlagDto(
    val emoji: String = "",
)

@Serializable
data class LanguageDto(
    val name: String,
    @SerialName("native_name") val nativeName: String = "",
    val bcp47: String = "",
    @SerialName("iso639_1") val iso6391: String = "",
    @SerialName("iso639_2b") val iso6392b: String = "",
    @SerialName("iso639_2t") val iso6392t: String = "",
    @SerialName("iso639_3") val iso6393: String = "",
)

@Serializable
data class CapitalDto(
    val name: String,
    val coordinates: CoordinatesDto? = null,
    val attributes: CapitalAttributesDto = CapitalAttributesDto(),
)

// Countries like South Africa have several capitals with different roles.
@Serializable
data class CapitalAttributesDto(
    val primary: Boolean = false,
    val administrative: Boolean = false,
    val constitutional: Boolean = false,
    val executive: Boolean = false,
    val judicial: Boolean = false,
    val legislative: Boolean = false,
)

@Serializable
data class CoordinatesDto(
    val lat: Double,
    val lng: Double,
)
