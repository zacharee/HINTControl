package dev.zwander.common.util

expect object BugsnagUtils {
    fun notify(e: Throwable)
}