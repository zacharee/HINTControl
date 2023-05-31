package dev.zwander.common.model.adapters

import kotlinx.serialization.Serializable

@Serializable
data class SetLoginAction(
    val usernameNew: String,
    val passwordNew: String,
)
