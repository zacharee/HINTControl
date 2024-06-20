package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.Serializable

@Serializable
data class RebootAction(
    val action: String = "Reboot",
)
