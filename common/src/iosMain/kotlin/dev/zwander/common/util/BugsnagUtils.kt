@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")
package dev.zwander.common.util

import com.rickclephas.kmp.nsexceptionkt.core.asNSException
import dev.zwander.bugsnag.cinterop.BSGBreadcrumbType
import dev.zwander.bugsnag.cinterop.Bugsnag
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual object BugsnagUtils {
    actual fun notify(e: Throwable) {
        Bugsnag.notify(e.asNSException(true)) { true }
    }

    actual fun addBreadcrumb(
        message: String,
        data: Map<String?, Any?>,
    ) {
        Bugsnag.leaveBreadcrumbWithMessage(
            message,
            data.mapKeys { it.key },
            BSGBreadcrumbType.BSGBreadcrumbTypeRequest,
        )
    }
}