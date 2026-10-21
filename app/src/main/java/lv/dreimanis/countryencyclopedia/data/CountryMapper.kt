package lv.dreimanis.countryencyclopedia.data

import lv.dreimanis.countryencyclopedia.data.local.CountryEntity
import lv.dreimanis.countryencyclopedia.data.local.LanguageEntity
import lv.dreimanis.countryencyclopedia.data.remote.CountryDto
import java.text.Normalizer

fun CountryDto.toEntity(): CountryEntity {
    val capital = capitals.firstOrNull { it.attributes.primary } ?: capitals.firstOrNull()

    return CountryEntity(
        uuid = uuid,
        commonName = names.common,
        officialName = names.official,
        alpha2 = codes.alpha2,
        alpha3 = codes.alpha3,
        population = population,
        flagEmoji = flag.emoji,
        capitalName = capital?.name,
        capitalLat = capital?.coordinates?.lat,
        capitalLng = capital?.coordinates?.lng,
        lat = coordinates?.lat,
        lng = coordinates?.lng,
        borders = borders,
        languages = languages.map { LanguageEntity(it.name, it.nativeName, it.iso6393) },
        searchText = buildSearchText(),
    )
}

private fun CountryDto.buildSearchText(): String {
    val allNames = listOf(names.common, names.official, codes.alpha2, codes.alpha3) +
            names.native.values.flatMap { listOf(it.common, it.official) } +
            names.translations.values.flatMap { listOf(it.common, it.official) } +
            names.alternates

    return allNames.joinToString(" ") { it.normalizeForSearch() }
}

// Letters that Unicode doesn't split into "letter + accent", so Normalizer can't simplify them.
private val specialLetters = mapOf(
    'ł' to "l", 'ø' to "o", 'đ' to "d", 'ħ' to "h", 'ı' to "i",
    'ß' to "ss", 'æ' to "ae", 'œ' to "oe", 'þ' to "th",
)

fun String.normalizeForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .lowercase()
        .map { specialLetters[it] ?: it.toString() }
        .joinToString("")