package dev.zwander.common.widget

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import dev.zwander.common.model.GlobalModel
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch

class ConnectionStatusWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val httpClient = GlobalModel.httpClient.value
        val cellData = httpClient?.getCellData()
        val signalData = httpClient?.getMainData()?.signal

        provideContent {
            val scope = rememberCoroutineScope()

            GlanceTheme {
                AppWidgetColumn {
                    Button(
                        text = dev.icerock.moko.resources.compose.stringResource(MR.strings.refresh),
                        onClick = {
                            scope.launch {
                                update(context, id)
                            }
                        },
                    )

                    LazyColumn {
                        item {

                        }
                    }
                }
            }
        }
    }

}

class ConnectionStatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ConnectionStatusWidget()
}