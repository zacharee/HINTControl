@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
expect fun CAlertDialog(
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
)

@Composable
@HiddenFromObjC
fun AlertDialogDef(
    showing: Boolean,
    onDismissRequest: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    maxWidthPercent: Float = 0.9f,
) {
    CAlertDialog(showing, onDismissRequest,
        buttons, modifier, title, text,
        shape, backgroundColor, contentColor,
        maxWidthPercent)
}

@Composable
@HiddenFromObjC
fun AlertDialogContents(
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)?,
    text: (@Composable ColumnScope.() -> Unit)?,
    shape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = shape,
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            title?.let {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    val textStyle = MaterialTheme.typography.headlineSmall
                    ProvideTextStyle(textStyle) {
                        it()
                    }
                }
            }

            text?.let {
                Column(modifier = Modifier.weight(1f, false)) {
                    it()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))

                buttons()
            }
        }
    }
}
