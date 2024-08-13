package dev.zwander.common.util

actual object UpdateUtil {
    actual suspend fun checkForUpdate(): UpdateInfo? {
        return null
    }

    actual suspend fun installUpdate() {
    }

    actual fun supported(): Boolean {
        return false
    }
}