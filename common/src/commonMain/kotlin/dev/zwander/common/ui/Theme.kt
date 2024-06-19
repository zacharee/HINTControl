@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun Theme(
    content: @Composable () -> Unit,
) {
    val themeInfo = rememberThemeInfo()

    MaterialTheme(
        colorScheme = themeInfo.colors,
        content = content,
    )
}
