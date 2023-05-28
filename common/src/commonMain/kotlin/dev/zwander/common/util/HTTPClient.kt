package dev.zwander.common.util

import dev.zwander.common.model.Endpoints
import dev.zwander.common.model.Endpoints.createFullUrl
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.LoginResultData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SimDataRoot
import dev.zwander.common.model.adapters.WifiConfig
import io.ktor.client.HttpClient
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
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object HTTPClient {
    private val unauthedClient = HttpClient()

    private val httpClient = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(UserModel.token.value ?: "", "")
                }
                this.refreshTokens {
                    logIn(UserModel.username.value, UserModel.password.value ?: "", false)

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
                httpClient.post(Endpoints.setWifiConfigURL.createFullUrl()) {
                    contentType(ContentType.parse("application/json"))
                    setBody(newData)
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
        for (i in 0 .. 100) {
            try {
                val response = httpClient
                    .get(Endpoints.getWifiConfigURL.createFullUrl())

                if (response.status.isSuccess()) {
                    return true
                }
            } catch (_: Exception) {}

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
