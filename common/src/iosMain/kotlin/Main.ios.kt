@file:Suppress("FunctionName", "unused")

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import dev.zwander.common.App

fun MainViewController() = ComposeUIViewController {
    App(
        modifier = Modifier,
        fullPadding = WindowInsets.safeContent.only(WindowInsetsSides.Horizontal).asPaddingValues(),
    )
}
