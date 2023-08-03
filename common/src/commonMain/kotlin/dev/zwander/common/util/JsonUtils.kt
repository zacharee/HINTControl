@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

package dev.zwander.common.util

import dev.zwander.common.exceptions.InvalidJSONException
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

suspend inline fun <reified T> StringFormat.decodeFromString(string: String?): T? =
    try {
        decodeFromString(serializersModule.serializer(), string ?: "{}")
    } catch (e: kotlinx.serialization.json.internal.JsonDecodingException) {
        GlobalModel.updateHttpError(
            InvalidJSONException("Invalid JSON: $string", e),
        )

        // If there's a JSON exception, we may have somehow chosen the wrong client.
        // Force an update and also a reauth.
        GlobalModel.updateClient()
        GlobalModel.httpClient.value?.logIn(
            UserModel.username.value,
            UserModel.password.value ?: "",
            false,
        )
        null
    }
