package dev.zwander.ios

import dev.zwander.bugsnag.cinterop.BugsnagConfiguration
import dev.zwander.common.util.CrossPlatformBugsnag
import kotlinx.cinterop.ExperimentalForeignApi

object BugsnagDelegate {
    @OptIn(ExperimentalForeignApi::class)
    fun createBugsnagConfig(): BugsnagConfiguration {
        val config = BugsnagConfiguration.loadConfig()

        config.addOnSendErrorBlock { event ->
            CrossPlatformBugsnag.generateExtraErrorData().forEach { data ->
                event?.addMetadata(
                    metadata = data.value,
                    withKey = data.key,
                    toSection = data.tabName
                )
            }
            true
        }

        return config
    }
}
