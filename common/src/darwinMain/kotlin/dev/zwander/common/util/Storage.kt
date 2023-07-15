package dev.zwander.common.util

import platform.Foundation.NSHomeDirectory

actual fun pathTo(subPath: String): String {
    return "${NSHomeDirectory()}/$subPath"
}
