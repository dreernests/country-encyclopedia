package lv.dreimanis.countryencyclopedia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import lv.dreimanis.countryencyclopedia.BuildConfig
import lv.dreimanis.countryencyclopedia.data.remote.CountriesApi
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        // The API returns many fields the app doesn't model.
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor())
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            },
        )
        .build()

    @Provides
    @Singleton
    fun provideCountriesApi(client: OkHttpClient, json: Json): CountriesApi = Retrofit.Builder()
        .baseUrl(CountriesApi.BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CountriesApi::class.java)

    private fun apiKeyInterceptor() = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.REST_COUNTRIES_API_KEY}")
            .build()
        chain.proceed(request)
    }
}
