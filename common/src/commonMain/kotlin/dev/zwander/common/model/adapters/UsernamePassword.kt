package dev.zwander.common.model.adapters

import kotlinx.serialization.Serializable

@Serializable
data class UsernamePassword(
    val username: String,
    val password: String,
)
