@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import dev.zwander.common.data.Theme
import dev.zwander.common.model.SettingsModel
import dev.zwander.compose.DynamicMaterialTheme
import dev.zwander.compose.isSystemInDarkTheme
import dev.zwander.compose.rememberThemeInfo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun Theme(
    content: @Composable () -> Unit,
) {
    val currentTheme by SettingsModel.theme.collectAsState()

    if (currentTheme != Theme.BLACK) {
        val isDark = when {
            currentTheme == Theme.SYSTEM -> isSystemInDarkTheme()
            else -> currentTheme == Theme.DARK
        }

        DynamicMaterialTheme(
            isDarkMode = isDark,
            content = content,
        )
    } else {
        val themeInfo = rememberThemeInfo(isDarkMode = true).let {
            it.copy(
                colors = it.colors.copy(
                    background = Color.Black,
                    primaryContainer = Color.Black,
                    surfaceContainer = Color.Black,
                    surface = Color.Black,
                    surfaceContainerLowest = Color.Black.copy(alpha = 0.8f).compositeOver(it.colors.surfaceContainerLowest),
                    surfaceContainerLow = Color.Black.copy(alpha = 0.8f).compositeOver(it.colors.surfaceContainerLow),
                    surfaceContainerHigh = Color.Black.copy(alpha = 0.8f).compositeOver(it.colors.surfaceContainerHigh),
                    surfaceContainerHighest = Color.Black.copy(alpha = 0.8f).compositeOver(it.colors.surfaceContainerHighest),
                ),
            )
        }

        MaterialTheme(colorScheme = themeInfo.colors, content = content)
    }
}
