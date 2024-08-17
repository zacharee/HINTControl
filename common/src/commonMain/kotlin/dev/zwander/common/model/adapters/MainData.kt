package dev.zwander.common.model.adapters

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainData(
    val device: DeviceData? = null,
    val signal: SignalData? = null,
    val time: TimeData? = null,
)

@Serializable
data class DeviceData(
    val friendlyName: String? = null,
    val hardwareVersion: String? = null,
    val isEnabled: Boolean? = null,
    val isMeshSupported: Boolean? = null,
    val macId: String? = null,
    val manufacturer: String? = null,
    val model: String? = null,
    val name: String? = null,
    val role: String? = null,
    val serial: String? = null,
    val softwareVersion: String? = null,
    val type: String? = null,
    val updateState: String? = null,
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
    val bands: List<String>?
    val bars: Double?
    val rsrp: Int?
    val rsrq: Int?
    val sinr: Int?
    val cid: Long?
    val rssi: Int?
    val nbid: Long?
    val antennaUsed: String?
}

@Serializable
data class CellDataLTE(
    override val cid: Long? = null,
    @SerialName("eNBID")
    override val nbid: Long? = null,
    override val rssi: Int? = null,
    override val bands: List<String>? = null,
    override val bars: Double? = null,
    override val rsrp: Int? = null,
    override val rsrq: Int? = null,
    override val sinr: Int? = null,
    override val antennaUsed: String? = null,
) : BaseCellData

@Serializable
data class CellData5G(
    @SerialName("gNBID")
    override val nbid: Long? = null,
    override val bands: List<String>? = null,
    override val bars: Double? = null,
    override val rsrp: Int? = null,
    override val rsrq: Int? = null,
    override val sinr: Int? = null,
    override val rssi: Int? = null,
    override val cid: Long? = null,
    override val antennaUsed: String? = null,
) : BaseCellData

@Serializable
data class GenericData(
    val apn: String? = null,
    val hasIPv6: Boolean? = null,
    val registration: String? = null,
    val roaming: Boolean? = null,
)

@Serializable
data class TimeData(
    val daylightSavings: DaylightSavingsData? = null,
    val localTime: Long? = null,
    val localTimeZone: String? = null,
    val upTime: Long? = null,
)

@Serializable
data class DaylightSavingsData(
    val isUsed: Boolean,
)
