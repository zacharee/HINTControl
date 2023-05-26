package dev.zwander.common.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zwander.common.util.animateContentHeight

@Composable
fun EmptyableContent(
    content: @Composable ColumnScope.() -> Unit,
    emptyContent: @Composable ColumnScope.() -> Unit,
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = isEmpty,
        modifier = Modifier.animateContentHeight().then(modifier),
    ) {
        if (it) {
            Column {
                emptyContent()
            }
        } else {
            Column {
                content()
            }
        }
    }
}
