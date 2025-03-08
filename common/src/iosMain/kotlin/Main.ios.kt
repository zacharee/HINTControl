@file:Suppress("FunctionName", "unused")

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.uikit.InterfaceOrientation
import androidx.compose.ui.uikit.LocalInterfaceOrientation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import dev.zwander.common.App
import dev.zwander.common.ui.LocalOrientation
import dev.zwander.common.ui.Orientation
import platform.UIKit.UIViewController

@OptIn(InternalComposeUiApi::class)
fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        val orientation = LocalInterfaceOrientation.current
        val adaptedOrientation = when (orientation) {
            InterfaceOrientation.LandscapeLeft -> Orientation.LANDSCAPE_90
            InterfaceOrientation.LandscapeRight -> Orientation.LANDSCAPE_270
            InterfaceOrientation.Portrait -> Orientation.PORTRAIT
            InterfaceOrientation.PortraitUpsideDown -> Orientation.PORTRAIT_180
        }

        CompositionLocalProvider(
            LocalOrientation provides adaptedOrientation,
        ) {
            App(
                modifier = Modifier,
                fullPadding = if (orientation == InterfaceOrientation.LandscapeLeft || orientation == InterfaceOrientation.LandscapeRight) {
                    WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues()
                } else {
                    PaddingValues(0.dp)
                },
            )
        }
    }
}
