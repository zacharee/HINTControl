package dev.zwander.common.util

import dev.zwander.common.GradleConfig
import net.harawata.appdirs.AppDirsFactory

actual fun pathTo(subPath: String): String {
    return "${AppDirsFactory.getInstance().getUserDataDir(GradleConfig.appName, null, "Zachary Wander")}/$subPath"
}