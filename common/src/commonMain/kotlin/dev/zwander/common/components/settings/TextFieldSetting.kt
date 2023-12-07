package dev.zwander.common.components.settings

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.resources.common.MR

@Composable
fun TextFieldSetting(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: (@Composable () -> Unit)? = null,
) {
    val focusRequester = remember {
        FocusRequester()
    }

    var tempValue by remember(value) {
        mutableStateOf(value)
    }

    OutlinedTextField(
        value = tempValue,
        onValueChange = { tempValue = it },
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = keyboardOptions.copy(
            imeAction = ImeAction.Done,
        ),
        trailingIcon = {
            IconButton(
                onClick = {
                    onValueChange(tempValue)
                },
                enabled = tempValue != value,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(MR.strings.save),
                )
            }
        },
        label = label,
        enabled = enabled,
        keyboardActions = KeyboardActions(onDone = { focusRequester.freeFocus() }),
    )
}
