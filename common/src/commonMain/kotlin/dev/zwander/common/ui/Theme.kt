package dev.zwander.common.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun Theme(
    content: @Composable () -> Unit,
) {
    val themeInfo = getThemeInfo()

    val colorScheme = if (themeInfo.isDarkMode) {
        darkColorScheme().setColors(themeInfo)
    } else {
        lightColorScheme().setColors(themeInfo)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

@Composable
private fun ColorScheme.setColors(themeInfo: ThemeInfo): ColorScheme {
    val base = themeInfo.colors?.mergeWithColorScheme(this) ?: this

    val onPrimary = themeInfo.colors?.onPrimary ?: Color.White
    val onSecondary = themeInfo.colors?.onSecondary ?: Color.White
    val onBackground = themeInfo.colors?.onBackground ?: base.onBackground
    val onSurface = themeInfo.colors?.onSurface ?: base.onSurface

    return base.copy(
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
    )
}
