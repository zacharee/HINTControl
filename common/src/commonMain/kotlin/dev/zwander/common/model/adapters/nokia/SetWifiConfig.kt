package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetWifiConfig(
    val action: String = "WLANConfig",
    val paralist: List<SetSSIDConfig>?,
)

@Serializable
data class SetSSIDConfig(
    @SerialName("BeaconType")
    val beaconType: String?,
    @SerialName("Enable")
    val enable: Boolean?,
    @SerialName("id")
    val id: List<String>?,
    @SerialName("PreSharedKey")
    val preSharedKey: String?,
    @SerialName("SSID")
    val ssid: String?,
    @SerialName("SSIDAdvertisementEnabled")
    val ssidAdvertisementEnabled: Boolean?,
    @SerialName("WPAEncryptionModes")
    val wpaEncryptionModes: String?,
)
