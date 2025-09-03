@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import dev.zwander.compose.alertdialog.InWindowAlertDialog
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun BandConfigLayout(
    modifier: Modifier = Modifier,
) {
    var tempState by MainModel.tempWifiState.collectAsMutableState()

    var showingFiveGigDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TextSwitch(
            text = stringResource(MR.strings.twoGig_radio),
            checked = (tempState?.twoGig?.isRadioEnabled ?: false),
            onCheckedChange = { checked ->
                tempState = tempState?.copy(
                    twoGig = tempState?.twoGig?.copy(
                        isRadioEnabled = checked,
                    ),
                )
            },
        )

        tempState?.twoGig?.transmissionPower?.let { transmissionPower ->
            TransmissionPowerGroup(
                currentPower = TransmissionPower.fromRaw(transmissionPower),
                onPowerChange = {
                    tempState = tempState?.copy(
                        twoGig = tempState?.twoGig?.copy(
                            transmissionPower = it.raw,
                        ),
                    )
                },
                title = stringResource(MR.strings.twoGig_transmission_power),
            )
        }

        NarrowHorizontalDivider()

        TextSwitch(
            text = stringResource(MR.strings.fiveGig_radio),
            checked = (tempState?.fiveGig?.isRadioEnabled ?: false),
            onCheckedChange = { checked ->
                if (!checked && GlobalModel.httpClient.value?.isUnifiedApi == true) {
                    showingFiveGigDialog = true
                } else {
                    tempState = tempState?.copy(
                        fiveGig = tempState?.fiveGig?.copy(
                            isRadioEnabled = checked,
                        ),
                    )
                }
            },
        )

        tempState?.fiveGig?.transmissionPower?.let { transmissionPower ->
            TransmissionPowerGroup(
                currentPower = TransmissionPower.fromRaw(transmissionPower),
                onPowerChange = {
                    tempState = tempState?.copy(
                        fiveGig = tempState?.fiveGig?.copy(
                            transmissionPower = it.raw,
                        ),
                    )
                },
                title = stringResource(MR.strings.fiveGig_transmission_power),
            )
        }

        tempState?.sixGig?.let { sixGig ->
            NarrowHorizontalDivider()

            TextSwitch(
                text = stringResource(MR.strings.sixGig_radio),
                checked = (tempState?.sixGig?.isRadioEnabled ?: false),
                onCheckedChange = { checked ->
                    tempState = tempState?.copy(
                        sixGig = tempState?.sixGig?.copy(
                            isRadioEnabled = checked,
                        ),
                    )
                },
            )

            sixGig.transmissionPower?.let { transmissionPower ->
                TransmissionPowerGroup(
                    currentPower = TransmissionPower.fromRaw(transmissionPower),
                    onPowerChange = {
                        tempState = tempState?.copy(
                            sixGig = tempState?.sixGig?.copy(
                                transmissionPower = it.raw,
                            ),
                        )
                    },
                    title = stringResource(MR.strings.sixGig_transmission_power),
                )
            }
        }

        NarrowHorizontalDivider()

        TextSwitch(
            text = stringResource(MR.strings.band_steering),
            checked = tempState?.bandSteering?.isEnabled ?: false,
            onCheckedChange = { checked ->
                tempState = tempState?.copy(
                    bandSteering = tempState?.bandSteering?.copy(
                        isEnabled = checked,
                    )
                )
            },
        )
    }

    InWindowAlertDialog(
        showing = showingFiveGigDialog,
        title = { Text(text = stringResource(MR.strings.warning)) },
        text = { Text(text = stringResource(MR.strings.disable_five_ghz_message)) },
        buttons = {
            TextButton(
                onClick = { showingFiveGigDialog = false },
            ) {
                Text(text = stringResource(MR.strings.no))
            }

            TextButton(
                onClick = {
                    showingFiveGigDialog = false
                    tempState = tempState?.copy(
                        fiveGig = tempState?.fiveGig?.copy(
                            isRadioEnabled = false,
                        )
                    )
                },
            ) {
                Text(
                    text = stringResource(MR.strings.yes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        onDismissRequest = { showingFiveGigDialog = false },
    )
}
