package dev.zwander.common.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource
import dev.zwander.common.pages.ClientListPage
import dev.zwander.common.pages.LoginPage
import dev.zwander.common.pages.MainPage
import dev.zwander.common.pages.WifiConfigPage
import dev.zwander.resources.common.MR

sealed class Page(
    val titleRes: StringResource,
    val icon: ImageVector,
    val render: @Composable (modifier: Modifier) -> Unit
) {
    object Login : Page(MR.strings.log_in, Icons.Default.Lock,  { LoginPage(it) })
    object Main : Page(MR.strings.main_data, Icons.Default.Home, { MainPage(it) })
    object Clients : Page(MR.strings.client_data, Icons.Default.List, { ClientListPage((it)) })
    object WifiConfig : Page(MR.strings.wifi_data, Icons.Default.Settings, { WifiConfigPage(it) })
}
