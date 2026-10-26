package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.data.model.GameItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldStar
import com.example.ui.theme.StarBackground
import com.example.ui.theme.StatusBacklog
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDropped
import com.example.ui.theme.StatusMastered
import com.example.ui.theme.StatusPlaying
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StarRatingDisplay(
    rating: Float,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    showNumber: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        for (i in 1..maxStars) {
            val starScore = rating - (i - 1)
            val icon = when {
                starScore >= 0.75f -> Icons.Filled.Star
                starScore >= 0.25f -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            val tint = if (starScore >= 0.25f) GoldStar else StarBackground

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(starSize)
            )
        }
        if (showNumber && rating > 0f) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                color = GoldStar,
                fontWeight = FontWeight.Bold,
                fontSize = (starSize.value * 0.85f).sp
            )
        }
    }
}

@Composable
fun InteractiveStarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        for (i in 1..5) {
            val isFull = rating >= i
            val isHalf = rating >= (i - 0.5f) && rating < i

            val icon = when {
                isFull -> Icons.Filled.Star
                isHalf -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            val tint = if (isFull || isHalf) GoldStar else StarBackground

            IconButton(
                onClick = {
                    val nextRating = when {
                        rating == i.toFloat() -> (i - 0.5f)
                        rating == (i - 0.5f) -> (i - 1f)
                        else -> i.toFloat()
                    }
                    onRatingChanged(nextRating.coerceAtLeast(0f))
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("star_button_$i")
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Star $i",
                    tint = tint,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

@Composable
fun GameStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (labelAr, labelEn, bgColor, fgColor) = when (status.uppercase()) {
        "PLAYING" -> Quad("ألعبها حالياً", "Playing", StatusPlaying.copy(alpha = 0.2f), StatusPlaying)
        "COMPLETED" -> Quad("مكتملة", "Completed", StatusCompleted.copy(alpha = 0.2f), StatusCompleted)
        "BACKLOG" -> Quad("في الانتظار", "Backlog", StatusBacklog.copy(alpha = 0.2f), StatusBacklog)
        "MASTERED" -> Quad("100% بلاتينيوم", "Mastered", StatusMastered.copy(alpha = 0.25f), StatusMastered)
        "DROPPED" -> Quad("متوقفة", "Dropped", StatusDropped.copy(alpha = 0.2f), StatusDropped)
        else -> Quad("غير مسجلة", "Unlogged", DarkSurfaceElevated, TextMuted)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, fgColor.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (compact) 6.dp else 10.dp,
                vertical = if (compact) 2.dp else 4.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(fgColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = labelAr,
                color = fgColor,
                fontSize = if (compact) 10.sp else 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun PlatformTag(
    platform: String,
    modifier: Modifier = Modifier
) {
    val chipColor = when {
        platform.contains("PS", ignoreCase = true) || platform.contains("PlayStation", ignoreCase = true) -> Color(0xFF1E40AF)
        platform.contains("Xbox", ignoreCase = true) -> Color(0xFF15803D)
        platform.contains("Switch", ignoreCase = true) || platform.contains("Nintendo", ignoreCase = true) -> Color(0xFFB91C1C)
        platform.contains("PC", ignoreCase = true) -> Color(0xFF7E22CE)
        else -> Color(0xFF334155)
    }

    Surface(
        color = chipColor.copy(alpha = 0.25f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, chipColor.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Text(
            text = platform,
            color = chipColor.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color(0xFF0C101A),
        Color(0xFF161F30),
        Color(0xFF26354D),
        Color(0xFF161F30),
        Color(0xFF0C101A)
    )
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = -500f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 350f, translateAnim + 350f)
    )
}

@Composable
fun PosterShimmerPlaceholder(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun CyberPosterImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(12.dp),
    fallbackText: String? = null
) {
    val context = LocalContext.current
    val optimizedUrl = remember(imageUrl) {
        com.example.data.remote.GameSearchHelper.optimizePosterUrl(imageUrl)
    }

    if (optimizedUrl.isNotBlank()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(optimizedUrl)
                .crossfade(300)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier.clip(shape),
            loading = {
                PosterShimmerPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    shape = shape
                )
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(DarkSurfaceVariant, DarkSurface)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!fallbackText.isNullOrBlank()) {
                        Text(
                            text = fallbackText,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = CyberPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            success = {
                SubcomposeAsyncImageContent()
            }
        )
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(DarkSurfaceVariant, DarkSurface)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (!fallbackText.isNullOrBlank()) {
                Text(
                    text = fallbackText,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(6.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = CyberPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun GamePosterThumbnail(
    game: GameItem,
    modifier: Modifier = Modifier,
    height: Dp = 130.dp,
    width: Dp = 95.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = { onLongClick?.invoke() }
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .then(clickModifier)
    ) {
        CyberPosterImage(
            imageUrl = game.coverUrl,
            contentDescription = game.title,
            fallbackText = "${game.title}\n(${game.releaseYear})",
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.matchParentSize()
        )
        // Rating pill in top right corner if rated
        if (game.userRating > 0f) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(bottomStart = 8.dp),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = GoldStar,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = String.format("%.1f", game.userRating),
                        color = GoldStar,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun parseGradient(str: String): List<Color> {
    return try {
        val parts = str.split(",")
        if (parts.size >= 2) {
            listOf(
                Color(android.graphics.Color.parseColor(parts[0].trim())),
                Color(android.graphics.Color.parseColor(parts[1].trim()))
            )
        } else {
            listOf(CyberPrimary, DarkSurfaceVariant)
        }
    } catch (e: Exception) {
        listOf(CyberPrimary, DarkSurfaceVariant)
    }
}
