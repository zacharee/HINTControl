@file:Suppress("FunctionName", "unused")

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import dev.zwander.common.App
import platform.UIKit.UIViewController

@OptIn(InternalComposeUiApi::class)
fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        App(
            modifier = Modifier,
            fullPadding = WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal).asPaddingValues(),
        )
    }
}
