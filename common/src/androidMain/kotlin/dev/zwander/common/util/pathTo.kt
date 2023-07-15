package dev.zwander.common.util

import dev.zwander.common.App

actual fun pathTo(subPath: String): String {
    return "${App.instance?.filesDir?.path}/$subPath"
}