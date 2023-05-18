package dev.zwander.common.util

import com.russhwolf.settings.Settings

object SettingsManager {
    object Keys {
        const val USERNAME = "login_username"
        const val PASSWORD = "login_password"
    }

    private val settings = Settings()

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
