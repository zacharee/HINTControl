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

/**
 * Derive 5G cell identifiers (gNBID and CID) from an ECGI string.
 *
 * This removes the PLMN prefix from [ecgi], interprets the remainder as a 36-bit number,
 * then splits it into the top [gnbBits] for the gNBID and the remaining lower bits for the CID.
 *
 * Examples of common bit splits:
 * - T-Mobile: gNBID 24 bits / CID 12 bits
 * - AT&T: gNBID 26 bits / CID 10 bits
 * - Verizon: gNBID 22 bits / CID 14 bits
 *
 * The function returns nulls if inputs are invalid or cannot be parsed.
 *
 * @param ecgi The full ECGI as a numeric string including PLMN prefix.
 * @param plmn The PLMN to strip from the front of [ecgi] when present.
 * @param gnbBits Number of bits to allocate to gNBID in the 36-bit remainder (0 < [gnbBits] < 36).
 * @return Pair of (CID, gNBID) as [Long]s, or (null, null) on failure.
 */
fun deriveCidGnbidFromEcgi(ecgi: String?, plmn: String?, gnbBits: Int): Pair<Long?, Long?> {
    if (ecgi.isNullOrBlank()) return Pair(null, null)
    if (gnbBits <= 0 || gnbBits >= 36) return Pair(null, null)

    val trimmed = try {
        when {
            !plmn.isNullOrBlank() && ecgi.startsWith(plmn) -> ecgi.substring(plmn.length)
            !plmn.isNullOrBlank() && ecgi.length > plmn.length -> ecgi.substring(plmn.length)
            ecgi.length > 6 -> ecgi.substring(6) // fallback: drop common 6-digit PLMN
            ecgi.length > 5 -> ecgi.substring(5) // fallback: drop 5 digits if that's all we can
            else -> return Pair(null, null)
        }
    } catch (_: Exception) {
        return Pair(null, null)
    }

    val remainder = trimmed.toLongOrNull() ?: return Pair(null, null)
    val cidBits = 36 - gnbBits
    val cidMask = (1L shl cidBits) - 1L
    val cid = remainder and cidMask
    val gnbid = remainder ushr cidBits

    return Pair(cid, gnbid)
}
