@file:JvmName("BugsnagUtilsAndroid")

package dev.zwander.common.util

import com.bugsnag.android.Bugsnag

actual object BugsnagUtils {
    actual fun notify(e: Throwable) {
        Bugsnag.notify(e)
    }
}