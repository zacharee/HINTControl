@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

package dev.zwander.common.util

import dev.zwander.common.exceptions.InvalidJSONException
import dev.zwander.common.model.GlobalModel
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

inline fun <reified T> StringFormat.decodeFromString(string: String?): T? =
    try {
        decodeFromString(serializersModule.serializer(), string ?: "{}")
    } catch (e: kotlinx.serialization.json.internal.JsonDecodingException) {
        GlobalModel.updateHttpError(
            InvalidJSONException("Invalid JSON: $string", e),
        )
        null
    }
