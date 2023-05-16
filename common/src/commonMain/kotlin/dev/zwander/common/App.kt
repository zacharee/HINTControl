package dev.zwander.common

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.pullrefresh.PullRefreshIndicator
import dev.zwander.common.components.pullrefresh.pullRefresh
import dev.zwander.common.components.pullrefresh.rememberPullRefreshState
import dev.zwander.common.data.Page
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.ui.Theme
import dev.zwander.resources.common.MR
import korlibs.memory.Platform
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
) {
    Theme {
        Surface {
            val scope = rememberCoroutineScope()
            val token by UserModel.token.collectAsState()
            val isLoading by GlobalModel.isLoading.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            var currentPage by GlobalModel.currentPage.collectAsMutableState()
            var error by GlobalModel.httpError.collectAsMutableState()

            LaunchedEffect(error) {
                if (error != null) {
                    snackbarHostState.showSnackbar(
                        message = error!!,
                        withDismissAction = true,
                    )
                }
            }

            LaunchedEffect(token) {
                if (currentPage == Page.Login && token != null) {
                    currentPage = Page.Main
                } else if (token == null) {
                    currentPage = Page.Login
                }
            }

            LaunchedEffect(currentPage) {
                if (currentPage.refreshAction != null && currentPage.needsRefresh?.invoke() == true) {
                    error = handleRefresh(currentPage)
                }
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
            ) {
                val constraints = this.constraints
                val maxWidthDp = with(LocalDensity.current) { constraints.maxWidth.toDp() }

                val sideRail = maxWidthDp >= 600.dp

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = false,
                    onRefresh = {
                        scope.launch {
                            error = handleRefresh(currentPage)
                        }
                    }
                )

                Scaffold(
                    modifier = modifier.fillMaxSize(),
                    bottomBar = {
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
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                        ) {
                            Snackbar(
                                snackbarData = it,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                ) { padding ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(padding),
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

                        Box(
                            modifier = Modifier.weight(1f).then(
                                if (!sideRail && currentPage.refreshAction != null) {
                                    Modifier.pullRefresh(
                                        state = pullRefreshState
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        ) {
                            AppView(
                                currentPage = currentPage,
                                modifier = Modifier.widthIn(max = 1000.dp)
                                    .fillMaxSize()
                                    .align(Alignment.TopCenter),
                            )

                            PullRefreshIndicator(
                                refreshing = false,
                                state = pullRefreshState,
                                modifier = Modifier.align(Alignment.TopCenter),
                            )
                        }
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
    Box(
        modifier = modifier,
    ) {
        Crossfade(
            targetState = currentPage,
            modifier = Modifier.fillMaxSize(),
        ) {
            it.render(Modifier.fillMaxSize())
        }
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
    val scope = rememberCoroutineScope()
    val pages = remember {
        listOf(
            Page.Main,
            Page.Clients,
            Page.Advanced,
            Page.WifiConfig,
        )
    }

    var error by GlobalModel.httpError.collectAsMutableState()

    if (vertical) {
        NavigationRail(
            modifier = modifier.padding(vertical = 16.dp),
        ) {
            pages.forEach { page ->
                NavigationRailItem(
                    selected = currentPage == page,
                    onClick = { onPageChange(page) },
                    label = { Text(text = stringResource(page.titleRes)) },
                    icon = { Icon(imageVector = page.icon, contentDescription = null) },
                )
            }

            if (currentPage.refreshAction != null) {
                NavigationRailItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            error = handleRefresh(currentPage)
                        }
                    },
                    label = { Text(text = stringResource(MR.strings.refresh)) },
                    icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = null) }
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

            if (!Platform.isAndroid && currentPage.refreshAction != null) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            error = handleRefresh(currentPage)
                        }
                    },
                    label = { Text(text = stringResource(MR.strings.refresh)) },
                    icon = { Icon(imageVector = Icons.Default.Refresh, contentDescription = null) }
                )
            }
        }
    }
}

private suspend fun handleRefresh(page: Page): String? {
    return try {
        page.refreshAction?.invoke()
        null
    } catch (e: Exception) {
        e.message
    }
}
