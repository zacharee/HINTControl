@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.generateInfoList
import dev.zwander.common.data.set
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR
import korlibs.math.toIntFloor
import korlibs.time.TimeFormat
import korlibs.time.days
import korlibs.time.milliseconds
import korlibs.util.format
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun DeviceDataLayout(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentMainData.collectAsState()
    val deviceData = data?.device

    val timeFormat = remember {
        TimeFormat("HH'h' mm'm' ss's'")
    }
    
    val items = generateInfoList("DeviceData", deviceData, data) {
        this[MR.strings.friendly_name] = deviceData?.friendlyName
        this[MR.strings.name] = deviceData?.name
        this[MR.strings.softwareVersion] = deviceData?.softwareVersion
        this[MR.strings.hardware_version] = deviceData?.hardwareVersion
        this[MR.strings.mac] = deviceData?.macId
        this[MR.strings.serial] = deviceData?.serial
        this[MR.strings.update_state] = deviceData?.updateState
        this[MR.strings.mesh_supported] = deviceData?.isMeshSupported?.toString()
        this[MR.strings.enabled] = deviceData?.isEnabled?.toString()
        this[MR.strings.role] = deviceData?.role
        this[MR.strings.type] = deviceData?.type
        this[MR.strings.manufacturer] = deviceData?.manufacturer
        this[MR.strings.model] = deviceData?.model
        this[MR.strings.uptime] = data?.time?.upTime?.let { upTime ->
            val milliseconds = (upTime * 1000).milliseconds

            val days = milliseconds.days.toIntFloor()
            val rest = milliseconds - days.days

            "${"%2".format(days)}d ${timeFormat.format(rest)}"
        }
    }

    EmptiableContent(
        content = {
            InfoRow(
                items = items,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        emptyContent = {
            Text(
                text = stringResource(MR.strings.unavailable),
            )
        },
        isEmpty = items.isEmpty(),
        modifier = modifier,
    )
}
