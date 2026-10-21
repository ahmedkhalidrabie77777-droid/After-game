package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.GameItem
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    EXPLORE,
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
    private val repository: GameRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = GameRepository(database.gameDao())
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    val rawGames: StateFlow<List<GameItem>> = repository.allGames
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

    // Filtered games for Explore Tab
    val exploreGames: StateFlow<List<GameItem>> = combine(
        rawGames,
        _searchQuery,
        _selectedGenre
    ) { games, query, genre ->
        games.filter { game ->
            val matchesQuery = query.isBlank() ||
                game.title.contains(query, ignoreCase = true) ||
                game.titleAr.contains(query, ignoreCase = true) ||
                game.developer.contains(query, ignoreCase = true)

            val matchesGenre = genre == "All" || game.genres.contains(genre, ignoreCase = true)

            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered & Sorted games for Diary Tab
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

    // Statistics
    val stats: StateFlow<GamerStats> = rawGames.combine(_activeTab) { games, _ ->
        val logged = games.filter { it.isLogged }
        val completed = logged.filter { it.status == "COMPLETED" || it.status == "MASTERED" }.size
        val playing = logged.filter { it.status == "PLAYING" }.size
        val backlog = logged.filter { it.status == "BACKLOG" }.size
        val totalHours = logged.map { it.hoursPlayed }.sum()

        val rated = logged.filter { it.userRating > 0f }
        val avgScore = if (rated.isNotEmpty()) rated.map { it.userRating }.average().toFloat() else 0f

        val topRated = rated.sortedByDescending { it.userRating }.take(5)

        // Count genres
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

        // Count platforms
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
            val existing = rawGames.value.find { it.id == gameId } ?: return@launch
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
            repository.updateGame(updated)
            // also update selected detail if open
            if (_selectedGameForDetail.value?.id == gameId) {
                _selectedGameForDetail.value = updated
            }
            closeReviewDialog()
        }
    }

    fun toggleFavorite(game: GameItem) {
        viewModelScope.launch {
            val updated = game.copy(isFavorite = !game.isFavorite)
            repository.updateGame(updated)
            if (_selectedGameForDetail.value?.id == game.id) {
                _selectedGameForDetail.value = updated
            }
        }
    }

    fun quickChangeStatus(game: GameItem, newStatus: String) {
        viewModelScope.launch {
            val updated = game.copy(
                status = newStatus,
                loggedDate = if (game.loggedDate > 0L) game.loggedDate else System.currentTimeMillis()
            )
            repository.updateGame(updated)
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
            val gradients = listOf(
                "#8B5CF6,#06B6D4",
                "#EC4899,#8B5CF6",
                "#3B82F6,#10B981",
                "#F59E0B,#EF4444",
                "#6366F1,#A855F7"
            )
            val randomGradient = gradients.random()
            val newGame = GameItem(
                title = title.trim(),
                titleAr = titleAr.trim(),
                developer = developer.trim().ifBlank { "Independent" },
                releaseYear = if (releaseYear in 1970..2030) releaseYear else 2024,
                genres = genres.trim().ifBlank { "Action" },
                platforms = platforms.trim().ifBlank { "PC, Console" },
                gradientColors = randomGradient,
                synopsis = synopsis.trim().ifBlank { "لعبة جديدة تمت إضافتها إلى المكتبة الخاصة بك." },
                communityRating = 4.5f,
                userRating = userRating,
                status = status,
                hoursPlayed = hoursPlayed,
                userReview = userReview.trim(),
                platformPlayed = platformPlayed,
                loggedDate = if (userRating > 0f || status != "NONE") System.currentTimeMillis() else 0L
            )
            repository.insertGame(newGame)
            _isAddGameDialogOpen.value = false
        }
    }

    fun deleteGame(game: GameItem) {
        viewModelScope.launch {
            repository.deleteGame(game)
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
            repository.resetToDefaults()
        }
    }
}
