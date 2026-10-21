package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Dialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.AuthManager
import com.example.ui.auth.AuthScreen
import com.example.ui.dialogs.AddGameDialog
import com.example.ui.dialogs.GameDetailSheet
import com.example.ui.dialogs.ReviewDialog
import com.example.ui.screens.CommentsBottomSheet
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.NavigationTab
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RootAuthGate()
            }
        }
    }
}

@Composable
fun RootAuthGate() {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var showAuthDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        AfterGameApp(
            onRequireAuth = { showAuthDialog = true }
        )
    }

    if (showAuthDialog) {
        AuthRequiredDialog(
            onDismiss = { showAuthDialog = false },
            onSignIn = {
                AuthManager.onGoogleSignInClicked(
                    context = context,
                    credentialManager = credentialManager,
                    onAuthSuccess = {
                        currentUser = Firebase.auth.currentUser
                        showAuthDialog = false
                    },
                    onAuthError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        showAuthDialog = false
                    },
                    scope = coroutineScope,
                    onAuthCancelled = { showAuthDialog = false }
                )
            }
        )
    }
}

@Composable
fun AuthRequiredDialog(
    onDismiss: () -> Unit,
    onSignIn: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = DarkBackground,
            shape = RoundedCornerShape(22.dp),
            tonalElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                CyberPrimary.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .border(
                                1.dp,
                                CyberPrimary.copy(alpha = 0.8f),
                                RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Groups,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = "تسجيل الدخول مطلوب",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "لإكمال هذا التفاعل، سجل دخولك أولاً لمشاركة تقييمك، تعليقك، أو مراجعتك مع المجتمع.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))

                    Button(
                        onClick = onSignIn,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "تسجيل الدخول عبر Google",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "لاحقًا",
                            color = CyberSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AfterGameApp(
    viewModel: GameViewModel = viewModel(),
    onRequireAuth: () -> Unit = {}
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val exploreGames by viewModel.exploreGames.collectAsStateWithLifecycle()
    val diaryGames by viewModel.diaryGames.collectAsStateWithLifecycle()
    val communityReviews by viewModel.communityReviews.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
    val diaryStatusFilter by viewModel.diaryStatusFilter.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val isSearchingRawg by viewModel.isSearchingRawg.collectAsStateWithLifecycle()

    val selectedDetailGame by viewModel.selectedGameForDetail.collectAsStateWithLifecycle()
    val selectedReviewGame by viewModel.selectedGameForReview.collectAsStateWithLifecycle()
    val isAddDialogOpen by viewModel.isAddGameDialogOpen.collectAsStateWithLifecycle()
    val selectedReviewForComments by viewModel.selectedReviewForComments.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()

    val isSignedIn = Firebase.auth.currentUser != null

    BackHandler(
        enabled = selectedReviewForComments != null || selectedDetailGame != null ||
            selectedReviewGame != null || isAddDialogOpen || activeTab != NavigationTab.EXPLORE
    ) {
        when {
            selectedReviewForComments != null -> viewModel.closeComments()
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
                val isExplore = activeTab == NavigationTab.EXPLORE
                NavigationBarItem(
                    selected = isExplore,
                    onClick = { viewModel.setActiveTab(NavigationTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = if (isExplore) Icons.Filled.Explore else Icons.Outlined.Explore,
                            contentDescription = "Explore",
                            modifier = Modifier.size(22.dp)
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

                val isCommunity = activeTab == NavigationTab.COMMUNITY
                NavigationBarItem(
                    selected = isCommunity,
                    onClick = { viewModel.setActiveTab(NavigationTab.COMMUNITY) },
                    icon = {
                        Icon(
                            imageVector = if (isCommunity) Icons.Filled.Groups else Icons.Outlined.Groups,
                            contentDescription = "Community",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "المجتمع",
                            fontSize = 11.sp,
                            fontWeight = if (isCommunity) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberSecondary,
                        indicatorColor = CyberSecondary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_community")
                )

                val isDiary = activeTab == NavigationTab.DIARY
                NavigationBarItem(
                    selected = isDiary,
                    onClick = { viewModel.setActiveTab(NavigationTab.DIARY) },
                    icon = {
                        Icon(
                            imageVector = if (isDiary) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks,
                            contentDescription = "Diary",
                            modifier = Modifier.size(22.dp)
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

                val isStats = activeTab == NavigationTab.STATS
                NavigationBarItem(
                    selected = isStats,
                    onClick = { viewModel.setActiveTab(NavigationTab.STATS) },
                    icon = {
                        Icon(
                            imageVector = if (isStats) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                            contentDescription = "Stats",
                            modifier = Modifier.size(22.dp)
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
                            onReviewClick = {
                                if (isSignedIn) viewModel.openReviewDialog(it) else onRequireAuth()
                            },
                            onAddNewGameClick = {
                                if (isSignedIn) viewModel.setAddGameDialogOpen(true) else onRequireAuth()
                            },
                            isSearchingRawg = isSearchingRawg
                        )
                    }
                    NavigationTab.COMMUNITY -> {
                        CommunityScreen(
                            reviews = communityReviews,
                            onLikeReview = { reviewId ->
                                if (isSignedIn) viewModel.likeReview(reviewId) else onRequireAuth()
                            },
                            onOpenComments = { review ->
                                if (isSignedIn) viewModel.openCommentsForReview(review) else onRequireAuth()
                            }
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
                            onReviewClick = {
                                if (isSignedIn) viewModel.openReviewDialog(it) else onRequireAuth()
                            },
                            onToggleFavorite = {
                                if (isSignedIn) viewModel.toggleFavorite(it) else onRequireAuth()
                            },
                            onDeleteGame = {
                                if (isSignedIn) viewModel.deleteGame(it) else onRequireAuth()
                            },
                            onExploreClick = { viewModel.setActiveTab(NavigationTab.EXPLORE) }
                        )
                    }
                    NavigationTab.STATS -> {
                        StatsScreen(
                            stats = stats,
                            onGameClick = { viewModel.openGameDetail(it) },
                            onResetDefaults = {
                                if (isSignedIn) viewModel.resetToDefaults() else onRequireAuth()
                            }
                        )
                    }
                }
            }
        }
    }

    selectedDetailGame?.let { game ->
        GameDetailSheet(
            game = game,
            onDismiss = { viewModel.closeGameDetail() },
            onOpenReview = {
                if (isSignedIn) viewModel.openReviewDialog(game) else onRequireAuth()
            },
            onToggleFavorite = {
                if (isSignedIn) viewModel.toggleFavorite(game) else onRequireAuth()
            },
            onDeleteGame = {
                if (isSignedIn) viewModel.deleteGame(game) else onRequireAuth()
            }
        )
    }

    selectedReviewGame?.let { game ->
        ReviewDialog(
            game = game,
            onDismiss = { viewModel.closeReviewDialog() },
            onSave = { rating, status, hours, reviewTitle, reviewText, pros, cons, gameplay, story, visuals, audio, platformPlayed, isFav ->
                if (isSignedIn) {
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
                } else {
                    onRequireAuth()
                }
            }
        )
    }

    if (isAddDialogOpen) {
        AddGameDialog(
            onDismiss = { viewModel.setAddGameDialogOpen(false) },
            onAdd = { title, titleAr, developer, releaseYear, genres, platforms, synopsis, rating, status, hours, reviewText, platformPlayed ->
                if (isSignedIn) {
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
                } else {
                    onRequireAuth()
                }
            }
        )
    }

    selectedReviewForComments?.let { review ->
        CommentsBottomSheet(
            review = review,
            comments = activeComments,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { text ->
                if (isSignedIn) {
                    viewModel.addComment(review.id, text)
                } else {
                    onRequireAuth()
                }
            }
        )
    }
}
