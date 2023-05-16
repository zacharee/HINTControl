package dev.zwander.common.model

import dev.zwander.common.data.Page
import kotlinx.coroutines.flow.MutableStateFlow

object GlobalModel {
    val isLoading = MutableStateFlow(false)
    val currentPage = MutableStateFlow<Page>(Page.Login)
    val httpError = MutableStateFlow<String?>(null)
}
