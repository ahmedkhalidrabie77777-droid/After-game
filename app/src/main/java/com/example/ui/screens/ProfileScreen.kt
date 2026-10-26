package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberTertiary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldStar
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.GamerStats
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    currentUser: FirebaseUser?,
    stats: GamerStats,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onNavigateToDiary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (currentUser == null) {
        // Unauthenticated Profile View (Exact replica of video at 00:33)
        GuestProfileView(
            onSignInClick = onSignInClick,
            onSettingsClick = { showSettingsDialog = true },
            modifier = modifier
        )
    } else {
        // Authenticated Account View
        UserAccountView(
            currentUser = currentUser,
            stats = stats,
            onSignOutClick = onSignOutClick,
            onSettingsClick = { showSettingsDialog = true },
            onNavigateToDiary = onNavigateToDiary,
            modifier = modifier
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = CyberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الإعدادات والمظهر",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "مظهر التطبيق: After Game Cyan Dark (الافتراضي)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "مصدر البيانات: RAWG Video Games Database + Firebase Cloud",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "إصدار التطبيق: v2.4 (Community Edition)",
                        color = CyberSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("إغلاق", color = CyberPrimary)
                }
            },
            containerColor = DarkSurfaceVariant,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun GuestProfileView(
    onSignInClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Dramatic Background Art at top (fading to dark background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay for smooth cinematic fade
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                DarkBackground.copy(alpha = 0.35f),
                                DarkBackground.copy(alpha = 0.70f),
                                DarkBackground
                            )
                        )
                    )
            )
        }

        // Top Settings Icon Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("settings_icon")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary.copy(alpha = 0.9f),
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 90.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Infinity Controller Logo (Transparent background, compact size)
            Image(
                painter = painterResource(id = R.drawable.ic_after_game_logo),
                contentDescription = "After Game Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(110.dp)
                    .height(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "After Game",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            val isArabic = AppLanguage.isArabic
            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = if (isArabic) "تقييمات وتجارب حقيقية من مجتمع اللاعبين" else "Real, honest game ratings from everyday gamers",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Action Items (Menu Cards like in Video 00:33)
            ProfileOptionCard(
                icon = Icons.AutoMirrored.Filled.Login,
                label = "Sign in",
                labelAr = "تسجيل الدخول",
                iconColor = CyberPrimary,
                onClick = onSignInClick,
                testTag = "btn_profile_signin"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionCard(
                icon = Icons.Default.PersonAdd,
                label = "Create account",
                labelAr = "إنشاء حساب جديد",
                iconColor = CyberSecondary,
                onClick = onSignInClick,
                testTag = "btn_profile_signup"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionCard(
                icon = Icons.Default.Settings,
                label = "Settings & Theme",
                labelAr = "الإعدادات والمظهر",
                iconColor = TextSecondary,
                onClick = onSettingsClick,
                testTag = "btn_profile_settings"
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Bottom Artwork text
            Text(
                text = if (isArabic) "لوحة فنية من مجتمع • After Game" else "Artwork from • After Game Community",
                color = TextMuted.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun UserAccountView(
    currentUser: FirebaseUser,
    stats: GamerStats,
    onSignOutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToDiary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top Row with Title & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الملف الشخصي",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPrimary.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User Info Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar Circle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(CyberPrimary, CyberSecondary)
                            )
                        )
                        .padding(2.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser.displayName?.take(1)
                                ?: currentUser.email?.take(1)
                                ?: "G").uppercase(),
                            color = CyberPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser.displayName ?: "لاعب After Game",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = currentUser.email ?: "",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pro Badge
                    Surface(
                        color = CyberPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            CyberPrimary.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = CyberPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "لاعب موثق • حساب سحابي",
                                color = CyberPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Gamer Stats Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AccountStatItem(
                    label = "مسجلة",
                    value = "${stats.totalLogged}",
                    color = CyberPrimary
                )
                AccountStatItem(
                    label = "أنهيتها",
                    value = "${stats.completedCount}",
                    color = Color(0xFF34D399)
                )
                AccountStatItem(
                    label = "ساعات",
                    value = "%.0f".format(stats.totalHours),
                    color = CyberSecondary
                )
                AccountStatItem(
                    label = "معدل تقييمك",
                    value = if (stats.averageScore > 0f) "%.1f".format(stats.averageScore) else "—",
                    color = GoldStar
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "المكتبة والإعدادات",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Navigation to Diary
        ProfileOptionCard(
            icon = Icons.Default.SportsEsports,
            label = "My Game Diary",
            labelAr = "سجل ألعابي ويومياتي",
            iconColor = CyberPrimary,
            onClick = onNavigateToDiary,
            testTag = "btn_profile_diary"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // App Settings
        ProfileOptionCard(
            icon = Icons.Default.Palette,
            label = "Theme & Customization",
            labelAr = "المظهر والتخصيص",
            iconColor = CyberSecondary,
            onClick = onSettingsClick,
            testTag = "btn_profile_theme"
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sign Out Button (Red styling)
        Button(
            onClick = onSignOutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF4444).copy(alpha = 0.16f),
                contentColor = Color(0xFFFCA5A5)
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_sign_out")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تسجيل الخروج (Sign Out)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ProfileOptionCard(
    icon: ImageVector,
    label: String,
    labelAr: String,
    iconColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                val isArabic = AppLanguage.isArabic
                Text(
                    text = if (isArabic) labelAr else label,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AccountStatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}
