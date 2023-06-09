package dev.zwander.common.util

import dev.icerock.moko.resources.StringResource

const val BULLET = "•"

fun List<Pair<StringResource, Any?>>.filterBlanks(): List<Pair<StringResource, Any?>> {
    return filterNot { it.second?.toString().isNullOrBlank() }
}

fun <T> List<T>.bulletedList(): String {
    return joinToString(" $BULLET ")
}

fun <T> MutableList<T>.addAll(vararg elements: T) {
    addAll(elements.toList())
}

fun buildItemList(block: MutableList<Pair<StringResource, Any?>>.() -> Unit): List<Pair<StringResource, Any?>> {
    val list = mutableListOf<Pair<StringResource, Any?>>()

    block(list)

    return list.filterBlanks()
}
