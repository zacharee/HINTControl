@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.BandConfigLayout
import dev.zwander.common.components.PageGrid
import dev.zwander.common.components.SSIDListLayout
import dev.zwander.common.model.MainModel
import dev.zwander.common.util.HTTPClient
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class ItemData(
    val title: StringResource,
    val render: @Composable (Modifier) -> Unit,
)

@Composable
@HiddenFromObjC
fun WifiConfigPage(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val data by MainModel.currentWifiData.collectAsState()

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
        PageGrid(
            items = items,
            modifier = Modifier.fillMaxWidth().weight(1f),
            renderItemTitle = {
                Text(
                    text = stringResource(it.title),
                )
            },
            renderItem = {
                it.render(Modifier.fillMaxWidth())
            },
        )

        Button(
            onClick = {
                scope.launch {
                    tempState?.let {
                        HTTPClient.setWifiData(it)
                    }
                    MainModel.currentWifiData.value = HTTPClient.getWifiData()
                }
            },
            enabled = tempState != data,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Text(
                text = stringResource(MR.strings.save)
            )
        }
    }
}
