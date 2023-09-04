@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.WiFiBand
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun ChannelSelector(
    whichBand: WiFiBand,
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val actualList = remember(whichBand) {
        arrayOf("Auto") + whichBand.possibleBands
    }

    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    LabeledDropdown(
        label = stringResource(
            MR.strings.channel,
            stringResource(whichBand.labelRes),
        ),
        expanded = dropdownExpanded,
        onExpandChange = { dropdownExpanded = it },
        modifier = modifier,
        selectedValue = currentValue,
    ) {
        actualList.forEach { channel ->
            SelectableDropdownMenuItem(
                text = {
                    Text(text = channel)
                },
                onClick = {
                    onValueChange(channel)
                    dropdownExpanded = false
                },
                isSelected = currentValue == channel,
            )
        }
    }
}
