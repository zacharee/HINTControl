package dev.zwander.common.util

import dev.zwander.common.exceptions.InvalidJSONException
import dev.zwander.common.exceptions.NoGatewayFoundException
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

        if (e.message?.contains("No such host is known") == true) {
            return
        }

        if (e.message?.contains("No route to host") == true) {
            return
        }

        if (e.message?.contains("Internal Server Error") == true) {
            return
        }

        if (e is InvalidJSONException && e.message?.lowercase()?.startsWith("<!doctype html>") == true) {
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
        )
    }
}

expect object BugsnagUtils {
    fun notify(e: Throwable)
}