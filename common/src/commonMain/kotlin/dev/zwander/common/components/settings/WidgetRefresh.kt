package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.SettingsModel
import dev.zwander.resources.common.MR

@Composable
fun WidgetRefresh() {
    var widgetRefreshMs by SettingsModel.widgetRefresh.collectAsMutableState()

    TextFieldSetting(
        value = widgetRefreshMs.toString(),
        onValueChange = { it.toLongOrNull()?.let { p -> widgetRefreshMs = p } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = stringResource(MR.strings.period_ms))
        },
    )
}
