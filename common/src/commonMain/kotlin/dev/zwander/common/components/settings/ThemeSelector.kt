package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.zwander.common.model.SettingsModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.LabeledDropdown
import dev.zwander.common.components.SelectableDropdownMenuItem
import dev.zwander.common.data.Theme
import dev.zwander.resources.common.MR

@Composable
fun ColumnScope.ThemeSelector() {
    var selectedOption by SettingsModel.theme.collectAsMutableState()
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    LabeledDropdown(
        label = stringResource(MR.strings.theme),
        expanded = menuExpanded,
        onExpandChange = { menuExpanded = it },
        modifier = Modifier.fillMaxWidth(),
        selectedValue = selectedOption,
        valueToString = { stringResource(selectedOption.label) },
    ) {
        Theme.entries.forEach { theme ->
            SelectableDropdownMenuItem(
                text = {
                    Text(text = stringResource(theme.label))
                },
                onClick = {
                    selectedOption = theme
                    menuExpanded = false
                },
                isSelected = selectedOption == theme,
            )
        }
    }
}
