package dev.zwander.common.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.BandConfigLayout
import dev.zwander.common.components.SSIDListLayout
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.common.util.HTTPClient
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch

private data class ItemData(
    val title: StringResource,
    val render: @Composable (Modifier) -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WifiConfigPage(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val data by MainModel.currentWifiData.collectAsState()
    val isLoading by GlobalModel.isLoading.collectAsState()

    var tempState by MainModel.tempWifiState.collectAsMutableState()

    LaunchedEffect(data) {
        tempState = data
    }

    val items = remember(data) {
        listOf(
            ItemData(
                title = MR.strings.band_mgmnt,
                render = {
                    BandConfigLayout(it)
                },
            ),
            ItemData(
                title = MR.strings.ssids,
                render = {
                    SSIDListLayout(it)
                },
            ),
        )
    }

    Column(
        modifier = modifier,
    ) {
        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(8.dp),
            columns = AdaptiveMod(300.dp, items.size),
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
        ) {
            items(items = items, key = { it.title }) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Text(
                            text = stringResource(it.title),
                            style = MaterialTheme.typography.titleMedium,
                        )

                        it.render(Modifier.fillMaxWidth())
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                scope.launch {
                    tempState?.let {
                        HTTPClient.setWifiData(it)
                    }
                    MainModel.currentWifiData.value = HTTPClient.getWifiData()
                }
            },
            enabled = tempState != data && !isLoading,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Text(
                text = stringResource(MR.strings.save)
            )
        }
    }
}
