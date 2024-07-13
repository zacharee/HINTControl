package dev.zwander.common.components.dialog

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
internal actual fun PlatformAlertDialog(
    showing: Boolean,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape,
    backgroundColor: Color,
    contentColor: Color,
    maxWidth: Dp,
) {
    val density = LocalDensity.current

    if (showing) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                decorFitsSystemWindows = false,
                usePlatformDefaultWidth = false,
            )
        ) {
            BoxWithConstraints {
                val constraints = constraints
                val width = minOf(
                    with (density) { (constraints.maxWidth * 0.9f).toDp() },
                    maxWidth,
                )

                AlertDialogContents(
                    buttons,
                    modifier.imePadding()
                        .systemBarsPadding()
                        .width(width),
                    title,
                    text,
                    shape,
                    backgroundColor,
                    contentColor,
                )
            }
        }
    }
}
