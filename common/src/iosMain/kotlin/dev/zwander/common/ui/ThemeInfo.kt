package dev.zwander.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.interop.LocalUIViewController
import dev.icerock.moko.resources.compose.colorResource
import dev.zwander.common.monet.ColorScheme
import dev.zwander.resources.common.MR
import kotlinx.cinterop.get
import platform.CoreGraphics.CGColorGetComponents
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.tintColor

@Composable
actual fun getThemeInfo(): ThemeInfo {
    val controller = LocalUIViewController.current
    val dark = controller.traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
    val (red, green, blue) = CGColorGetComponents(controller.view.tintColor.CGColor).run {
        arrayOf(this?.get(0), this?.get(1), this?.get(2))
    }

    return ThemeInfo(
        isDarkMode = dark,
        colors = ColorScheme(
            if (red != null && green != null && blue != null) {
                Color(red.toFloat(), green.toFloat(), blue.toFloat(), 1.0f)
            } else {
                colorResource(MR.colors.icon_color_one)
            }.toArgb(),
            dark
        ).toComposeColorScheme().toNullableColorScheme(),
    )
}
