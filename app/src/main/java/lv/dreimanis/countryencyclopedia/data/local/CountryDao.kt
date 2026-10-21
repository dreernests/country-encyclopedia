package lv.dreimanis.countryencyclopedia.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    @Upsert
    suspend fun upsertAll(countries: List<CountryEntity>)

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun count(): Int

    @Query("SELECT * FROM countries WHERE searchText LIKE '%' || :query || '%' ORDER BY commonName")
    fun search(query: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): CountryEntity?

    @Query("SELECT COUNT(*) + 1 FROM countries WHERE population > :population")
    suspend fun populationRank(population: Long): Int

    @Query("SELECT * FROM countries WHERE alpha3 IN (:codes) ORDER BY commonName")
    suspend fun getByAlpha3(codes: List<String>): List<CountryEntity>

    @Query("SELECT * FROM countries ORDER BY commonName")
    suspend fun getAll(): List<CountryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE countryUuid = :uuid")
    suspend fun removeFavorite(uuid: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE countryUuid = :uuid)")
    fun isFavorite(uuid: String): Flow<Boolean>

    @Query("SELECT countryUuid FROM favorites")
    fun favoriteUuids(): Flow<List<String>>
}