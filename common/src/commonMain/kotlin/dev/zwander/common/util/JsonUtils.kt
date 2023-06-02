package dev.zwander.common.util

import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer

inline fun <reified T> StringFormat.decodeFromString(string: String?): T =
    decodeFromString(serializersModule.serializer(), string ?: "{}")
