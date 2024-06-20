package dev.zwander.common.components.settings

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.dialog.InWindowAlertDialog
import dev.zwander.resources.common.MR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldSetting(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
) {
    var showingDialog by remember {
        mutableStateOf(false)
    }
    var tempValue by remember(value) {
        mutableStateOf(value)
    }

    OutlinedTextFieldDefaults.DecorationBox(
        value = value,
        innerTextField = {
            Text(
                text = value,
                modifier = modifier,
            )
        },
        visualTransformation = VisualTransformation.None,
        singleLine = true,
        enabled = enabled,
        label = { Text(text = label) },
        interactionSource = remember {
            object : InteractionSource {
                override val interactions: Flow<Interaction> = emptyFlow()
            }
        },
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
    )

    InWindowAlertDialog(
        showing = showingDialog,
        title = { Text(text = label) },
        text = {
            OutlinedTextField(
                value = tempValue,
                onValueChange = { tempValue = it },
                keyboardOptions = keyboardOptions.copy(
                    imeAction = ImeAction.Send,
                ),
                label = { Text(text = label) },
                modifier = Modifier.fillMaxWidth(),
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
