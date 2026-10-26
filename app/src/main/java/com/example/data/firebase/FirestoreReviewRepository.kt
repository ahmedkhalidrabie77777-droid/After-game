package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreReviewRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to access community reviews.")
    }

    fun observeAllCommunityReviews(): Flow<List<FirebaseReview>> = callbackFlow {
        val path = "reviews"
        val registration = db.collection(path)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.toObjects(FirebaseReview::class.java)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                    trySend(reviews)
                }
            }
        awaitClose { registration.remove() }
    }

    fun observeComments(reviewId: String): Flow<List<FirebaseComment>> = callbackFlow {
        val path = "reviews/$reviewId/comments"
        val registration = db.collection("reviews").document(reviewId).collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comments = snapshot.toObjects(FirebaseComment::class.java)
                        .sortedBy { it.createdAt?.seconds ?: 0L }
                    trySend(comments)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun publishReview(review: FirebaseReview): String {
        val uid = requireUserId()
        val author = auth.currentUser?.displayName?.ifBlank { "Gamer" } ?: "Gamer"
        val photo = auth.currentUser?.photoUrl?.toString() ?: ""
        val docRef = if (review.id.isNotBlank()) db.collection("reviews").document(review.id) else db.collection("reviews").document()

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "authorName" to author,
            "authorPhotoUrl" to photo,
            "gameId" to review.gameId,
            "gameTitle" to review.gameTitle,
            "gameTitleAr" to review.gameTitleAr,
            "rating" to review.rating,
            "status" to review.status,
            "hoursPlayed" to review.hoursPlayed,
            "reviewTitle" to review.reviewTitle,
            "reviewBody" to review.reviewBody,
            "platformPlayed" to review.platformPlayed,
            "pros" to review.pros,
            "cons" to review.cons,
            "subGameplayScore" to review.subGameplayScore,
            "subStoryScore" to review.subStoryScore,
            "subVisualsScore" to review.subVisualsScore,
            "subAudioScore" to review.subAudioScore,
            "likesCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun updateReview(review: FirebaseReview) {
        val uid = requireUserId()
        require(review.id.isNotBlank()) { "Review ID must not be blank for update" }
        val docRef = db.collection("reviews").document(review.id)

        val updates = hashMapOf<String, Any>(
            "rating" to review.rating,
            "status" to review.status,
            "hoursPlayed" to review.hoursPlayed,
            "reviewTitle" to review.reviewTitle,
            "reviewBody" to review.reviewBody,
            "platformPlayed" to review.platformPlayed,
            "pros" to review.pros,
            "cons" to review.cons,
            "subGameplayScore" to review.subGameplayScore,
            "subStoryScore" to review.subStoryScore,
            "subVisualsScore" to review.subVisualsScore,
            "subAudioScore" to review.subAudioScore,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.update(updates).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteReview(reviewId: String) {
        requireUserId()
        val docRef = db.collection("reviews").document(reviewId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun likeReview(reviewId: String) {
        requireUserId()
        val docRef = db.collection("reviews").document(reviewId)
        try {
            docRef.update("likesCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun addComment(reviewId: String, commentText: String) {
        val uid = requireUserId()
        val author = auth.currentUser?.displayName?.ifBlank { "Gamer" } ?: "Gamer"
        val commentsRef = db.collection("reviews").document(reviewId).collection("comments")
        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "authorName" to author,
            "commentText" to commentText.trim(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        try {
            commentsRef.add(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, commentsRef.path)
            throw e
        }
    }

    suspend fun logPlaytime(
        gameId: Long,
        gameTitle: String,
        gameTitleAr: String,
        hoursPlayed: Float,
        currentStatus: String = "PLAYING",
        currentRating: Float = 0f
    ) {
        val uid = requireUserId()
        val author = auth.currentUser?.displayName?.ifBlank { "Gamer" } ?: "Gamer"
        val photo = auth.currentUser?.photoUrl?.toString() ?: ""
        val docId = "rev_${uid}_$gameId"
        val docRef = db.collection("reviews").document(docId)

        val validStatus = when (currentStatus) {
            "PLAYING", "COMPLETED", "BACKLOG", "MASTERED", "DROPPED" -> currentStatus
            else -> "PLAYING"
        }

        try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val updates = hashMapOf<String, Any>(
                    "hoursPlayed" to hoursPlayed.toDouble(),
                    "status" to validStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updates).await()
            } else {
                val payload = hashMapOf<String, Any>(
                    "userId" to uid,
                    "authorName" to author,
                    "authorPhotoUrl" to photo,
                    "gameId" to "game_$gameId",
                    "gameTitle" to gameTitle.ifBlank { "Game" },
                    "gameTitleAr" to gameTitleAr,
                    "rating" to currentRating.toDouble().coerceIn(0.0, 5.0),
                    "status" to validStatus,
                    "hoursPlayed" to hoursPlayed.toDouble(),
                    "reviewTitle" to "",
                    "reviewBody" to "",
                    "platformPlayed" to "PC",
                    "pros" to "",
                    "cons" to "",
                    "subGameplayScore" to 0.0,
                    "subStoryScore" to 0.0,
                    "subVisualsScore" to 0.0,
                    "subAudioScore" to 0.0,
                    "likesCount" to 0L,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }
}

