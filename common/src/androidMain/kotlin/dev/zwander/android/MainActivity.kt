package dev.zwander.android

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.zwander.common.App
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.widget.ConnectionStatusWidgetReceiver

class MainActivity : AppCompatActivity() {
    private val appWidgetManager by lazy { getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContent {
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
        App.instance.cancelWidgetRefresh()

        if (appWidgetManager.getAppWidgetIds(ComponentName(this, ConnectionStatusWidgetReceiver::class.java)).isNotEmpty()) {
            App.instance.scheduleWidgetRefresh()
        }
    }
}
