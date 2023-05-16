package dev.zwander.common.model

import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.WifiConfig
import kotlinx.coroutines.flow.MutableStateFlow

object MainModel {
    val currentMainData = MutableStateFlow<MainData?>(null)
    val currentClientData = MutableStateFlow<ClientDeviceData?>(null)
    val currentWifiData = MutableStateFlow<WifiConfig?>(null)
}
