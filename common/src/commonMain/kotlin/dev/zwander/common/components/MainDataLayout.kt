package dev.zwander.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainDataLayout(
    modifier: Modifier = Modifier
) {
    val data by MainModel.currentMainData.collectAsState()

    val items = remember(data) {
        listOf(
            MR.strings.apn to data?.signal?.generic?.apn,
            MR.strings.ipv6 to data?.signal?.generic?.hasIPv6,
            MR.strings.registration to data?.signal?.generic?.registration,
            MR.strings.roaming to data?.signal?.generic?.roaming,
        )
    }

    Column(
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            items.forEach { (labelRes, value) ->
                FormatText(
                    text = stringResource(labelRes),
                    textFormat = value.toString(),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}
