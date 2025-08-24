@file:OptIn(ExperimentalObjCRefinement::class)
@file:Suppress("INVISIBLE_MEMBER")

package dev.zwander.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.pullrefresh.PullRefreshIndicator
import dev.zwander.common.components.pullrefresh.pullRefresh
import dev.zwander.common.components.pullrefresh.rememberPullRefreshState
import dev.zwander.common.data.Page
import dev.zwander.common.exceptions.TooManyAttemptsException
import dev.zwander.common.exceptions.UnauthorizedException
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.ui.LayoutMode
import dev.zwander.common.ui.LocalLayoutMode
import dev.zwander.common.ui.Theme
import dev.zwander.common.util.Storage
import dev.zwander.common.util.invoke
import dev.zwander.compose.alertdialog.InWindowAlertDialog
import dev.zwander.resources.common.MR
import korlibs.platform.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@HiddenFromObjC
@Composable
fun App(
    modifier: Modifier = Modifier,
    fullPadding: PaddingValues = PaddingValues(0.dp),
) {
    val scope = rememberCoroutineScope()

    val isBlocking by GlobalModel.isBlocking.collectAsState()
    val isLoading by GlobalModel.isLoading.collectAsState()
    val autoRefresh by SettingsModel.enableAutoRefresh.collectAsState()
    val autoRefreshMs by SettingsModel.autoRefreshMs.collectAsState()
    val isLoggedIn by UserModel.isLoggedIn.collectAsState(false)
    val username by UserModel.username.collectAsState()
    val password by UserModel.password.collectAsState()

    var currentPage by GlobalModel.currentPage.collectAsMutableState()
    val error by GlobalModel.httpError.collectAsState()

    val fuzzerEnabled by SettingsModel.fuzzerEnabled.collectAsState()

    LaunchedEffect(autoRefresh, currentPage) {
        while (autoRefresh && currentPage.refreshAction != null) {
            delay(autoRefreshMs)
            handleRefresh(currentPage)
        }
    }

    LaunchedEffect(null) {
        if (username.isNotBlank() && !password.isNullOrBlank()) {
            GlobalModel.updateClient()
        }
    }

    LaunchedEffect(null) {
        launch(Dispatchers.IO) {
            Storage.migrateSnapshotsIfNeeded()
        }
    }

    Theme {
        Surface(
            modifier = Modifier.onPreviewKeyEvent {
                if (it.key == Key.R && !isBlocking) {
                    if (((Platform.isMac || Platform.isIos) && it.isMetaPressed) ||
                        (!(Platform.isMac || Platform.isIos) && it.isCtrlPressed)
                    ) {
                        scope.launch {
                            handleRefresh(currentPage)
                        }
                    }
                }
                false
            },
        ) {
            val pages = remember(fuzzerEnabled) {
                listOf(
                    Page.Main,
                    Page.SavedData,
                    Page.Clients,
                    Page.WifiConfig,
                    Page.SettingsPage,
                ) + if (fuzzerEnabled) {
                    listOf(Page.FuzzerPage)
                } else {
                    listOf()
                }
            }

            LaunchedEffect(isLoggedIn) {
                if (currentPage == Page.Login && isLoggedIn) {
                    currentPage = SettingsModel.defaultPage.value
                } else if (!isLoggedIn) {
                    currentPage = Page.Login
                }
            }

            LaunchedEffect(currentPage) {
                if (currentPage.refreshAction != null && (currentPage.needsRefresh?.invoke() == true || autoRefresh)) {
                    handleRefresh(currentPage)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                        .padding(fullPadding),
                ) {
                    val constraints = this.constraints
                    val maxWidthDp = with(LocalDensity.current) { constraints.maxWidth.toDp() }

                    val sideRail = maxWidthDp >= 600.dp
                    val showBottomBar = isLoggedIn && !sideRail

                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = isLoading,
                        onRefresh = {
                            scope.launch {
                                handleRefresh(currentPage)
                            }
                        }
                    )

                    val fabVisible =
                        !sideRail && !Platform.isAndroid && !Platform.isIos && currentPage.refreshAction != null

                    CompositionLocalProvider(
                        LocalLayoutMode provides if (sideRail) LayoutMode.SIDE_RAIL else LayoutMode.BOTTOM_BAR,
                    ) {
                        Scaffold(
                            modifier = modifier.fillMaxSize(),
                            bottomBar = {
                                AnimatedVisibility(
                                    visible = showBottomBar,
                                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
                                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    NavBar(
                                        currentPage = currentPage,
                                        pages = pages,
                                        onPageChange = { currentPage = it },
                                        vertical = false,
                                    )
                                }
                            },
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        ) { padding ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(padding),
                            ) {
                                AnimatedVisibility(
                                    visible = isLoggedIn && sideRail,
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
                                        pages = pages,
                                        onPageChange = { currentPage = it },
                                        modifier = Modifier
                                            .widthIn(max = 1200.dp)
                                            .fillMaxSize()
                                            .align(Alignment.TopCenter),
                                    )

                                    PullRefreshIndicator(
                                        refreshing = isLoading,
                                        state = pullRefreshState,
                                        modifier = Modifier.align(Alignment.TopCenter),
                                    )

                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = fabVisible,
                                        enter = fadeIn() + scaleIn(),
                                        exit = fadeOut() + scaleOut(),
                                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
                                    ) {
                                        FloatingActionButton(
                                            onClick = {
                                                scope.launch {
                                                    handleRefresh(currentPage)
                                                }
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = stringResource(MR.strings.refresh),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isBlocking,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LoadingScrim(
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        InWindowAlertDialog(
            showing = error != null,
            title = {
                Text(
                    text = stringResource(MR.strings.error)
                )
            },
            text = {
                var localError by remember {
                    mutableStateOf("")
                }

                LaunchedEffect(error) {
                    localError = when (error) {
                        is UnauthorizedException -> MR.strings.unauthorized_error_text()
                        is TooManyAttemptsException -> MR.strings.too_many_attempts_text()
                        else -> error?.message ?: localError
                    }
                }

                SelectionContainer {
                    Text(text = localError)
                }
            },
            onDismissRequest = { GlobalModel.updateHttpError(null) },
            buttons = {
                TextButton(
                    onClick = { GlobalModel.updateHttpError(null) },
                ) {
                    Text(text = stringResource(MR.strings.ok))
                }
            },
        )
    }
}

private sealed class PageTransitionMode {
    data object Login : PageTransitionMode()
    data object Vertical : PageTransitionMode()
    data class Horizontal(val userScrollEnabled: Boolean) : PageTransitionMode()
}

@Composable
private fun AppView(
    pages: List<Page>,
    currentPage: Page,
    onPageChange: (Page) -> Unit,
    sideRail: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Crossfade(
            targetState = when {
                currentPage == Page.Login -> PageTransitionMode.Login
                sideRail -> PageTransitionMode.Vertical
                !sideRail && Platform.isJvm -> PageTransitionMode.Horizontal(false)
                else -> PageTransitionMode.Horizontal(true)
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            val state = rememberPagerState(
                pages.indexOf(SettingsModel.defaultPage.value)
            ) {
                pages.size
            }

            if (currentPage != Page.Login) {
                LaunchedEffect(currentPage) {
                    if (pages[state.targetPage] != currentPage) {
                        state.animateScrollToPage(pages.indexOf(currentPage))
                    }
                }

                LaunchedEffect(state.targetPage) {
                    onPageChange(pages[state.targetPage])
                }

                LaunchedEffect(LocalWindowInfo.current.containerSize) {
                    state.scrollToPage(pages.indexOf(currentPage), 0f)
                }
            }

            when (it) {
                PageTransitionMode.Login -> {
                    currentPage.render(
                        Modifier.fillMaxSize()
                            .then(
                                if (Platform.isIos) {
                                    Modifier.imePadding()
                                } else {
                                    Modifier
                                }
                            ),
                    )
                }

                PageTransitionMode.Vertical -> {
                    VerticalPager(
                        state = state,
                        userScrollEnabled = false,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        pages[page].render(Modifier.fillMaxSize())
                    }
                }

                is PageTransitionMode.Horizontal -> {
                    HorizontalPager(
                        state = state,
                        userScrollEnabled = it.userScrollEnabled,
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
) {
    val scope = rememberCoroutineScope()

    if (vertical) {
        NavigationRail(
            modifier = modifier.padding(vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            windowInsets = NavigationRailDefaults.windowInsets.only(WindowInsetsSides.Vertical),
        ) {
            pages.forEach { page ->
                NavigationRailItem(
                    selected = currentPage == page,
                    onClick = { 
                        onPageChange(page) 
                    },
                    label = { Text(text = stringResource(page.titleRes)) },
                    icon = { Icon(painter = page.icon(), contentDescription = null) },
                )
            }

            AnimatedVisibility(
                currentPage.refreshAction != null,
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
            ) {
                NavigationRailItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            handleRefresh(currentPage)
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
            windowInsets = NavigationBarDefaults.windowInsets,
        ) {
            pages.forEach { page ->
                NavigationBarItem(
                    selected = currentPage == page,
                    onClick = { 
                        onPageChange(page) 
                    },
                    label = {
                        Text(
                            text = stringResource(page.titleRes),
                            softWrap = false,
                        )
                    },
                    icon = { Icon(painter = page.icon(), contentDescription = null) },
                    alwaysShowLabel = pages.size < 6,
                )
            }
        }
    }
}

private suspend fun handleRefresh(page: Page) {
    val isBlocking = GlobalModel.isBlocking.value
    val isRefreshing = GlobalModel.isLoading.value

    if (!isBlocking && !isRefreshing) {
        page.refreshAction?.invoke()
    }
}
