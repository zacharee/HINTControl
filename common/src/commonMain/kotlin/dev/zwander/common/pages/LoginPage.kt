package dev.zwander.common.pages

import androidx.compose.animation.*
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.util.HTTPClient
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val userFocusRequester = remember { FocusRequester() }
    val passFocusRequester = remember { FocusRequester() }
    val loginInteractionSource = remember { MutableInteractionSource() }

    var username by UserModel.username.collectAsMutableState()
    var password by UserModel.password.collectAsMutableState()

    val isLoading by GlobalModel.isLoading.collectAsState()
    val token by UserModel.token.collectAsState()

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var showingPassword by remember {
        mutableStateOf(false)
    }

    fun performLogin() {
        error = null
        focusManager.clearFocus()
        scope.launch {
            try {
                HTTPClient.logIn(username ?: "", password ?: "")
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = username ?: "",
                onValueChange = { username = it.trim() },
                isError = error != null,
                modifier = Modifier.focusRequester(userFocusRequester)
                    .onKeyEvent {
                        when (it.key) {
                            Key.Enter -> {
                                performLogin()
                                true
                            }
                            Key.Tab -> {
                                if (!it.isShiftPressed) {
                                    passFocusRequester.requestFocus()
                                    true
                                } else {
                                    false
                                }
                            }
                            else -> {
                                false
                            }
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                ),
            )

            OutlinedTextField(
                value = password ?: "",
                onValueChange = { password = it.trim() },
                visualTransformation = if (showingPassword) {
                    VisualTransformation.None
                } else {
                    VisualTransformation {
                        TransformedText(
                            AnnotatedString("•".repeat(it.text.length)),
                            OffsetMapping.Identity,
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { showingPassword = !showingPassword },
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            painter = painterResource(if (showingPassword) MR.images.eye_off else MR.images.eye),
                            contentDescription = stringResource(if (showingPassword) MR.strings.hide_password else MR.strings.show_password),
                        )
                    }
                },
                isError = error != null,
                modifier = Modifier.focusRequester(passFocusRequester)
                    .onKeyEvent {
                        when (it.key) {
                            Key.Enter -> {
                                performLogin()
                                true
                            }
                            Key.Tab -> {
                                if (it.isShiftPressed) {
                                    userFocusRequester.requestFocus()
                                    true
                                } else {
                                    false
                                }
                            }
                            else -> {
                                false
                            }
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                ),
            )

            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                var localState by remember {
                    mutableStateOf<String?>(null)
                }

                LaunchedEffect(error) {
                    if (error != null) {
                        localState = error
                    }
                }

                Text(text = error ?: "")
            }

            Button(
                onClick = {
                    performLogin()
                },
                enabled = !username.isNullOrBlank() && !password.isNullOrBlank() && !isLoading && token == null,
                interactionSource = loginInteractionSource,
            ) {
                Text(
                    text = stringResource(MR.strings.log_in),
                )
            }
        }
    }
}
