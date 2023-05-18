package dev.zwander.common.model

import dev.zwander.common.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow

object UserModel {
    val username = MutableStateFlow(SettingsManager.username)
    val password = MutableStateFlow(SettingsManager.password)
    val token = MutableStateFlow<String?>(null)
}
