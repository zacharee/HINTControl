package dev.zwander.common.util

import dev.zwander.common.exceptions.GatewayTimeoutException
import dev.zwander.common.exceptions.InvalidJSONException
import dev.zwander.common.exceptions.NoGatewayFoundException
import dev.zwander.common.exceptions.TimeoutException
import dev.zwander.common.exceptions.TooManyRequestsException
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException

data class ExtraErrorData(
    val tabName: String,
    val key: String,
    val value: Any?,
)

object CrossPlatformBugsnag {
    fun notify(e: Throwable) {
        e.printStackTrace()

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

        if (e is TimeoutException) {
            return
        }

        if (e is TooManyRequestsException) {
            return
        }

        if (e is GatewayTimeoutException) {
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

        if (e.message?.contains("No such host is known") == true) {
            return
        }

        if (e.message?.contains("No route to host") == true) {
            return
        }

        if (e.message?.contains("Internal Server Error") == true) {
            return
        }

        if (e.message?.contains("bad gateway") == true) {
            return
        }

        if (e.message?.contains("Fail to lookup ubus id") == true) {
            return
        }

        if (e.message?.run { startsWith("<!doctype html>") && contains("<div id=\"root\"></div>") } == true) {
            // Don't report when the Nokia gateway returns a blank HTML page.
            return
        }

        if (e.message?.contains("Enter your RADIUS credentials:") == true) {
            return
        }

        if (e is InvalidJSONException && e.message == "Invalid JSON: ") {
            return
        }

        if (e.message?.contains("The Internet connection appears to be offline.") == true) {
            return
        }

        if (e.message?.contains("blocked@eero.com") == true) {
            return
        }

        if (e.message?.contains("Could not connect to the server") == true) {
            return
        }

        BugsnagUtils.notify(e)
    }

    fun generateExtraErrorData(): List<ExtraErrorData> {
        return listOf(
            ExtraErrorData(
                tabName = "gateway",
                key = "firmware",
                value = MainModel.currentMainData.value?.device?.softwareVersion,
            ),
            ExtraErrorData(
                tabName = "gateway",
                key = "model",
                value = MainModel.currentMainData.value?.device?.model,
            ),
            ExtraErrorData(
                tabName = "gateway",
                key = "client",
                value = GlobalModel.httpClient.value?.let { it::class.qualifiedName ?: it::class.simpleName },
            ),
        )
    }
}

expect object BugsnagUtils {
    fun notify(e: Throwable)

    fun addBreadcrumb(
        message: String,
        data: Map<String?, Any?>,
    )
}