package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RawgGamesResponse(
    @Json(name = "count") val count: Int = 0,
    @Json(name = "results") val results: List<RawgGameDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RawgGameDto(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "released") val released: String? = null,
    @Json(name = "background_image") val backgroundImage: String? = null,
    @Json(name = "rating") val rating: Float = 0f,
    @Json(name = "rating_top") val ratingTop: Int = 5,
    @Json(name = "ratings_count") val ratingsCount: Int = 0,
    @Json(name = "metacritic") val metacritic: Int? = null,
    @Json(name = "playtime") val playtime: Int = 0,
    @Json(name = "genres") val genres: List<RawgGenreDto> = emptyList(),
    @Json(name = "platforms") val platforms: List<RawgPlatformSlotDto> = emptyList(),
    @Json(name = "description_raw") val descriptionRaw: String? = null
)

@JsonClass(generateAdapter = true)
data class RawgGenreDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "slug") val slug: String = ""
)

@JsonClass(generateAdapter = true)
data class RawgPlatformSlotDto(
    @Json(name = "platform") val platform: RawgPlatformDto? = null
)

@JsonClass(generateAdapter = true)
data class RawgPlatformDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "slug") val slug: String = ""
)
