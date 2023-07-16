package dev.zwander.common.util

import com.russhwolf.settings.Settings

object SettingsManager {
    object Keys {
        const val USERNAME = "login_username"
        const val PASSWORD = "login_password"
        const val AUTO_REFRESH = "auto_refresh_enabled"
        const val AUTO_REFRESH_PERIOD_MS = "auto_refresh_period_ms"
        const val FUZZER_ENABLED = "fuzzer_enabled"
        const val DEFAULT_PAGE = "default_page"
        const val GATEWAY_IP = "gateway_ip"
        const val WIDGET_REFRESH_PERIOD_MS = "widget_refresh_period_ms"
        const val RECORD_SNAPSHOTS = "record_snapshots"
    }

    val settings = Settings()

    var username: String
        get() = settings.getString(Keys.USERNAME, "admin")
        set(value) {
            settings.putString(Keys.USERNAME, value)
        }

    var password: String?
        get() = settings.getStringOrNull(Keys.PASSWORD)
        set(value) {
            if (value == null) {
                settings.remove(Keys.PASSWORD)
            } else {
                settings.putString(Keys.PASSWORD, value)
            }
        }
}
