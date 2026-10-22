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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.AuthManager
import com.example.ui.dialogs.AddGameDialog
import com.example.ui.dialogs.GameDetailSheet
import com.example.ui.dialogs.ReviewDialog
import com.example.ui.screens.CommentsBottomSheet
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.CompareScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ListsScreen
import com.example.ui.screens.ProfileScreen
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
import com.google.firebase.auth.FirebaseUser
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

    val launchSignInFlow: () -> Unit = {
        AuthManager.onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                showAuthDialog = false
                Toast.makeText(context, "تم تسجيل الدخول بنجاح!", Toast.LENGTH_SHORT).show()
            },
            onAuthError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                showAuthDialog = false
            },
            scope = coroutineScope,
            onAuthCancelled = {
                showAuthDialog = false
            }
        )
    }

    // App opens directly without blocking with an auth screen at startup!
    AfterGameApp(
        currentUser = currentUser,
        onRequireAuth = { showAuthDialog = true },
        onSignInClick = launchSignInFlow,
        onSignOutClick = {
            AuthManager.signOut(
                context = context,
                credentialManager = credentialManager,
                onSignOutComplete = {
                    currentUser = null
                    Toast.makeText(context, "تم تسجيل الخروج", Toast.LENGTH_SHORT).show()
                },
                scope = coroutineScope
            )
        }
    )

    if (showAuthDialog) {
        AuthRequiredDialog(
            onDismiss = { showAuthDialog = false },
            onSignIn = launchSignInFlow
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
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Column(
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "تسجيل الدخول مطلوب",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "لإكمال هذا التفاعل، سجل دخولك أولاً لمشاركة تقييمك، تعليقك، أو مراجعتك مع مجتمع After Game.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onSignIn,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_dialog_signin")
                    ) {
                        Text(
                            text = "تسجيل الدخول عبر Google",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
    currentUser: FirebaseUser?,
    viewModel: GameViewModel = viewModel(),
    onRequireAuth: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
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
    val popularGames by viewModel.popularGames.collectAsStateWithLifecycle()
    val newReleases by viewModel.newReleases.collectAsStateWithLifecycle()
    val topRatedGames by viewModel.topRatedGames.collectAsStateWithLifecycle()
    val isLoadingApi by viewModel.isLoadingApi.collectAsStateWithLifecycle()

    val compareGameA by viewModel.compareGameA.collectAsStateWithLifecycle()
    val compareGameB by viewModel.compareGameB.collectAsStateWithLifecycle()

    val selectedDetailGame by viewModel.selectedGameForDetail.collectAsStateWithLifecycle()
    val selectedReviewGame by viewModel.selectedGameForReview.collectAsStateWithLifecycle()
    val isAddDialogOpen by viewModel.isAddGameDialogOpen.collectAsStateWithLifecycle()
    val selectedReviewForComments by viewModel.selectedReviewForComments.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()

    val isSignedIn = currentUser != null

    BackHandler(
        enabled = selectedReviewForComments != null || selectedDetailGame != null ||
            selectedReviewGame != null || isAddDialogOpen || activeTab != NavigationTab.HOME
    ) {
        when {
            selectedReviewForComments != null -> viewModel.closeComments()
            selectedReviewGame != null -> viewModel.closeReviewDialog()
            selectedDetailGame != null -> viewModel.closeGameDetail()
            isAddDialogOpen -> viewModel.setAddGameDialogOpen(false)
            activeTab != NavigationTab.HOME -> viewModel.setActiveTab(NavigationTab.HOME)
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
                // 1. Home Tab
                val isHome = activeTab == NavigationTab.HOME
                NavigationBarItem(
                    selected = isHome,
                    onClick = { viewModel.setActiveTab(NavigationTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (isHome) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontSize = 11.sp,
                            fontWeight = if (isHome) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_home")
                )

                // 2. Discover Tab
                val isDiscover = activeTab == NavigationTab.DISCOVER || activeTab == NavigationTab.EXPLORE
                NavigationBarItem(
                    selected = isDiscover,
                    onClick = { viewModel.setActiveTab(NavigationTab.DISCOVER) },
                    icon = {
                        Icon(
                            imageVector = if (isDiscover) Icons.Filled.Explore else Icons.Outlined.Explore,
                            contentDescription = "Discover",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Discover",
                            fontSize = 11.sp,
                            fontWeight = if (isDiscover) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_discover")
                )

                // 3. Compare Tab
                val isCompare = activeTab == NavigationTab.COMPARE
                NavigationBarItem(
                    selected = isCompare,
                    onClick = { viewModel.setActiveTab(NavigationTab.COMPARE) },
                    icon = {
                        Icon(
                            imageVector = if (isCompare) Icons.Filled.CompareArrows else Icons.Outlined.CompareArrows,
                            contentDescription = "Compare",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Compare",
                            fontSize = 11.sp,
                            fontWeight = if (isCompare) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_compare")
                )

                // 4. Lists Tab
                val isLists = activeTab == NavigationTab.LISTS || activeTab == NavigationTab.DIARY
                NavigationBarItem(
                    selected = isLists,
                    onClick = { viewModel.setActiveTab(NavigationTab.LISTS) },
                    icon = {
                        Icon(
                            imageVector = if (isLists) Icons.Filled.Bookmarks else Icons.Outlined.Bookmarks,
                            contentDescription = "Lists",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Lists",
                            fontSize = 11.sp,
                            fontWeight = if (isLists) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_lists")
                )

                // 5. Profile Tab
                val isProfile = activeTab == NavigationTab.PROFILE || activeTab == NavigationTab.STATS
                NavigationBarItem(
                    selected = isProfile,
                    onClick = { viewModel.setActiveTab(NavigationTab.PROFILE) },
                    icon = {
                        Icon(
                            imageVector = if (isProfile) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            fontSize = 11.sp,
                            fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = CyberPrimary,
                        indicatorColor = CyberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_profile")
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
                    NavigationTab.HOME -> {
                        HomeScreen(
                            games = exploreGames,
                            reviews = communityReviews,
                            currentUser = currentUser,
                            onGameClick = { viewModel.openGameDetail(it) },
                            onReviewClick = {
                                if (isSignedIn) viewModel.openReviewDialog(it) else onRequireAuth()
                            },
                            onLikeReview = {
                                if (isSignedIn) viewModel.likeReview(it) else onRequireAuth()
                            },
                            onOpenComments = {
                                if (isSignedIn) viewModel.openCommentsForReview(it) else onRequireAuth()
                            },
                            onRequireAuth = onRequireAuth,
                            onOpenListsTab = { viewModel.setActiveTab(NavigationTab.LISTS) },
                            popularGames = popularGames,
                            newReleases = newReleases,
                            topRatedGames = topRatedGames,
                            isLoadingApi = isLoadingApi
                        )
                    }

                    NavigationTab.DISCOVER, NavigationTab.EXPLORE -> {
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
                            isSearchingRawg = isSearchingRawg || isLoadingApi
                        )
                    }

                    NavigationTab.COMPARE -> {
                        CompareScreen(
                            gameA = compareGameA,
                            gameB = compareGameB,
                            allGames = exploreGames,
                            onSelectGameA = { viewModel.setCompareGameA(it) },
                            onSelectGameB = { viewModel.setCompareGameB(it) }
                        )
                    }

                    NavigationTab.LISTS, NavigationTab.DIARY -> {
                        ListsScreen(
                            userGames = diaryGames,
                            onGameClick = { viewModel.openGameDetail(it) },
                            onAddNewListClick = {
                                if (isSignedIn) viewModel.setAddGameDialogOpen(true) else onRequireAuth()
                            }
                        )
                    }

                    NavigationTab.PROFILE, NavigationTab.STATS -> {
                        ProfileScreen(
                            currentUser = currentUser,
                            stats = stats,
                            onSignInClick = onSignInClick,
                            onSignOutClick = onSignOutClick,
                            onNavigateToDiary = { viewModel.setActiveTab(NavigationTab.LISTS) }
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
