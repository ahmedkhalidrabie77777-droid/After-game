package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseComment
import com.example.data.firebase.FirebaseReview
import com.example.data.firebase.FirestoreReviewRepository
import com.example.data.local.AppDatabase
import com.example.data.model.GameItem
import com.example.data.remote.RawgApiClient
import com.example.data.repository.GameRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class NavigationTab {
    EXPLORE,
    COMMUNITY,
    DIARY,
    STATS
}

enum class SortOption(val titleAr: String, val titleEn: String) {
    RECENT_LOGGED("الأحدث تسجيلاً", "Recently Logged"),
    HIGHEST_RATED("الأعلى تقييماً", "Highest Rated"),
    MOST_HOURS("الأكثر لعباً", "Most Played Hours"),
    TITLE("الاسم أبجدياً", "Title A-Z"),
    RELEASE_YEAR("سنة الإصدار", "Release Year")
}

data class GamerStats(
    val totalLogged: Int = 0,
    val completedCount: Int = 0,
    val playingCount: Int = 0,
    val backlogCount: Int = 0,
    val totalHours: Float = 0f,
    val averageScore: Float = 0f,
    val topRatedGames: List<GameItem> = emptyList(),
    val favoriteGenres: List<Pair<String, Int>> = emptyList(),
    val platformCounts: List<Pair<String, Int>> = emptyList()
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository: GameRepository
    private val firestoreRepository: FirestoreReviewRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        localRepository = GameRepository(database.gameDao())
        firestoreRepository = FirestoreReviewRepository(application)

        viewModelScope.launch {
            localRepository.seedIfEmpty()
        }
    }

    val rawGames: StateFlow<List<GameItem>> = localRepository.allGames
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Real-time Community Reviews from Firestore
    val communityReviews: StateFlow<List<FirebaseReview>> = firestoreRepository.observeAllCommunityReviews()
        .catch { err ->
            Log.e("GameViewModel", "Error observing community reviews", err)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeTab = MutableStateFlow(NavigationTab.EXPLORE)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _selectedPlatform = MutableStateFlow("All")
    val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

    private val _diaryStatusFilter = MutableStateFlow("ALL")
    val diaryStatusFilter: StateFlow<String> = _diaryStatusFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RECENT_LOGGED)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _selectedGameForReview = MutableStateFlow<GameItem?>(null)
    val selectedGameForReview: StateFlow<GameItem?> = _selectedGameForReview.asStateFlow()

    private val _selectedGameForDetail = MutableStateFlow<GameItem?>(null)
    val selectedGameForDetail: StateFlow<GameItem?> = _selectedGameForDetail.asStateFlow()

    private val _isAddGameDialogOpen = MutableStateFlow(false)
    val isAddGameDialogOpen: StateFlow<Boolean> = _isAddGameDialogOpen.asStateFlow()

    // RAWG Live API Search Results
    private val _rawgApiGames = MutableStateFlow<List<GameItem>>(emptyList())
    val rawgApiGames: StateFlow<List<GameItem>> = _rawgApiGames.asStateFlow()

    private val _isSearchingRawg = MutableStateFlow(false)
    val isSearchingRawg: StateFlow<Boolean> = _isSearchingRawg.asStateFlow()

    // Active review for viewing comments thread
    private val _selectedReviewForComments = MutableStateFlow<FirebaseReview?>(null)
    val selectedReviewForComments: StateFlow<FirebaseReview?> = _selectedReviewForComments.asStateFlow()

    private val _activeComments = MutableStateFlow<List<FirebaseComment>>(emptyList())
    val activeComments: StateFlow<List<FirebaseComment>> = _activeComments.asStateFlow()

    // Combined explore list (Local DB + Live RAWG API results)
    val exploreGames: StateFlow<List<GameItem>> = combine(
        rawGames,
        _rawgApiGames,
        _searchQuery,
        _selectedGenre
    ) { localGames, apiGames, query, genre ->
        val source = if (apiGames.isNotEmpty() && query.isNotBlank()) {
            apiGames
        } else {
            localGames
        }

        source.filter { game ->
            val matchesQuery = query.isBlank() ||
                game.title.contains(query, ignoreCase = true) ||
                game.titleAr.contains(query, ignoreCase = true) ||
                game.developer.contains(query, ignoreCase = true)

            val matchesGenre = genre == "All" || game.genres.contains(genre, ignoreCase = true)

            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Diary games
    val diaryGames: StateFlow<List<GameItem>> = combine(
        rawGames,
        _diaryStatusFilter,
        _selectedPlatform,
        _sortOption
    ) { games, statusFilter, platform, sort ->
        val logged = games.filter { game ->
            val isLoggedMatch = when (statusFilter) {
                "ALL" -> game.isLogged
                "FAVORITES" -> game.isFavorite
                else -> game.status.equals(statusFilter, ignoreCase = true)
            }

            val isPlatformMatch = platform == "All" ||
                game.platformPlayed.contains(platform, ignoreCase = true) ||
                game.platforms.contains(platform, ignoreCase = true)

            isLoggedMatch && isPlatformMatch
        }

        when (sort) {
            SortOption.RECENT_LOGGED -> logged.sortedByDescending { it.loggedDate }
            SortOption.HIGHEST_RATED -> logged.sortedByDescending { it.userRating }
            SortOption.MOST_HOURS -> logged.sortedByDescending { it.hoursPlayed }
            SortOption.TITLE -> logged.sortedBy { it.title }
            SortOption.RELEASE_YEAR -> logged.sortedByDescending { it.releaseYear }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gamer stats
    val stats: StateFlow<GamerStats> = rawGames.combine(_activeTab) { games, _ ->
        val logged = games.filter { it.isLogged }
        val completed = logged.filter { it.status == "COMPLETED" || it.status == "MASTERED" }.size
        val playing = logged.filter { it.status == "PLAYING" }.size
        val backlog = logged.filter { it.status == "BACKLOG" }.size
        val totalHours = logged.map { it.hoursPlayed }.sum()

        val rated = logged.filter { it.userRating > 0f }
        val avgScore = if (rated.isNotEmpty()) rated.map { it.userRating }.average().toFloat() else 0f

        val topRated = rated.sortedByDescending { it.userRating }.take(5)

        val genreMap = mutableMapOf<String, Int>()
        logged.forEach { game ->
            game.genres.split(",").forEach { rawG ->
                val g = rawG.trim()
                if (g.isNotBlank()) {
                    genreMap[g] = (genreMap[g] ?: 0) + 1
                }
            }
        }
        val favoriteGenres = genreMap.toList().sortedByDescending { it.second }.take(4)

        val platformMap = mutableMapOf<String, Int>()
        logged.forEach { game ->
            val p = if (game.platformPlayed.isNotBlank()) game.platformPlayed else "PC"
            platformMap[p] = (platformMap[p] ?: 0) + 1
        }
        val platformCounts = platformMap.toList().sortedByDescending { it.second }

        GamerStats(
            totalLogged = logged.size,
            completedCount = completed,
            playingCount = playing,
            backlogCount = backlog,
            totalHours = totalHours,
            averageScore = avgScore,
            topRatedGames = topRated,
            favoriteGenres = favoriteGenres,
            platformCounts = platformCounts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GamerStats())

    fun setActiveTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.length >= 3) {
            searchRawgLive(query)
        } else if (query.isBlank()) {
            _rawgApiGames.value = emptyList()
        }
    }

    private fun searchRawgLive(query: String) {
        viewModelScope.launch {
            _isSearchingRawg.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    RawgApiClient.service.getGames(
                        apiKey = RawgApiClient.apiKey,
                        search = query,
                        pageSize = 15
                    )
                }
                val converted = response.results.map { dto ->
                    GameItem(
                        id = dto.id,
                        title = dto.name,
                        titleAr = "",
                        developer = "RAWG Database",
                        releaseYear = dto.released?.take(4)?.toIntOrNull() ?: 2024,
                        platforms = dto.platforms.mapNotNull { it.platform?.name }.take(4).joinToString(", ").ifBlank { "PC, Console" },
                        genres = dto.genres.map { it.name }.take(3).joinToString(", ").ifBlank { "Action" },
                        coverUrl = dto.backgroundImage ?: "",
                        communityRating = (dto.rating).coerceIn(0f, 5f),
                        synopsis = "بيانات مباشرة من RAWG Video Games Database لـ ${dto.name} مع ${dto.ratingsCount} تقييم عالمي."
                    )
                }
                _rawgApiGames.value = converted
            } catch (e: Exception) {
                Log.w("GameViewModel", "RAWG search fallback to local", e)
            } finally {
                _isSearchingRawg.value = false
            }
        }
    }

    fun setSelectedGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun setSelectedPlatform(platform: String) {
        _selectedPlatform.value = platform
    }

    fun setDiaryStatusFilter(filter: String) {
        _diaryStatusFilter.value = filter
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun openReviewDialog(game: GameItem) {
        _selectedGameForReview.value = game
    }

    fun closeReviewDialog() {
        _selectedGameForReview.value = null
    }

    fun openGameDetail(game: GameItem) {
        _selectedGameForDetail.value = game
    }

    fun closeGameDetail() {
        _selectedGameForDetail.value = null
    }

    fun setAddGameDialogOpen(isOpen: Boolean) {
        _isAddGameDialogOpen.value = isOpen
    }

    fun openCommentsForReview(review: FirebaseReview) {
        _selectedReviewForComments.value = review
        viewModelScope.launch {
            firestoreRepository.observeComments(review.id).collect { comments ->
                _activeComments.value = comments
            }
        }
    }

    fun closeComments() {
        _selectedReviewForComments.value = null
        _activeComments.value = emptyList()
    }

    fun likeReview(reviewId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.likeReview(reviewId)
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error liking review", e)
            }
        }
    }

    fun addComment(reviewId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            try {
                firestoreRepository.addComment(reviewId, text)
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error adding comment", e)
            }
        }
    }

    fun saveReview(
        gameId: Long,
        userRating: Float,
        status: String,
        hoursPlayed: Float,
        reviewTitle: String,
        userReview: String,
        pros: String,
        cons: String,
        subGameplayScore: Float,
        subStoryScore: Float,
        subVisualsScore: Float,
        subAudioScore: Float,
        platformPlayed: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            // Find game from local or API results
            val existing = rawGames.value.find { it.id == gameId }
                ?: _rawgApiGames.value.find { it.id == gameId }
                ?: return@launch

            val updated = existing.copy(
                userRating = userRating,
                status = status,
                hoursPlayed = hoursPlayed,
                reviewTitle = reviewTitle.trim(),
                userReview = userReview.trim(),
                pros = pros.trim(),
                cons = cons.trim(),
                subGameplayScore = subGameplayScore,
                subStoryScore = subStoryScore,
                subVisualsScore = subVisualsScore,
                subAudioScore = subAudioScore,
                platformPlayed = platformPlayed,
                isFavorite = isFavorite,
                loggedDate = if (existing.loggedDate > 0L) existing.loggedDate else System.currentTimeMillis()
            )

            // 1. Save to local SQLite Room
            localRepository.insertGame(updated)
            if (_selectedGameForDetail.value?.id == gameId) {
                _selectedGameForDetail.value = updated
            }

            // 2. Publish to Cloud Firebase Firestore so ALL users see it in real-time!
            try {
                val cloudReview = FirebaseReview(
                    gameId = "game_${existing.id}",
                    gameTitle = existing.title,
                    gameTitleAr = existing.titleAr,
                    rating = userRating.toDouble(),
                    status = status,
                    hoursPlayed = hoursPlayed.toDouble(),
                    reviewTitle = reviewTitle.trim(),
                    reviewBody = userReview.trim(),
                    platformPlayed = platformPlayed,
                    pros = pros.trim(),
                    cons = cons.trim(),
                    subGameplayScore = subGameplayScore.toDouble(),
                    subStoryScore = subStoryScore.toDouble(),
                    subVisualsScore = subVisualsScore.toDouble(),
                    subAudioScore = subAudioScore.toDouble()
                )
                firestoreRepository.publishReview(cloudReview)
            } catch (e: Exception) {
                Log.w("GameViewModel", "Cloud sync skipped: ${e.message}")
            }

            closeReviewDialog()
        }
    }

    fun toggleFavorite(game: GameItem) {
        viewModelScope.launch {
            val updated = game.copy(isFavorite = !game.isFavorite)
            localRepository.updateGame(updated)
            if (_selectedGameForDetail.value?.id == game.id) {
                _selectedGameForDetail.value = updated
            }
        }
    }

    fun addCustomGame(
        title: String,
        titleAr: String,
        developer: String,
        releaseYear: Int,
        genres: String,
        platforms: String,
        synopsis: String,
        userRating: Float,
        status: String,
        hoursPlayed: Float,
        userReview: String,
        platformPlayed: String
    ) {
        viewModelScope.launch {
            val newGame = GameItem(
                title = title.trim(),
                titleAr = titleAr.trim(),
                developer = developer.trim().ifBlank { "Independent" },
                releaseYear = if (releaseYear in 1970..2030) releaseYear else 2024,
                genres = genres.trim().ifBlank { "Action" },
                platforms = platforms.trim().ifBlank { "PC, Console" },
                synopsis = synopsis.trim().ifBlank { "لعبة مخصصة مسجلة في المكتبة." },
                communityRating = 4.5f,
                userRating = userRating,
                status = status,
                hoursPlayed = hoursPlayed,
                userReview = userReview.trim(),
                platformPlayed = platformPlayed,
                loggedDate = if (userRating > 0f || status != "NONE") System.currentTimeMillis() else 0L
            )
            val generatedId = localRepository.insertGame(newGame)

            // Also publish to community Firestore if reviewed
            if (userRating > 0f || userReview.isNotBlank()) {
                try {
                    val cloudReview = FirebaseReview(
                        gameId = "game_$generatedId",
                        gameTitle = newGame.title,
                        gameTitleAr = newGame.titleAr,
                        rating = userRating.toDouble(),
                        status = status,
                        hoursPlayed = hoursPlayed.toDouble(),
                        reviewBody = userReview.trim(),
                        platformPlayed = platformPlayed
                    )
                    firestoreRepository.publishReview(cloudReview)
                } catch (e: Exception) {
                    Log.w("GameViewModel", "Cloud publish skipped: ${e.message}")
                }
            }

            _isAddGameDialogOpen.value = false
        }
    }

    fun deleteGame(game: GameItem) {
        viewModelScope.launch {
            localRepository.deleteGame(game)
            if (_selectedGameForDetail.value?.id == game.id) {
                _selectedGameForDetail.value = null
            }
            if (_selectedGameForReview.value?.id == game.id) {
                _selectedGameForReview.value = null
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            localRepository.resetToDefaults()
        }
    }
}
