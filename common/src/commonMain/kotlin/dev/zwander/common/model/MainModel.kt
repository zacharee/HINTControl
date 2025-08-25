package dev.zwander.common.model

import dev.zwander.common.model.adapters.*
import dev.zwander.common.util.TimestampedMutableStateFlow
import dev.zwander.common.util.deriveCidGnbidFromEcgi
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
        // Get current data
        var mainData = currentMainData.value
        val cellData = currentCellData.value
        val simData = currentSimData.value
        
        // Apply derivation for negative gNBID/eNBID values if needed
        mainData = mainData?.let { data ->
            val signal = data.signal
            val isFbbHomeApn = signal?.generic?.apn?.equals("FBB.HOME", ignoreCase = true) == true
            
            // Create corrected signal data
            val correctedSignal = signal?.let { sig ->
                // Fix 5G gNBID if negative
                val corrected5G = if (isFbbHomeApn && sig.fiveG != null && (sig.fiveG.nbid ?: 0) < 0) {
                    val (derivedCid, derivedNbid) = deriveCidGnbidFromEcgi(
                        cellData?.cell?.fiveG?.ecgi,
                        cellData?.cell?.fiveG?.plmn,
                        24 // T-Mobile uses 24 bits for gNBID
                    )
                    sig.fiveG.copy(
                        cid = derivedCid ?: sig.fiveG.cid,
                        nbid = derivedNbid ?: sig.fiveG.nbid
                    )
                } else {
                    sig.fiveG
                }
                
                // Fix LTE eNBID if negative (apply same logic)
                val corrected4G = if (isFbbHomeApn && sig.fourG != null && (sig.fourG.nbid ?: 0) < 0) {
                    val (derivedCid, derivedNbid) = deriveCidGnbidFromEcgi(
                        cellData?.cell?.fourG?.ecgi,
                        cellData?.cell?.fourG?.plmn,
                        20 // Standard LTE uses 20 bits for eNBID
                    )
                    sig.fourG.copy(
                        cid = derivedCid ?: sig.fourG.cid,
                        nbid = derivedNbid ?: sig.fourG.nbid
                    )
                } else {
                    sig.fourG
                }
                
                sig.copy(
                    fourG = corrected4G,
                    fiveG = corrected5G
                )
            }
            
            // Return mainData with corrected signal
            data.copy(signal = correctedSignal)
        }
        
        val entry = SavedDataEntry(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            mainData = mainData,
            cellData = cellData,
            simData = simData
        )
        savedDataEntries.value = savedDataEntries.value + entry
    }
    
    fun clearSavedData() {
        savedDataEntries.value = emptyList()
    }
}
