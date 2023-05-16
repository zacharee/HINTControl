package dev.zwander.common.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zwander.common.pages.LoginPage
import dev.zwander.common.pages.MainPage

sealed class Page(val render: @Composable (modifier: Modifier) -> Unit) {
    object Login : Page({ LoginPage(it) })
    object Main : Page({ MainPage(it) })
}
