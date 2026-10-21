package com.example.data.firebase

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreReviewRepositoryRuleTest : FirestoreEmulatorTestBase() {

  private val aliceEmail = "alice@example.com"
  private val bobEmail = "bob@example.com"

  @Test
  fun publishReview_authenticatedUser_succeeds() = runBlocking {
    signInTestUser(aliceEmail)
    val repository = FirestoreReviewRepository(firestore)

    val review = FirebaseReview(
      gameId = "game_1",
      gameTitle = "Elden Ring",
      gameTitleAr = "ألدن رينج",
      rating = 5.0,
      status = "COMPLETED",
      hoursPlayed = 140.0,
      reviewTitle = "Great soulslike",
      reviewBody = "Challenging and rewarding gameplay."
    )

    val docId = repository.publishReview(review)
    assertNotNull(docId)
    assertTrue(docId.isNotBlank())
  }

  @Test
  fun unauthenticatedUser_cannotPublishReview() = runBlocking {
    auth.signOut()
    val repository = FirestoreReviewRepository(firestore)

    val review = FirebaseReview(
      gameId = "game_2",
      gameTitle = "The Witcher 3",
      rating = 5.0,
      status = "COMPLETED"
    )

    var caught = false
    try {
      repository.publishReview(review)
    } catch (e: Exception) {
      caught = true
    }
    assertTrue("Unauthenticated review publish should be rejected", caught)
  }

  @Test
  fun communityUser_canCommentOnReview() = runBlocking {
    signInTestUser(aliceEmail)
    val repositoryAlice = FirestoreReviewRepository(firestore)
    val review = FirebaseReview(
      gameId = "game_3",
      gameTitle = "Baldur's Gate 3",
      rating = 4.5,
      status = "PLAYING"
    )
    val docId = repositoryAlice.publishReview(review)

    // Bob signs in and comments
    signInTestUser(bobEmail)
    val repositoryBob = FirestoreReviewRepository(firestore)
    repositoryBob.addComment(docId, "Awesome review, loved Act 1 as well!")
  }
}
