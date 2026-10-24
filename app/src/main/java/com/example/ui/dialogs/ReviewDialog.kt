package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldStar
import com.example.ui.theme.StarBackground
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
    var thumbsUp by remember { mutableStateOf(true) }
    var reviewText by remember { mutableStateOf(game.userReview) }
    var isDetailedAccordionOpen by remember { mutableStateOf(false) }

    // Detailed scores
    var gameplayScore by remember { mutableFloatStateOf(if (game.subGameplayScore > 0f) game.subGameplayScore else 8.5f) }
    var storyScore by remember { mutableFloatStateOf(if (game.subStoryScore > 0f) game.subStoryScore else 8.0f) }
    var visualsScore by remember { mutableFloatStateOf(if (game.subVisualsScore > 0f) game.subVisualsScore else 9.0f) }
    var audioScore by remember { mutableFloatStateOf(if (game.subAudioScore > 0f) game.subAudioScore else 8.5f) }

    // Additional inputs matching Video 3:
    var hoursInputText by remember { mutableStateOf(if (game.hoursPlayed > 0f) game.hoursPlayed.toInt().toString() else "25") }
    var selectedPlatform by remember { mutableStateOf(game.platformPlayed.ifBlank { "PlayStation 5" }) }
    var playPeriod by remember { mutableStateOf("2024 - 2026") }
    var timesCompleted by remember { mutableIntStateOf(1) }
    var isRecommended by remember { mutableStateOf(true) }
    var isPlayedWithFriends by remember { mutableStateOf(false) }

    val platformOptions = listOf("PlayStation 5", "PC", "Xbox Series X", "Nintendo Switch")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header Bar: "اكتب مراجعة" with Close button on right (Video 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.dp, BorderSubtle, CircleShape)
                        .testTag("btn_close_review")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "اكتب مراجعة",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(36.dp))
            }

            // Subtitle: "التقييم العام (اختياري)"
            Text(
                text = "التقييم العام (اختياري)",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stars Row + Thumbs Up Toggle (Video 3 00:10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 5 Interactive Stars
                for (i in 1..5) {
                    val isFilled = rating >= i
                    IconButton(
                        onClick = { rating = i.toFloat() },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Star $i",
                            tint = if (isFilled) GoldStar else StarBackground,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Thumbs Up / Down Toggle
                IconButton(
                    onClick = { thumbsUp = !thumbsUp },
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (thumbsUp) Color(0xFF10B981).copy(alpha = 0.2f) else DarkSurface,
                            CircleShape
                        )
                        .border(
                            1.dp,
                            if (thumbsUp) Color(0xFF10B981) else BorderSubtle,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (thumbsUp) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Thumbs",
                        tint = if (thumbsUp) Color(0xFF10B981) else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Review Input: "أضف مراجعة..."
            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = {
                    Text(text = "أضف مراجعة...", color = TextMuted, fontSize = 13.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .testTag("input_review_text"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Expandable Accordion: "التقييمات التفصيلية" (Video 3 00:10)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDetailedAccordionOpen = !isDetailedAccordionOpen }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التقييمات التفصيلية",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isDetailedAccordionOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CyberPrimary
                        )
                    }

                    AnimatedVisibility(
                        visible = isDetailedAccordionOpen,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            SubScoreSliderRow("أسلوب اللعب (Gameplay)", gameplayScore) { gameplayScore = it }
                            SubScoreSliderRow("القصة والحوارات (Story)", storyScore) { storyScore = it }
                            SubScoreSliderRow("الرسوم والتصميم (Visuals)", visualsScore) { visualsScore = it }
                            SubScoreSliderRow("الموسيقى والمؤثرات (Audio)", audioScore) { audioScore = it }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two-column layout for Hours & Platform (Video 3 00:10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // "إجمالي ساعات اللعبة / مثال: 120"
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "إجمالي ساعات اللعبة",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = hoursInputText,
                        onValueChange = { hoursInputText = it },
                        placeholder = { Text("مثال: 120", color = TextMuted, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // "فترة اللعب"
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "فترة اللعب",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = playPeriod,
                        onValueChange = { playPeriod = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Platform Selector: "المنصة"
            Text(
                text = "المنصة",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                platformOptions.forEach { plat ->
                    val isSelected = selectedPlatform == plat
                    Surface(
                        color = if (isSelected) CyberPrimary.copy(alpha = 0.2f) else DarkSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyberPrimary else BorderSubtle
                        ),
                        modifier = Modifier.clickable { selectedPlatform = plat }
                    ) {
                        Text(
                            text = plat,
                            color = if (isSelected) CyberPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "عدد مرات التختيم" with [-] 1 [+]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "عدد مرات التختيم",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (timesCompleted > 1) timesCompleted-- },
                        modifier = Modifier
                            .size(34.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "$timesCompleted",
                        color = CyberPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = { timesCompleted++ },
                        modifier = Modifier
                            .size(34.dp)
                            .background(DarkSurfaceElevated, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggles: "وصاني" & "لعبتها مع"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = if (isRecommended) CyberPrimary.copy(alpha = 0.2f) else DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isRecommended) CyberPrimary else BorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isRecommended = !isRecommended }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Recommend, contentDescription = null, tint = if (isRecommended) CyberPrimary else TextMuted, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "وصاني", color = if (isRecommended) CyberPrimary else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    color = if (isPlayedWithFriends) CyberSecondary.copy(alpha = 0.2f) else DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlayedWithFriends) CyberSecondary else BorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isPlayedWithFriends = !isPlayedWithFriends }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.People, contentDescription = null, tint = if (isPlayedWithFriends) CyberSecondary else TextMuted, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "لعبتها مع أصدقاء", color = if (isPlayedWithFriends) CyberSecondary else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Save Button: "حفظ" (Video 3 00:10)
            Button(
                onClick = {
                    val parsedHours = hoursInputText.toFloatOrNull() ?: game.hoursPlayed
                    onSave(
                        rating,
                        if (timesCompleted > 0) "COMPLETED" else "PLAYING",
                        parsedHours,
                        if (isRecommended) "يوصى بها بشدة" else "",
                        reviewText,
                        if (isRecommended) "تجربة متكاملة وممتعة" else "",
                        "",
                        gameplayScore,
                        storyScore,
                        visualsScore,
                        audioScore,
                        selectedPlatform,
                        thumbsUp
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_review")
            ) {
                Text(
                    text = "حفظ",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SubScoreSliderRow(
    title: String,
    score: Float,
    onScoreChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = TextSecondary, fontSize = 12.sp)
            Text(
                text = String.format("%.1f/10", score),
                color = CyberPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Slider(
            value = score,
            onValueChange = onScoreChange,
            valueRange = 1f..10f,
            steps = 17,
            colors = SliderDefaults.colors(
                thumbColor = CyberPrimary,
                activeTrackColor = CyberPrimary,
                inactiveTrackColor = BorderSubtle
            )
        )
    }
}
