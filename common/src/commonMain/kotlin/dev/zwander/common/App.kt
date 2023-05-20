@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
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
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalMaterial3Api::class)
@HiddenFromObjC
@Composable
fun App(
    modifier: Modifier = Modifier,
    windowInsets: PaddingValues = PaddingValues(0.dp),
) {
    val layoutDirection = LocalLayoutDirection.current

    Theme {
        Surface {
            val scope = rememberCoroutineScope()
            val token by UserModel.token.collectAsState()
            val isLoading by GlobalModel.isLoading.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            var currentPage by GlobalModel.currentPage.collectAsMutableState()
            var error by GlobalModel.httpError.collectAsMutableState()

            val pages = remember {
                listOf(
                    Page.Main,
                    Page.Clients,
                    Page.Advanced,
                    Page.WifiConfig,
                )
            }

            val okString = stringResource(MR.strings.ok)

            LaunchedEffect(error) {
                if (error != null) {
                    snackbarHostState.showSnackbar(
                        message = error!!,
                        duration = SnackbarDuration.Indefinite,
                        actionLabel = okString,
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

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                        .absolutePadding(
                            top = windowInsets.calculateTopPadding(),
                            left = windowInsets.calculateLeftPadding(layoutDirection),
                            right = windowInsets.calculateRightPadding(layoutDirection),
                        ),
                ) {
                    val constraints = this.constraints
                    val maxWidthDp = with(LocalDensity.current) { constraints.maxWidth.toDp() }

                    val sideRail = maxWidthDp >= 600.dp
                    val showBottomBar = token != null && !sideRail

                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = false,
                        onRefresh = {
                            scope.launch {
                                error = handleRefresh(currentPage)
                            }
                        }
                    )

                    Scaffold(
                        modifier = modifier.fillMaxSize()
                            .padding(
                                bottom = if (!showBottomBar) windowInsets.calculateBottomPadding() else 0.dp
                            ),
                        bottomBar = {
                            AnimatedVisibility(
                                visible = showBottomBar,
                                enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
                            ) {
                                NavBar(
                                    currentPage = currentPage,
                                    pages = pages,
                                    onPageChange = { currentPage = it },
                                    vertical = false,
                                    windowInsets = WindowInsets(bottom = windowInsets.calculateBottomPadding()),
                                )
                            }
                        },
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                            ) {
                                Snackbar(
                                    snackbarData = it,
                                    modifier = Modifier.fillMaxWidth(),
                                    actionOnNewLine = true,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    actionColor = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    ) { padding ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(padding),
                        ) {
                            AnimatedVisibility(
                                visible = token != null && sideRail,
                                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start),
                            ) {
                                NavBar(
                                    currentPage = currentPage,
                                    onPageChange = { currentPage = it },
                                    pages = pages,
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
                                    sideRail = sideRail,
                                    pageCount = pages.size,
                                    pages = pages,
                                    onPageChange = { currentPage = it },
                                    modifier = Modifier.widthIn(max = 1200.dp)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppView(
    pages: List<Page>,
    currentPage: Page,
    onPageChange: (Page) -> Unit,
    sideRail: Boolean,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Crossfade(
            targetState = sideRail || currentPage == Page.Login || Platform.isJvm,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (it) {
                Crossfade(
                    targetState = currentPage,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    page.render(Modifier.fillMaxSize())
                }
            } else {
                val state = rememberPagerState()

                LaunchedEffect(state.currentPage, state.isScrollInProgress) {
                    if (!state.isScrollInProgress) {
                        onPageChange(pages[state.currentPage])
                    }
                }

                LaunchedEffect(currentPage) {
                    state.animateScrollToPage(pages.indexOf(currentPage))
                }

                HorizontalPager(
                    pageCount = pageCount,
                    state = state,
                ) { page ->
                    pages[page].render(Modifier.fillMaxSize())
                }
            }
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
    pages: List<Page>,
    currentPage: Page,
    onPageChange: (Page) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = true,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
) {
    val scope = rememberCoroutineScope()

    var error by GlobalModel.httpError.collectAsMutableState()

    if (vertical) {
        NavigationRail(
            modifier = modifier.padding(vertical = 8.dp),
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
            windowInsets = windowInsets,
        ) {
            pages.forEach { page ->
                NavigationBarItem(
                    selected = currentPage == page,
                    onClick = { onPageChange(page) },
                    label = {
                        Text(
                            text = stringResource(page.titleRes),
                            softWrap = false,
                        )
                    },
                    icon = { Icon(imageVector = page.icon, contentDescription = null) },
                )
            }

            if (!Platform.isAndroid && !Platform.isIos && currentPage.refreshAction != null) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            error = handleRefresh(currentPage)
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(MR.strings.refresh),
                            softWrap = false,
                        )
                    },
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
