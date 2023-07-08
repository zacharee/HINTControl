package dev.zwander.common.util

import cocoapods.Bugsnag.BSGBreadcrumbType
import cocoapods.Bugsnag.Bugsnag
import com.rickclephas.kmp.nsexceptionkt.core.asNSException

actual object BugsnagUtils {
    actual fun notify(e: Throwable) {
        Bugsnag.notify(e.asNSException(true))
    }

    actual fun addBreadcrumb(
        message: String,
        data: Map<String?, Any?>
    ) {
        Bugsnag.leaveBreadcrumbWithMessage(
            message,
            data.mapKeys { it.key },
            BSGBreadcrumbType.BSGBreadcrumbTypeRequest,
        )
    }
}