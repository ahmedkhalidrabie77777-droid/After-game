package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val titleAr: String = "",
    val developer: String = "",
    val releaseYear: Int = 2024,
    val platforms: String = "PC, PS5, Xbox",
    val genres: String = "Action RPG",
    val coverUrl: String = "",
    val gradientColors: String = "#8B5CF6,#06B6D4",
    val synopsis: String = "",
    val communityRating: Float = 4.5f,
    val userRating: Float = 0f, // 0 = unrated, 0.5 to 5.0 stars
    val status: String = "NONE", // NONE, PLAYING, COMPLETED, BACKLOG, DROPPED, MASTERED
    val hoursPlayed: Float = 0f,
    val userReview: String = "",
    val reviewTitle: String = "",
    val pros: String = "",
    val cons: String = "",
    val subGameplayScore: Float = 0f,
    val subStoryScore: Float = 0f,
    val subVisualsScore: Float = 0f,
    val subAudioScore: Float = 0f,
    val isFavorite: Boolean = false,
    val loggedDate: Long = 0L,
    val platformPlayed: String = "PC"
) {
    val isRated: Boolean
        get() = userRating > 0f

    val isLogged: Boolean
        get() = isRated || status != "NONE" || hoursPlayed > 0f || userReview.isNotBlank()
}
