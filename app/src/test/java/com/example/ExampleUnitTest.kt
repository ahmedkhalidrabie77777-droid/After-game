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
  fun testRawgApiDtoParsing() = kotlinx.coroutines.runBlocking {
    val response = com.example.data.remote.RawgApiClient.service.getGames(
      apiKey = com.example.data.remote.RawgApiClient.apiKey,
      ordering = "-added",
      pageSize = 5
    )
    assertNotNull(response.results)
    assertTrue(response.results!!.isNotEmpty())
    assertNotNull(response.results!![0].name)
  }

  @Test
  fun testRawgSearchGod() = kotlinx.coroutines.runBlocking {
    val response = com.example.data.remote.RawgApiClient.service.getGames(
      apiKey = com.example.data.remote.RawgApiClient.apiKey,
      search = "god",
      pageSize = 10
    )
    println("RAWG search count: ${response.count}, results: ${response.results?.size}")
    response.results?.forEach {
      println("Game: ${it.name}, bg: ${it.backgroundImage}")
    }
    assertNotNull(response.results)
    assertTrue(response.results!!.isNotEmpty())
  }

  @Test
  fun testRawgEndpoints() = kotlinx.coroutines.runBlocking {
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
