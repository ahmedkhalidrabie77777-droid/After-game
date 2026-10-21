package com.example.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
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
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DarkBackground,
                        DarkBackground,
                        DarkSurface.copy(alpha = 0.96f)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CyberPrimary.copy(alpha = 0.10f),
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.98f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CyberPrimary.copy(alpha = 0.14f),
                            CyberSecondary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(2.dp, CyberPrimary, CircleShape)
                    .background(DarkSurfaceVariant.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_after_game_logo),
                    contentDescription = "After Game Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "AFTER GAME",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Text(
                text = "مجتمع تقييم ومراجعات ألعاب الفيديو",
                color = CyberSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(26.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurfaceVariant.copy(alpha = 0.92f)
                ),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FeatureRow(
                        icon = Icons.Default.Groups,
                        title = "تفاعل مباشر مع كل الجيمرز",
                        desc = "تقييمك ومراجعاتك وتعليقاتك تظهر فورياً عبر السحابة."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureRow(
                        icon = Icons.Default.Star,
                        title = "بيانات 500,000+ لعبة عبر RAWG API",
                        desc = "صور، تقييمات، تفاصيل المطورين، ومنصات اللعب مباشرة."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureRow(
                        icon = Icons.Default.Gamepad,
                        title = "قائمة ومكتبة ألعاب منسقة",
                        desc = "تابع لعبك، احفظ تقييماتك، واحتفظ بسجل يومياتك بسهولة."
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFEF4444).copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFFCA5A5),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    AuthManager.onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isLoading = false
                            onAuthSuccess()
                        },
                        onAuthError = { message ->
                            isLoading = false
                            errorMessage = message
                        },
                        scope = coroutineScope,
                        onAuthCancelled = {
                            isLoading = false
                        }
                    )
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = DarkBackground,
                    disabledContainerColor = Color.White.copy(alpha = 0.7f),
                    disabledContentColor = DarkBackground.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(CyberPrimary.copy(alpha = 0.18f), CircleShape)
                            .border(1.dp, CyberPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            color = CyberPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = DarkBackground,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Text(
                        text = if (isLoading) "جاري تسجيل الدخول..." else "تسجيل الدخول عبر Google",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "الدخول يتيح لك تقييم الألعاب ومشاركة رأيك مع المجتمع",
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(CyberPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .border(1.dp, CyberPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
