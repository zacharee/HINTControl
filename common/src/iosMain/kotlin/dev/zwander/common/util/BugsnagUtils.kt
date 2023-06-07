package dev.zwander.common.util

import cocoapods.Bugsnag.Bugsnag
import com.rickclephas.kmp.nsexceptionkt.core.asNSException

actual object BugsnagUtils {
    actual fun notify(e: Throwable) {
        Bugsnag.notify(e.asNSException(true))
    }
}