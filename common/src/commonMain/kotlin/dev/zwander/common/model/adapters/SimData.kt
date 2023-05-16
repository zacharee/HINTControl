package dev.zwander.common.model.adapters

import kotlinx.serialization.Serializable

@Serializable
data class SimDataRoot(
    val sim: SimData,
)

@Serializable
data class SimData(
    val iccId: String? = null,
    val imei: String? = null,
    val imsi: String? = null,
    val msisdn: String? = null,
    val status: Boolean? = null,
)
