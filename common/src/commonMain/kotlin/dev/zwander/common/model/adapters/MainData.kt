package dev.zwander.common.model.adapters

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainData(
    val device: DeviceData,
    val signal: SignalData,
    val time: TimeData,
)

@Serializable
data class DeviceData(
    val friendlyName: String,
    val hardwareVersion: String,
    val isEnabled: Boolean,
    val isMeshSupported: Boolean,
    val macId: String,
    val manufacturer: String,
    val model: String,
    val name: String,
    val role: String,
    val serial: String,
    val softwareVersion: String,
    val type: String,
    val updateState: String,
)

@Serializable
data class SignalData(
    @SerialName("4g")
    val fourG: CellDataLTE? = null,
    @SerialName("5g")
    val fiveG: CellData5G? = null,
    val generic: GenericData? = null,
)

interface BaseCellData {
    val bands: List<String>
    val bars: Double
    val rsrp: Int
    val rsrq: Int
    val sinr: Int
    val cid: Long
    val rssi: Int
}

@Serializable
data class CellDataLTE(
    override val cid: Long,
    val eNBID: Long? = null,
    override val rssi: Int,
    override val bands: List<String>,
    override val bars: Double,
    override val rsrp: Int,
    override val rsrq: Int,
    override val sinr: Int,
) : BaseCellData

@Serializable
data class CellData5G(
    val gNBID: Int? = null,
    override val bands: List<String>,
    override val bars: Double,
    override val rsrp: Int,
    override val rsrq: Int,
    override val sinr: Int,
    override val rssi: Int,
    override val cid: Long,
) : BaseCellData

@Serializable
data class GenericData(
    val apn: String,
    val hasIPv6: Boolean,
    val registration: String,
    val roaming: Boolean,
)

@Serializable
data class TimeData(
    val daylightSavings: DaylightSavingsData?,
    val localTime: Long?,
    val localTimeZone: String?,
    val upTime: Long?,
)

@Serializable
data class DaylightSavingsData(
    val isUsed: Boolean,
)
