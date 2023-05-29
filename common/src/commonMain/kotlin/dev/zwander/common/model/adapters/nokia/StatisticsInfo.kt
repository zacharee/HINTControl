package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatisticsInfo(
    @SerialName("network_cfg")
    val networkConfig: List<NetworkConfig>? = null,
    @SerialName("sim_cfg")
    val simConfig: List<SIMConfig>? = null,
)

@Serializable
data class NetworkConfig(
    @SerialName("IMEI")
    val imei: String? = null,
)

@Serializable
data class SIMConfig(
    @SerialName("Type")
    val type: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("IMSI")
    val imsi: String? = null,
    @SerialName("ICCID")
    val iccid: String? = null,
    @SerialName("MSISDN")
    val msisdn: String? = null,
)
