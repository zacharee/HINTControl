package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
        )
    }

    Column(
        modifier = modifier,
    ) {
        InfoRow(
            items = items,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
