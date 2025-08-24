package dev.zwander.common.model

import dev.zwander.common.model.adapters.*
import dev.zwander.common.util.TimestampedMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.serialization.Serializable

@OptIn(ExperimentalTime::class)
@Serializable
data class SavedDataEntry(
    val timestamp: Long,
    val mainData: MainData?,
    val cellData: CellDataRoot?,
    val simData: SimDataRoot?
)

object MainModel {
    val currentMainData = TimestampedMutableStateFlow<MainData?>(null)
    val currentClientData = TimestampedMutableStateFlow<ClientDeviceData?>(null)
    val currentWifiData = TimestampedMutableStateFlow<WifiConfig?>(null)
    val currentCellData = TimestampedMutableStateFlow<CellDataRoot?>(null)
    val currentSimData = TimestampedMutableStateFlow<SimDataRoot?>(null)

    val tempWifiState = MutableStateFlow<WifiConfig?>(null)
    
    val savedDataEntries = MutableStateFlow<List<SavedDataEntry>>(emptyList())
    
    @OptIn(ExperimentalTime::class)
    fun saveCurrentData() {
        val entry = SavedDataEntry(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            mainData = currentMainData.value,
            cellData = currentCellData.value,
            simData = currentSimData.value
        )
        savedDataEntries.value = savedDataEntries.value + entry
    }
    
    fun clearSavedData() {
        savedDataEntries.value = emptyList()
    }
}
