package dev.zwander.common.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dev.icerock.moko.resources.StringResource
import dev.zwander.android.MainActivity
import dev.zwander.common.App
import dev.zwander.common.R
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.resources.common.MR
import kotlinx.coroutines.launch

class ConnectionStatusWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val httpClient = GlobalModel.updateClient()
        httpClient?.logIn(UserModel.username.value, UserModel.password.value ?: "", true)
        val cellData = httpClient?.getCellData()
        val signalData = httpClient?.getMainData()?.signal

        provideContent {
            val scope = rememberCoroutineScope()

            CompositionLocalProvider(
                LocalContext provides context,
            ) {
                GlanceTheme {
                    AppWidgetColumn(
                        modifier = GlanceModifier.clickable(
                            actionStartActivity(ComponentName(context, MainActivity::class.java))
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = dev.icerock.moko.resources.compose.stringResource(MR.strings.connection),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onBackground,
                                    fontSize = 14.sp,
                                ),
                                modifier = GlanceModifier.defaultWeight(),
                            )

                            Box(
                                modifier = GlanceModifier.size(32.dp)
                                    .cornerRadius(16.dp),
                            ) {
                                Image(
                                    provider = ImageProvider(R.drawable.refresh),
                                    contentDescription = dev.icerock.moko.resources.compose.stringResource(MR.strings.refresh),
                                    modifier = GlanceModifier.clickable {
                                        scope.launch {
                                            update(context, id)
                                        }
                                    }.padding(6.dp),
                                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                                )
                            }
                        }

                        Spacer(GlanceModifier.defaultWeight())

                        LazyColumn(
                            modifier = GlanceModifier.fillMaxWidth(),
                        ) {
                            item {
                                ConnectionInfoItem(
                                    data = signalData?.fourG,
                                    advancedData = cellData?.cell?.fourG,
                                    modifier = GlanceModifier.fillMaxWidth(),
                                )
                            }

                            item {
                                Spacer(GlanceModifier.size(4.dp))
                            }

                            item {
                                ConnectionInfoItem(
                                    data = signalData?.fiveG,
                                    advancedData = cellData?.cell?.fiveG,
                                    modifier = GlanceModifier.fillMaxWidth(),
                                )
                            }
                        }

                        Spacer(GlanceModifier.defaultWeight())
                    }
                }
            }
        }
    }

    @Composable
    private fun ConnectionInfoItem(
        data: BaseCellData?,
        advancedData: BaseAdvancedData?,
        modifier: GlanceModifier = GlanceModifier,
    ) {
        val context = androidx.glance.LocalContext.current

        Box(
            modifier = modifier.then(
                GlanceModifier.cornerRadius(8.dp)
            ).clickable(
                actionStartActivity(ComponentName(context, MainActivity::class.java))
            ),
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(GlanceTheme.colors.primaryContainer)
                    .padding(4.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                if (data != null || advancedData != null) {
                    data?.bands?.let { bands ->
                        TwoRowText(
                            label = MR.strings.bands,
                            value = bands.joinToString(", "),
                        )
                    }

                    data?.rsrp?.let {
                        TwoRowText(
                            label = MR.strings.rsrp,
                            value = it.toString(),
                        )
                    }

                    data?.rsrq?.let {
                        TwoRowText(
                            label = MR.strings.rsrq,
                            value = it.toString(),
                        )
                    }

                    advancedData?.bandwidth?.let {
                        TwoRowText(
                            label = MR.strings.bandwidth,
                            value = it,
                        )
                    }
                } else {
                    Text(
                        text = dev.icerock.moko.resources.compose.stringResource(MR.strings.not_connected),
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimaryContainer,
                        ),
                    )
                }
            }
        }
    }

    @Composable
    private fun RowScope.TwoRowText(
        label: StringResource,
        value: String,
        modifier: GlanceModifier = GlanceModifier.defaultWeight(),
        textColor: ColorProvider = GlanceTheme.colors.onPrimaryContainer,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = textColor,
                ),
                maxLines = 1,
            )
            Text(
                text = dev.icerock.moko.resources.compose.stringResource(label),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = textColor,
                ),
                maxLines = 1,
            )
        }
    }
}

class ConnectionStatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ConnectionStatusWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        App.instance.scheduleWidgetRefresh()
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        App.instance.cancelWidgetRefresh()
    }
}