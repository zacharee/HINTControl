package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR

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
        ).filter { it.second != null }
    }

    Column(
        modifier = modifier,
    ) {
        if (items.isNotEmpty()) {
            InfoRow(
                items = items,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                text = stringResource(MR.strings.unavailable),
            )
        }
    }
}
