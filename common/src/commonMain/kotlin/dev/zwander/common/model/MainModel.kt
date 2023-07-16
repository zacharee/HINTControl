package dev.zwander.common.model

import dev.zwander.common.model.adapters.*
import dev.zwander.common.util.TimestampedMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow

object MainModel {
    val currentMainData = TimestampedMutableStateFlow<MainData?>(null)
    val currentClientData = TimestampedMutableStateFlow<ClientDeviceData?>(null)
    val currentWifiData = TimestampedMutableStateFlow<WifiConfig?>(null)
    val currentCellData = TimestampedMutableStateFlow<CellDataRoot?>(null)
    val currentSimData = TimestampedMutableStateFlow<SimDataRoot?>(null)

    val tempWifiState = MutableStateFlow<WifiConfig?>(null)
}
