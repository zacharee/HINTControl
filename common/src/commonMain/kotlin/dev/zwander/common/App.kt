@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@HiddenFromObjC
@Composable
fun App(
    modifier: Modifier = Modifier,
    windowInsets: PaddingValues = PaddingValues(0.dp),
) {
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    val isLoading by GlobalModel.isLoading.collectAsState()

    var currentPage by GlobalModel.currentPage.collectAsMutableState()

    Theme {
        Surface(
            modifier = Modifier.onPreviewKeyEvent {
                if (it.key == Key.R && !isLoading) {
                    if (((Platform.isMac || Platform.isIos) && it.isMetaPressed) ||
                        (!(Platform.isMac || Platform.isIos) && it.isCtrlPressed)) {
                        scope.launch {
                            if (!isLoading) {
                                currentPage.refreshAction?.invoke()
                            }
                        }
                    }
                }
                false
            }
        ) {
            val token by UserModel.token.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

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
                                modifier = Modifier
                                    .weight(1f)
                                    .then(
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
                                    modifier = Modifier
                                        .widthIn(max = 1200.dp)
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

private enum class CrossfadeState {
    LOGIN,
    VERTICAL,
    CROSSFADE,
    HORIZONTAL,
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
            targetState = when {
                currentPage == Page.Login -> CrossfadeState.LOGIN
                sideRail && !Platform.isJvm -> CrossfadeState.VERTICAL
                sideRail && Platform.isJvm -> CrossfadeState.CROSSFADE
                else -> CrossfadeState.HORIZONTAL
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            val state = rememberPagerState()

            if (currentPage != Page.Login) {
                LaunchedEffect(state.currentPage, state.isScrollInProgress) {
                    if (!state.isScrollInProgress) {
                        onPageChange(pages[state.currentPage])
                    }
                }

                LaunchedEffect(currentPage) {
                    state.animateScrollToPage(pages.indexOf(currentPage))
                }
            }

            when (it) {
                CrossfadeState.LOGIN -> {
                    currentPage.render(Modifier.fillMaxSize())
                }
                CrossfadeState.VERTICAL -> {
                    VerticalPager(
                        pageCount = pageCount,
                        state = state,
                        userScrollEnabled = false,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        pages[page].render(Modifier.fillMaxSize())
                    }
                }
                CrossfadeState.CROSSFADE -> {
                    Crossfade(
                        targetState = currentPage,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        page.render(Modifier.fillMaxSize())
                    }
                }
                CrossfadeState.HORIZONTAL -> {
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
