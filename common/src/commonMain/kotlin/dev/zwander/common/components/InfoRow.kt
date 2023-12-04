@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalLayoutApi::class)
@Composable
@HiddenFromObjC
fun InfoRow(
    items: List<Pair<StringResource, Any?>>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        items.forEach { (labelRes, value) ->
            val valueString = if (value is StringResource) {
                stringResource(value)
            } else {
                value.toString()
            }

            FormatText(
                text = stringResource(labelRes),
                textFormat = valueString,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}
