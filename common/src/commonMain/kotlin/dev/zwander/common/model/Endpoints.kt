package dev.zwander.common.model

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

object Endpoints {
    const val baseUrl = "http://192.168.12.1/TMI/v1/"

    const val gateWayURL = "gateway/?get=all"
    const val getWifiConfigURL = "network/configuration/v2?get=ap"
    const val setWifiConfigURL = "network/configuration/v2?set=ap"
    const val getDevicesURL = "network/telemetry/?get=clients"
    const val rebootURL = "gateway/reset?set=reboot"
    const val authURL = "auth/login"
    const val resetURL = "auth/admin/reset"

    fun String.createFullUrl(): String {
        return if (hostOs == OS.JS) {
            "api/$this"
        } else {
            "$baseUrl$this"
        }
    }
}
