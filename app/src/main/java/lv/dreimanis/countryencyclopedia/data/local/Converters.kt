package lv.dreimanis.countryencyclopedia.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun stringListToJson(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun jsonToStringList(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun languagesToJson(value: List<LanguageEntity>): String = Json.encodeToString(value)

    @TypeConverter
    fun jsonToLanguages(value: String): List<LanguageEntity> = Json.decodeFromString(value)
}