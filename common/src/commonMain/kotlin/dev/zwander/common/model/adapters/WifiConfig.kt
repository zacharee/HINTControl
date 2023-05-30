package dev.zwander.common.model.adapters

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WifiConfig(
    @SerialName("2.4ghz")
    val twoGig: BandConfig? = null,
    @SerialName("5.0ghz")
    val fiveGig: BandConfig? = null,
    val bandSteering: BandSteeringConfig? = null,
    val ssids: List<SSIDConfig>? = null,
    val canAddAndRemove: Boolean = true,
)

@Serializable
data class BandConfig(
    val airtimeFairness: Boolean? = null,
    val channel: String? = null,
    val channelBandwidth: String? = null,
    val isMUMIMOEnabled: Boolean? = null,
    val isRadioEnabled: Boolean? = null,
    val isWMMEnabled: Boolean? = null,
    val maxClients: Int? = null,
    val mode: String? = null,
    val transmissionPower: String? = null,
)

@Serializable
data class BandSteeringConfig(
    val isEnabled: Boolean,
)

@Serializable
data class SSIDConfig(
    @SerialName("2.4ghzSsid")
    val twoGigSsid: Boolean? = null,
    @SerialName("5.0ghzSsid")
    val fiveGigSsid: Boolean? = null,
    val encryptionMode: String? = null,
    val encryptionVersion: String? = null,
    val guest: Boolean? = null,
    val isBroadcastEnabled: Boolean? = null,
    val ssidName: String? = null,
    val wpaKey: String? = null,
    val canEditFrequencyAndGuest: Boolean = true,
    val ssidId: Int? = null,
    val enabled: Boolean? = null,
)
