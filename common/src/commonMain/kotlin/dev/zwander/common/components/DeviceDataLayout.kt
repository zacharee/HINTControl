@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.generateInfoList
import dev.zwander.common.data.set
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR
import korlibs.util.format
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
@HiddenFromObjC
fun DeviceDataLayout(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentMainData.collectAsState()
    val deviceData = data?.device

    val items = generateInfoList(deviceData, data) {
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
            val duration = (upTime * 1000).milliseconds
            val days = duration.inWholeDays
            val hours = (duration - days.days).inWholeHours
            val minutes = (duration - days.days - hours.hours).inWholeMinutes
            val seconds = (duration - days.days - hours.hours - minutes.minutes).inWholeSeconds

            "${"%2".format(days)}d ${"%02".format(hours)}h ${"%02".format(minutes)}m ${"%02".format(seconds)}s"
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
