package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface RawgApiService {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("genres") genres: String? = null,
        @Query("ordering") ordering: String? = "-added",
        @Query("dates") dates: String? = null,
        @Query("page") page: Int = 1
    ): RawgGamesResponse
}

object RawgApiClient {
    private const val BASE_URL = "https://api.rawg.io/api/"

    // Demo RAWG fallback public development key (or injected from BuildConfig.RAWG_API_KEY)
    val apiKey: String
        get() = try {
            val key = BuildConfig.RAWG_API_KEY
            if (key.isNotBlank() && key != "DEFAULT_RAWG_API_KEY") key else "c542e67aec3a4340908f9de9e86038af"
        } catch (e: Exception) {
            "c542e67aec3a4340908f9de9e86038af"
        }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val service: RawgApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RawgApiService::class.java)
    }
}
