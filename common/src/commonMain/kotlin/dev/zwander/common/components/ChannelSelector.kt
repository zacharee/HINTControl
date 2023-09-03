@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun ChannelSelector(
    whichBand: Band,
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
            DropdownMenuItem(
                text = {
                    Text(text = channel)
                },
                onClick = {
                    onValueChange(channel)
                    dropdownExpanded = false
                },
            )
        }
    }
}

sealed class Band(
    val possibleBands: Array<String>,
    val labelRes: StringResource,
) {
    data object TwoGig : Band(
        possibleBands = arrayOf(
            "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11",
        ),
        labelRes = MR.strings.twoGig,
    )

    data object FiveGig : Band(
        possibleBands = arrayOf(
            "32", "36", "40", "44", "48",
            "52", "56", "60", "64", "68",
            "96", "100", "104", "108", "112",
            "116", "120", "124", "128", "132",
            "136", "140", "144", "149", "153",
            "157", "161", "165",
        ),
        labelRes = MR.strings.fiveGig,
    )
}
