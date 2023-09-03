@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.zwander.common.model.MainModel
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun ChannelConfigLayout(
    modifier: Modifier = Modifier,
) {
    var tempState by MainModel.tempWifiState.collectAsMutableState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ChannelSelector(
            whichBand = Band.TwoGig,
            currentValue = tempState?.twoGig?.channel ?: "Auto",
            onValueChange = {
                tempState = tempState?.copy(
                    twoGig = tempState?.twoGig?.copy(
                        channel = it,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        ChannelSelector(
            whichBand = Band.FiveGig,
            currentValue = tempState?.fiveGig?.channel ?: "Auto",
            onValueChange = {
                tempState = tempState?.copy(
                    fiveGig = tempState?.fiveGig?.copy(
                        channel = it,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
