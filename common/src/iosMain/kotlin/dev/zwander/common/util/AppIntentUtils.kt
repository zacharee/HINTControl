@file:Suppress("unused")

package dev.zwander.common.util

import dev.zwander.common.model.GlobalModel
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

suspend fun performRebootAction(): NSError? {
    return try {
        GlobalModel.updateClient()
        GlobalModel.httpClient.value?.reboot()

        return GlobalModel.httpError.value?.let {
            NSError("RebootError", 101, mapOf(NSLocalizedDescriptionKey to it.message))
        }
    } catch (e: Throwable) {
        NSError("RebootError", 101, mapOf(NSLocalizedDescriptionKey to e.message))
    }
}
