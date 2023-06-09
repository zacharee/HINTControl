package dev.zwander.common.util

import dev.icerock.moko.resources.StringResource

fun List<Pair<StringResource, Any?>>.filterBlanks(): List<Pair<StringResource, Any?>> {
    return filterNot { it.second?.toString().isNullOrBlank() }
}
