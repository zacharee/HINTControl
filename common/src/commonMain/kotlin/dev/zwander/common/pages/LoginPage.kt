package dev.zwander.common.pages

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    var username by UserModel.username.collectAsMutableState()
    var password by UserModel.password.collectAsMutableState()

    var isLoading by GlobalModel.isLoading.collectAsMutableState()

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var showingPassword by remember {
        mutableStateOf(false)
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
                onValueChange = { username = it },
                isError = error != null,
            )

            OutlinedTextField(
                value = password ?: "",
                onValueChange = { password = it },
                visualTransformation = if (showingPassword) {
                    VisualTransformation.None
                } else {
                    VisualTransformation {
                        TransformedText(
                            AnnotatedString("*".repeat(it.text.length)),
                            OffsetMapping.Identity,
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = { showingPassword = !showingPassword }
                    ) {
                        Icon(
                            painter = painterResource(if (showingPassword) MR.images.eye_off else MR.images.eye),
                            contentDescription = stringResource(if (showingPassword) MR.strings.hide_password else MR.strings.show_password),
                        )
                    }
                },
                isError = error != null,
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
                    error = null
                    scope.launch {
                        isLoading = true
                        error = HTTPClient.logIn(username ?: "", password ?: "")
                        isLoading = false
                    }
                },
                enabled = !username.isNullOrBlank() && !password.isNullOrBlank(),
            ) {
                Text(
                    text = stringResource(MR.strings.log_in),
                )
            }
        }
    }
}
