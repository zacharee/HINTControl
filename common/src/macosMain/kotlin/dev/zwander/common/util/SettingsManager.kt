package dev.zwander.common.util

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual fun ActualSettings(): Settings {
    return NSUserDefaultsSettings.Factory().create("group.dev.zwander.arcadyankvd21control.group")
}
