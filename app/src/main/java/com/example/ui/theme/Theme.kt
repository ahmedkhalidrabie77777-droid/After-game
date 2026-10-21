package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AfterGameColorScheme = darkColorScheme(
  primary = CyberPrimary,
  onPrimary = Color.White,
  primaryContainer = DarkSurfaceElevated,
  onPrimaryContainer = CyberSecondary,
  secondary = CyberSecondary,
  onSecondary = Color.Black,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = Color.White,
  tertiary = CyberTertiary,
  onTertiary = Color.White,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = BorderSubtle,
  outlineVariant = DarkSurfaceElevated
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek gaming dark mode
  dynamicColor: Boolean = false, // Keep intentional cyber palette
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = AfterGameColorScheme,
    typography = Typography,
    content = content
  )
}
