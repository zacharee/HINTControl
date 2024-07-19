package dev.zwander.common.util

const val BULLET = "•"

fun <T> List<T>.bulletedList(
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null,
): String {
    return joinToString(
        separator = " $BULLET ",
        prefix = prefix,
        postfix = postfix,
        limit = limit,
        truncated = truncated,
        transform = transform,
    )
}

fun nullableMinOf(vararg numbers: Int?, defaultMin: Int? = null): Int? {
    return numbers.filterNotNull().minOrNull() ?: defaultMin
}

fun nullableMaxOf(vararg numbers: Int?, defaultMax: Int? = null): Int? {
    return numbers.filterNotNull().maxOrNull() ?: defaultMax
}
