package dev.zwander.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import dev.zwander.common.data.Theme
import dev.zwander.common.model.SettingsModel

@Composable
fun HybridElevatedCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.elevatedShape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    interactionSource: MutableInteractionSource? = null,
    border: BorderStroke? = if (SettingsModel.theme.collectAsState().value == Theme.BLACK) CardDefaults.outlinedCardBorder(enabled = enabled) else null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = shape,
            border = border,
            elevation = elevation,
            colors = colors,
            content = content,
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            border = border,
            elevation = elevation,
            colors = colors,
            content = content,
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
        )
    }
}
