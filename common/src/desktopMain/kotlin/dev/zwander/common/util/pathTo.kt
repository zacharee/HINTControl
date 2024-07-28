package dev.zwander.common.util

import dev.zwander.resources.common.MR
import net.harawata.appdirs.AppDirsFactory

actual fun pathTo(subPath: String, startingTag: String): String {
    return "${AppDirsFactory.getInstance().getUserDataDir(MR.strings.app_name.localized(), null, "Zachary Wander")}/$subPath"
}