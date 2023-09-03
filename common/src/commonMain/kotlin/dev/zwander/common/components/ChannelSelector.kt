@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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


}

sealed class Band(val possibleBands: Array<String>) {
    data object TwoGig : Band(
        possibleBands = arrayOf(
            "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11",
        ),
    )

    data object FiveGig : Band(
        possibleBands = arrayOf(
            "32", "36", "40", "44", "48",
            "52", "56", "60", "64", "68",
            "96", "100", "104", "108", "112",
            "116", "120", "124", "128", "132",
            "136", "140", "144", "149", "153",
            "157", "161", "165", "169", "173",
            "177",
        ),
    )
}
