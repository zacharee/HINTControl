package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR

@Composable
fun DeviceDataLayout(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentMainData.collectAsState()
    val deviceData = data?.device

    val items = remember(deviceData) {
        listOf(
            MR.strings.friendly_name to deviceData?.friendlyName,
            MR.strings.name to deviceData?.name,
            MR.strings.softwareVersion to deviceData?.softwareVersion,
            MR.strings.hardware_version to deviceData?.hardwareVersion,
            MR.strings.mac to deviceData?.macId,
            MR.strings.serial to deviceData?.serial,
            MR.strings.update_state to deviceData?.updateState,
            MR.strings.mesh_supported to deviceData?.isMeshSupported,
            MR.strings.enabled to deviceData?.isEnabled,
            MR.strings.role to deviceData?.role,
            MR.strings.type to deviceData?.type,
            MR.strings.manufacturer to deviceData?.manufacturer,
            MR.strings.model to deviceData?.model,
        )
    }

    Column(
        modifier = modifier,
    ) {
        InfoRow(
            items = items,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
