package dev.zwander.common

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.Page
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel

@Composable
fun App() {
    MaterialTheme {
        Surface {
            val token by UserModel.token.collectAsState()

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
                    modifier = Modifier.fillMaxSize(),
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

    val isLoading by GlobalModel.isLoading.collectAsState()

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
                    enabled = !isLoading,
                )
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically) + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically) + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally),
                modifier = Modifier.padding(8.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
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
                    enabled = !isLoading,
                )
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically) + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically) + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally),
                modifier = Modifier.align(Alignment.CenterVertically).padding(8.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
