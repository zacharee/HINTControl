package dev.zwander.common.model

import dev.zwander.common.util.PersistentMutableStateFlow
import dev.zwander.common.util.SettingsManager

object SettingsModel {
    val enableAutoRefresh = PersistentMutableStateFlow(SettingsManager.Keys.AUTO_REFRESH, false)
    val autoRefreshMs = PersistentMutableStateFlow(SettingsManager.Keys.AUTO_REFRESH_PERIOD_MS, 5000L)
    val fuzzerEnabled = PersistentMutableStateFlow(SettingsManager.Keys.FUZZER_ENABLED, false)
}