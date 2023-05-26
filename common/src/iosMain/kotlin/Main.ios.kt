@file:Suppress("FunctionName", "unused")

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import dev.zwander.common.App
import kotlinx.cinterop.useContents
import platform.UIKit.safeAreaInsets
import platform.UIKit.window

fun MainViewController() = ComposeUIViewController {
    val controller = LocalUIViewController.current

    BoxWithConstraints {
        var insets by remember {
            mutableStateOf(PaddingValues(0.dp))
        }

        LaunchedEffect(constraints.maxWidth) {
            val rv = controller.view.window?.rootViewController?.view

            rv?.safeAreaInsets?.useContents {
                insets = PaddingValues.Absolute(
                    left = left.dp,
                    top = top.dp,
                    right = right.dp,
                    bottom = bottom.dp,
                )
            }
        }

        App(
            modifier = Modifier,
            windowInsets = insets,
        )
    }
}
