package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testRawgApiDtoParsing() {
    kotlinx.coroutines.runBlocking {
      val response = com.example.data.remote.RawgApiClient.service.getGames(
        apiKey = com.example.data.remote.RawgApiClient.apiKey,
        ordering = "-added",
        pageSize = 5
      )
      assertNotNull(response.results)
      assertTrue(response.results!!.isNotEmpty())
      assertNotNull(response.results!![0].name)
    }
  }

  @Test
  fun testRawgSearchSpider() {
    kotlinx.coroutines.runBlocking {
      val response = com.example.data.remote.RawgApiClient.service.getGames(
        apiKey = com.example.data.remote.RawgApiClient.apiKey,
        search = "spider",
        searchPrecise = true,
        pageSize = 20
      )
      assertNotNull(response.results)
      assertTrue("Search results must not be empty", response.results!!.isNotEmpty())
      val spiderGames = response.results!!.filter { it.name?.contains("spider", ignoreCase = true) == true }
      assertTrue("Multiple games must contain spider in name", spiderGames.size >= 5)
      println("Found ${spiderGames.size} spider games out of ${response.results!!.size}")

      val optimized = com.example.data.remote.GameSearchHelper.optimizePosterUrl(
        "https://media.rawg.io/media/games/20a/20aa03a10cda45239fe22d035c0ebe64.jpg"
      )
      assertTrue(optimized.contains("/crop/600/400/"))
    }
  }

  @Test
  fun testRawgEndpoints() {
    kotlinx.coroutines.runBlocking {

    val pop = com.example.data.remote.RawgApiClient.service.getGames(
      apiKey = com.example.data.remote.RawgApiClient.apiKey,
      ordering = "-added",
      pageSize = 10
    )
    println("=== POPULAR GAMES ===")
    pop.results?.forEach {
      println("${it.id}|${it.name}|${it.backgroundImage}")
    }

    val upcoming = com.example.data.remote.RawgApiClient.service.getGames(
      apiKey = com.example.data.remote.RawgApiClient.apiKey,
      dates = "2025-01-01,2027-12-31",
      ordering = "-added",
      pageSize = 10
    )
    println("=== UPCOMING GAMES ===")
    upcoming.results?.forEach {
      println("${it.id}|${it.name}|${it.backgroundImage}")
    }

    val newRel = com.example.data.remote.RawgApiClient.service.getGames(
      apiKey = com.example.data.remote.RawgApiClient.apiKey,
      dates = "2024-01-01,2026-12-31",
      ordering = "-released",
      pageSize = 10
    )
    println("=== NEW RELEASES ===")
    newRel.results?.forEach {
      println("${it.id}|${it.name}|${it.backgroundImage}")
    }
    }
  }
}

