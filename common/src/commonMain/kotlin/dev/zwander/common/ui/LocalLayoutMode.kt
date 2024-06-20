package dev.zwander.common.ui

import androidx.compose.runtime.compositionLocalOf

val LocalLayoutMode = compositionLocalOf<LayoutMode> { error("No layout mode provided!") }

enum class LayoutMode {
    BOTTOM_BAR,
    SIDE_RAIL,
}
