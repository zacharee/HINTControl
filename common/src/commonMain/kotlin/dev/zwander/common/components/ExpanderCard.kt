package dev.zwander.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.resources.common.MR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpanderCard(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    inverted: Boolean = false,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
    ) {
        Card(
            onClick = {
                onExpandChange(!expanded)
            },
            colors = colors,
            elevation = elevation,
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
