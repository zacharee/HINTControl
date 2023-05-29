package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceStatus(
    @SerialName("num_extenders")
    val numExtenders: Int? = null,
    @SerialName("extender_info")
    val extenderInfo: List<ExtenderInfo>? = null,
)

@Serializable
data class ExtenderInfo(
    @SerialName("beacon_mac")
    val beaconMac: String? = null,
    @SerialName("beacon_sno")
    val beaconSno: String? = null,
    @SerialName("beacon_state")
    val beaconState: String? = null,
    @SerialName("beacon_isOnline")
    val beaconIsOnline: Int? = null,
    @SerialName("beacon_swVersion")
    val beaconSwVersion: String? = null,
)
