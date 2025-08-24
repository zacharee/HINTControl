package dev.zwander.common.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
import dev.zwander.common.util.Storage
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
        const val SAVED_DATA_PAGE_KEY = "saved_data_page"
        const val CLIENTS_PAGE_KEY = "clients_page"
        const val WIFI_PAGE_KEY = "wifi_page"
        const val SETTINGS_PAGE_KEY = "settings_page"
        const val FUZZER_PAGE_KEY = "fuzzer_page"

        fun pageFromKey(key: String): Page {
            return when (key) {
                LOG_IN_PAGE_KEY -> Login
                MAIN_PAGE_KEY -> Main
                SAVED_DATA_PAGE_KEY -> SavedData
                CLIENTS_PAGE_KEY -> Clients
                WIFI_PAGE_KEY -> WifiConfig
                SETTINGS_PAGE_KEY -> SettingsPage
                FUZZER_PAGE_KEY -> FuzzerPage
                else -> throw IllegalArgumentException("Unknown key $key")
            }
        }
    }

    data object Login : Page(
        titleRes = MR.strings.log_in,
        key = LOG_IN_PAGE_KEY,
        icon = { rememberVectorPainter(Icons.Default.Lock) },
        refreshAction = null,
        needsRefresh = { false },
        render = { LoginPage(it) },
    )
    data object Main : Page(
        titleRes = MR.strings.main_data,
        key = MAIN_PAGE_KEY,
        icon = { rememberVectorPainter(Icons.Default.Home) },
        refreshAction = {
            MainModel.currentCellData.value = GlobalModel.httpClient.value?.getCellData()
            MainModel.currentSimData.value = GlobalModel.httpClient.value?.getSimData()
            MainModel.currentMainData.value = GlobalModel.httpClient.value?.getMainData()

            Storage.makeSnapshot(
                snapshotTime = MainModel.currentMainData.timestampedValue.first,
                mainData = MainModel.currentMainData.value,
                simData = MainModel.currentSimData.value,
                cellData = MainModel.currentCellData.value,
            )
        },
        needsRefresh = {
            MainModel.currentCellData.value == null ||
                    MainModel.currentSimData.value == null ||
                    MainModel.currentMainData.value == null
        },
        render = { MainPage(it) },
    )
    
    data object SavedData : Page(
        titleRes = MR.strings.saved,
        key = SAVED_DATA_PAGE_KEY,
        icon = { rememberVectorPainter(Icons.Default.Add) },
        refreshAction = null,
        needsRefresh = { false },
        render = { SavedDataPage(it) },
    )
    data object Clients : Page(
        titleRes = MR.strings.client_data,
        key = CLIENTS_PAGE_KEY,
        icon = { rememberVectorPainter(Icons.AutoMirrored.Filled.List) },
        refreshAction = {
            MainModel.currentClientData.value = GlobalModel.httpClient.value?.getDeviceData()

            Storage.makeSnapshot(
                snapshotTime = MainModel.currentClientData.timestampedValue.first,
                clientData = MainModel.currentClientData.value,
            )
        },
        needsRefresh = { MainModel.currentClientData.value == null },
        render = { ClientListPage((it)) },
    )
    data object WifiConfig : Page(
        titleRes = MR.strings.wifi_data,
        key = WIFI_PAGE_KEY,
        icon = { painterResource(MR.images.wifi) },
        refreshAction = {
            MainModel.currentWifiData.value = GlobalModel.httpClient.value?.getWifiData()
        },
        needsRefresh = { MainModel.currentWifiData.value == null },
        render = { WifiConfigPage(it) },
    )

    data object SettingsPage : Page(
        titleRes = MR.strings.settings,
        key = SETTINGS_PAGE_KEY,
        icon = { rememberVectorPainter(Icons.Default.Settings) },
        refreshAction = null,
        needsRefresh = null,
        render = { SettingsPage(it) },
    )

    data object FuzzerPage : Page(
        titleRes = MR.strings.fuzzer,
        key = FUZZER_PAGE_KEY,
        icon = { rememberVectorPainter(Icons.Default.Lock) },
        refreshAction = null,
        needsRefresh = null,
        render = { FuzzerPage(it) },
    )
}
