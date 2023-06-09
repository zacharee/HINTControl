@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.*
import dev.zwander.common.components.dialog.AlertDialogDef
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.util.filterBlanks
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class ItemInfo(
    val title: StringResource,
    val render: @Composable (Modifier) -> Unit,
    val titleAccessory: (@Composable (Modifier) -> Unit)? = null,
)

@Composable
@HiddenFromObjC
fun MainPage(
    modifier: Modifier = Modifier,
) {
    val httpClient by GlobalModel.httpClient.collectAsState()
    val scope = rememberCoroutineScope()

    val items = remember {
        listOf(
            ItemInfo(
                title = MR.strings.lte,
                render = {
                    val basicData by MainModel.currentMainData.collectAsState()
                    val advancedData by MainModel.currentCellData.collectAsState()

                    CellDataLayout(
                        data = basicData?.signal?.fourG,
                        advancedData = advancedData?.cell?.fourG,
                        expandedKey = "lte_cell_data_expanded",
                        modifier = it,
                    )
                },
                titleAccessory = {
                    val basicData by MainModel.currentMainData.collectAsState()

                    CellBars(
                        bars = basicData?.signal?.fourG?.bars?.toInt(),
                        modifier = it,
                    )
                },
            ),
            ItemInfo(
                title = MR.strings.five_g,
                render = {
                    val basicData by MainModel.currentMainData.collectAsState()
                    val advancedData by MainModel.currentCellData.collectAsState()

                    CellDataLayout(
                        data = basicData?.signal?.fiveG,
                        advancedData = advancedData?.cell?.fiveG,
                        expandedKey = "5g_cell_data_expanded",
                        modifier = it,
                    )
                },
                titleAccessory = {
                    val basicData by MainModel.currentMainData.collectAsState()

                    CellBars(
                        bars = basicData?.signal?.fiveG?.bars?.toInt(),
                        modifier = it,
                    )
                },
            ),
            ItemInfo(
                title = MR.strings.device,
                render = { DeviceDataLayout(it) }
            ),
            ItemInfo(
                title = MR.strings.general,
                render = { MainDataLayout(it) },
            ),
            ItemInfo(
                title = MR.strings.sim,
                render = {
                    val simData by MainModel.currentSimData.collectAsState()

                    val items = remember(simData) {
                        listOf(
                            MR.strings.iccid to simData?.sim?.iccId,
                            MR.strings.imei to simData?.sim?.imei,
                            MR.strings.imsi to simData?.sim?.imsi,
                            MR.strings.msisdn to simData?.sim?.msisdn,
                            MR.strings.status to simData?.sim?.status,
                        ).filterBlanks()
                    }

                    InfoRow(
                        items = items,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            ),
        )
    }

    var showingRebootConfirmation by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            PageGrid(
                items = items,
                modifier = Modifier.fillMaxWidth().weight(1f),
                renderItemTitle = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = stringResource(it.title),
                            modifier = Modifier.weight(1f),
                        )

                        it.titleAccessory?.invoke(Modifier.fillMaxHeight().aspectRatio(1f))
                    }
                },
                renderItem = {
                    it.render(Modifier.fillMaxWidth())
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            UserModel.logOut()
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(MR.strings.log_out),
                    )
                }

                Button(
                    onClick = {
                        showingRebootConfirmation = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.error),
                    ),
                ) {
                    Text(
                        text = stringResource(MR.strings.reboot),
                    )
                }
            }
        }
    }

    AlertDialogDef(
        showing = showingRebootConfirmation,
        onDismissRequest = { showingRebootConfirmation = false },
        title = {
            Text(
                text = stringResource(MR.strings.reboot),
            )
        },
        text = {
            Text(
                text = stringResource(MR.strings.reboot_confirmation),
            )
        },
        buttons = {
            TextButton(
                onClick = {
                    showingRebootConfirmation = false
                },
            ) {
                Text(
                    text = stringResource(MR.strings.no),
                )
            }

            TextButton(
                onClick = {
                    showingRebootConfirmation = false

                    scope.launch {
                        httpClient?.reboot()
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(
                    text = stringResource(MR.strings.yes),
                )
            }
        },
    )
}
