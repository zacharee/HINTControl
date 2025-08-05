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

    data object SixGig : WiFiBand(
        possibleBands = arrayOf(
            "1", "5", "9", "13", "17",
            "21", "25", "29", "33", "37",
            "41", "45", "49", "53", "57",
            "61", "65", "69", "73", "77",
            "81", "85", "89", "93", "97",
            "101", "105", "109", "113", "117",
            "121", "125", "129", "133", "137",
            "141", "145", "149", "153", "157",
            "161", "165", "169", "173", "177",
            "181", "185", "189", "193", "197",
            "201", "205", "209", "213", "217",
            "221", "225", "229", "233",
        ),
        possibleBandwidths = arrayOf(
            "40MHz", "80MHz", "160MHz", "320MHz",
        ),
        labelRes = MR.strings.sixGig,
    )
}