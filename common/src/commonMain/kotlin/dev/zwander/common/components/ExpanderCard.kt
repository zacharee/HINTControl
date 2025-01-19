package dev.zwander.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.resources.common.MR

@Composable
fun ExpanderCard(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    inverted: Boolean = false,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp,
    ) {
        Card(
            onClick = {
                onExpandChange(!expanded)
            },
            colors = colors,
            elevation = elevation,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val rotation by animateFloatAsState(
                    when {
                        expanded && !inverted -> 180f
                        expanded && inverted -> 0f
                        !expanded && !inverted -> 0f
                        !expanded && inverted -> 180f
                        else -> 0f
                    }
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) MR.strings.collapse else MR.strings.expand),
                    modifier = Modifier.rotate(rotation),
                )
            }
        }
    }
}
