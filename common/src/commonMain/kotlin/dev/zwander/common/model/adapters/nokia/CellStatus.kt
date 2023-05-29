package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CellStatus(
    @SerialName("cell_stat_generic")
    val cellStatGeneric: List<CellStatGeneric>? = null,
    @SerialName("cell_stat_lte")
    val cellStatLte: List<CellStat>? = null,
    @SerialName("cell_stat_5G")
    val cellStat5G: List<CellStat>? = null,
)

@Serializable
data class CellStatGeneric(
    @SerialName("RoamingStatus")
    val roamingStatus: String? = null,
    @SerialName("CurrentAccessTechnology")
    val currentAccessTechnology: String? = null,
    @SerialName("X_ALU_COM_TAC")
    val tac: String? = null,
    @SerialName("BytesSent")
    val bytesSent: Long? = null,
    @SerialName("BytesReceived")
    val bytesReceived: Long? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("CpeAbnormalStatus")
    val cpeAbnormalStatus: Int? = null,
)

@Serializable
data class CellStat(
    @SerialName("RSRPThreshold")
    val rsrpThreshold: String? = null,
    @SerialName("SNRCurrent")
    val snr: Int? = null,
    @SerialName("RSRPCurrent")
    val rsrp: Int? = null,
    @SerialName("RSRQCurrent")
    val rsrq: Int? = null,
    @SerialName("Band")
    val band: String? = null,
    @SerialName("Bandwidth")
    val bandwidth: String? = null,
    @SerialName("RSRPStrengthIndexCurrent")
    val rsrpStrengthIndex: Int? = null,
    @SerialName("RSRPSignalLevelCode")
    val rsrpSignalLevelCode: Int? = null,
    @SerialName("PLMNName")
    val plmnName: String? = null,
    @SerialName("CQI")
    val cqi: String? = null,
    @SerialName("ECGI")
    val ecgi: String? = null,
    @SerialName("MCC")
    val mcc: String? = null,
    @SerialName("MNC")
    val mnc: String? = null,
    @SerialName("eNBID")
    val enbid: String? = null,
    @SerialName("Cellid")
    val cellId: String? = null,
    @SerialName("RSSICurrent")
    val rssi: Int? = null,
)