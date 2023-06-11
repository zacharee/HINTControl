package dev.zwander.common.util

import dev.zwander.common.exceptions.NoGatewayFoundException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException

object Bugsnag {
    fun notify(e: Throwable) {
        if (e is SocketTimeoutException) {
            return
        }

        if (e is HttpRequestTimeoutException) {
            return
        }

        if (e is ConnectTimeoutException) {
            return
        }

        if (e is NoGatewayFoundException) {
            return
        }

        if (e is CancellationException) {
            return
        }

        if (e is kotlin.coroutines.cancellation.CancellationException) {
            return
        }

        if (e.message?.contains("Failed to connect to /") == true) {
            return
        }

        if (e.message == "Software caused connection abort") {
            return
        }

        if (e.message?.contains("Unable to resolve host") == true) {
            return
        }

        if (e.message?.contains("Internal Server Error") == true) {
            return
        }

        BugsnagUtils.notify(e)
    }
}

expect object BugsnagUtils {
    fun notify(e: Throwable)
}