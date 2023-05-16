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
import dev.zwander.common.components.TextSwitch
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
    var data by MainModel.currentWifiData.collectAsMutableState()

    var isLoading by GlobalModel.isLoading.collectAsMutableState()

    var tempState by remember(data) {
        mutableStateOf(data)
    }

    LaunchedEffect(null) {
        data = HTTPClient.getWifiData()
    }

    val items = remember {
        listOf(
            ItemData(
                title = MR.strings.band_mgmnt,
                render = {
                    Column(
                        modifier = it,
                    ) {
                        TextSwitch(
                            text = stringResource(MR.strings.twoGig_radio),
                            checked = tempState?.twoGig?.isRadioEnabled ?: false,
                            onCheckedChange = { checked ->
                                tempState = tempState?.copy(
                                    twoGig = tempState?.twoGig?.copy(
                                        isRadioEnabled = checked
                                    )
                                )
                            },
                            enabled = !isLoading,
                        )

                        TextSwitch(
                            text = stringResource(MR.strings.fiveGig_radio),
                            checked = tempState?.fiveGig?.isRadioEnabled ?: false,
                            onCheckedChange = { checked ->
                                tempState = tempState?.copy(
                                    fiveGig = tempState?.fiveGig?.copy(
                                        isRadioEnabled = checked
                                    )
                                )
                            },
                            enabled = !isLoading,
                        )

                        TextSwitch(
                            text = stringResource(MR.strings.band_steering),
                            checked = tempState?.bandSteering?.isEnabled ?: false,
                            onCheckedChange = { checked ->
                                tempState = tempState?.copy(
                                    bandSteering = tempState?.bandSteering?.copy(
                                        isEnabled = checked
                                    )
                                )
                            },
                            enabled = !isLoading,
                        )
                    }
                }
            )
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
                    isLoading = true
                    tempState?.let {
                        HTTPClient.setWifiData(it)
                    }
                    MainModel.currentWifiData.value = HTTPClient.getWifiData()
                    isLoading = false
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
