@file:OptIn(DelicateCoroutinesApi::class)

package dev.zwander.common.model

import dev.zwander.common.util.SettingsManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

const val TEST_USER = "google_play_test_user"
const val TEST_PASS = "google_play_test_pass"
const val TEST_TOKEN = "1234567890"

object UserModel {
    val username = MutableStateFlow(SettingsManager.username)
    val password = MutableStateFlow(SettingsManager.password)

    val token = MutableStateFlow<String?>(null)
    val cookie = MutableStateFlow<String?>(null)

    val isTest = combine(username, password) { username, password ->
        username == TEST_USER && password == TEST_PASS
    }.stateIn(
        scope = GlobalScope,
        started = SharingStarted.Eagerly,
        initialValue = username.value == TEST_USER && password.value == TEST_PASS,
    )

    val isLoggedIn = combine(token, cookie) { token, cookie ->
        !token.isNullOrBlank() || !cookie.isNullOrBlank()
    }

    suspend fun logOut() {
        token.value = null
        cookie.value = null

        username.value = "admin"
        password.value = null

        SettingsManager.username = "admin"
        SettingsManager.password = null

        GlobalModel.httpClient.value?.logOut()
    }
}
