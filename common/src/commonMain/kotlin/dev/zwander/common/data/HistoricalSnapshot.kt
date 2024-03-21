package dev.zwander.common.data

import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SimDataRoot
import kotlinx.serialization.Serializable

@Serializable
data class HistoricalSnapshot(
    val timeMillis: Long,
    val cellData: CellDataRoot? = null,
    val clientData: ClientDeviceData? = null,
    val mainData: MainData? = null,
    val simData: SimDataRoot? = null,
)
