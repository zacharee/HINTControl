package dev.zwander.common.components.settings

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.dialog.InWindowAlertDialog
import dev.zwander.resources.common.MR

@Composable
fun TextFieldSetting(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
) {
    var showingDialog by remember {
        mutableStateOf(false)
    }
    var tempValue by remember(value) {
        mutableStateOf(value)
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        trailingIcon = {
            IconButton(
                onClick = {
                    showingDialog = true
                },
                enabled = enabled,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(MR.strings.edit),
                )
            }
        },
        label = label,
        singleLine = true,
        readOnly = true,
        enabled = enabled,
    )

    InWindowAlertDialog(
        showing = showingDialog,
        title = label,
        text = {
            OutlinedTextField(
                value = tempValue,
                onValueChange = { tempValue = it },
                keyboardOptions = keyboardOptions.copy(
                    imeAction = ImeAction.Send,
                ),
                label = label,
            )
        },
        buttons = {
            TextButton(
                onClick = {
                    showingDialog = false
                },
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            TextButton(
                onClick = {
                    showingDialog = false
                    onValueChange(tempValue)
                },
            ) {
                Text(text = stringResource(MR.strings.ok))
            }
        },
        onDismissRequest = { showingDialog = false },
    )
}
