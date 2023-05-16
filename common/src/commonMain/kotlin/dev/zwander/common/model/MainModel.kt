package dev.zwander.common.model

import dev.zwander.common.model.adapters.*
import kotlinx.coroutines.flow.MutableStateFlow

object MainModel {
    val currentMainData = MutableStateFlow<MainData?>(null)
    val currentClientData = MutableStateFlow<ClientDeviceData?>(null)
    val currentWifiData = MutableStateFlow<WifiConfig?>(null)
    val currentCellData = MutableStateFlow<CellDataRoot?>(null)
    val currentSimData = MutableStateFlow<SimDataRoot?>(null)

    val tempWifiState = MutableStateFlow<WifiConfig?>(null)
}
