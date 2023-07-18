package dev.zwander.android

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import dev.zwander.common.App
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.widget.ConnectionStatusWidgetReceiver

class MainActivity : AppCompatActivity() {
    private val appWidgetManager by lazy { getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !isSystemInDarkTheme()
                isAppearanceLightNavigationBars = isAppearanceLightStatusBars
            }

            val widgetRefresh by SettingsModel.widgetRefresh.collectAsState()

            LaunchedEffect(widgetRefresh) {
                updateWidgetRefresh()
            }

            App(
                modifier = Modifier.imePadding(),
            )
        }
    }

    private fun updateWidgetRefresh() {
        App.instance?.cancelWidgetRefresh()

        if (appWidgetManager.getAppWidgetIds(ComponentName(this, ConnectionStatusWidgetReceiver::class.java)).isNotEmpty()) {
            App.instance?.scheduleWidgetRefresh()
        }
    }
}
