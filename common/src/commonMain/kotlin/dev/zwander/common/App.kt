package dev.zwander.common

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.Page
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.ui.Theme

@Composable
fun App(
    modifier: Modifier = Modifier,
) {
    Theme {
        Surface {
            val token by UserModel.token.collectAsState()
            val isLoading by GlobalModel.isLoading.collectAsState()

            var currentPage by GlobalModel.currentPage.collectAsMutableState()

            LaunchedEffect(token) {
                if (currentPage == Page.Login && token != null) {
                    currentPage = Page.Main
                } else if (token == null) {
                    currentPage = Page.Login
                }
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
            ) {
                val constraints = this.constraints
                val maxWidthDp = with(LocalDensity.current) { constraints.maxWidth.toDp() }

                val sideRail = maxWidthDp >= 600.dp

                Column(
                    modifier = modifier.fillMaxSize(),
                ) {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    ) {
                        AnimatedVisibility(
                            visible = token != null && sideRail,
                            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start),
                        ) {
                            NavBar(
                                currentPage = currentPage,
                                onPageChange = { currentPage = it },
                                vertical = true,
                            )
                        }

                        AppView(
                            currentPage = currentPage,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    AnimatedVisibility(
                        visible = token != null && !sideRail,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
                    ) {
                        NavBar(
                            currentPage = currentPage,
                            onPageChange = { currentPage = it },
                            vertical = false,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LoadingScrim(
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AppView(
    currentPage: Page,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = currentPage,
        modifier = modifier,
    ) {
        it.render(Modifier.fillMaxSize())
    }
}

@Composable
private fun LoadingScrim(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NavBar(
    currentPage: Page,
    onPageChange: (Page) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = true,
) {
    val pages = remember {
        listOf(
            Page.Main,
            Page.Clients,
            Page.Advanced,
            Page.WifiConfig,
        )
    }

    if (vertical) {
        NavigationRail(
            modifier = modifier,
        ) {
            pages.forEach { page ->
                NavigationRailItem(
                    selected = currentPage == page,
                    onClick = { onPageChange(page) },
                    label = { Text(text = stringResource(page.titleRes)) },
                    icon = { Icon(imageVector = page.icon, contentDescription = null) },
                )
            }
        }
    } else {
        NavigationBar(
            modifier = modifier,
        ) {
            pages.forEach { page ->
                NavigationBarItem(
                    selected = currentPage == page,
                    onClick = { onPageChange(page) },
                    label = { Text(text = stringResource(page.titleRes)) },
                    icon = { Icon(imageVector = page.icon, contentDescription = null) },
                )
            }
        }
    }
}
