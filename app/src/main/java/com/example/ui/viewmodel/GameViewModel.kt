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
import com.example.data.remote.RawgGameDto
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
    HOME,
    DISCOVER,
    COMPARE,
    LISTS,
    PROFILE,
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
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val localRepository = GameRepository(database.gameDao())
    private val firestoreRepository = FirestoreReviewRepository(application)

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

    private val _activeTab = MutableStateFlow(NavigationTab.HOME)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _compareGameA = MutableStateFlow<GameItem?>(null)
    val compareGameA: StateFlow<GameItem?> = _compareGameA.asStateFlow()

    private val _compareGameB = MutableStateFlow<GameItem?>(null)
    val compareGameB: StateFlow<GameItem?> = _compareGameB.asStateFlow()

    fun setCompareGameA(game: GameItem?) {
        _compareGameA.value = game
    }

    fun setCompareGameB(game: GameItem?) {
        _compareGameB.value = game
    }

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

    // RAWG API Live Data Stores
    private val _apiPopularGamesRaw = MutableStateFlow<List<RawgGameDto>>(emptyList())
    private val _apiTopRatedGamesRaw = MutableStateFlow<List<RawgGameDto>>(emptyList())
    private val _apiNewReleasesRaw = MutableStateFlow<List<RawgGameDto>>(emptyList())
    private val _apiUpcomingGamesRaw = MutableStateFlow<List<RawgGameDto>>(emptyList())
    private val _apiExploreGamesRaw = MutableStateFlow<List<RawgGameDto>>(emptyList())

    private val _isLoadingApi = MutableStateFlow(false)
    val isLoadingApi: StateFlow<Boolean> = _isLoadingApi.asStateFlow()

    private val _isSearchingRawg = MutableStateFlow(false)
    val isSearchingRawg: StateFlow<Boolean> = _isSearchingRawg.asStateFlow()

    // Active review for viewing comments thread
    private val _selectedReviewForComments = MutableStateFlow<FirebaseReview?>(null)
    val selectedReviewForComments: StateFlow<FirebaseReview?> = _selectedReviewForComments.asStateFlow()

    private val _activeComments = MutableStateFlow<List<FirebaseComment>>(emptyList())
    val activeComments: StateFlow<List<FirebaseComment>> = _activeComments.asStateFlow()

    private fun mapDtoToGameItem(dto: RawgGameDto, localMap: Map<Long, GameItem>): GameItem {
        val gameName = dto.name?.ifBlank { null } ?: "Game #${dto.id}"
        val existing = localMap[dto.id] ?: localMap.values.find { it.title.equals(gameName, ignoreCase = true) }
        val platformNames = dto.platforms?.mapNotNull { it.platform?.name }?.take(4)?.joinToString(", ")
            ?.ifBlank { "PC, PlayStation, Xbox" } ?: "PC, PlayStation, Xbox"
        val genreNames = dto.genres?.mapNotNull { it.name }?.take(3)?.joinToString(", ")
            ?.ifBlank { "Action" } ?: "Action"
        val releaseYearVal = dto.released?.take(4)?.toIntOrNull() ?: 2024
        val rawRating = dto.rating ?: 0f
        val communityScore = if (dto.metacritic != null && dto.metacritic > 0) {
            (dto.metacritic / 20f).coerceIn(0.5f, 5.0f)
        } else if (rawRating > 0f) {
            rawRating.coerceIn(0.5f, 5.0f)
        } else {
            4.5f
        }
        val ratingsCnt = dto.ratingsCount ?: 0
        val desc = dto.descriptionRaw?.ifBlank { null }
            ?: "بيانات حية من قاعدة ألعاب RAWG لـ $gameName مع $ratingsCnt تقييم عالمي ومتوسط ${String.format("%.1f", communityScore)}/5."

        return GameItem(
            id = dto.id,
            title = gameName,
            titleAr = existing?.titleAr ?: "",
            developer = "RAWG Database",
            releaseYear = releaseYearVal,
            platforms = platformNames,
            genres = genreNames,
            coverUrl = dto.backgroundImage ?: existing?.coverUrl ?: "",
            synopsis = desc,
            communityRating = communityScore,
            userRating = existing?.userRating ?: 0f,
            status = existing?.status ?: "NONE",
            hoursPlayed = existing?.hoursPlayed ?: 0f,
            userReview = existing?.userReview ?: "",
            reviewTitle = existing?.reviewTitle ?: "",
            pros = existing?.pros ?: "",
            cons = existing?.cons ?: "",
            subGameplayScore = existing?.subGameplayScore ?: 0f,
            subStoryScore = existing?.subStoryScore ?: 0f,
            subVisualsScore = existing?.subVisualsScore ?: 0f,
            subAudioScore = existing?.subAudioScore ?: 0f,
            isFavorite = existing?.isFavorite ?: false,
            loggedDate = existing?.loggedDate ?: 0L,
            platformPlayed = existing?.platformPlayed ?: "PC"
        )
    }

    val popularGames: StateFlow<List<GameItem>> = combine(
        _apiPopularGamesRaw,
        rawGames
    ) { apiList, localList ->
        val localMap = localList.associateBy { it.id }
        if (apiList.isNotEmpty()) {
            apiList.map { mapDtoToGameItem(it, localMap) }
        } else {
            localList
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topRatedGames: StateFlow<List<GameItem>> = combine(
        _apiTopRatedGamesRaw,
        rawGames
    ) { apiList, localList ->
        val localMap = localList.associateBy { it.id }
        if (apiList.isNotEmpty()) {
            apiList.map { mapDtoToGameItem(it, localMap) }
        } else {
            localList.sortedByDescending { it.communityRating }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newReleases: StateFlow<List<GameItem>> = combine(
        _apiNewReleasesRaw,
        rawGames
    ) { apiList, localList ->
        val localMap = localList.associateBy { it.id }
        if (apiList.isNotEmpty()) {
            apiList.map { mapDtoToGameItem(it, localMap) }
        } else {
            localList.sortedByDescending { it.releaseYear }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingGames: StateFlow<List<GameItem>> = combine(
        _apiUpcomingGamesRaw,
        rawGames
    ) { apiList, localList ->
        val localMap = localList.associateBy { it.id }
        if (apiList.isNotEmpty()) {
            apiList.map { mapDtoToGameItem(it, localMap) }
        } else {
            val future = localList.filter { it.releaseYear >= 2025 }
            if (future.isNotEmpty()) future else localList.reversed()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Explore Screen games (Live from RAWG API based on search/genre, merged with local catalog and ratings)
    val exploreGames: StateFlow<List<GameItem>> = combine(
        _apiExploreGamesRaw,
        rawGames,
        _searchQuery,
        _selectedGenre
    ) { apiList, localList, query, genre ->
        val localMap = localList.associateBy { it.id }
        val mappedApi = apiList.map { mapDtoToGameItem(it, localMap) }
        
        // Merge without duplicating games (prioritize local if user logged/rated, otherwise API)
        val combined = (mappedApi + localList).distinctBy { it.title.lowercase().trim() }
        
        val q = query.trim()
        val hasQuery = q.isNotEmpty()
        val hasGenre = genre != "All" && genre != "الكل"

        var filtered = combined

        if (hasQuery) {
            filtered = filtered.filter { game ->
                game.title.contains(q, ignoreCase = true) ||
                game.titleAr.contains(q, ignoreCase = true) ||
                game.developer.contains(q, ignoreCase = true) ||
                game.genres.contains(q, ignoreCase = true)
            }
        }

        if (hasGenre) {
            val genreClean = genre.lowercase().trim()
            filtered = filtered.filter { game ->
                game.genres.contains(genre, ignoreCase = true) ||
                game.genres.lowercase().contains(genreClean)
            }
        }

        if (filtered.isNotEmpty()) {
            filtered
        } else if (hasQuery || hasGenre) {
            emptyList()
        } else {
            localList
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

    init {
        viewModelScope.launch {
            localRepository.seedIfEmpty()
        }
        fetchInitialApiGames()
    }

    fun setActiveTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun refreshApiGames() {
        fetchInitialApiGames()
    }

    private fun fetchInitialApiGames() {
        viewModelScope.launch {
            _isLoadingApi.value = true

            // 1. Popular / Trending Games (Live from RAWG, 30 items for carousel + poster grid)
            launch {
                try {
                    val popResponse = withContext(Dispatchers.IO) {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            ordering = "-added",
                            pageSize = 30
                        )
                    }
                    val validPop = popResponse.results?.filter { !it.backgroundImage.isNullOrBlank() } ?: emptyList()
                    if (validPop.isNotEmpty()) {
                        _apiPopularGamesRaw.value = validPop
                        if (_apiExploreGamesRaw.value.isEmpty()) {
                            _apiExploreGamesRaw.value = validPop
                        }
                        if (_compareGameA.value == null) {
                            _compareGameA.value = mapDtoToGameItem(validPop[0], emptyMap())
                        }
                        if (_compareGameB.value == null && validPop.size > 1) {
                            _compareGameB.value = mapDtoToGameItem(validPop[1], emptyMap())
                        }
                    }
                } catch (e: Exception) {
                    Log.w("GameViewModel", "RAWG pop error: ${e.message}")
                } finally {
                    _isLoadingApi.value = false
                }
            }

            // 2. Upcoming Games (Ordered by popularity with future release dates)
            launch {
                try {
                    val upcomingResponse = withContext(Dispatchers.IO) {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            dates = "2025-01-01,2027-12-31",
                            ordering = "-added",
                            pageSize = 20
                        )
                    }
                    val validUpcoming = upcomingResponse.results?.filter { !it.backgroundImage.isNullOrBlank() } ?: emptyList()
                    if (validUpcoming.isNotEmpty()) {
                        _apiUpcomingGamesRaw.value = validUpcoming
                    }
                } catch (e: Exception) {
                    Log.w("GameViewModel", "RAWG upcoming error: ${e.message}")
                }
            }

            // 3. New Releases (Ordered by -released so newly released games appear first automatically)
            launch {
                try {
                    val newResponse = withContext(Dispatchers.IO) {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            dates = "2024-01-01,2026-12-31",
                            ordering = "-released",
                            pageSize = 20
                        )
                    }
                    val validNew = newResponse.results?.filter { !it.backgroundImage.isNullOrBlank() } ?: emptyList()
                    if (validNew.isNotEmpty()) {
                        _apiNewReleasesRaw.value = validNew
                    }
                } catch (e: Exception) {
                    Log.w("GameViewModel", "RAWG new releases error: ${e.message}")
                }
            }

            // 4. Top-Rated Games of All Time
            launch {
                try {
                    val topResponse = withContext(Dispatchers.IO) {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            ordering = "-metacritic",
                            pageSize = 15
                        )
                    }
                    val validTop = topResponse.results?.filter { !it.backgroundImage.isNullOrBlank() } ?: emptyList()
                    if (validTop.isNotEmpty()) {
                        _apiTopRatedGamesRaw.value = validTop
                    }
                } catch (e: Exception) {
                    Log.w("GameViewModel", "RAWG top error: ${e.message}")
                }
            }
        }
    }

    private fun getRawgGenreSlug(genre: String): String? {
        return when (genre.lowercase().trim()) {
            "all", "الكل" -> null
            "action rpg", "آكشن rpg" -> "action,role-playing-games-rpg"
            "soulslike", "سولزلايك" -> null
            "open world", "عالم مفتوح" -> "adventure,action"
            "indie", "ألعاب مستقلة" -> "indie"
            "adventure", "مغامرات" -> "adventure"
            "horror", "رعب بقاء" -> null
            "roguelike", "روغلايك" -> null
            "shooter", "تصويب" -> "shooter"
            "strategy", "استراتيجية" -> "strategy"
            "racing", "سباقات" -> "racing"
            else -> genre.lowercase().replace(" ", "-")
        }
    }

    fun setSelectedGenre(genre: String) {
        _selectedGenre.value = genre
        if (_searchQuery.value.isBlank()) {
            loadGamesByGenre(genre)
        }
    }

    private fun loadGamesByGenre(genre: String) {
        viewModelScope.launch {
            _isLoadingApi.value = true
            try {
                val slug = getRawgGenreSlug(genre)
                val response = withContext(Dispatchers.IO) {
                    if (slug != null) {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            genres = slug,
                            ordering = "-added",
                            pageSize = 25
                        )
                    } else if (genre != "All" && genre != "الكل") {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            search = genre,
                            ordering = "-rating",
                            pageSize = 25
                        )
                    } else {
                        RawgApiClient.service.getGames(
                            apiKey = RawgApiClient.apiKey,
                            ordering = "-added",
                            pageSize = 25
                        )
                    }
                }
                val validGames = response.results?.filter { !it.backgroundImage.isNullOrBlank() } ?: emptyList()
                if (validGames.isNotEmpty()) {
                    _apiExploreGamesRaw.value = validGames
                }
            } catch (e: Exception) {
                Log.w("GameViewModel", "RAWG genre error: ${e.message}")
            } finally {
                _isLoadingApi.value = false
            }
        }
    }

    private var searchJob: kotlinx.coroutines.Job? = null

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        val q = query.trim()
        if (q.length >= 2) {
            searchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(350)
                searchRawgLive(q)
            }
        } else if (q.isBlank()) {
            loadGamesByGenre(_selectedGenre.value)
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
                        pageSize = 25
                    )
                }
                val validGames = response.results?.filter { !it.backgroundImage.isNullOrBlank() } ?: emptyList()
                if (validGames.isNotEmpty()) {
                    // Prepend new search results so they show up at top of explore list
                    val current = _apiExploreGamesRaw.value
                    _apiExploreGamesRaw.value = (validGames + current).distinctBy { it.id }
                }
            } catch (e: Exception) {
                Log.w("GameViewModel", "RAWG live search error: ${e.message}")
            } finally {
                _isSearchingRawg.value = false
            }
        }
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

    fun updateGameStatus(game: GameItem, newStatus: String) {
        viewModelScope.launch {
            val updated = game.copy(
                status = newStatus,
                loggedDate = if (game.loggedDate > 0L) game.loggedDate else System.currentTimeMillis()
            )
            localRepository.insertGame(updated)
            if (_selectedGameForDetail.value?.id == game.id) {
                _selectedGameForDetail.value = updated
            }
        }
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
            // Find game from API lists or local database
            val existing = exploreGames.value.find { it.id == gameId }
                ?: popularGames.value.find { it.id == gameId }
                ?: topRatedGames.value.find { it.id == gameId }
                ?: newReleases.value.find { it.id == gameId }
                ?: upcomingGames.value.find { it.id == gameId }
                ?: rawGames.value.find { it.id == gameId }
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
