package dev.zwander.common.model.adapters

import kotlinx.serialization.Serializable

@Serializable
data class LoginResultData(
    val auth: AuthResultData
)

@Serializable
data class AuthResultData(
    val token: String,
)
