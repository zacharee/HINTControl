package dev.zwander.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainDataLayout(
    modifier: Modifier = Modifier
) {
    val data by MainModel.currentMainData.collectAsState()

    Column(
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FormatText(
                text = stringResource(MR.strings.apn),
                textFormat = data?.signal?.generic?.apn.toString(),
            )

            FormatText(
                text = stringResource(MR.strings.ipv6),
                textFormat = data?.signal?.generic?.hasIPv6.toString(),
            )

            FormatText(
                text = stringResource(MR.strings.registration),
                textFormat = data?.signal?.generic?.registration.toString(),
            )

            FormatText(
                text = stringResource(MR.strings.roaming),
                textFormat = data?.signal?.generic?.registration.toString(),
            )
        }
    }
}
