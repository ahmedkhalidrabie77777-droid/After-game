package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.dialogs.AddGameDialog
import com.example.ui.dialogs.GameDetailSheet
import com.example.ui.dialogs.ReviewDialog
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AfterGameApp()
            }
        }
    }
}

@Composable
fun AfterGameApp(viewModel: GameViewModel = viewModel()) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val exploreGames by viewModel.exploreGames.collectAsStateWithLifecycle()
    val diaryGames by viewModel.diaryGames.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
    val diaryStatusFilter by viewModel.diaryStatusFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

    val selectedDetailGame by viewModel.selectedGameForDetail.collectAsStateWithLifecycle()
    val selectedReviewGame by viewModel.selectedGameForReview.collectAsStateWithLifecycle()
    val isAddDialogOpen by viewModel.isAddGameDialogOpen.collectAsStateWithLifecycle()

    // Handle back button for sub-sheets and tabs
    BackHandler(enabled = selectedDetailGame != null || selectedReviewGame != null || isAddDialogOpen || activeTab != NavigationTab.EXPLORE) {
        when {
            selectedReviewGame != null -> viewModel.closeReviewDialog()
            selectedDetailGame != null -> viewModel.closeGameDetail()
            isAddDialogOpen -> viewModel.setAddGameDialogOpen(false)
            activeTab != NavigationTab.EXPLORE -> viewModel.setActiveTab(NavigationTab.EXPLORE)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                // 1. Explore Tab
                val isExplore = activeTab == NavigationTab.EXPLORE
                NavigationBarItem(
                    selected = isExplore,
                    onClick = { viewModel.setActiveTab(NavigationTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = if (isExplore) Icons.Filled.Explore else Icons.Outlined.Explore,
                            contentDescription = "Explore",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "اكتشف",
                            fontSize = 11.sp,
                            fontWeight = if (isExplore) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_explore")
                )

                // 2. Diary Tab
                val isDiary = activeTab == NavigationTab.DIARY
                NavigationBarItem(
                    selected = isDiary,
                    onClick = { viewModel.setActiveTab(NavigationTab.DIARY) },
                    icon = {
                        Icon(
                            imageVector = if (isDiary) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks,
                            contentDescription = "Diary",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "سجلي",
                            fontSize = 11.sp,
                            fontWeight = if (isDiary) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_diary")
                )

                // 3. Stats Tab
                val isStats = activeTab == NavigationTab.STATS
                NavigationBarItem(
                    selected = isStats,
                    onClick = { viewModel.setActiveTab(NavigationTab.STATS) },
                    icon = {
                        Icon(
                            imageVector = if (isStats) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                            contentDescription = "Stats",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "إحصائياتي",
                            fontSize = 11.sp,
                            fontWeight = if (isStats) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_stats")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = activeTab, label = "tab_fade") { tab ->
                when (tab) {
                    NavigationTab.EXPLORE -> {
                        ExploreScreen(
                            games = exploreGames,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedGenre = selectedGenre,
                            onGenreSelect = { viewModel.setSelectedGenre(it) },
                            onGameClick = { viewModel.openGameDetail(it) },
                            onReviewClick = { viewModel.openReviewDialog(it) },
                            onAddNewGameClick = { viewModel.setAddGameDialogOpen(true) }
                        )
                    }
                    NavigationTab.DIARY -> {
                        DiaryScreen(
                            games = diaryGames,
                            statusFilter = diaryStatusFilter,
                            onStatusFilterChange = { viewModel.setDiaryStatusFilter(it) },
                            platformFilter = selectedPlatform,
                            onPlatformFilterChange = { viewModel.setSelectedPlatform(it) },
                            sortOption = sortOption,
                            onSortOptionChange = { viewModel.setSortOption(it) },
                            onGameClick = { viewModel.openGameDetail(it) },
                            onReviewClick = { viewModel.openReviewDialog(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onDeleteGame = { viewModel.deleteGame(it) },
                            onExploreClick = { viewModel.setActiveTab(NavigationTab.EXPLORE) }
                        )
                    }
                    NavigationTab.STATS -> {
                        StatsScreen(
                            stats = stats,
                            onGameClick = { viewModel.openGameDetail(it) },
                            onResetDefaults = { viewModel.resetToDefaults() }
                        )
                    }
                }
            }
        }
    }

    // Detail Sheet
    selectedDetailGame?.let { game ->
        GameDetailSheet(
            game = game,
            onDismiss = { viewModel.closeGameDetail() },
            onOpenReview = {
                viewModel.openReviewDialog(game)
            },
            onToggleFavorite = { viewModel.toggleFavorite(game) },
            onDeleteGame = { viewModel.deleteGame(game) }
        )
    }

    // Review Dialog
    selectedReviewGame?.let { game ->
        ReviewDialog(
            game = game,
            onDismiss = { viewModel.closeReviewDialog() },
            onSave = { rating, status, hours, reviewTitle, reviewText, pros, cons, gameplay, story, visuals, audio, platformPlayed, isFav ->
                viewModel.saveReview(
                    gameId = game.id,
                    userRating = rating,
                    status = status,
                    hoursPlayed = hours,
                    reviewTitle = reviewTitle,
                    userReview = reviewText,
                    pros = pros,
                    cons = cons,
                    subGameplayScore = gameplay,
                    subStoryScore = story,
                    subVisualsScore = visuals,
                    subAudioScore = audio,
                    platformPlayed = platformPlayed,
                    isFavorite = isFav
                )
            }
        )
    }

    // Add Custom Game Dialog
    if (isAddDialogOpen) {
        AddGameDialog(
            onDismiss = { viewModel.setAddGameDialogOpen(false) },
            onAdd = { title, titleAr, developer, releaseYear, genres, platforms, synopsis, rating, status, hours, reviewText, platformPlayed ->
                viewModel.addCustomGame(
                    title = title,
                    titleAr = titleAr,
                    developer = developer,
                    releaseYear = releaseYear,
                    genres = genres,
                    platforms = platforms,
                    synopsis = synopsis,
                    userRating = rating,
                    status = status,
                    hoursPlayed = hours,
                    userReview = reviewText,
                    platformPlayed = platformPlayed
                )
            }
        )
    }
}
