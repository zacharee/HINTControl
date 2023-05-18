package dev.zwander.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InfoRow(
    items: List<Pair<StringResource, Any?>>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier,
    ) {
        items.forEach { (labelRes, value) ->
            FormatText(
                text = stringResource(labelRes),
                textFormat = value.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}
