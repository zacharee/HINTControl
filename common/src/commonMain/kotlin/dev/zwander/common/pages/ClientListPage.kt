package dev.zwander.common.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import dev.zwander.common.components.FormatText
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.BaseClientData
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.resources.common.MR

private data class ClientListItem(
    val title: StringResource,
    val datas: List<BaseClientData>?,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClientListPage(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentClientData.collectAsState()

    val items = remember(data) {
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

    LazyVerticalStaggeredGrid(
        contentPadding = PaddingValues(8.dp),
        columns = AdaptiveMod(300.dp, items.size),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(items = items, key = { it.title }) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    ClientList(
                        title = stringResource(it.title),
                        datas = it.datas,
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientList(
    title: String,
    datas: List<BaseClientData>?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClientItem(
    data: BaseClientData,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = data.name?.takeIf { it.isNotBlank() } ?: data.mac.toString(),
            fontWeight = FontWeight.Bold,
        )

        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            FormatText(
                text = stringResource(MR.strings.connected),
                textFormat = data.connected.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.ipv4),
                textFormat = data.ipv4.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.ipv6),
                textFormat = data.ipv6?.joinToString("\n").toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.mac),
                textFormat = data.mac.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}
