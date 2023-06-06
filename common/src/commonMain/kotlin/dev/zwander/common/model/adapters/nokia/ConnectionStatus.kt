package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionStatus(
    @SerialName("connection_status")
    val connectionStatus: List<ConnectionStatusStatus>? = null,
    @SerialName("apn_cfg")
    val apnConfigs: List<ApnConfig>? = null,
    @SerialName("cellular_stats")
    val cellularStats: List<CellularStats>? = null,
    @SerialName("ethernet_stats")
    val ethernetStats: List<EthernetStats>? = null,
    @SerialName("cell_CA_stats_cfg")
    val caStats: List<CellCAStatsConfig>? = null,
    @SerialName("cell_5G_stats_cfg")
    val fiveGStats: List<CellStatsConfig5G>? = null,
    @SerialName("cell_LTE_stats_cfg")
    val fourGStats: List<CellStatsConfigLte>? = null,
)

@Serializable
data class CellularStats(
    @SerialName("BytesReceived")
    val bytesReceived: Long? = null,
    @SerialName("BytesSent")
    val bytesSent: Long? = null,
)

@Serializable
data class Stats(
    @SerialName("BytesReceived")
    val bytesReceived: Long? = null,
    @SerialName("BytesSent")
    val bytesSent: Long? = null,
    @SerialName("PacketsReceived")
    val packetsReceived: Long? = null,
    @SerialName("PacketsSent")
    val packetsSent: Long? = null,
)

@Serializable
data class EthernetStats(
    @SerialName("Enable")
    val enable: Int? = null,
    @SerialName("Status")
    val status: String? = null,
    val stat: Stats? = null,
)

@Serializable
data class CellCAStatsConfig(
    @SerialName("X_ALU_COM_DLCarrierAggregationNumberOfEntries")
    val dlEntries: Int? = null,
    @SerialName("X_ALU_COM_UL_CarrierAggregationNumberOfEntries")
    val ulEntries: Int? = null,
    @SerialName("ca4GDL")
    val fourGDL: Map<String, CAInfo>? = null,
    @SerialName("ca4GUL")
    val fourGUL: Map<String, CAInfo>? = null,
)

@Serializable
data class CAInfo(
    @SerialName("PhysicalCellID")
    val pci: Int? = null,
    @SerialName("ScellBand")
    val band: String? = null,
    @SerialName("ScellChannel")
    val channel: Int? = null,
)

interface CellStatsConfig {
    val stat: CellStats?
}

@Serializable
data class CellStatsConfigLte(
    override val stat: CellStatsLte? = null,
) : CellStatsConfig

@Serializable
data class CellStatsLte(
    @SerialName("RSSICurrent")
    override val rssi: Int? = null,
    @SerialName("SNRCurrent")
    override val snr: Int? = null,
    @SerialName("RSRPCurrent")
    override val rsrp: Int? = null,
    @SerialName("RSRPStrengthIndexCurrent")
    override val rsrpStrengthIndex: Int? = null,
    @SerialName("PhysicalCellID")
    override val pci: Int? = null,
    @SerialName("RSRQCurrent")
    override val rsrq: Int? = null,
    @SerialName("SignalStrengthLevel")
    override val signalStrengthLevel: Int? = null,
    @SerialName("Band")
    override val band: String? = null,
    @SerialName("DownlinkEarfcn")
    val earfcn: Long? = null,
) : CellStats

@Serializable
data class CellStatsConfig5G(
    override val stat: CellStats5G? = null,
) : CellStatsConfig

@Serializable
data class CellStats5G(
    @SerialName("RSSICurrent")
    override val rssi: Int? = null,
    @SerialName("SNRCurrent")
    override val snr: Int? = null,
    @SerialName("RSRPCurrent")
    override val rsrp: Int? = null,
    @SerialName("RSRPStrengthIndexCurrent")
    override val rsrpStrengthIndex: Int? = null,
    @SerialName("PhysicalCellID")
    override val pci: Int? = null,
    @SerialName("RSRQCurrent")
    override val rsrq: Int? = null,
    @SerialName("SignalStrengthLevel")
    override val signalStrengthLevel: Int? = null,
    @SerialName("Band")
    override val band: String? = null,
    @SerialName("Downlink_NR_ARFCN")
    val nrarfcn: Long? = null,
) : CellStats

interface CellStats {
    @SerialName("RSSICurrent")
    val rssi: Int?
    @SerialName("SNRCurrent")
    val snr: Int?
    @SerialName("RSRPCurrent")
    val rsrp: Int?
    @SerialName("RSRPStrengthIndexCurrent")
    val rsrpStrengthIndex: Int?
    @SerialName("PhysicalCellID")
    val pci: Int?
    @SerialName("RSRQCurrent")
    val rsrq: Int?
    @SerialName("SignalStrengthLevel")
    val signalStrengthLevel: Int?
    @SerialName("Band")
    val band: String?
}

@Serializable
data class ConnectionStatusStatus(
    @SerialName("ConnectionStatus")
    val connectionStatus: Int? = null,
)

@Serializable
data class ApnConfig(
    val oid: Int? = null,
    @SerialName("Enable")
    val enable: Int? = null,
    @SerialName("APN")
    val apn: String? = null,
    @SerialName("X_ALU_COM_ServiceType")
    val serviceType: String? = null,
    @SerialName("X_ALU_COM_ConnectionState")
    val connectionState: Int? = null,
    @SerialName("X_ALU_COM_IPAddressV4")
    val ipv4: String? = null,
    @SerialName("X_ALU_COM_IPAddressV6")
    val ipv6: String? = null,
)
