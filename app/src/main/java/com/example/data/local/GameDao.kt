package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GameItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY id ASC")
    fun getAllGames(): Flow<List<GameItem>>

    @Query("SELECT * FROM games WHERE userRating > 0 OR status != 'NONE' OR hoursPlayed > 0 ORDER BY loggedDate DESC")
    fun getLoggedGames(): Flow<List<GameItem>>

    @Query("SELECT * FROM games WHERE isFavorite = 1 ORDER BY loggedDate DESC")
    fun getFavoriteGames(): Flow<List<GameItem>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: Long): Flow<GameItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameItem>)

    @Update
    suspend fun updateGame(game: GameItem)

    @Delete
    suspend fun deleteGame(game: GameItem)

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGameCount(): Int
}
