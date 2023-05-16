package dev.zwander.common.util

import dev.zwander.common.model.Endpoints
import dev.zwander.common.model.Endpoints.createFullUrl
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.LoginResultData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.WifiConfig
import io.ktor.client.*
import io.ktor.client.call.*
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
    }

    val json = Json {
        ignoreUnknownKeys = true
//        isLenient = true
    }

    suspend fun logIn(username: String, password: String): String? {
        println(Endpoints.authURL.createFullUrl())
        val response = httpClient.post(Endpoints.authURL.createFullUrl()) {
            setBody("{\"username\": \"${username}\", \"password\": \"${password}\"}")
        }

        return if (response.status.isSuccess()) {
            UserModel.username.value = username
            UserModel.password.value = password

            UserModel.token.value = json.decodeFromString<LoginResultData>(response.bodyAsText()).auth.token

            null
        } else {
            println(response.bodyAsText())
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
                .bodyAsText().also {
                    println(it)
                }
        )
    }

    suspend fun setWifiData(newData: WifiConfig): String? {
        return httpClient.post(Endpoints.setWifiConfigURL.createFullUrl()) {
            setBody(newData)
        }.run {
            if (status.isSuccess()) null else status.description
        }
    }

    suspend fun setLogin(newUsername: String, newPassword: String): String? {
        return httpClient.post(Endpoints.resetURL.createFullUrl()) {
            setBody("{\"usernameNew\": \"${newUsername}\", \"passwordNew\": \"${newUsername}\"}")
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
