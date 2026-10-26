package com.example.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

object AppLanguage {
    val isArabic: Boolean
        @Composable
        @ReadOnlyComposable
        get() {
            val locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()
            return locale.language.equals("ar", ignoreCase = true)
        }

    fun isDeviceArabic(): Boolean {
        return Locale.getDefault().language.equals("ar", ignoreCase = true)
    }
}
