package dev.zwander.common.util

data class UpdateInfo(
    val newVersion: String,
)

expect object UpdateUtil {
    suspend fun checkForUpdate(): UpdateInfo?

    suspend fun installUpdate()

    fun supported(): Boolean
}
