package dev.zwander.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.BaseClientData
import dev.zwander.resources.common.MR

@Composable
fun ClientDataLayout(
    modifier: Modifier = Modifier
) {
    val data by MainModel.currentClientData.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ClientList(
            title = stringResource(MR.strings.twoGig),
            datas = data?.clients?.twoGig,
            modifier = Modifier.fillMaxWidth(),
        )

        Divider()

        ClientList(
            title = stringResource(MR.strings.fiveGig),
            datas = data?.clients?.fiveGig,
            modifier = Modifier.fillMaxWidth(),
        )

        Divider()

        ClientList(
            title = stringResource(MR.strings.wired),
            datas = data?.clients?.ethernet,
            modifier = Modifier.fillMaxWidth(),
        )
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
            style = MaterialTheme.typography.titleMedium,
        )

        datas?.forEachIndexed { index, data ->
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
            )

            FormatText(
                text = stringResource(MR.strings.ipv4),
                textFormat = data.ipv4.toString(),
            )

            FormatText(
                text = stringResource(MR.strings.ipv6),
                textFormat = data.ipv6.toString(),
            )

            FormatText(
                text = stringResource(MR.strings.mac),
                textFormat = data.mac.toString(),
            )
        }
    }
}
