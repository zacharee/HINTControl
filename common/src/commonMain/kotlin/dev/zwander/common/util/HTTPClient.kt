package dev.zwander.common.util

import dev.zwander.common.model.Endpoints
import dev.zwander.common.model.Endpoints.createFullUrl
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json

object HTTPClient {
    val httpClient = HttpClient {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(UserModel.token.value ?: "", "")
                }
                this.refreshTokens {
                    logIn(UserModel.username.value ?: "", UserModel.password.value ?: "")

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

    val json = Json {
        ignoreUnknownKeys = true
//        isLenient = true
    }

    suspend fun logIn(username: String, password: String): String? {
        val response = httpClient.post(Endpoints.authURL.createFullUrl()) {
            setBody("{\"username\": \"${username}\", \"password\": \"${password}\"}")
        }

        return if (response.status.isSuccess()) {
            UserModel.username.value = username
            UserModel.password.value = password

            UserModel.token.value = json.decodeFromString<LoginResultData>(response.bodyAsText()).auth.token

            null
        } else {
            response.status.description
        }
    }

    suspend fun getMainData(): MainData {
        return json.decodeFromString(
            httpClient
                .get(Endpoints.gateWayURL.createFullUrl())
                .bodyAsText()
        )
    }

    suspend fun getWifiData(): WifiConfig {
        return json.decodeFromString(
            httpClient
                .get(Endpoints.getWifiConfigURL.createFullUrl())
                .bodyAsText()
        )
    }

    suspend fun getDeviceData(): ClientDeviceData {
        return json.decodeFromString(
            httpClient
                .get(Endpoints.getDevicesURL.createFullUrl())
                .bodyAsText()
        )
    }

    suspend fun getCellData(): CellDataRoot {
        return json.decodeFromString(
            httpClient
                .get(Endpoints.getCellURL.createFullUrl())
                .bodyAsText().also {
                    println(it)
                }
        )
    }

    suspend fun getSimData(): SimDataRoot {
        return json.decodeFromString(
            httpClient
                .get(Endpoints.getSimURL.createFullUrl())
                .bodyAsText()
        )
    }

    suspend fun setWifiData(newData: WifiConfig): String? {
        return try {
            httpClient.post(Endpoints.setWifiConfigURL.createFullUrl()) {
                contentType(ContentType.parse("application/json"))
                setBody(newData)
                timeout {
                    requestTimeoutMillis = 20000
                    connectTimeoutMillis = 20000
                    socketTimeoutMillis = 20000
                }
            }.run {
                if (status.isSuccess()) null else status.description
            }
        } catch (e: HttpRequestTimeoutException) {
            return HttpStatusCode.RequestTimeout.description
        }
    }

    suspend fun setLogin(newUsername: String, newPassword: String): String? {
        return httpClient.post(Endpoints.resetURL.createFullUrl()) {
            setBody("{\"usernameNew\": \"${newUsername}\", \"passwordNew\": \"${newPassword}\"}")
        }.run {
            if (status.isSuccess()) null else status.description
        }
    }

    suspend fun reboot(): String? {
        return httpClient.post(Endpoints.rebootURL.createFullUrl())
            .run {
                if (status.isSuccess()) null else status.description
            }
    }
}
