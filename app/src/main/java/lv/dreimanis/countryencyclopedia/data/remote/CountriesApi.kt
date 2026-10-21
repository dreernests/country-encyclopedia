package lv.dreimanis.countryencyclopedia.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface CountriesApi {

    @GET("countries/v5")
    suspend fun getCountries(
        @Query("limit") limit: Int = PAGE_SIZE,
        @Query("offset") offset: Int,
        @Query("response_fields") fields: String = RESPONSE_FIELDS,
    ): CountriesResponseDto

    companion object {
        const val BASE_URL = "https://api.restcountries.com/"

        /** Maximum page size on the free plan. */
        const val PAGE_SIZE = 100

        /** Only the fields the app uses, to keep responses small. */
        const val RESPONSE_FIELDS = "uuid,names.common,names.official,names.native," +
            "names.translations,names.alternates,codes.alpha_2,codes.alpha_3,population," +
            "flag.emoji,borders,languages,capitals,coordinates"
    }
}
