package dev.zwander.common.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ThemeInfo(
    val isDarkMode: Boolean,
    val colors: ColorScheme,
)

@Composable
expect fun rememberThemeInfo(): ThemeInfo
