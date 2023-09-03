package dev.zwander.common.data

import dev.icerock.moko.resources.StringResource
import dev.zwander.resources.common.MR

sealed class WiFiBand(
    val possibleBands: Array<String>,
    val possibleBandwidths: Array<String>,
    val labelRes: StringResource,
) {
    data object TwoGig : WiFiBand(
        possibleBands = arrayOf(
            "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11",
        ),
        possibleBandwidths = arrayOf(
            "20MHz", "40MHz",
        ),
        labelRes = MR.strings.twoGig,
    )

    data object FiveGig : WiFiBand(
        possibleBands = arrayOf(
            "36", "40", "44", "48",
            "52", "56", "60", "64",
            "100", "104", "108", "112",
            "116", "120", "124", "128", "132",
            "136", "140", "144", "149", "153",
            "157", "161", "165",
        ),
        possibleBandwidths = arrayOf(
            "20MHz", "40MHz", "80MHz",
        ),
        labelRes = MR.strings.fiveGig,
    )
}