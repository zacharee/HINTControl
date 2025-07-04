package dev.zwander.common.model

import dev.zwander.common.data.Page
import dev.zwander.common.data.Theme
import dev.zwander.common.util.PersistentMutableStateFlow
import dev.zwander.common.util.SettingsManager

object SettingsModel {
    val enableAutoRefresh = PersistentMutableStateFlow(SettingsManager.Keys.AUTO_REFRESH, false)
    val autoRefreshMs = PersistentMutableStateFlow(SettingsManager.Keys.AUTO_REFRESH_PERIOD_MS, 5000L)
    val fuzzerEnabled = PersistentMutableStateFlow(SettingsManager.Keys.FUZZER_ENABLED, false)
    val defaultPage = PersistentMutableStateFlow<Page>(SettingsManager.Keys.DEFAULT_PAGE, Page.Main)
    val gatewayIp = PersistentMutableStateFlow<String>(SettingsManager.Keys.GATEWAY_IP, "192.168.12.1")
    val widgetRefresh = PersistentMutableStateFlow(SettingsManager.Keys.WIDGET_REFRESH_PERIOD, 60L)
    val recordSnapshots = PersistentMutableStateFlow(SettingsManager.Keys.RECORD_SNAPSHOTS, false)
    val theme = PersistentMutableStateFlow<Theme>(SettingsManager.Keys.THEME, Theme.SYSTEM)
}