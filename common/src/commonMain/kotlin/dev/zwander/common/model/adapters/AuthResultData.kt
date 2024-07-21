package dev.zwander.common.model.adapters

import kotlinx.serialization.Serializable

@Serializable
data class LoginResultData(
    val auth: AuthResultData? = null,
    val result: ResultData? = null,
)

@Serializable
data class AuthResultData(
    val token: String? = null,
)

@Serializable
data class ResultData(
    val error: String? = null,
    val message: String? = null,
    val statusCode: Int? = null,
)
