@file:JvmName("BugsnagUtilsJVM")

package dev.zwander.common.util

import com.bugsnag.Bugsnag

actual object BugsnagUtils {
    val bugsnag by lazy { Bugsnag("e709115241c5468fd88637578daa5cfa") }

    actual fun notify(e: Throwable) {
        bugsnag.notify(e)
    }
}