package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.firebase.FirebaseReview
import com.example.data.model.GameItem
import com.example.ui.components.CyberPosterImage
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberTertiary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldStar
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.AppLanguage
import com.google.firebase.auth.FirebaseUser

enum class HomeSection {
    HOME,
    COMMUNITY
}

enum class HomeSubTab(val titleAr: String, val titleEn: String) {
    GAMES("الألعاب", "Games"),
    REVIEWS("المراجعات", "Reviews"),
    LISTS("القوائم", "Lists"),
    JOURNAL("مقالات", "Journal")
}

@Composable
fun HomeScreen(
    games: List<GameItem>,
    reviews: List<FirebaseReview>,
    currentUser: FirebaseUser?,
    onGameClick: (GameItem) -> Unit,
    onReviewClick: (GameItem) -> Unit,
    onLikeReview: (String) -> Unit,
    onOpenComments: (FirebaseReview) -> Unit,
    onRequireAuth: () -> Unit,
    onOpenListsTab: () -> Unit,
    onNavigateToExplore: () -> Unit = {},
    onPosterLongClick: (GameItem) -> Unit = {},
    popularGames: List<GameItem> = emptyList(),
    newReleases: List<GameItem> = emptyList(),
    upcomingGames: List<GameItem> = emptyList(),
    topRatedGames: List<GameItem> = emptyList(),
    isLoadingApi: Boolean = false,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(HomeSection.HOME) }
    var activeSubTab by remember { mutableStateOf(HomeSubTab.GAMES) }
    var communitySubTab by remember { mutableStateOf("Community") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top Header: Infinity Logo + "After Game"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_after_game_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(36.dp)
                    .height(24.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "After Game",
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        // Segmented Pill Toggle: [Home] [Community] (as seen in video 00:00 - 00:01)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.padding(2.dp)
            ) {
                Row(modifier = Modifier.padding(3.dp)) {
                    val isArabic = AppLanguage.isArabic
                    val isHome = activeSection == HomeSection.HOME
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isHome) Color.White else Color.Transparent)
                            .clickable { activeSection = HomeSection.HOME }
                            .padding(horizontal = 28.dp, vertical = 7.dp)
                            .testTag("toggle_home")
                    ) {
                        Text(
                            text = if (isArabic) "الرئيسية" else "Home",
                            color = if (isHome) Color.Black else TextSecondary,
                            fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }

                    val isCommunity = activeSection == HomeSection.COMMUNITY
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isCommunity) Color.White else Color.Transparent)
                            .clickable { activeSection = HomeSection.COMMUNITY }
                            .padding(horizontal = 28.dp, vertical = 7.dp)
                            .testTag("toggle_community")
                    ) {
                        Text(
                            text = if (isArabic) "المجتمع" else "Community",
                            color = if (isCommunity) Color.Black else TextSecondary,
                            fontWeight = if (isCommunity) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Sub-tabs row based on active section
        val isArabic = AppLanguage.isArabic
        if (activeSection == HomeSection.HOME) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                HomeSubTab.values().forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { activeSubTab = tab }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isArabic) tab.titleAr else tab.titleEn,
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(2.5.dp)
                                .width(if (isSelected) 24.dp else 0.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CyberPrimary)
                        )
                    }
                }
            }
        } else {
            // Community sub-tabs: [Community] [Friends] [Popular] [Following]
            val communityTabs = if (isArabic) {
                listOf("المجتمع", "الأصدقاء", "الشائع", "المتابَعون")
            } else {
                listOf("Community", "Friends", "Popular", "Following")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                communityTabs.forEach { cTab ->
                    val isSelected = communitySubTab == cTab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { communitySubTab = cTab }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = cTab,
                            color = if (isSelected) TextPrimary else TextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .height(2.5.dp)
                                .width(if (isSelected) 24.dp else 0.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CyberPrimary)
                        )
                    }
                }
            }
        }

        // Main Body Content
        if (activeSection == HomeSection.HOME) {
            when (activeSubTab) {
                HomeSubTab.GAMES -> {
                    GamesHomeContent(
                        games = games,
                        popularGames = popularGames,
                        newReleases = newReleases,
                        upcomingGames = upcomingGames,
                        topRatedGames = topRatedGames,
                        isLoadingApi = isLoadingApi,
                        currentUser = currentUser,
                        onGameClick = onGameClick,
                        onRequireAuth = onRequireAuth,
                        onNavigateToExplore = onNavigateToExplore,
                        onPosterLongClick = onPosterLongClick
                    )
                }
                HomeSubTab.REVIEWS -> {
                    ReviewsHomeContent(
                        reviews = reviews,
                        currentUser = currentUser,
                        onLikeReview = onLikeReview,
                        onOpenComments = onOpenComments,
                        onRequireAuth = onRequireAuth,
                        games = games,
                        onReviewClick = onReviewClick
                    )
                }
                HomeSubTab.LISTS -> {
                    ListsHomeContent(
                        onOpenListsTab = onOpenListsTab,
                        onRequireAuth = onRequireAuth
                    )
                }
                HomeSubTab.JOURNAL -> {
                    JournalHomeContent()
                }
            }
        } else {
            // Community Section Content (as seen in video 00:15 - 00:25)
            CommunityHomeContent(
                reviews = reviews,
                currentUser = currentUser,
                onLikeReview = onLikeReview,
                onOpenComments = onOpenComments,
                onRequireAuth = onRequireAuth
            )
        }
    }
}

@Composable
private fun GamesHomeContent(
    games: List<GameItem>,
    popularGames: List<GameItem>,
    newReleases: List<GameItem>,
    upcomingGames: List<GameItem>,
    topRatedGames: List<GameItem>,
    isLoadingApi: Boolean,
    currentUser: FirebaseUser?,
    onGameClick: (GameItem) -> Unit,
    onRequireAuth: () -> Unit,
    onNavigateToExplore: () -> Unit = {},
    onPosterLongClick: (GameItem) -> Unit = {}
) {
    val displayReleases = if (newReleases.isNotEmpty()) newReleases else games.take(10)
    val displayUpcoming = if (upcomingGames.isNotEmpty()) upcomingGames else games.filter { it.releaseYear >= 2025 }.take(10)
    val displayPopular = if (popularGames.isNotEmpty()) popularGames else games
    val popularChunks = remember(displayPopular) { displayPopular.chunked(3) }
    val isArabic = AppLanguage.isArabic

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (isLoadingApi && displayPopular.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = CyberPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جاري الاتصال بقاعدة بيانات RAWG العالمية للألعاب...",
                            color = CyberSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Welcome Card (if unauthenticated, as in video 00:00 - 00:03)
        if (currentUser == null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    val isArabic = AppLanguage.isArabic
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "مرحباً بك في After Game" else "Welcome to After Game",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isArabic) {
                                    "استكشف آلاف الألعاب من RAWG و Steam. سجّل دخولك لحفظ قوائمك وتقييم تجاربك."
                                } else {
                                    "Explore games from RAWG, Steam, IGDB. Sign in to save lists & rate."
                                },
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = onRequireAuth,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_welcome_signup")
                        ) {
                            Text(
                                text = if (isArabic) "تسجيل الدخول" else "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 1: New Releases (Horizontal carousel of newly released games)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "أحدث الإصدارات" else "New Releases",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isArabic) "عرض الكل >" else "View All >",
                    color = CyberPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToExplore() }
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayReleases) { game ->
                    GameReleaseCard(
                        game = game,
                        onClick = { onGameClick(game) },
                        onLongClick = { onPosterLongClick(game) },
                        onRequireAuth = onRequireAuth,
                        badgeText = "${game.releaseYear}"
                    )
                }
            }
        }

        // Section 2: Upcoming Games (Horizontal carousel with Soon badge)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "ألعاب قادمة" else "Upcoming Games",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isArabic) "عرض الكل >" else "View All >",
                    color = CyberPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToExplore() }
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayUpcoming) { game ->
                    GameReleaseCard(
                        game = game,
                        onClick = { onGameClick(game) },
                        onLongClick = { onPosterLongClick(game) },
                        onRequireAuth = onRequireAuth,
                        badgeText = if (isArabic) "قريباً" else "Soon"
                    )
                }
            }
        }

        // Section 3: Popular Games (Featured 3-column poster grid)
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isArabic) "الألعاب الأكثر شهرة" else "Popular Games",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isArabic) "استكشف ألعاباً منتقاة بعناية مع تقييمات موثوقة" else "Explore curated titles with complete verified ratings",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        items(popularChunks) { rowGames ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (game in rowGames) {
                    Box(modifier = Modifier.weight(1f)) {
                        GamePosterGridCard(
                            game = game,
                            onClick = { onGameClick(game) },
                            onLongClick = { onPosterLongClick(game) }
                        )
                    }
                }
                for (i in 0 until (3 - rowGames.size)) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun GamePosterGridCard(
    game: GameItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val gradientColors = com.example.ui.components.parseGradient(game.gradientColors)
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CyberPosterImage(
                imageUrl = game.coverUrl,
                contentDescription = game.title,
                fallbackText = game.title,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay for bottom text legibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black)
                        )
                    )
            )

            // Top-right release year chip
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            ) {
                Text(
                    text = "${game.releaseYear}",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            // Bottom title and score
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = game.title,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldStar,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "%.1f".format(game.communityRating),
                        color = GoldStar,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun GameReleaseCard(
    game: GameItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onRequireAuth: () -> Unit,
    badgeText: String
) {
    val gradientColors = com.example.ui.components.parseGradient(game.gradientColors)
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .width(150.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                CyberPosterImage(
                    imageUrl = game.coverUrl,
                    contentDescription = game.title,
                    fallbackText = game.title,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxSize()
                )

                // Year / Soon Badge top-left
                Surface(
                    color = if (badgeText == "Soon") CyberTertiary else Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Gradient shadow at bottom of image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, DarkSurface)
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = game.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldStar,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "%.1f".format(game.communityRating),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = CyberTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "%.1f".format(game.communityRating),
                            color = CyberTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsHomeContent(
    reviews: List<FirebaseReview>,
    currentUser: FirebaseUser?,
    onLikeReview: (String) -> Unit,
    onOpenComments: (FirebaseReview) -> Unit,
    onRequireAuth: () -> Unit,
    games: List<GameItem>,
    onReviewClick: (GameItem) -> Unit
) {
    val isArabic = AppLanguage.isArabic
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "مراجعات اللاعبين الحقيقية" else "Honest Gamer Reviews",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        if (currentUser != null && games.isNotEmpty()) {
                            onReviewClick(games.first())
                        } else {
                            onRequireAuth()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTertiary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) "اكتب مراجعة" else "Write Review",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (reviews.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isArabic) "لا توجد مراجعات حتى الآن" else "No community reviews yet",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic) {
                                "جميع التقييمات في After Game حقيقية 100% ومن المستخدمين الفعليين. سجّل دخولك بحساب Google وكن أول من يقيّم وينشر مراجعته!"
                            } else {
                                "All ratings on After Game are 100% genuine from real players. Sign in and be the first to post a review!"
                            },
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        items(reviews) { review ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CyberPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (review.authorName.ifBlank { "G" }).take(1).uppercase(),
                                    color = CyberPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = review.authorName.ifBlank { "لاعب After Game" },
                                    color = CyberPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "15m ago",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = review.gameTitle,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = review.reviewBody,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onLikeReview(review.id) }
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ThumbUp,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "${review.likesCount}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onOpenComments(review) }
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "نقاش",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Poster thumbnail on right (as in video 00:05)
                    Box(
                        modifier = Modifier
                            .width(76.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = review.gameTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListsHomeContent(
    onOpenListsTab: () -> Unit,
    onRequireAuth: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Curated Lists",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onRequireAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "+ New List",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        val sampleLists = listOf(
            Triple("Top Rated Films & Games Of All Time", "The 50 highest-rated titles, weighted by number of gamer votes.", "50 titles"),
            Triple("Hidden Gems", "Highly rated games and experiences that far fewer people have played.", "40 titles"),
            Triple("Highest-Grossing Masterpieces", "The biggest blockbuster releases in the industry.", "50 titles"),
            Triple("Best Action & RPG Releases", "The top high-impact games by weighted score.", "35 titles")
        )

        items(sampleLists) { (title, desc, count) ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenListsTab() }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = count,
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun JournalHomeContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Gaming Journal & Stories",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        val articles = listOf(
            Pair("The Best Game of Every Decade", "Eight decades, eight crowd favourites that shaped interactive storytelling."),
            Pair("Immersion vs CGI: The Next Generation", "How real physics and neural rendering are rewriting video games.")
        )

        items(articles) { (title, subtitle) ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityHomeContent(
    reviews: List<FirebaseReview>,
    currentUser: FirebaseUser?,
    onLikeReview: (String) -> Unit,
    onOpenComments: (FirebaseReview) -> Unit,
    onRequireAuth: () -> Unit
) {
    val isArabic = AppLanguage.isArabic
    val realGamers = remember(reviews) {
        reviews.mapNotNull { it.authorName.ifBlank { null } }.distinct()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        if (realGamers.isNotEmpty()) {
            item {
                Text(
                    text = if (isArabic) "لاعبون في المجتمع" else "Community Gamers",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(realGamers) { gamerName ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.width(120.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(CyberPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gamerName.take(1).uppercase(),
                                        color = CyberPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = gamerName,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onRequireAuth,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "متابعة" else "Follow",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Community Feed Items
        item {
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(reviews) { review ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${review.gameTitle} • ${review.authorName.ifBlank { "لاعب After Game" }}",
                            color = CyberPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.reviewBody,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onLikeReview(review.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ThumbUp,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${review.likesCount}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(18.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onOpenComments(review) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تعليق",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(18.dp))

                            Text(
                                text = "15m ago",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner),
                            contentDescription = review.gameTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
