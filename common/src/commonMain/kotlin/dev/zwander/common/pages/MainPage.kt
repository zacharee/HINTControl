package dev.zwander.common.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.ClientDataLayout
import dev.zwander.common.components.MainDataLayout
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.common.util.HTTPClient
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch

private data class ItemInfo(
    val title: StringResource,
    val render: @Composable (Modifier) -> Unit,
    val refresh: suspend () -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(null) {
        retrieveAllInfo()
    }

    val scope = rememberCoroutineScope()

    val items = remember {
        listOf(
            ItemInfo(
                title = MR.strings.main_data,
                render = { MainDataLayout(it) },
                refresh = { MainModel.currentMainData.value = HTTPClient.getMainData() },
            ),
            ItemInfo(
                title = MR.strings.client_data,
                render = { ClientDataLayout(it) },
                refresh = { MainModel.currentClientData.value = HTTPClient.getDeviceData() },
            ),
            ItemInfo(
                title = MR.strings.wifi_data,
                render = {},
                refresh = { MainModel.currentWifiData.value = HTTPClient.getWifiData() },
            ),
        )
    }

    Box(
        modifier = modifier
    ) {
        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(8.dp),
            columns = AdaptiveMod(300.dp, items.size),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
        ) {
            items(items = items, key = { it.title }) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(it.title),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleLarge,
                            )

                            IconButton(
                                onClick = { scope.launch { it.refresh() } }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(MR.strings.refresh),
                                )
                            }
                        }

                        it.render(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

private suspend fun retrieveAllInfo() {
    with (HTTPClient) {
        MainModel.currentMainData.value = getMainData()
        MainModel.currentClientData.value = getDeviceData()
        MainModel.currentWifiData.value = getWifiData()
    }
}
