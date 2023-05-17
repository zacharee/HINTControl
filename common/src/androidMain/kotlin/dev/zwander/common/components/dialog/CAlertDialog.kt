package dev.zwander.common.components.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun CAlertDialog(
    showing: Boolean,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape,
    backgroundColor: Color,
    contentColor: Color,
    maxWidthPercent: Float,
) {
    if (showing) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                decorFitsSystemWindows = false,
                usePlatformDefaultWidth = false,
            )
        ) {
            AlertDialogContents(
                buttons,
                modifier.imePadding()
                    .systemBarsPadding()
                    .fillMaxWidth(maxWidthPercent),
                title,
                text,
                shape,
                backgroundColor,
                contentColor
            )
        }
    }
}
