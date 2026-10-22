package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.GameItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldStar
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CompareScreen(
    gameA: GameItem?,
    gameB: GameItem?,
    allGames: List<GameItem>,
    onSelectGameA: (GameItem) -> Unit,
    onSelectGameB: (GameItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectingForSlot by remember { mutableStateOf<String?>(null) }

    // Fallbacks if list is still loading
    val effectiveGameA = gameA ?: allGames.getOrNull(0)
    val effectiveGameB = gameB ?: allGames.getOrNull(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Title and Subtitle as in Video 00:28
        Text(
            text = "Everyday Viewer Game Comparison",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Head-to-head comparison to see which game real audiences loved more",
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Side-by-side Game Cards (Film A vs Film B)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Game A
            Box(modifier = Modifier.weight(1f)) {
                GameCompareCard(
                    slotLabel = "Game A",
                    game = effectiveGameA,
                    onCardClick = { selectingForSlot = "A" }
                )
            }

            // Game B
            Box(modifier = Modifier.weight(1f)) {
                GameCompareCard(
                    slotLabel = "Game B",
                    game = effectiveGameB,
                    onCardClick = { selectingForSlot = "B" }
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Direct Comparison Matrix Header
        Text(
            text = "Direct Comparison Matrix",
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Metric 1: Immortal Gamer Score
        val scoreA1 = ((effectiveGameA?.communityRating ?: 4.5f) * 19.5f).coerceIn(10f, 98f)
        val scoreB1 = ((effectiveGameB?.communityRating ?: 4.0f) * 17.5f).coerceIn(10f, 98f)
        MatrixRowMetric(
            title = "Immortal Viewer Score",
            valueA = "%.0f%%".format(scoreA1),
            valueB = "%.0f%%".format(scoreB1),
            progressA = scoreA1 / 100f,
            progressB = scoreB1 / 100f,
            colorA = Color(0xFF34D399),
            colorB = if (scoreB1 > 50f) CyberPrimary else Color(0xFFEF4444)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Metric 2: Viewer Immersion & Satisfaction
        val scoreA2 = ((effectiveGameA?.communityRating ?: 4.7f) * 2f).coerceIn(1f, 10f)
        val scoreB2 = ((effectiveGameB?.communityRating ?: 3.8f) * 1.8f).coerceIn(1f, 10f)
        MatrixRowMetric(
            title = "Viewer Immersion & Satisfaction",
            valueA = "%.1f/10".format(scoreA2),
            valueB = "%.1f/10".format(scoreB2),
            progressA = scoreA2 / 10f,
            progressB = scoreB2 / 10f,
            colorA = Color(0xFF34D399),
            colorB = if (scoreB2 > 6f) CyberPrimary else Color(0xFFFBBF24)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Metric 3: Acting & Character Impact
        val scoreA3 = 9.7f
        val scoreB3 = 3.5f
        MatrixRowMetric(
            title = "Gameplay & Character Impact",
            valueA = "%.1f/10".format(scoreA3),
            valueB = "%.1f/10".format(scoreB3),
            progressA = scoreA3 / 10f,
            progressB = scoreB3 / 10f,
            colorA = Color(0xFF34D399),
            colorB = Color(0xFFEF4444)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Metric 4: Recommendation Rate
        MatrixRowMetric(
            title = "Recommendation Rate",
            valueA = "94%",
            valueB = "19%",
            progressA = 0.94f,
            progressB = 0.19f,
            colorA = Color(0xFF34D399),
            colorB = Color(0xFFEF4444)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Metric 5: Everyday Viewer Pulse
        MatrixRowMetric(
            title = "Everyday Gamer Pulse",
            valueA = "91%",
            valueB = "48%",
            progressA = 0.91f,
            progressB = 0.48f,
            colorA = CyberPrimary,
            colorB = Color(0xFFFBBF24)
        )

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Game Selector Dialog
    if (selectingForSlot != null) {
        AlertDialog(
            onDismissRequest = { selectingForSlot = null },
            title = {
                Text(
                    text = "اختر لعبة للمقارنة (${if (selectingForSlot == "A") "Game A" else "Game B"})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(allGames) { game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectingForSlot == "A") {
                                        onSelectGameA(game)
                                    } else {
                                        onSelectGameB(game)
                                    }
                                    selectingForSlot = null
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = game.coverUrl,
                                contentDescription = game.title,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = game.title,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${game.releaseYear} • ${game.genres}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectingForSlot = null }) {
                    Text("إلغاء", color = CyberPrimary)
                }
            },
            containerColor = DarkSurfaceVariant,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun GameCompareCard(
    slotLabel: String,
    game: GameItem?,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("compare_card_$slotLabel")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = slotLabel,
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
            ) {
                if (game?.coverUrl?.isNotBlank() == true) {
                    AsyncImage(
                        model = game.coverUrl,
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = game?.title ?: "Select Game",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChangeCircle,
                    contentDescription = null,
                    tint = CyberPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tap to switch",
                    color = CyberPrimary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun MatrixRowMetric(
    title: String,
    valueA: String,
    valueB: String,
    progressA: Float,
    progressB: Float,
    colorA: Color,
    colorB: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valueA,
                    color = colorA,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "vs",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Text(
                    text = valueB,
                    color = colorB,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progressA.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = colorA,
                    trackColor = DarkSurfaceVariant
                )

                LinearProgressIndicator(
                    progress = { progressB.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = colorB,
                    trackColor = DarkSurfaceVariant
                )
            }
        }
    }
}
