package lv.dreimanis.countryencyclopedia.data

import lv.dreimanis.countryencyclopedia.data.remote.CapitalAttributesDto
import lv.dreimanis.countryencyclopedia.data.remote.CapitalDto
import lv.dreimanis.countryencyclopedia.data.remote.CoordinatesDto
import lv.dreimanis.countryencyclopedia.data.remote.CountryDto
import lv.dreimanis.countryencyclopedia.data.remote.NameDto
import lv.dreimanis.countryencyclopedia.data.remote.NamesDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryMapperTest {

    @Test
    fun `normalizeForSearch removes accents and lowercases`() {
        assertEquals("letonia", "Letónia".normalizeForSearch())
        assertEquals("zambia", "ŽĀMBĪĀ".normalizeForSearch())
        assertEquals("lotwa", "Łotwa".normalizeForSearch())
    }

    @Test
    fun `toEntity uses primary capital and adds translations to search text`() {
        val dto = CountryDto(
            uuid = "1",
            names = NamesDto(
                common = "Latvia",
                official = "Republic of Latvia",
                translations = mapOf("est" to NameDto(common = "Läti")),
            ),
            capitals = listOf(
                CapitalDto(name = "Other", coordinates = CoordinatesDto(1.0, 1.0)),
                CapitalDto(
                    name = "Riga",
                    coordinates = CoordinatesDto(56.95, 24.1),
                    attributes = CapitalAttributesDto(primary = true),
                ),
            ),
        )

        val entity = dto.toEntity()

        assertEquals("Riga", entity.capitalName)
        assertTrue("lati" in entity.searchText)
    }
}
