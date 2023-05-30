package dev.zwander.common.model

object Endpoints {
    const val baseIp = "http://192.168.12.1"
    const val baseUrl = "$baseIp/TMI/v1/"

    const val gateWayURL = "gateway/?get=all"
    const val getWifiConfigURL = "network/configuration/v2?get=ap"
    const val setWifiConfigURL = "network/configuration/v2?set=ap"
    const val getDevicesURL = "network/telemetry/?get=clients"
    const val getCellURL = "network/telemetry/?get=cell"
    const val getSimURL = "network/telemetry/?get=sim"
    const val rebootURL = "gateway/reset?set=reboot"
    const val authURL = "auth/login"
    const val resetURL = "auth/admin/reset"

    const val nokiaLogin = "login_app.cgi"
    const val nokiaDeviceStatus = "dashboard_device_status_web_app.cgi"
    const val nokiaRadioStatus = "fastmile_radio_status_web_app.cgi"
    const val nokiaDeviceInfoStatus = "dashboard_device_info_status_web_app.cgi"
    const val nokiaStatisticsStatus = "fastmile_statistics_status_web_app.cgi"
    const val nokiaCellStatus = "cell_status_app.cgi"
    const val nokiaWifiListing = "wlan_list_app.cgi"
    const val nokiaServiceFunction = "service_function_app.cgi"
    const val nokiaSntpStatus = "sntp_status_app.cgi"
    const val nokiaLoginPasswordReset = "login_password_reset_app.cgi"
    const val nokiaSimStatus = "sim_status_app.cgi"

    fun String.createFullUrl(): String {
        return "$baseUrl$this"
    }

    fun String.createNokiaUrl(): String {
        return "$baseIp/$this"
    }
}
