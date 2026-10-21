package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameItem
import com.example.ui.components.GamePosterThumbnail
import com.example.ui.components.InteractiveStarRatingBar
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldStar
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReviewDialog(
    game: GameItem,
    onDismiss: () -> Unit,
    onSave: (
        rating: Float,
        status: String,
        hours: Float,
        reviewTitle: String,
        reviewText: String,
        pros: String,
        cons: String,
        gameplay: Float,
        story: Float,
        visuals: Float,
        audio: Float,
        platformPlayed: String,
        isFav: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var rating by remember { mutableFloatStateOf(if (game.userRating > 0f) game.userRating else 4.0f) }
    var status by remember { mutableStateOf(if (game.status != "NONE") game.status else "COMPLETED") }
    var hours by remember { mutableFloatStateOf(game.hoursPlayed) }
    var reviewTitle by remember { mutableStateOf(game.reviewTitle) }
    var reviewText by remember { mutableStateOf(game.userReview) }
    var pros by remember { mutableStateOf(game.pros) }
    var cons by remember { mutableStateOf(game.cons) }
    var gameplayScore by remember { mutableFloatStateOf(if (game.subGameplayScore > 0f) game.subGameplayScore else 8.5f) }
    var storyScore by remember { mutableFloatStateOf(if (game.subStoryScore > 0f) game.subStoryScore else 8.0f) }
    var visualsScore by remember { mutableFloatStateOf(if (game.subVisualsScore > 0f) game.subVisualsScore else 9.0f) }
    var audioScore by remember { mutableFloatStateOf(if (game.subAudioScore > 0f) game.subAudioScore else 8.5f) }
    var platformPlayed by remember { mutableStateOf(game.platformPlayed.ifBlank { "PlayStation 5" }) }
    var isFavorite by remember { mutableStateOf(game.isFavorite) }

    val statusOptions = listOf(
        "PLAYING" to "ألعبها الآن",
        "COMPLETED" to "مكتملة",
        "BACKLOG" to "في الانتظار",
        "MASTERED" to "100% بلاتينيوم",
        "DROPPED" to "متوقفة"
    )

    val platformsList = listOf(
        "PlayStation 5",
        "PC",
        "Xbox Series X",
        "Nintendo Switch",
        "Steam Deck",
        "PS4",
        "Xbox One"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSurfaceVariant, CircleShape)
                        .testTag("close_review_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "تسجيل ومراجعة اللعبة",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isFavorite) Color(0xFFEF4444).copy(alpha = 0.2f) else DarkSurfaceVariant,
                            CircleShape
                        )
                        .testTag("favorite_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEF4444) else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Game banner card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GamePosterThumbnail(
                        game = game,
                        height = 90.dp,
                        width = 65.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = game.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        if (game.titleAr.isNotBlank()) {
                            Text(
                                text = game.titleAr,
                                color = CyberSecondary,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${game.developer} • ${game.releaseYear}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = game.genres,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Star Rating
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تقييمك للعبة (من 5 نجوم)",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (rating > 0f) "${String.format("%.1f", rating)} / 5.0" else "غير مقيمة بعد",
                        color = GoldStar,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InteractiveStarRatingBar(
                        rating = rating,
                        onRatingChanged = { rating = it },
                        starSize = 36.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Status Selector
            Text(
                text = "حالة اللعب",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusOptions.forEach { (key, label) ->
                    val isSelected = status == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { status = key },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyberPrimary else BorderSubtle
                        ),
                        modifier = Modifier.testTag("status_chip_$key")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Platform Played & Hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hours Counter Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ساعات اللعب",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${hours.toInt()} ساعة",
                            color = CyberSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { hours = (hours - 5f).coerceAtLeast(0f) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DarkSurfaceElevated, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Minus",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = { hours += 5f },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(DarkSurfaceElevated, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Plus",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Platform Played
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "جهاز اللعب",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = platformPlayed,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Quick toggle through platforms
                        OutlinedButton(
                            onClick = {
                                val curIndex = platformsList.indexOf(platformPlayed)
                                val nextIndex = (curIndex + 1) % platformsList.size
                                platformPlayed = platformsList[nextIndex]
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "تغيير الجهاز", fontSize = 11.sp, color = CyberPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Detailed Review Notes
            Text(
                text = "مراجعتك الخاصة",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = reviewTitle,
                onValueChange = { reviewTitle = it },
                label = { Text("عنوان المراجعة (مثال: تجربة لا تنسى)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_title_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("اكتب رأيك وتفاصيل تجربتك مع اللعبة...") },
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_body_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Pros and Cons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = pros,
                    onValueChange = { pros = it },
                    label = { Text("الإيجابيات (مفصولة بفواصل)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = Color(0xFF34D399),
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = cons,
                    onValueChange = { cons = it },
                    label = { Text("السلبيات (مفصولة بفواصل)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = Color(0xFFF87171),
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Sub-criteria Scores (1-10)
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تفصيل درجات العناصر (من 10)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SubScoreSlider(label = "أسلوب اللعب (Gameplay)", score = gameplayScore) { gameplayScore = it }
                    SubScoreSlider(label = "القصة والشخصيات (Story)", score = storyScore) { storyScore = it }
                    SubScoreSlider(label = "الرسومات والعالم (Visuals)", score = visualsScore) { visualsScore = it }
                    SubScoreSlider(label = "الموسيقى والصوتيات (Audio)", score = audioScore) { audioScore = it }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions: Save & Cancel
            Button(
                onClick = {
                    onSave(
                        rating,
                        status,
                        hours,
                        reviewTitle,
                        reviewText,
                        pros,
                        cons,
                        gameplayScore,
                        storyScore,
                        visualsScore,
                        audioScore,
                        platformPlayed,
                        isFavorite
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_review_button")
            ) {
                Text(
                    text = "حفظ المراجعة في السجل",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = "إلغاء")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SubScoreSlider(
    label: String,
    score: Float,
    onScoreChanged: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextSecondary, fontSize = 12.sp)
            Text(
                text = "${String.format("%.1f", score)} / 10",
                color = CyberSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = score,
            onValueChange = onScoreChanged,
            valueRange = 1f..10f,
            steps = 17, // 0.5 increments
            colors = SliderDefaults.colors(
                thumbColor = CyberSecondary,
                activeTrackColor = CyberPrimary,
                inactiveTrackColor = DarkSurfaceElevated
            )
        )
    }
}
