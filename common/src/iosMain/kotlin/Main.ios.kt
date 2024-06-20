@file:Suppress("FunctionName", "unused")

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.uikit.InterfaceOrientation
import androidx.compose.ui.uikit.LocalInterfaceOrientation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import dev.zwander.common.App

@OptIn(InternalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController {
    val orientation = LocalInterfaceOrientation.current

    App(
        modifier = Modifier,
        fullPadding = if (orientation == InterfaceOrientation.LandscapeLeft || orientation == InterfaceOrientation.LandscapeRight) {
            WindowInsets.safeContent.only(WindowInsetsSides.Horizontal).asPaddingValues()
        } else {
            PaddingValues(0.dp)
        },
    )
}
