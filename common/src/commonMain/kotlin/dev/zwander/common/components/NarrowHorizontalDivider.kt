package dev.zwander.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NarrowHorizontalDivider(
    modifier: Modifier = Modifier,
    fraction: Float = 0.5f,
    verticalPadding: Dp = 2.dp,
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(vertical = verticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(fraction = fraction),
        )
    }
}
