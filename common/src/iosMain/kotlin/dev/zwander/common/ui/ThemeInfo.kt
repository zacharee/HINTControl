@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.interop.LocalUIViewController
import dev.icerock.moko.resources.compose.colorResource
import dev.zwander.common.monet.ColorScheme
import dev.zwander.resources.common.MR
import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.value
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGFloatVar
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIColor
import platform.UIKit.UITraitCollection
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.currentTraitCollection
import kotlin.experimental.ExperimentalObjCRefinement

@Composable
@HiddenFromObjC
actual fun getThemeInfo(): ThemeInfo {
    val controller = LocalUIViewController.current
    val rootViewController = controller.view.window?.rootViewController

    val rootTint = rootViewController?.view?.tintColor

    val (red, green, blue, alpha) = rootTint?.run {
        memScoped {
            val red = alloc<CGFloatVar>()
            val green = alloc<CGFloatVar>()
            val blue = alloc<CGFloatVar>()
            val alpha = alloc<CGFloatVar>()

            val success = getRed(red.ptr, green.ptr, blue.ptr, alpha.ptr)

            if (success) {
                arrayOf(
                    red.value,
                    green.value,
                    blue.value,
                    alpha.value
                )
            } else {
                arrayOfNulls<CGFloat?>(4)
            }
        }
    } ?: arrayOfNulls(4)

    var style: UIUserInterfaceStyle by remember {
        mutableStateOf(UITraitCollection.currentTraitCollection.userInterfaceStyle)
    }

    val dark by remember {
        derivedStateOf { style == UIUserInterfaceStyle.UIUserInterfaceStyleDark }
    }

    val viewController: UIViewController = LocalUIViewController.current
    DisposableEffect(Unit) {
        val view: UIView = viewController.view
        val traitView = TraitView {
            style = UITraitCollection.currentTraitCollection.userInterfaceStyle
        }
        view.addSubview(traitView)

        onDispose {
            traitView.removeFromSuperview()
        }
    }

    val colorScheme = ColorScheme(
        if (red != null && green != null && blue != null && alpha != null) {
            Color(red.toFloat(), green.toFloat(), blue.toFloat(), alpha.toFloat())
        } else {
            colorResource(MR.colors.icon_color_one)
        }.toArgb(),
        dark
    ).toComposeColorScheme()

    val colors = ThemeInfo(
        isDarkMode = dark,
        colors = colorScheme.toNullableColorScheme(),
    )

    val backgroundColor = colorScheme.background
    val uiColor = UIColor.colorWithRed(
        backgroundColor.red.toDouble(),
        backgroundColor.green.toDouble(),
        backgroundColor.blue.toDouble(),
        backgroundColor.alpha.toDouble(),
    )

    val rv = controller.view.window?.rootViewController?.view
    rv?.backgroundColor = uiColor

    return colors
}

// https://github.com/JetBrains/compose-multiplatform/issues/3213#issuecomment-1572378546
@ExportObjCClass
private class TraitView(
    private val onTraitChanged: () -> Unit
) : UIView(frame = CGRectZero.readValue()) {
    override fun traitCollectionDidChange(previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        onTraitChanged()
    }
}
