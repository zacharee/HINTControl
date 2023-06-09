package dev.zwander.common.util

import dev.zwander.common.model.Endpoints
import dev.zwander.common.model.Endpoints.createFullUrl
import dev.zwander.common.model.Endpoints.createNokiaUrl
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.TEST_TOKEN
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.AdvancedCellData
import dev.zwander.common.model.adapters.AdvancedData5G
import dev.zwander.common.model.adapters.AdvancedDataLTE
import dev.zwander.common.model.adapters.CellData5G
import dev.zwander.common.model.adapters.CellDataLTE
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.ClientsData
import dev.zwander.common.model.adapters.DeviceData
import dev.zwander.common.model.adapters.GenericData
import dev.zwander.common.model.adapters.LoginResultData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SSIDConfig
import dev.zwander.common.model.adapters.SetLoginAction
import dev.zwander.common.model.adapters.SignalData
import dev.zwander.common.model.adapters.SimData
import dev.zwander.common.model.adapters.SimDataRoot
import dev.zwander.common.model.adapters.TimeData
import dev.zwander.common.model.adapters.UsernamePassword
import dev.zwander.common.model.adapters.WifiConfig
import dev.zwander.common.model.adapters.WiredClientData
import dev.zwander.common.model.adapters.WirelessClientData
import dev.zwander.common.model.adapters.nokia.CellStatus
import dev.zwander.common.model.adapters.nokia.ConnectionStatus
import dev.zwander.common.model.adapters.nokia.DeviceInfoStatus
import dev.zwander.common.model.adapters.nokia.RebootAction
import dev.zwander.common.model.adapters.nokia.SetSSIDConfig
import dev.zwander.common.model.adapters.nokia.SetWifiConfig
import dev.zwander.common.model.adapters.nokia.StatisticsInfo
import dev.zwander.common.model.adapters.nokia.WifiListing
import dev.zwander.common.util.HttpUtils.formatForReport
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import io.ktor.utils.io.ByteReadChannel
import korlibs.io.async.async
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

private object CommonClients {
    val unauthedClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
}

private object ASClients {
    val mockEngine = MockEngine { request ->
        respond(
            content = ByteReadChannel(
                when (request.url.fullPath.replace("/TMI/v1/", "")) {
                    Endpoints.authURL -> {
                        """
                                {
                                  "auth": {
                                    "expiration": 1685324186,
                                    "refreshCountLeft": 4,
                                    "refreshCountMax": 4,
                                    "token": "$TEST_TOKEN"
                                  }
                                }
                            """.trimIndent()
                    }

                    Endpoints.gateWayURL -> {
                        """
                            {
                              "device": {
                                "friendlyName": "5G Gateway",
                                "hardwareVersion": "R01",
                                "isEnabled": true,
                                "isMeshSupported": true,
                                "macId": "11:AC:67:81:18:86",
                                "manufacturer": "Arcadyan",
                                "manufacturerOUI": "001A2A",
                                "model": "KVD21",
                                "name": "5G Gateway",
                                "role": "gateway",
                                "serial": "123456789B",
                                "softwareVersion": "1.00.18",
                                "type": "HSID",
                                "updateState": "latest"
                              },
                              "signal": {
                                "4g": {
                                  "bands": [
                                    "b2"
                                  ],
                                  "bars": 2.0,
                                  "cid": 12,
                                  "eNBID": 310463,
                                  "rsrp": -112,
                                  "rsrq": -8,
                                  "rssi": -104,
                                  "sinr": 5
                                },
                                "5g": {
                                  "bands": [
                                    "n41"
                                  ],
                                  "bars": 4.0,
                                  "cid": 0,
                                  "gNBID": 0,
                                  "rsrp": -96,
                                  "rsrq": -2,
                                  "rssi": -94,
                                  "sinr": 22
                                },
                                "generic": {
                                  "apn": "FBB.HOME",
                                  "hasIPv6": true,
                                  "registration": "registered",
                                  "roaming": false
                                }
                              },
                              "time": {
                                "daylightSavings": {
                                  "isUsed": true
                                },
                                "localTime": 1685309071,
                                "localTimeZone": "<-04>4",
                                "upTime": 30996
                              }
                            }
                        """.trimIndent()
                    }

                    Endpoints.getWifiConfigURL -> {
                        """
                            {
                              "2.4ghz": {
                                "airtimeFairness": true,
                                "channel": "Auto",
                                "channelBandwidth": "Auto",
                                "isMUMIMOEnabled": true,
                                "isRadioEnabled": false,
                                "isWMMEnabled": true,
                                "maxClients": 128,
                                "mode": "auto",
                                "transmissionPower": "100%"
                              },
                              "5.0ghz": {
                                "airtimeFairness": true,
                                "channel": "Auto",
                                "channelBandwidth": "80MHz",
                                "isMUMIMOEnabled": true,
                                "isRadioEnabled": false,
                                "isWMMEnabled": true,
                                "maxClients": 128,
                                "mode": "auto",
                                "transmissionPower": "100%"
                              },
                              "bandSteering": {
                                "isEnabled": true
                              },
                              "ssids": [
                                {
                                  "2.4ghzSsid": true,
                                  "5.0ghzSsid": true,
                                  "encryptionMode": "AES",
                                  "encryptionVersion": "WPA2/WPA3",
                                  "guest": false,
                                  "isBroadcastEnabled": true,
                                  "ssidName": "WIFI_SSID",
                                  "wpaKey": "some_wifi_password"
                                }
                              ]
                            }
                        """.trimIndent()
                    }

                    Endpoints.getDevicesURL -> {
                        """
                            {
                              "clients": {
                                "2.4ghz": [],
                                "5.0ghz": [],
                                "ethernet": [
                                  {
                                    "connected": true,
                                    "ipv4": "192.168.12.187",
                                    "ipv6": [
                                      "fe80::7a45:58ff:fee6:72c6",
                                    ],
                                    "mac": "D2:B5:49:86:71:DE",
                                    "name": ""
                                  }
                                ]
                              }
                            }
                        """.trimIndent()
                    }

                    Endpoints.getCellURL -> {
                        """
                            {
                              "cell": {
                                "4g": {
                                  "bandwidth": "15M",
                                  "cqi": 9,
                                  "earfcn": "875",
                                  "ecgi": "31026079478540",
                                  "mcc": "310",
                                  "mnc": "260",
                                  "pci": "63",
                                  "plmn": "310260",
                                  "sector": {
                                    "bands": [
                                      "b2"
                                    ],
                                    "bars": 2.0,
                                    "cid": 12,
                                    "eNBID": 310463,
                                    "rsrp": -113,
                                    "rsrq": -7,
                                    "rssi": -105,
                                    "sinr": 6
                                  },
                                  "status": true,
                                  "supportedBands": [
                                    "b2",
                                    "b4",
                                    "b5",
                                    "b12",
                                    "b41",
                                    "b46",
                                    "b66",
                                    "b71"
                                  ],
                                  "tac": "22233"
                                },
                                "5g": {
                                  "bandwidth": "100M",
                                  "cqi": 12,
                                  "earfcn": "520110",
                                  "ecgi": "3102600",
                                  "mcc": "310",
                                  "mnc": "260",
                                  "pci": "781",
                                  "plmn": "310260",
                                  "sector": {
                                    "bands": [
                                      "n41"
                                    ],
                                    "bars": 4.0,
                                    "cid": 0,
                                    "gNBID": 0,
                                    "rsrp": -96,
                                    "rsrq": -2,
                                    "rssi": -93,
                                    "sinr": 23
                                  },
                                  "status": true,
                                  "supportedBands": [
                                    "n25",
                                    "n41",
                                    "n66",
                                    "n71"
                                  ],
                                  "tac": "0"
                                },
                                "generic": {
                                  "apn": "FBB.HOME",
                                  "hasIPv6": true,
                                  "registration": "registered",
                                  "roaming": false
                                },
                                "gps": {
                                  "latitude": 39.9526,
                                  "longitude": -75.1652
                                }
                              }
                            }
                        """.trimIndent()
                    }

                    Endpoints.getSimURL -> {
                        """
                            {
                              "sim": {
                                "iccId": "1856372956105738573",
                                "imei": "126504487235463",
                                "imsi": "684367390758466",
                                "msisdn": "18564345678",
                                "status": true
                              }
                            }
                        """.trimIndent()
                    }

                    Endpoints.setWifiConfigURL -> {
                        ""
                    }

                    Endpoints.resetURL -> {
                        ""
                    }

                    Endpoints.rebootURL -> {
                        ""
                    }

                    else -> "Unsupported!"
                }
            ),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
    val mockClient = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json()
        }
    }

    val httpClient = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(UserModel.token.value ?: "", "")
                }
                refreshTokens {
                    ArcadyanSagemcomClient.logIn(
                        UserModel.username.value,
                        UserModel.password.value ?: "",
                        false
                    )

                    BearerTokens(UserModel.token.value ?: "", "")
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000
        }
        this.developmentMode = true
    }
}

private object NokiaClients {
    val cookieStorage = GlobalCookiesStorage()

    val httpClient = HttpClient {
        install(HttpCookies) {
            storage = cookieStorage
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000
        }
        defaultRequest {
            headers.appendIfNameAbsent(HttpHeaders.Cookie, UserModel.cookie.value ?: "")
        }
        install(HttpRequestRetry) {
            this.modifyRequest {
                runBlocking {
                    NokiaClient.logIn(
                        UserModel.username.value,
                        UserModel.password.value ?: "",
                        false,
                    )
                }
            }
        }
    }
}

object ClientUtils {
    suspend fun chooseClient(test: Boolean): HTTPClient? {
        if (test) {
            return ArcadyanSagemcomClient
        }

        val arcadyanExists = async(Dispatchers.Unconfined) { ArcadyanSagemcomClient.exists() }
        val nokiaExists = async(Dispatchers.Unconfined) { NokiaClient.exists() }

        if (arcadyanExists.await()) {
            nokiaExists.cancel()
            return ArcadyanSagemcomClient
        }

        if (nokiaExists.await()) {
            arcadyanExists.cancel()
            return NokiaClient
        }

        GlobalModel.updateHttpError(
            Exception(
                "No T-Mobile gateway found!",
                Exception("${ArcadyanSagemcomClient.testUrl}, ${NokiaClient.testUrl}"),
            ),
        )

        return null
    }
}

interface HTTPClient {
    val testUrl: String
    val httpClient: HttpClient
    val unauthedClient: HttpClient

    suspend fun logIn(username: String, password: String, rememberCredentials: Boolean)
    suspend fun getMainData(): MainData?
    suspend fun getWifiData(): WifiConfig?
    suspend fun getDeviceData(): ClientDeviceData?
    suspend fun getCellData(): CellDataRoot?
    suspend fun getSimData(): SimDataRoot?
    suspend fun setWifiData(newData: WifiConfig)
    suspend fun setLogin(newUsername: String, newPassword: String)
    suspend fun reboot()

    suspend fun exists(): Boolean

    suspend fun logOut() {}

    suspend fun genericRequest(
        showError: Boolean = true,
        methodBlock: suspend HttpClient.() -> HttpResponse
    ): HttpResponse? {
        return httpClient.handleCatch(showError = showError, methodBlock = methodBlock)
    }

    suspend fun <T> withLoader(blocking: Boolean = false, block: suspend () -> T): T? {
        return try {
            if (blocking) {
                GlobalModel.isBlocking.value = true
            } else {
                GlobalModel.isLoading.value = true
            }
            block()
        } catch (_: CancellationException) {
            null
        } catch (e: Throwable) {
            GlobalModel.updateHttpError(e)
            null
        } finally {
            if (blocking) {
                GlobalModel.isBlocking.value = false
            } else {
                GlobalModel.isLoading.value = false
            }
        }
    }

    suspend fun waitForLive(condition: (suspend () -> Boolean)? = null): Boolean {
        for (i in 0..100) {
            try {
                if (condition == null) {
                    val response = httpClient.get(testUrl)

                    if (response.status.isSuccess()) {
                        return true
                    }
                } else if (condition()) {
                    return true
                }
            } catch (_: Exception) {}

            delay(1000L)
        }

        return false
    }

    suspend fun HttpClient.handleCatch(
        showError: Boolean = true,
        methodBlock: suspend HttpClient.() -> HttpResponse,
    ): HttpResponse? {
        try {
            return methodBlock().also { if (showError) it.setError() }
        } catch (_: CancellationException) {
        } catch (e: HttpRequestTimeoutException) {
            Exception(e).printStackTrace()
            if (!waitForLive()) {
                if (showError) {
                    GlobalModel.updateHttpError(e)
                } else {
                    BugsnagUtils.notify(e)
                }
            }
        } catch (e: Exception) {
            Exception(e).printStackTrace()
            if (showError) {
                GlobalModel.updateHttpError(e)
            } else {
                BugsnagUtils.notify(e)
            }
        }

        return null
    }

    suspend fun HttpResponse.setError(): Boolean {
        return if (!status.isSuccess()) {
            val items = mutableListOf(status.description)

            items.add(this.formatForReport().map { "${it.key}==${it.value}" }.joinToString("\n", "{", "}"))

            val body = bodyAsText()
            if (body.isNotBlank()) {
                items.add(body)
            }

            GlobalModel.updateHttpError(Exception(items.joinToString("\n")))
            true
        } else {
            false
        }
    }
}

private object NokiaClient : HTTPClient {
    override val testUrl: String = Endpoints.nokiaDeviceStatus.createNokiaUrl()

    override val httpClient: HttpClient
        get() = NokiaClients.httpClient

    override val unauthedClient: HttpClient
        get() = CommonClients.unauthedClient

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    override suspend fun logIn(username: String, password: String, rememberCredentials: Boolean) {
        withLoader(true) {
            val response = unauthedClient.handleCatch {
                submitForm(
                    url = Endpoints.nokiaLogin.createNokiaUrl(),
                    formParameters = parameters {
                        append("name", UserModel.username.value)
                        append("pswd", UserModel.password.value ?: "")
                    }
                )
            }

            if (response?.status?.isSuccess() == true) {
                UserModel.username.value = username
                UserModel.password.value = password

                if (rememberCredentials) {
                    SettingsManager.username = username
                    SettingsManager.password = password
                }

                UserModel.cookie.value =
                    response.headers.getAll(HttpHeaders.SetCookie)?.joinToString(";")
            }
        }
    }

    override suspend fun logOut() {
        NokiaClients.cookieStorage.clear()
    }

    override suspend fun getMainData(): MainData? {
        return withLoader {
            val nokiaDeviceData = json.decodeFromString<DeviceInfoStatus>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaDeviceInfoStatus.createNokiaUrl())
                }?.bodyAsText()
            )
            val cellStatus = json.decodeFromString<CellStatus>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaCellStatus.createNokiaUrl())
                }?.bodyAsText()
            )
            val connectionStatus = json.decodeFromString<ConnectionStatus>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaRadioStatus.createNokiaUrl())
                }?.bodyAsText()
            )

            val lteConnected =
                connectionStatus.fourGStats?.firstOrNull()?.stat?.earfcn != UInt.MAX_VALUE.toLong()
            val fiveGConnected =
                connectionStatus.fiveGStats?.firstOrNull()?.stat?.nrarfcn != UInt.MAX_VALUE.toLong()

            MainData(
                device = DeviceData(
                    name = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.description,
                    manufacturer = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.manufacturer,
                    type = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.productClass,
                    hardwareVersion = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.hardwareVersion,
                    softwareVersion = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.softwareVersion,
                    isEnabled = nokiaDeviceData.deviceConfig?.firstOrNull()?.active?.let { it == 1 },
                    isMeshSupported = true,
                    macId = nokiaDeviceData.deviceConfig?.firstOrNull()?.macAddress,
                    serial = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.serialNumber,
                ),
                signal = SignalData(
                    fourG = if (lteConnected) {
                        CellDataLTE(
                            eNBID = cellStatus.cellStatLte?.firstOrNull()?.enbid?.toLongOrNull(),
                            rssi = cellStatus.cellStatLte?.firstOrNull()?.rssi,
                            bands = cellStatus.cellStatLte?.firstOrNull()?.band?.let { listOf(it) },
                            bars = cellStatus.cellStatLte?.firstOrNull()?.rsrpStrengthIndex?.toDouble(),
                            rsrp = cellStatus.cellStatLte?.firstOrNull()?.rsrp,
                            rsrq = cellStatus.cellStatLte?.firstOrNull()?.rsrq,
                            sinr = cellStatus.cellStatLte?.firstOrNull()?.snr,
                        )
                    } else {
                        null
                    },
                    fiveG = if (fiveGConnected) {
                        CellData5G(
                            gNBID = cellStatus.cellStat5G?.firstOrNull()?.enbid?.toLongOrNull(),
                            rssi = cellStatus.cellStat5G?.firstOrNull()?.rssi,
                            bands = cellStatus.cellStat5G?.firstOrNull()?.band?.let { listOf(it) },
                            bars = cellStatus.cellStat5G?.firstOrNull()?.rsrpStrengthIndex?.toDouble(),
                            rsrp = cellStatus.cellStat5G?.firstOrNull()?.rsrp,
                            rsrq = cellStatus.cellStat5G?.firstOrNull()?.rsrq,
                            sinr = cellStatus.cellStat5G?.firstOrNull()?.snr,
                        )
                    } else {
                        null
                    },
                    generic = GenericData(
                        apn = connectionStatus.apnConfigs?.firstOrNull()?.apn,
                        hasIPv6 = connectionStatus.apnConfigs?.firstOrNull()?.ipv6 != null,
                        roaming = cellStatus.cellStatGeneric?.firstOrNull()?.roamingStatus?.lowercase()
                            ?.let { it != "home" },
                    ),
                ),
                time = TimeData(
                    upTime = nokiaDeviceData.deviceAppStatus?.firstOrNull()?.upTime,
                ),
            )
        }
    }

    override suspend fun getWifiData(): WifiConfig? {
        return withLoader {
            val wifiListing = json.decodeFromString<WifiListing>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaWifiListing.createNokiaUrl())
                }?.bodyAsText()
            )

            WifiConfig(
                ssids = wifiListing.wlanList?.map { wlan ->
                    SSIDConfig(
                        canEditFrequencyAndGuest = false,
                        twoGigSsid = wlan.type == "2.4G",
                        fiveGigSsid = wlan.type == "5G",
                        encryptionMode = if (wlan.wpaEncryptionModes?.startsWith("AES") == true) "AES" else "TKIP",
                        encryptionVersion = NokiaConverter.convertNokiaEncryptionVersionToArcadyan(
                            wlan.beaconType
                        ),
                        guest = wlan.isGuestSsid == 1,
                        isBroadcastEnabled = wlan.ssidAdvertisementEnabled == 1,
                        ssidName = wlan.ssid,
                        ssidId = wlan.oid,
                        wpaKey = wlan.preSharedKey,
                        enabled = wlan.enable == 1,
                    )
                },
                canAddAndRemove = false,
            )
        }
    }

    override suspend fun getDeviceData(): ClientDeviceData? {
        return withLoader {
            val deviceInfo = json.decodeFromString<DeviceInfoStatus>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaDeviceInfoStatus.createNokiaUrl())
                }?.bodyAsText()
            )

            val wiredClients = deviceInfo.deviceConfig?.filter { it.interfaceType == "Ethernet" }
            val twoGigClients = deviceInfo.deviceConfig?.filter { it.interfaceType == "802.11" }
            val fiveGigClients = deviceInfo.deviceConfig?.filter { it.interfaceType == "802.11ac" || it.interfaceType == "802.11ax" }

            ClientDeviceData(
                clients = ClientsData(
                    ethernet = wiredClients?.map {
                        WiredClientData(
                            connected = it.active == 1,
                            ipv4 = it.ipAddress,
                            mac = it.macAddress,
                            name = it.hostName,
                        )
                    },
                    twoGig = twoGigClients?.map {
                        WirelessClientData(
                            connected = it.active == 1,
                            ipv4 = it.ipAddress,
                            mac = it.macAddress,
                            name = it.hostName,
                        )
                    },
                    fiveGig = fiveGigClients?.map {
                        WirelessClientData(
                            connected = it.active == 1,
                            ipv4 = it.ipAddress,
                            mac = it.macAddress,
                            name = it.hostName,
                        )
                    },
                ),
            )
        }
    }

    override suspend fun getCellData(): CellDataRoot? {
        return withLoader {
            val connectionStatus = json.decodeFromString<ConnectionStatus>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaRadioStatus.createNokiaUrl())
                }?.bodyAsText()
            )
            val cellStatus = json.decodeFromString<CellStatus>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaCellStatus.createNokiaUrl())
                }?.bodyAsText()
            )

            val lteConnected =
                connectionStatus.fourGStats?.firstOrNull()?.stat?.earfcn != UInt.MAX_VALUE.toLong()
            val fiveGConnected =
                connectionStatus.fiveGStats?.firstOrNull()?.stat?.nrarfcn != UInt.MAX_VALUE.toLong()

            CellDataRoot(
                cell = AdvancedCellData(
                    fourG = if (lteConnected) {
                        AdvancedDataLTE(
                            cqi = cellStatus.cellStatLte?.firstOrNull()?.cqi?.toIntOrNull(),
                            earfcn = connectionStatus.fourGStats?.firstOrNull()?.stat?.earfcn?.toString(),
                            ecgi = cellStatus.cellStatLte?.firstOrNull()?.ecgi,
                            pci = connectionStatus.fourGStats?.firstOrNull()?.stat?.pci,
                            tac = cellStatus.cellStatGeneric?.firstOrNull()?.tac,
                            bandwidth = cellStatus.cellStatLte?.firstOrNull()?.bandwidth,
                            mcc = cellStatus.cellStatLte?.firstOrNull()?.mcc,
                            mnc = cellStatus.cellStatLte?.firstOrNull()?.mnc,
                            plmn = cellStatus.cellStatLte?.firstOrNull()?.plmnName,
                        )
                    } else {
                        null
                    },
                    fiveG = if (fiveGConnected) {
                        AdvancedData5G(
                            cqi = cellStatus.cellStat5G?.firstOrNull()?.cqi?.toIntOrNull(),
                            earfcn = connectionStatus.fiveGStats?.firstOrNull()?.stat?.nrarfcn?.toString(),
                            ecgi = cellStatus.cellStat5G?.firstOrNull()?.ecgi,
                            pci = connectionStatus.fiveGStats?.firstOrNull()?.stat?.pci,
                            bandwidth = cellStatus.cellStat5G?.firstOrNull()?.bandwidth,
                            mcc = cellStatus.cellStat5G?.firstOrNull()?.mcc,
                            mnc = cellStatus.cellStat5G?.firstOrNull()?.mnc,
                            plmn = cellStatus.cellStat5G?.firstOrNull()?.plmnName,
                        )
                    } else {
                        null
                    },
                    generic = GenericData(
                        apn = connectionStatus.apnConfigs?.firstOrNull()?.apn,
                        hasIPv6 = !connectionStatus.apnConfigs?.firstOrNull()?.ipv6.isNullOrBlank(),
                        roaming = cellStatus.cellStatGeneric?.firstOrNull()?.roamingStatus?.lowercase()
                            ?.let { it != "home" }
                    ),
                ),
            )
        }
    }

    override suspend fun getSimData(): SimDataRoot? {
        return withLoader {
            val statistics = json.decodeFromString<StatisticsInfo>(
                httpClient.handleCatch {
                    get(Endpoints.nokiaStatisticsStatus.createNokiaUrl())
                }?.bodyAsText()
            )

            SimDataRoot(
                sim = SimData(
                    iccId = statistics.simConfig?.firstOrNull()?.iccid,
                    imei = statistics.networkConfig?.firstOrNull()?.imei,
                    imsi = statistics.simConfig?.firstOrNull()?.imsi,
                    msisdn = statistics.simConfig?.firstOrNull()?.msisdn,
                    status = statistics.simConfig?.firstOrNull()?.status?.lowercase()
                        ?.let { it == "Valid" },
                ),
            )
        }
    }

    override suspend fun setWifiData(newData: WifiConfig) {
        withLoader(true) {
            val nokiaConfig = SetWifiConfig(
                paralist = newData.ssids?.map { ssid ->
                    SetSSIDConfig(
                        beaconType = NokiaConverter.convertArcadyanEncryptionVersionToNokia(ssid.encryptionVersion),
                        enable = ssid.enabled,
                        id = ssid.ssidId?.let { listOf(it.toString()) },
                        preSharedKey = ssid.wpaKey,
                        ssid = ssid.ssidName,
                        ssidAdvertisementEnabled = ssid.isBroadcastEnabled,
                        wpaEncryptionModes = "${ssid.encryptionMode}Encryption"
                    )
                },
            )

            val response = httpClient.handleCatch {
                post(Endpoints.nokiaServiceFunction.createNokiaUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(nokiaConfig)
                }
            }

            if (response?.status?.isSuccess() == true) {
                delay(10000L)

                waitForLive {
                    httpClient.get(Endpoints.nokiaWifiListing.createNokiaUrl())
                    true
                }
            }
        }
    }

    override suspend fun setLogin(newUsername: String, newPassword: String) {
        // Not implemented.
    }

    override suspend fun reboot() {
        withLoader(true) {
            httpClient.handleCatch {
                post(Endpoints.nokiaServiceFunction.createNokiaUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(RebootAction())
                }
            }
        }
    }

    override suspend fun exists(): Boolean {
        return try {
            unauthedClient.get(Endpoints.nokiaDeviceStatus.createNokiaUrl()).status.isSuccess()
        } catch (e: Exception) {
            BugsnagUtils.notify(e)
            false
        }
    }
}

private object ArcadyanSagemcomClient : HTTPClient {
    override val unauthedClient: HttpClient
        get() = if (UserModel.isTest.value) ASClients.mockClient else CommonClients.unauthedClient

    override val httpClient: HttpClient
        get() = if (UserModel.isTest.value) ASClients.mockClient else ASClients.httpClient

    override val testUrl = Endpoints.getWifiConfigURL.createFullUrl()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun logIn(username: String, password: String, rememberCredentials: Boolean) {
        withLoader(true) {
            val response = unauthedClient.handleCatch {
                post(Endpoints.authURL.createFullUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(UsernamePassword(username, password))
                }
            }

            if (response?.status?.isSuccess() == true) {
                UserModel.username.value = username
                UserModel.password.value = password

                if (rememberCredentials) {
                    SettingsManager.username = username
                    SettingsManager.password = password
                }

                val text = response.bodyAsText()
                val token = json.decodeFromString<LoginResultData>(text).auth?.token

                UserModel.token.value = token

                if (token == null) {
                    GlobalModel.updateHttpError(Exception(text))
                }
            }
        }
    }

    override suspend fun getMainData(): MainData? {
        return withLoader {
            json.decodeFromString(
                httpClient.handleCatch {
                    get(Endpoints.gateWayURL.createFullUrl())
                }?.bodyAsText()
            )
        }
    }

    override suspend fun getWifiData(): WifiConfig? {
        return withLoader {
            json.decodeFromString(
                httpClient.handleCatch {
                    get(Endpoints.getWifiConfigURL.createFullUrl())
                }?.bodyAsText()
            )
        }
    }

    override suspend fun getDeviceData(): ClientDeviceData? {
        return withLoader {
            json.decodeFromString(
                httpClient.handleCatch {
                    get(Endpoints.getDevicesURL.createFullUrl())
                }?.bodyAsText()
            )
        }
    }

    override suspend fun getCellData(): CellDataRoot? {
        return withLoader {
            json.decodeFromString(
                httpClient.handleCatch {
                    get(Endpoints.getCellURL.createFullUrl())
                }?.bodyAsText()
            )
        }
    }

    override suspend fun getSimData(): SimDataRoot? {
        return withLoader {
            json.decodeFromString(
                httpClient.handleCatch {
                    get(Endpoints.getSimURL.createFullUrl())
                }?.bodyAsText()
            )
        }
    }

    override suspend fun setWifiData(newData: WifiConfig) {
        withLoader(true) {
            httpClient.handleCatch {
                post(Endpoints.setWifiConfigURL.createFullUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(newData)
                }
            }
        }
    }

    override suspend fun setLogin(newUsername: String, newPassword: String) {
        withLoader(true) {
            httpClient.handleCatch {
                post(Endpoints.resetURL.createFullUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(SetLoginAction(newUsername, newPassword))
                }
            }
        }
    }

    override suspend fun reboot() {
        withLoader(true) {
            httpClient.handleCatch {
                post(Endpoints.rebootURL.createFullUrl())
            }
        }
    }

    override suspend fun exists(): Boolean {
        return try {
            val response = httpClient.get(Endpoints.gateWayURL.createFullUrl())

            response.status.value != 404
        } catch (e: Exception) {
            BugsnagUtils.notify(e)
            false
        }
    }
}
