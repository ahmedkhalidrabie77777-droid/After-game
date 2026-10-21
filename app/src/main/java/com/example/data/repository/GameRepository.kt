package com.example.data.repository

import com.example.data.local.GameDao
import com.example.data.local.InitialGameData
import com.example.data.model.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {
    val allGames: Flow<List<GameItem>> = gameDao.getAllGames()
    val loggedGames: Flow<List<GameItem>> = gameDao.getLoggedGames()
    val favoriteGames: Flow<List<GameItem>> = gameDao.getFavoriteGames()

    fun getGameById(id: Long): Flow<GameItem?> = gameDao.getGameById(id)

    suspend fun insertGame(game: GameItem): Long = withContext(Dispatchers.IO) {
        gameDao.insertGame(game)
    }

    suspend fun updateGame(game: GameItem) = withContext(Dispatchers.IO) {
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: GameItem) = withContext(Dispatchers.IO) {
        gameDao.deleteGame(game)
    }

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        if (gameDao.getGameCount() == 0) {
            gameDao.insertAll(InitialGameData.getInitialGames())
        }
    }

    suspend fun resetToDefaults() = withContext(Dispatchers.IO) {
        gameDao.insertAll(InitialGameData.getInitialGames())
    }
}
