package dev.zwander.common.util

import dev.zwander.common.model.Endpoints
import dev.zwander.common.model.Endpoints.createFullUrl
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.TEST_TOKEN
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.LoginResultData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SimDataRoot
import dev.zwander.common.model.adapters.WifiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private object Clients {
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

    val unauthedClient = HttpClient()
    val httpClient = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(UserModel.token.value ?: "", "")
                }
                this.refreshTokens {
                    HTTPClient.logIn(
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
    }
}

object HTTPClient {
    private val unauthedClient: HttpClient
        get() = if (UserModel.isTest.value) Clients.mockClient else Clients.unauthedClient

    private val httpClient: HttpClient
        get() = if (UserModel.isTest.value) Clients.mockClient else Clients.httpClient

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun logIn(username: String, password: String, rememberCredentials: Boolean) {
        return withLoader(true) {
            val response = unauthedClient.post(Endpoints.authURL.createFullUrl()) {
                setBody("{\"username\": \"${username}\", \"password\": \"${password}\"}")
            }

            if (response.status.isSuccess()) {
                UserModel.username.value = username
                UserModel.password.value = password

                if (rememberCredentials) {
                    SettingsManager.username = username
                    SettingsManager.password = password
                }

                val text = response.bodyAsText()

                UserModel.token.value = json.decodeFromString<LoginResultData>(text).auth.token
            } else {
                println(response.status)
                throw IOException(response.status.description)
            }
        }
    }

    suspend fun getMainData(): MainData {
        return withLoader {
            json.decodeFromString(
                httpClient
                    .get(Endpoints.gateWayURL.createFullUrl())
                    .bodyAsText()
            )
        }
    }

    suspend fun getWifiData(): WifiConfig {
        return withLoader {
            json.decodeFromString(
                httpClient
                    .get(Endpoints.getWifiConfigURL.createFullUrl())
                    .bodyAsText()
            )
        }
    }

    suspend fun getDeviceData(): ClientDeviceData {
        return withLoader {
            json.decodeFromString(
                httpClient
                    .get(Endpoints.getDevicesURL.createFullUrl())
                    .bodyAsText()
            )
        }
    }

    suspend fun getCellData(): CellDataRoot {
        return withLoader {
            json.decodeFromString(
                httpClient
                    .get(Endpoints.getCellURL.createFullUrl())
                    .bodyAsText()
            )
        }
    }

    suspend fun getSimData(): SimDataRoot {
        return withLoader {
            json.decodeFromString(
                httpClient
                    .get(Endpoints.getSimURL.createFullUrl())
                    .bodyAsText()
            )
        }
    }

    suspend fun setWifiData(newData: WifiConfig) {
        withLoader(true) {
            try {
                val response = httpClient.post(Endpoints.setWifiConfigURL.createFullUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(newData)
                }

                if (!response.status.isSuccess()) {
                    throw Exception(response.bodyAsText())
                }
            } catch (e: HttpRequestTimeoutException) {
                if (!waitForLive()) {
                    GlobalModel.httpError.value = e.message
                }
                Unit
            }
        }
    }

    suspend fun setLogin(newUsername: String, newPassword: String) {
        withLoader(true) {
            httpClient.post(Endpoints.resetURL.createFullUrl()) {
                setBody("{\"usernameNew\": \"${newUsername}\", \"passwordNew\": \"${newPassword}\"}")
            }.apply {
                if (!status.isSuccess()) {
                    throw IOException(status.description)
                }
            }
        }
    }

    suspend fun reboot() {
        return withLoader(true) {
            try {
                httpClient.post(Endpoints.rebootURL.createFullUrl())
                    .apply {
                        if (!status.isSuccess()) {
                            throw IOException(status.description)
                        }
                    }
            } catch (e: HttpRequestTimeoutException) {
                if (!waitForLive()) {
                    GlobalModel.httpError.value = e.message
                }
                Unit
            }
        }
    }

    private suspend fun waitForLive(): Boolean {
        for (i in 0..100) {
            try {
                val response = httpClient
                    .get(Endpoints.getWifiConfigURL.createFullUrl())

                if (response.status.isSuccess()) {
                    return true
                }
            } catch (_: Exception) {
            }

            delay(1000L)
        }

        return false
    }

    private suspend fun <T> withLoader(blocking: Boolean = false, block: suspend () -> T): T {
        return try {
            if (blocking) {
                GlobalModel.isBlocking.value = true
            } else {
                GlobalModel.isLoading.value = true
            }
            block()
        } finally {
            if (blocking) {
                GlobalModel.isBlocking.value = false
            } else {
                GlobalModel.isLoading.value = false
            }
        }
    }
}
