package dev.zwander.common.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource
import dev.zwander.common.model.MainModel
import dev.zwander.common.pages.*
import dev.zwander.common.util.HTTPClient
import dev.zwander.resources.common.MR

sealed class Page(
    val titleRes: StringResource,
    val icon: ImageVector,
    val refreshAction: (suspend () -> Unit)?,
    val needsRefresh: (() -> Boolean)?,
    val render: @Composable (modifier: Modifier) -> Unit
) {
    object Login : Page(
        MR.strings.log_in,
        Icons.Default.Lock,
        null,
        { false },
        { LoginPage(it) }
    )
    object Main : Page(
        MR.strings.main_data,
        Icons.Default.Home,
        {
            MainModel.currentMainData.value = HTTPClient.getMainData()
        },
        { MainModel.currentMainData.value == null },
        { MainPage(it) }
    )
    object Clients : Page(
        MR.strings.client_data,
        Icons.Default.List,
        {
            MainModel.currentClientData.value = HTTPClient.getDeviceData()
        },
        { MainModel.currentClientData.value == null },
        { ClientListPage((it)) }
    )
    object Advanced : Page(
        MR.strings.advanced,
        Icons.Default.Warning,
        {
            MainModel.currentCellData.value = HTTPClient.getCellData()
            MainModel.currentSimData.value = HTTPClient.getSimData()
        },
        {
            MainModel.currentCellData.value == null ||
                    MainModel.currentSimData.value == null
        },
        { AdvancedPage(it) }
    )
    object WifiConfig : Page(
        MR.strings.wifi_data,
        Icons.Default.Settings,
        {
            MainModel.currentWifiData.value = HTTPClient.getWifiData()
        },
        { MainModel.currentWifiData.value == null },
        { WifiConfigPage(it) }
    )
}
