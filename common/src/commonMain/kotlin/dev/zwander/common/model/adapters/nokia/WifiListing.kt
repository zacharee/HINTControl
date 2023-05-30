package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WifiListing(
    @SerialName("wlan_list")
    val wlanList: List<WifiConfig>? = null,
    @SerialName("guest_list")
    val guestList: List<WifiConfig>? = null,
)

@Serializable
data class WifiConfig(
    val oid: Int? = null,
    @SerialName("Enable")
    val enable: Int? = null,
    @SerialName("Type")
    val type: String? = null,
    @SerialName("SSID")
    val ssid: String? = null,
    @SerialName("IsGuestSSID")
    val isGuestSsid: Int? = null,
    @SerialName("BeaconType")
    val beaconType: String? = null,
    @SerialName("BasicAuthenticationMode")
    val basicAuthenticationMode: String? = null,
    @SerialName("BasicEncryptionModes")
    val basicEncryptionModes: String? = null,
    @SerialName("WEPEncryptionLevel")
    val wepEncryptionLevel: String? = null,
    @SerialName("WPAAuthenticationMode")
    val wpaAuthenticationMode: String? = null,
    @SerialName("WPAEncryptionModes")
    val wpaEncryptionModes: String? = null,
    @SerialName("PreSharedKey")
    val preSharedKey: String? = null,
    @SerialName("WEPKey")
    val wepKey: String? = null,
    @SerialName("PossibleChannels")
    val possibleChannels: String? = null,
    @SerialName("SSIDAdvertisementEnabled")
    val ssidAdvertisementEnabled: Int? = null,
)
