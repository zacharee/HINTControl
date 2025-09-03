package dev.zwander.common.model.adapters

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientDeviceData(
    val clients: ClientsData? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ClientsData(
    @SerialName("2.4ghz")
    val twoGig: List<WirelessClientData>? = null,
    @SerialName("5.0ghz")
    val fiveGig: List<WirelessClientData>? = null,
    @SerialName("6.0ghz")
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val sixGig: List<WirelessClientData>? = null,
    val ethernet: List<WiredClientData>? = null,
    val wireless: List<WirelessClientData>? = null,
)

interface BaseClientData {
    val connected: Boolean
    val ipv4: String?
    val ipv6: List<String>?
    val mac: String?
    val name: String?
}

@Serializable
data class WiredClientData(
    override val connected: Boolean,
    override val ipv4: String? = null,
    override val ipv6: List<String>? = null,
    override val mac: String? = null,
    override val name: String? = null,
) : BaseClientData

@Serializable
data class WirelessClientData(
    override val connected: Boolean,
    override val ipv4: String? = null,
    override val ipv6: List<String>? = null,
    override val mac: String? = null,
    override val name: String? = null,
) : BaseClientData
