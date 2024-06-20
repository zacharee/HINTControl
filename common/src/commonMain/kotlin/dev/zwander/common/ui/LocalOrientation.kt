package dev.zwander.common.ui

import androidx.compose.runtime.compositionLocalOf

val LocalOrientation = compositionLocalOf { Orientation.PORTRAIT }

enum class Orientation {
    PORTRAIT,
    LANDSCAPE_90,
    LANDSCAPE_270,
    PORTRAIT_180,
}
