@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
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
import dev.zwander.common.components.TextSwitch
import dev.zwander.common.components.dialog.AlertDialogDef
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@HiddenFromObjC
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

    val isBlocking by GlobalModel.isBlocking.collectAsState()
    val client by GlobalModel.httpClient.collectAsMutableState()
    val isLoggedIn by UserModel.isLoggedIn.collectAsState(false)

    var error by GlobalModel.httpError.collectAsMutableState()

    var showingPassword by remember {
        mutableStateOf(false)
    }
    var rememberCredentials by remember {
        mutableStateOf(true)
    }
    var showingHelpDialog by remember {
        mutableStateOf(false)
    }

    suspend fun performLogin() {
        var actualClient = client

        if (actualClient == null) {
            actualClient = GlobalModel.updateClient()
        }
        error = null
        focusManager.clearFocus()
        actualClient?.logIn(username, password ?: "", rememberCredentials)
    }

    LaunchedEffect(client) {
        if (username.isNotBlank() && !password.isNullOrBlank() && client != null) {
            performLogin()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.width(IntrinsicSize.Min),
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it.filterNot(Char::isWhitespace) },
                isError = error != null,
                modifier = Modifier.focusRequester(userFocusRequester)
                    .onKeyEvent {
                        when (it.key) {
                            Key.Enter -> {
                                scope.launch {
                                    performLogin()
                                }
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
                label = {
                    Text(text = stringResource(MR.strings.gateway_username))
                },
            )

            OutlinedTextField(
                value = password ?: "",
                onValueChange = { password = it.filterNot(Char::isWhitespace) },
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
                                scope.launch {
                                    performLogin()
                                }
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
                label = {
                    Text(text = stringResource(MR.strings.gateway_password))
                },
            )

            TextButton(
                onClick = {
                    showingHelpDialog = true
                },
            ) {
                Text(text = stringResource(MR.strings.where_password))
            }

            TextSwitch(
                text = stringResource(MR.strings.remember_credentials),
                checked = rememberCredentials,
                onCheckedChange = { rememberCredentials = it },
            )

            Button(
                onClick = {
                    scope.launch {
                        performLogin()
                    }
                },
                enabled = username.isNotBlank() && !password.isNullOrBlank() && !isBlocking && !isLoggedIn,
                interactionSource = loginInteractionSource,
            ) {
                Text(
                    text = stringResource(MR.strings.log_in),
                )
            }
        }
    }

    AlertDialogDef(
        showing = showingHelpDialog,
        onDismissRequest = { showingHelpDialog = false },
        title = {
            Text(text = stringResource(MR.strings.where_password))
        },
        text = {
            Text(text = stringResource(MR.strings.login_hint))
        },
        buttons = {
            TextButton(
                onClick = {
                    showingHelpDialog = false
                },
            ) {
                Text(text = stringResource(MR.strings.ok))
            }
        }
    )
}
