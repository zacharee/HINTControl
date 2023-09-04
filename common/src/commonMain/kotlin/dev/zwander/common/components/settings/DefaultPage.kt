package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.LabeledDropdown
import dev.zwander.common.components.SelectableDropdownMenuItem
import dev.zwander.common.data.Page
import dev.zwander.common.model.SettingsModel
import dev.zwander.resources.common.MR

@Composable
fun ColumnScope.DefaultPage() {
    val validOptions = remember {
        listOf(
            Page.Main,
            Page.Clients,
            Page.WifiConfig,
        )
    }

    var selectedOption by SettingsModel.defaultPage.collectAsMutableState()
    var menuExpanded by remember(validOptions) {
        mutableStateOf(false)
    }

    LabeledDropdown(
        label = stringResource(MR.strings.default_page),
        expanded = menuExpanded,
        onExpandChange = { menuExpanded = it },
        modifier = Modifier.fillMaxWidth(),
        selectedValue = selectedOption,
        valueToString = { stringResource(selectedOption.titleRes) }
    ) {
        validOptions.forEach { option ->
            SelectableDropdownMenuItem(
                text = {
                    Text(text = stringResource(option.titleRes))
                },
                onClick = {
                    selectedOption = option
                    menuExpanded = false
                },
                isSelected = selectedOption == option,
            )
        }
    }
}
