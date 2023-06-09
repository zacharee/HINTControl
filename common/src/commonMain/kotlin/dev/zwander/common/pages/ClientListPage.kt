@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.InfoRow
import dev.zwander.common.components.PageGrid
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.BaseClientData
import dev.zwander.common.util.filterBlanks
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class ClientListItem(
    val title: StringResource,
    val datas: List<BaseClientData>?,
)

@Composable
@HiddenFromObjC
fun ClientListPage(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentClientData.collectAsState()

    val items: List<ClientListItem> = remember(data) {
        listOf(
            ClientListItem(
                title = MR.strings.twoGig,
                datas = data?.clients?.twoGig,
            ),
            ClientListItem(
                title = MR.strings.fiveGig,
                datas = data?.clients?.fiveGig,
            ),
            ClientListItem(
                title = MR.strings.wired,
                datas = data?.clients?.ethernet,
            ),
        )
    }

    PageGrid(
        items = items,
        modifier = modifier,
        renderItemTitle = {
            Text(
                text = stringResource(it.title),
            )
        },
        renderItem = {
            ClientList(
                datas = it.datas,
            )
        },
    )
}

@Composable
@HiddenFromObjC
private fun ClientList(
    datas: List<BaseClientData>?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        if (!datas.isNullOrEmpty()) {
            datas.forEachIndexed { index, data ->
                ClientItem(
                    data = data,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (index < datas.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        } else {
            Text(
                text = stringResource(MR.strings.no_clients),
            )
        }
    }
}

@Composable
@HiddenFromObjC
private fun ClientItem(
    data: BaseClientData,
    modifier: Modifier = Modifier,
) {
    val items = remember(data) {
        listOf(
            MR.strings.connected to data.connected,
            MR.strings.ipv4 to data.ipv4,
            MR.strings.ipv6 to data.ipv6?.joinToString(" • "),
            MR.strings.mac to data.mac,
        ).filterBlanks()
    }

    Column(modifier = modifier) {
        Text(
            text = data.name?.takeIf { it.isNotBlank() } ?: data.mac.toString(),
            fontWeight = FontWeight.Bold,
        )

        InfoRow(
            items = items,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
