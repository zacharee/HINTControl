@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import dev.zwander.common.components.FormField
import dev.zwander.common.components.TextSwitch
import dev.zwander.common.components.dialog.AlertDialogDef
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.util.BULLET
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun LoginPage(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val gatewayFocusRequester = remember { FocusRequester() }
    val userFocusRequester = remember { FocusRequester() }
    val passFocusRequester = remember { FocusRequester() }
    val loginInteractionSource = remember { MutableInteractionSource() }

    val username by UserModel.username.collectAsState()
    val password by UserModel.password.collectAsState()
    var gatewayIp by SettingsModel.gatewayIp.collectAsMutableState()

    var usernameTemp by remember {
        mutableStateOf(username)
    }

    var passwordTemp by remember {
        mutableStateOf(password)
    }

    var gatewayIpTemp by remember {
        mutableStateOf(gatewayIp)
    }

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
    var advanced by remember {
        mutableStateOf(false)
    }

    suspend fun performLogin() {
        gatewayIp = gatewayIpTemp

        var actualClient = client

        if (actualClient == null) {
            actualClient = GlobalModel.updateClient()
        }
        error = null
        focusManager.clearFocus()
        actualClient?.logIn(usernameTemp, passwordTemp ?: "", rememberCredentials)
    }

    LaunchedEffect(client) {
        if (usernameTemp.isNotBlank() && !passwordTemp.isNullOrBlank() && client != null) {
            performLogin()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(IntrinsicSize.Min)
                .verticalScroll(rememberScrollState()),
        ) {
            AnimatedVisibility(
                visible = advanced,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FormField(
                        value = gatewayIpTemp,
                        onValueChange = { gatewayIpTemp = it },
                        focusRequester = gatewayFocusRequester,
                        nextFocus = userFocusRequester,
                        isError = error != null,
                        label = {
                            Text(text = stringResource(MR.strings.gateway_address))
                        },
                        onEnter = {
                            scope.launch {
                                performLogin()
                            }
                        },
                    )

                    FormField(
                        value = usernameTemp,
                        onValueChange = { usernameTemp = it },
                        isError = error != null,
                        label = {
                            Text(text = stringResource(MR.strings.gateway_username))
                        },
                        focusRequester = userFocusRequester,
                        nextFocus = passFocusRequester,
                        previousFocus = gatewayFocusRequester,
                        onEnter = {
                            scope.launch {
                                performLogin()
                            }
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            FormField(
                value = passwordTemp ?: "",
                onValueChange = { passwordTemp = it },
                visualTransformation = if (showingPassword) {
                    VisualTransformation.None
                } else {
                    VisualTransformation {
                        TransformedText(
                            AnnotatedString(BULLET.repeat(it.text.length)),
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
                label = {
                    Text(text = stringResource(MR.strings.gateway_password))
                },
                focusRequester = passFocusRequester,
                previousFocus = if (advanced) userFocusRequester else null,
                onEnter = {
                    scope.launch {
                        performLogin()
                    }
                },
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.size(8.dp))

            TextButton(
                onClick = {
                    showingHelpDialog = true
                },
            ) {
                Text(text = stringResource(MR.strings.where_password))
            }

            Spacer(modifier = Modifier.size(8.dp))

            TextSwitch(
                text = stringResource(MR.strings.remember_credentials),
                checked = rememberCredentials,
                onCheckedChange = { rememberCredentials = it },
            )

            Spacer(modifier = Modifier.size(8.dp))

            TextSwitch(
                text = stringResource(MR.strings.advanced),
                checked = advanced,
                onCheckedChange = { advanced = it },
            )

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        performLogin()
                    }
                },
                enabled = usernameTemp.isNotBlank() && !passwordTemp.isNullOrBlank() && !isBlocking && !isLoggedIn,
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
