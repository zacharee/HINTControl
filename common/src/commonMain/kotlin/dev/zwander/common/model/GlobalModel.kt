package dev.zwander.common.model

import dev.zwander.common.data.Page
import dev.zwander.common.util.ClientUtils
import dev.zwander.common.util.HTTPClient
import kotlinx.coroutines.flow.MutableStateFlow

object GlobalModel {
    val isBlocking = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)
    val currentPage = MutableStateFlow<Page>(Page.Login)
    val httpError = MutableStateFlow<String?>(null)

    val httpClient = MutableStateFlow<HTTPClient?>(null)

    fun updateHttpError(error: String?) {
        httpError.value = null
        httpError.value = error
    }

    suspend fun updateClient(): HTTPClient? {
        httpClient.value = ClientUtils.chooseClient(UserModel.isTest.value)
        return httpClient.value
    }
}
