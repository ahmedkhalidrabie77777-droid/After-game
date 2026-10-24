package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.GameItem
import com.example.ui.components.parseGradient
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldStar
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class DetailPrimaryTab(val titleAr: String) {
    SERIES("السلسلة"),
    DLCS("إضافات"),
    SCREENSHOTS("لقطات")
}

enum class DetailSubTab(val titleAr: String) {
    DETAILS("تفاصيل"),
    PLATFORMS("منصات"),
    GENRES("أنواع"),
    LANGUAGES("لغات"),
    CAST("طاقم")
}

data class SeriesGameItem(
    val title: String,
    val year: String,
    val coverUrl: String
)

data class LanguageSupportItem(
    val language: String,
    val code: String,
    val ui: Boolean,
    val subtitle: Boolean,
    val audio: Boolean
)

@Composable
fun GameDetailScreen(
    game: GameItem,
    allGames: List<GameItem> = emptyList(),
    onDismiss: () -> Unit,
    onOpenReview: () -> Unit,
    onToggleFavorite: () -> Unit,
    onPosterLongClick: (GameItem) -> Unit = {}
) {
    val context = LocalContext.current
    BackHandler { onDismiss() }

    var selectedPrimaryTab by remember { mutableStateOf(DetailPrimaryTab.SERIES) }
    var selectedSubTab by remember { mutableStateOf(DetailSubTab.DETAILS) }
    var isSynopsisExpanded by remember { mutableStateOf(false) }
    var isDetailsExpanded by remember { mutableStateOf(false) }

    val gradientColors = remember(game.gradientColors) { parseGradient(game.gradientColors) }

    // Curated franchise series items
    val seriesItems = remember(game.title) {
        getFranchiseSeries(game.title)
    }

    // Similar games
    val similarGames = remember(game, allGames) {
        val filtered = allGames.filter { it.id != game.id }
        if (filtered.isNotEmpty()) filtered.take(6) else emptyList()
    }

    val languageList = remember {
        listOf(
            LanguageSupportItem("العربية / Arabic", "ar-SA", ui = true, subtitle = true, audio = true),
            LanguageSupportItem("English / English", "en-US", ui = true, subtitle = true, audio = true),
            LanguageSupportItem("French / Français", "fr-FR", ui = true, subtitle = true, audio = true),
            LanguageSupportItem("German / Deutsch", "de-DE", ui = true, subtitle = true, audio = true),
            LanguageSupportItem("Japanese / 日本語", "ja-JP", ui = true, subtitle = true, audio = true),
            LanguageSupportItem("Spanish / Español", "es-ES", ui = true, subtitle = true, audio = true),
            LanguageSupportItem("Chinese / 简体中文", "zh-CN", ui = true, subtitle = true, audio = false)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // 1. Cinematic Backdrop Header with Back & Action buttons (Video 2 reference)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Brush.verticalGradient(gradientColors))
            ) {
                if (game.coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(game.coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Dark gradient overlay fading down to pure obsidian DarkBackground
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Black.copy(alpha = 0.70f),
                                    DarkBackground
                                )
                            )
                        )
                )

                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                .border(1.dp, BorderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (game.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (game.isFavorite) Color.Red else Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                Toast.makeText(context, "تم نسخ رابط المشاركة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                .border(1.dp, BorderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                Toast.makeText(context, "${game.title} - خيارات إضافية", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                .border(1.dp, BorderSubtle, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // 2. Game Hero Info (Title, Dev, Action Chips + Poster on right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .offset(y = (-45).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left side: Title, Developer • Year, Trailer/Gameplay chips
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 14.dp)
                ) {
                    Text(
                        text = game.title,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${game.developer} • ${game.releaseYear}",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Chips: "عرض دعائي" (Trailer) & "طريقة اللعب" (Gameplay)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "تشغيل العرض الدعائي لـ ${game.title}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = CyberPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "عرض دعائي",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "استعراض طريقة اللعب (Gameplay)", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = CyberSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "طريقة اللعب",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Right side: Vertical Poster with Long-Press support!
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(138.dp)
                        .shadow(12.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CyberPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .background(Brush.verticalGradient(gradientColors))
                        .clickable { onPosterLongClick(game) }
                ) {
                    if (game.coverUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(game.coverUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = game.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = game.title,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 3. Synopsis Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .offset(y = (-30).dp)
            ) {
                Text(
                    text = game.synopsis.ifBlank {
                        "استكشف مغامرة ملحمية في عالم ${game.title} المفتوح المليء بالمغامرات والتفاصيل الرسومية المبهرة من تطوير ${game.developer}."
                    },
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    maxLines = if (isSynopsisExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )

                if (game.synopsis.length > 100) {
                    Text(
                        text = if (isSynopsisExpanded) "عرض أقل" else "...عرض المزيد",
                        color = CyberPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { isSynopsisExpanded = !isSynopsisExpanded }
                            .padding(top = 4.dp)
                    )
                }
            }

            // 4. Discussion Card: "التعليقات والأسئلة"
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .offset(y = (-20).dp)
                    .clickable {
                        Toast.makeText(context, "فتح نقاشات مجتمع ${game.title}", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(CyberPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = CyberPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "التعليقات والأسئلة",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "شارك حماسك، اسأل، وناقش اللعبة مع اللاعبين",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = ">",
                        color = TextMuted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. Notice Card: "تتوفر التقييمات وحالات اللعب عند صدور اللعبة..."
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(0.75.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .offset(y = (-12).dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "تتوفر التقييمات وحالات اللعب عند صدور اللعبة، يمكنك إضافتها إلى قائمة الأمنيات الآن.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // 6. Stats Row: "101 القوائم" | "3.8K قائمة الأمنيات"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .offset(y = (-6).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Lists Metric
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "101 القوائم",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Wishlist Metric
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3.8K قائمة الأمنيات",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 7. Multi-segment Primary Tabs: "السلسلة" | "إضافات" | "لقطات"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        DetailPrimaryTab.values().forEach { tab ->
                            val isSelected = selectedPrimaryTab == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) CyberPrimary else Color.Transparent)
                                    .clickable { selectedPrimaryTab = tab }
                                    .padding(horizontal = 22.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab.titleAr,
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Content for Primary Tabs
            when (selectedPrimaryTab) {
                DetailPrimaryTab.SERIES -> {
                    // Franchise Games Carousel (as seen in Video 2: GTA Vice City, GTA III, GTA 2, etc.)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(seriesItems) { sGame ->
                            Column(
                                modifier = Modifier
                                    .width(96.dp)
                                    .clickable {
                                        Toast.makeText(context, sGame.title, Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceElevated)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(sGame.coverUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = sGame.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sGame.title,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                DetailPrimaryTab.DLCS -> {
                    Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                        Text(
                            text = "لا توجد حزم إضافية مسجلة حالياً لهذه اللعبة.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
                DetailPrimaryTab.SCREENSHOTS -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(listOf(game.coverUrl, "https://media.rawg.io/media/screenshots/755/7552f856f88b0de1dee040121039b079.jpg", "https://media.rawg.io/media/screenshots/3c4/3c4c51b66741363d83b56ce66b1240ec.jpg")) { url ->
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 8. Secondary Sub-Tabs: "تفاصيل" | "منصات" | "أنواع" | "لغات" | "طاقم"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DetailSubTab.values().forEach { subTab ->
                    val isSelected = selectedSubTab == subTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) CyberPrimary else DarkSurface)
                            .border(1.dp, if (isSelected) CyberPrimary else BorderSubtle, RoundedCornerShape(14.dp))
                            .clickable { selectedSubTab = subTab }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = subTab.titleAr,
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content of Sub-Tabs (matching Video 2 subtabs)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedSubTab) {
                        DetailSubTab.DETAILS -> {
                            // "المطور"
                            DetailRowLabel("المطور", game.developer)
                            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                            // "تاريخ الإصدار"
                            DetailRowLabel("تاريخ الإصدار", "${game.releaseYear}")
                            HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                            // "أنماط اللعب"
                            DetailRowLabel("أنماط اللعب", "Single player, Multiplayer, Co-operative")

                            AnimatedVisibility(
                                visible = isDetailsExpanded,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                Column {
                                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                                    DetailRowLabel("منظور اللعب", "First person, Third person")
                                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                                    DetailRowLabel("التصنيف العمري", "M (Mature 17+) / PEGI 18")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isDetailsExpanded) "عرض أقل" else "عرض الكل",
                                color = CyberPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { isDetailsExpanded = !isDetailsExpanded }
                                    .padding(vertical = 4.dp)
                            )
                        }

                        DetailSubTab.PLATFORMS -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                game.platforms.split(",").forEach { platform ->
                                    val pName = platform.trim()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = pName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "متوفر", color = CyberSecondary, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "الأسعار والمتاجر",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = DarkSurfaceElevated,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Text(
                                            text = "PlayStation Store",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                    Surface(
                                        color = DarkSurfaceElevated,
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Text(
                                            text = "Xbox Games Store",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        DetailSubTab.GENRES -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = "الأنواع", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                game.genres.split(",").forEach { genre ->
                                    Text(
                                        text = "• ${genre.trim()}",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                                Text(text = "الثيمات (Themes)", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                listOf("Action", "Atmospheric", "Open World", "Crime", "Story Rich").forEach { theme ->
                                    Text(text = "• $theme", color = TextSecondary, fontSize = 13.sp)
                                }
                            }
                        }

                        DetailSubTab.LANGUAGES -> {
                            // Language table with UI, Subtitles, Audio checkmarks (as seen in Video 2 00:26)
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "اللغة", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                    Text(text = "الواجهة", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                    Text(text = "الترجمة", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                    Text(text = "الدبلجة", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                                }
                                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                                languageList.forEach { lang ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1.5f)) {
                                            Text(text = lang.language, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = lang.code, color = TextMuted, fontSize = 10.sp)
                                        }

                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            if (lang.ui) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(16.dp))
                                            else Text("-", color = TextMuted)
                                        }

                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            if (lang.subtitle) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(16.dp))
                                            else Text("-", color = TextMuted)
                                        }

                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                            if (lang.audio) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(16.dp))
                                            else Text("-", color = TextMuted)
                                        }
                                    }
                                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.4f), thickness = 0.5.dp)
                                }
                            }
                        }

                        DetailSubTab.CAST -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "فريق الإخراج والتطوير الرئيسي",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                listOf(
                                    "Sam Houser" to "منتج تنفيذي",
                                    "Dan Houser" to "مؤلف مشارك",
                                    "Rob Nelson" to "مخرج الإنتاج",
                                    "Woody Jackson" to "الموسيقى التصويرية الأصلية"
                                ).forEach { (name, role) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(text = role, color = CyberSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 9. Similar Games: "ألعاب مشابهة" (Video 2 00:30)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ألعاب مشابهة",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(similarGames) { sGame ->
                        Column(
                            modifier = Modifier
                                .width(96.dp)
                                .clickable { onPosterLongClick(sGame) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(134.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceElevated)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(sGame.coverUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = sGame.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sGame.title,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 10. Bottom-right Floating Action Button (+) with luminous cyan accent
        FloatingActionButton(
            onClick = onOpenReview,
            containerColor = CyberPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(56.dp)
                .shadow(12.dp, CircleShape, spotColor = CyberPrimary)
                .testTag("fab_add_review")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Review",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun DetailRowLabel(title: String, value: String) {
    Column {
        Text(text = title, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

private fun getFranchiseSeries(gameTitle: String): List<SeriesGameItem> {
    return if (gameTitle.contains("Grand Theft Auto", ignoreCase = true) || gameTitle.contains("GTA", ignoreCase = true)) {
        listOf(
            SeriesGameItem("Grand Theft Auto V", "2013", "https://media.rawg.io/media/games/20a/20aa03a10cda45239fe22d035c0ebe64.jpg"),
            SeriesGameItem("Grand Theft Auto: San Andreas", "2004", "https://media.rawg.io/media/games/13a/13a528ac9864bbe005bdde1190380f30.jpg"),
            SeriesGameItem("Grand Theft Auto: Vice City", "2002", "https://media.rawg.io/media/games/84d/84da102f8f7e9ab6f47d4834d58852e6.jpg"),
            SeriesGameItem("Grand Theft Auto III", "2001", "https://media.rawg.io/media/games/5fa/5fae5f625021703666f272a0f6226cb5.jpg"),
            SeriesGameItem("Grand Theft Auto 2", "1999", "https://media.rawg.io/media/games/851/8510f277a0ba7ddf97b69c450c05df14.jpg")
        )
    } else if (gameTitle.contains("Elden Ring", ignoreCase = true) || gameTitle.contains("Souls", ignoreCase = true)) {
        listOf(
            SeriesGameItem("Shadow of the Erdtree", "2024", "https://media.rawg.io/media/games/b29/b294fdd866dcdb643e7bab370a552855.jpg"),
            SeriesGameItem("Dark Souls III", "2016", "https://media.rawg.io/media/games/da1/da1b267764d77221f07a4386b6548e5a.jpg"),
            SeriesGameItem("Bloodborne", "2015", "https://media.rawg.io/media/games/214/2143ea5367ad1020019803d4cb0703ce.jpg"),
            SeriesGameItem("Dark Souls Remastered", "2018", "https://media.rawg.io/media/games/ebd/ebdb053f3199bc4fe84f22fbab034870.jpg")
        )
    } else {
        listOf(
            SeriesGameItem("Red Dead Redemption 2", "2018", "https://media.rawg.io/media/games/511/5118aff5091cb3efec399c808f8c598f.jpg"),
            SeriesGameItem("The Witcher 3", "2015", "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg"),
            SeriesGameItem("Cyberpunk 2077", "2020", "https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c8c59c92.jpg"),
            SeriesGameItem("God of War Ragnarök", "2022", "https://media.rawg.io/media/screenshots/3c4/3c4c51b66741363d83b56ce66b1240ec.jpg")
        )
    }
}
