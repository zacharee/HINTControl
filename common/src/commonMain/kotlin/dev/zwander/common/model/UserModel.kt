package dev.zwander.common.model

import dev.zwander.common.util.SettingsManager
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

    val isTest = combine(username, password) { username, password ->
        username == TEST_USER && password == TEST_PASS
    }.stateIn(
        scope = GlobalScope,
        started = SharingStarted.Eagerly,
        initialValue = username.value == TEST_USER && password.value == TEST_PASS,
    )
}
