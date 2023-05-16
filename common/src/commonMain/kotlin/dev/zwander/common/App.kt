package dev.zwander.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
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

            Crossfade(
                targetState = currentPage,
                modifier = Modifier.fillMaxSize()
            ) {
                it.render(Modifier.fillMaxSize())
            }
        }
    }
}
