@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import dev.icerock.moko.resources.compose.colorResource
import dev.zwander.common.monet.ColorScheme
import dev.zwander.resources.common.MR
import kotlinx.cinterop.useContents
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSUserDefaults
import kotlin.experimental.ExperimentalObjCRefinement

@Composable
@HiddenFromObjC
actual fun getThemeInfo(): ThemeInfo {
    val defaults = NSUserDefaults.standardUserDefaults()
    val osVersion = NSProcessInfo.processInfo().operatingSystemVersion()
    val switchesAutomatically = if (osVersion.useContents { this.majorVersion >= 11 || (this.majorVersion >= 10 && this.minorVersion >= 15) }) {
        defaults.boolForKey("AppleInterfaceStyleSwitchesAutomatically")
    } else {
        false
    }
    val darkString = defaults.stringForKey("AppleInterfaceStyle")

    val dark = when {
        switchesAutomatically -> {
            darkString == null
        }

        else -> {
            darkString == "Dark"
        }
    }

    val accentInt = defaults.objectForKey("AppleAccentColor")?.let {
        defaults.integerForKey("AppleAccentColor")
    }

    val accent = accentInt?.let {
        when (accentInt.toInt()) {
            -2 -> ACCENT_BLUE
            -1 -> ACCENT_GRAPHITE
            0 -> ACCENT_RED
            1 -> ACCENT_ORANGE
            2 -> ACCENT_YELLOW
            3 -> ACCENT_GREEN
            4 -> ACCENT_LILAC
            5 -> ACCENT_ROSE
            else -> null
        }
    }

    return ThemeInfo(
        isDarkMode = dark,
        colors = ColorScheme(
            seed = accent?.toArgb() ?: colorResource(MR.colors.icon_color_one).toArgb(),
            darkTheme = dark,
        ).toComposeColorScheme().toNullableColorScheme(),
    )
}
