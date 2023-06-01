package dev.zwander.android

import dev.zwander.common.App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !isSystemInDarkTheme()
                isAppearanceLightNavigationBars = isAppearanceLightStatusBars
            }

            App(
                modifier = Modifier.imePadding(),
                windowInsets = WindowInsets.systemBars
                    .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                    .asPaddingValues(),
            )
        }
    }
}
