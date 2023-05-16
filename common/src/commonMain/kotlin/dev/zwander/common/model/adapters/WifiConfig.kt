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
)

@Serializable
data class BandConfig(
    val airtimeFairness: Boolean,
    val channel: String,
    val channelBandwidth: String,
    val isMUMIMOEnabled: Boolean,
    val isRadioEnabled: Boolean,
    val isWMMEnabled: Boolean,
    val maxClients: Int,
    val mode: String,
    val transmissionPower: String,
)

@Serializable
data class BandSteeringConfig(
    val isEnabled: Boolean,
)

@Serializable
data class SSIDConfig(
    @SerialName("2.4ghzSsid")
    val twoGigSsid: Boolean,
    @SerialName("5.0ghzSsid")
    val fiveGigSsid: Boolean,
    val encryptionMode: String,
    val encryptionVersion: String,
    val guest: Boolean,
    val isBroadcastEnabled: Boolean,
    val ssidName: String?,
    val wpaKey: String?,
)
