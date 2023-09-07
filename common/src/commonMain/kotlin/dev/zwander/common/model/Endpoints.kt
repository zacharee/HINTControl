package dev.zwander.common.model

sealed class Endpoint(open val url: String) {
    abstract fun createFullUrl(): String
    
    data class CommonApiEndpoint(override val url: String) : Endpoint(url) {
        override fun createFullUrl(): String {
            return "http://${SettingsModel.gatewayIp.value}/${Endpoints.CommonApiV1.BASE_URL}/$url"
        }
    }
    
    data class NokiaApiEndpoint(override val url: String) : Endpoint(url) {
        override fun createFullUrl(): String {
            return "http://${SettingsModel.gatewayIp.value}/$url"
        }
    }
}

sealed class Endpoints {
    data object CommonApiV1 : Endpoints() {
        const val BASE_URL = "TMI/v1"

        val gatewayInfo = Endpoint.CommonApiEndpoint("gateway/?get=all")
        val getWifiConfig = Endpoint.CommonApiEndpoint("network/configuration/v2?get=ap")
        val setWifiConfig = Endpoint.CommonApiEndpoint("network/configuration/v2?set=ap")
        val getDevices = Endpoint.CommonApiEndpoint("network/telemetry/?get=clients")
        val getCellInfo = Endpoint.CommonApiEndpoint("network/telemetry/?get=cell")
        val getSimInfo = Endpoint.CommonApiEndpoint("network/telemetry/?get=sim")
        val reboot = Endpoint.CommonApiEndpoint("gateway/reset?set=reboot")
        val auth = Endpoint.CommonApiEndpoint("auth/login")
        val reset = Endpoint.CommonApiEndpoint("auth/admin/reset")
    }
    
    data object NokiaApi : Endpoints() {
        val login = Endpoint.NokiaApiEndpoint("login_app.cgi")
        val deviceStatus = Endpoint.NokiaApiEndpoint("dashboard_device_status_web_app.cgi")
        val radioStatus = Endpoint.NokiaApiEndpoint("fastmile_radio_status_web_app.cgi")
        val deviceInfoStatus = Endpoint.NokiaApiEndpoint("dashboard_device_info_status_web_app.cgi")
        val statisticsStatus = Endpoint.NokiaApiEndpoint("fastmile_statistics_status_web_app.cgi")
        val cellStatus = Endpoint.NokiaApiEndpoint("cell_status_app.cgi")
        val wifiListing = Endpoint.NokiaApiEndpoint("wlan_list_app.cgi")
        // POST: action="Reboot" or action="WLANConfig"
        val serviceFunction = Endpoint.NokiaApiEndpoint("service_function_app.cgi")
        // GET
        val sntpStatus = Endpoint.NokiaApiEndpoint("sntp_status_app.cgi")
        // POST
        val loginPasswordReset = Endpoint.NokiaApiEndpoint("login_password_reset_app.cgi")
        // GET
        val simStatus = Endpoint.NokiaApiEndpoint("sim_status_app.cgi")
    }
}
