package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.GameItem
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.util.AppLanguage
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class RadialActionItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val icon: ImageVector,
    val targetStatus: String? = null,
    val isReviewAction: Boolean = false
)

@Composable
fun RadialGameActionMenu(
    game: GameItem,
    onDismiss: () -> Unit,
    onStatusSelected: (String) -> Unit,
    onOpenReview: () -> Unit
) {
    val isArabic = AppLanguage.isArabic
    var isExpanded by remember { mutableStateOf(false) }
    var activeHoveredTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isExpanded = true
    }

    val actions = remember {
        listOf(
            RadialActionItem("playing", "ألعبها حالياً", "Playing", Icons.Filled.SportsEsports, targetStatus = "PLAYING"),
            RadialActionItem("completed", "تم اللعب", "Completed", Icons.Filled.Check, targetStatus = "COMPLETED"),
            RadialActionItem("wishlist", "قائمة الأمنيات", "Wishlist", Icons.Filled.Favorite, targetStatus = "BACKLOG"),
            RadialActionItem("dropped", "غير مكتملة", "Dropped", Icons.Filled.Close, targetStatus = "DROPPED"),
            RadialActionItem("review", "كتابة مراجعة", "Write Review", Icons.Filled.Edit, isReviewAction = true)
        )
    }

    val animProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radial_spring"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Center Game Poster with Glow
            Box(
                modifier = Modifier
                    .width(135.dp)
                    .height(190.dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = CyberPrimary)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, CyberPrimary.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkSurface, DarkBackground)
                        )
                    )
            ) {
                CyberPosterImage(
                    imageUrl = game.coverUrl,
                    contentDescription = game.title,
                    fallbackText = if (isArabic && game.titleAr.isNotBlank()) game.titleAr else game.title,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Radial Fan-Out Buttons around the poster on the left arc
            val baseRadiusDp = 120f * animProgress
            val angles = listOf(
                110.0, // Top-Left: Controller
                145.0, // Mid-Upper-Left: Checkmark
                180.0, // Directly Left: Heart
                215.0, // Mid-Lower-Left: Close X
                245.0  // Bottom-Left: Edit Review
            )

            actions.forEachIndexed { index, action ->
                val angleDeg = angles.getOrElse(index) { 180.0 }
                val angleRad = Math.toRadians(angleDeg)
                val offsetX = (cos(angleRad) * baseRadiusDp).roundToInt()
                val offsetY = (-sin(angleRad) * baseRadiusDp).roundToInt()

                val actionLabel = if (isArabic) action.titleAr else action.titleEn
                val isSelectedOrHovered = activeHoveredTitle == actionLabel

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.dp.roundToPx(), offsetY.dp.roundToPx()) }
                        .size(52.dp)
                        .shadow(8.dp, CircleShape, spotColor = CyberPrimary)
                        .clip(CircleShape)
                        .background(
                            if (isSelectedOrHovered) CyberPrimary else DarkSurface
                        )
                        .border(
                            1.dp,
                            if (isSelectedOrHovered) Color.White else CyberPrimary.copy(alpha = 0.6f),
                            CircleShape
                        )
                        .clickable {
                            activeHoveredTitle = actionLabel
                            if (action.isReviewAction) {
                                onDismiss()
                                onOpenReview()
                            } else {
                                action.targetStatus?.let { status ->
                                    onStatusSelected(status)
                                }
                                onDismiss()
                            }
                        }
                        .testTag("radial_action_${action.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = actionLabel,
                        tint = if (isSelectedOrHovered) Color.White else CyberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Floating Tooltip Pill - Positioned safely below all buttons to prevent any text overlapping
            val defaultTooltip = if (isArabic) "اختر إجراء سريع للعبة" else "Choose quick action"
            AnimatedVisibility(
                visible = activeHoveredTitle != null || isExpanded,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 175.dp) // Pushed well below button 5
            ) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.6f)),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = activeHoveredTitle ?: defaultTooltip,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}
