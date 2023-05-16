package dev.zwander.common.model

import kotlinx.coroutines.flow.MutableStateFlow

object UserModel {
    val username = MutableStateFlow<String?>("admin")
    val password = MutableStateFlow<String?>(null)
    val token = MutableStateFlow<String?>(null)
}
