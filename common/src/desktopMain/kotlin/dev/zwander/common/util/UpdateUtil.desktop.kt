package dev.zwander.common.util

import dev.hydraulic.conveyor.control.SoftwareUpdateController

actual object UpdateUtil {
    actual suspend fun checkForUpdate(): UpdateInfo? {
        return try {
            val controller = SoftwareUpdateController.getInstance() ?: return null
            val installedVersion = controller.currentVersion ?: return null
            val latestVersion = controller.currentVersionFromRepository ?: return null

            if (latestVersion <= installedVersion) {
                return null
            }

            UpdateInfo(latestVersion.version)
        } catch (e: Throwable) {
            CrossPlatformBugsnag.notify(e)
            null
        }
    }

    actual suspend fun installUpdate() {
        try {
            val controller = SoftwareUpdateController.getInstance() ?: return
            val canTriggerUpdateCheck = controller.canTriggerUpdateCheckUI()

            when (canTriggerUpdateCheck) {
                SoftwareUpdateController.Availability.AVAILABLE -> {
                    controller.triggerUpdateCheckUI()
                }
                else -> {
                    UrlHandler.launchUrl("https://github.com/zacharee/HINTControl/releases/latest")
                }
            }
        } catch (e: Throwable) {
            CrossPlatformBugsnag.notify(e)
        }
    }

    actual fun supported(): Boolean {
        return true
    }
}