package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.TextSwitch
import dev.zwander.common.model.SettingsModel
import dev.zwander.resources.common.MR

@Composable
fun AutoRefresh() {
    var enabled by SettingsModel.enableAutoRefresh.collectAsMutableState()
    var periodMs by SettingsModel.autoRefreshMs.collectAsMutableState()

    TextSwitch(
        text = stringResource(MR.strings.enabled),
        checked = enabled,
        onCheckedChange = { enabled = it },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.size(8.dp))

    TextFieldSetting(
        enabled = enabled,
        value = periodMs.toString(),
        onValueChange = { it.toLongOrNull()?.let { p -> periodMs = p } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        label = stringResource(MR.strings.period_ms),
    )
}
