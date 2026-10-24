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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class RadialActionItem(
    val id: String,
    val titleAr: String,
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
    var isExpanded by remember { mutableStateOf(false) }
    var activeHoveredTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isExpanded = true
    }

    val actions = remember {
        listOf(
            RadialActionItem("playing", "ألعبها حالياً", Icons.Filled.SportsEsports, targetStatus = "PLAYING"),
            RadialActionItem("completed", "تم اللعب", Icons.Filled.Check, targetStatus = "COMPLETED"),
            RadialActionItem("wishlist", "قائمة الأمنيات", Icons.Filled.Favorite, targetStatus = "BACKLOG"),
            RadialActionItem("dropped", "غير مكتملة", Icons.Filled.Close, targetStatus = "DROPPED"),
            RadialActionItem("review", "كتابة مراجعة", Icons.Filled.Edit, isReviewAction = true)
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
                .background(Color.Black.copy(alpha = 0.82f))
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
                    .border(1.5.dp, CyberPrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkSurface, DarkBackground)
                        )
                    )
            ) {
                if (game.coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = game.coverUrl,
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Radial Fan-Out Buttons around the poster (positioned on the left arc matching Video 1)
            // In Video 1, the 5 buttons are arranged in an arc to the left/around the poster:
            // angles from ~ 110 degrees down to 250 degrees (or around center-left)
            val baseRadiusDp = 130f * animProgress
            val angles = listOf(
                110.0, // Top-Left: Controller
                145.0, // Mid-Upper-Left: Checkmark
                180.0, // Directly Left: Heart
                215.0, // Mid-Lower-Left: Close X
                250.0  // Bottom-Left: Edit Review
            )

            actions.forEachIndexed { index, action ->
                val angleDeg = angles.getOrElse(index) { 180.0 }
                val angleRad = Math.toRadians(angleDeg)
                // In Compose coordinate system: x = cos(angle), y = -sin(angle)
                val offsetX = (cos(angleRad) * baseRadiusDp).roundToInt()
                val offsetY = (-sin(angleRad) * baseRadiusDp).roundToInt()

                val isSelectedOrHovered = activeHoveredTitle == action.titleAr

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.dp.roundToPx(), offsetY.dp.roundToPx()) }
                        .size(54.dp)
                        .shadow(8.dp, CircleShape, spotColor = CyberPrimary)
                        .clip(CircleShape)
                        .background(
                            if (isSelectedOrHovered) CyberPrimary else DarkSurface
                        )
                        .border(
                            1.dp,
                            if (isSelectedOrHovered) Color.White else CyberPrimary.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .clickable {
                            activeHoveredTitle = action.titleAr
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
                        contentDescription = action.titleAr,
                        tint = if (isSelectedOrHovered) Color.White else CyberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Floating Tooltip Pill (matching Video 1 bottom tooltip "ألعبها حالياً", "تم اللعب", etc.)
            AnimatedVisibility(
                visible = activeHoveredTitle != null || isExpanded,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 145.dp)
            ) {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = activeHoveredTitle ?: "اختر إجراء سريع للعبة",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
