package dev.zwander.common.data

import dev.icerock.moko.resources.StringResource
import dev.zwander.resources.common.MR

enum class Theme(
    val label: StringResource,
) {
    SYSTEM(MR.strings.theme_system),
    LIGHT(MR.strings.theme_light),
    DARK(MR.strings.theme_dark),
    BLACK(MR.strings.theme_black),
    ;
}
