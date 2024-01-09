package dev.zwander.common.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    previousFocus: FocusRequester? = null,
    nextFocus: FocusRequester? = null,
    onEnter: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        modifier = modifier
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { evt ->
                when (evt.key) {
                    Key.Enter -> {
                        if (onEnter != null) {
                            onEnter()
                            true
                        } else {
                            false
                        }
                    }

                    Key.Tab -> {
                        when {
//                            evt.type == KeyEventType.KeyUp && evt.isShiftPressed && previousFocus != null -> {
//                                previousFocus.requestFocus()
//                                true
//                            }
                            evt.type == KeyEventType.KeyUp && !evt.isShiftPressed && nextFocus != null -> {
                                nextFocus.requestFocus()
                                true
                            }

                            else -> {
                                false
                            }
                        }
                    }

                    else -> {
                        false
                    }
                }
            },
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = keyboardType,
            capitalization = KeyboardCapitalization.None,
            imeAction = if (nextFocus != null) {
                ImeAction.Next
            } else {
                ImeAction.Go
            },
        ),
        label = label,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus(true)
            },
            onGo = {
                onEnter?.invoke()
                focusManager.clearFocus(true)
            },
            onNext = {
                nextFocus?.requestFocus()
            },
        ),
    )
}
