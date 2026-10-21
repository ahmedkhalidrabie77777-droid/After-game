package com.example.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirebaseReview(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String = "",
    val gameId: String = "",
    val gameTitle: String = "",
    val gameTitleAr: String = "",
    val rating: Double = 0.0,
    val status: String = "COMPLETED",
    val hoursPlayed: Double = 0.0,
    val reviewTitle: String = "",
    val reviewBody: String = "",
    val platformPlayed: String = "PC",
    val pros: String = "",
    val cons: String = "",
    val subGameplayScore: Double = 0.0,
    val subStoryScore: Double = 0.0,
    val subVisualsScore: Double = 0.0,
    val subAudioScore: Double = 0.0,
    val likesCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseComment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val commentText: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseUserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val bio: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
