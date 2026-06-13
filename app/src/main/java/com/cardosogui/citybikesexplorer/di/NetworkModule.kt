package com.cardosogui.citybikesexplorer.di

import com.cardosogui.citybikesexplorer.data.remote.CityBikesApi
import com.cardosogui.citybikesexplorer.data.remote.LocalJsonInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Placeholder base URL: LocalJsonInterceptor short-circuits every call,
    // so nothing is ever requested from this host until the interceptor is removed.
    // Retrofit still validates it, so it must keep the https:// scheme and trailing slash.
    private const val BASE_URL = "https://itshouldbearealurlhere.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(localJsonInterceptor: LocalJsonInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            )
            .addInterceptor(localJsonInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideCityBikesApi(retrofit: Retrofit): CityBikesApi =
        retrofit.create(CityBikesApi::class.java)
}
