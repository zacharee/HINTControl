package dev.zwander.common.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import dev.zwander.common.pages.*
import dev.zwander.resources.common.MR

sealed class Page(
    val titleRes: StringResource,
    val key: String,
    val icon: @Composable () -> Painter,
    val refreshAction: (suspend () -> Unit)?,
    val needsRefresh: (() -> Boolean)?,
    val render: @Composable (modifier: Modifier) -> Unit
) {
    companion object {
        const val LOG_IN_PAGE_KEY = "log_in_page"
        const val MAIN_PAGE_KEY = "main_page"
        const val CLIENTS_PAGE_KEY = "clients_page"
        const val ADVANCED_PAGE_KEY = "advanced_page"
        const val WIFI_PAGE_KEY = "wifi_page"
        const val SETTINGS_PAGE_KEY = "settings_page"
        const val FUZZER_PAGE_KEY = "fuzzer_page"

        fun pageFromKey(key: String): Page {
            return when (key) {
                LOG_IN_PAGE_KEY -> Login
                MAIN_PAGE_KEY -> Main
                CLIENTS_PAGE_KEY -> Clients
                ADVANCED_PAGE_KEY -> Advanced
                WIFI_PAGE_KEY -> WifiConfig
                SETTINGS_PAGE_KEY -> SettingsPage
                FUZZER_PAGE_KEY -> FuzzerPage
                else -> throw IllegalArgumentException("Unknown key $key")
            }
        }
    }

    object Login : Page(
        MR.strings.log_in,
        LOG_IN_PAGE_KEY,
        { rememberVectorPainter(Icons.Default.Lock) },
        null,
        { false },
        { LoginPage(it) }
    )
    object Main : Page(
        MR.strings.main_data,
        MAIN_PAGE_KEY,
        { rememberVectorPainter(Icons.Default.Home) },
        {
            MainModel.currentMainData.value = GlobalModel.httpClient.value?.getMainData()
        },
        { MainModel.currentMainData.value == null },
        { MainPage(it) }
    )
    object Clients : Page(
        MR.strings.client_data,
        CLIENTS_PAGE_KEY,
        { rememberVectorPainter(Icons.Default.List) },
        {
            MainModel.currentClientData.value = GlobalModel.httpClient.value?.getDeviceData()
        },
        { MainModel.currentClientData.value == null },
        { ClientListPage((it)) }
    )
    object Advanced : Page(
        MR.strings.advanced,
        ADVANCED_PAGE_KEY,
        { rememberVectorPainter(Icons.Default.Warning) },
        {
            MainModel.currentCellData.value = GlobalModel.httpClient.value?.getCellData()
            MainModel.currentSimData.value = GlobalModel.httpClient.value?.getSimData()
        },
        {
            MainModel.currentCellData.value == null ||
                    MainModel.currentSimData.value == null
        },
        { AdvancedPage(it) }
    )
    object WifiConfig : Page(
        MR.strings.wifi_data,
        WIFI_PAGE_KEY,
        { painterResource(MR.images.wifi) },
        {
            MainModel.currentWifiData.value = GlobalModel.httpClient.value?.getWifiData()
        },
        { MainModel.currentWifiData.value == null },
        { WifiConfigPage(it) }
    )

    object SettingsPage : Page(
        MR.strings.settings,
        SETTINGS_PAGE_KEY,
        { rememberVectorPainter(Icons.Default.Settings) },
        null,
        null,
        { SettingsPage(it) },
    )

    object FuzzerPage : Page(
        MR.strings.fuzzer,
        FUZZER_PAGE_KEY,
        { rememberVectorPainter(Icons.Default.Lock) },
        null,
        null,
        { FuzzerPage(it) }
    )
}
