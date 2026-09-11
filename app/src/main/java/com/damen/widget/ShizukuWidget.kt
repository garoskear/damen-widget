package com.damen.widget

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import rikka.shizuku.Shizuku

// Nothing OS style durum widget'ı: siyah kare, beyaz S, LED + yazı.
// Dokunmak yalnızca durumu tazeler (hiçbir uygulama açılmaz).
class ShizukuWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent(context)
        }
    }

    @Composable
    private fun WidgetContent(context: Context) {
        val running = try {
            Shizuku.pingBinder()
        } catch (_: Throwable) {
            false
        }

        val dotColor = if (running) Color(0xFFFF0000) else Color(0xFF3A3A3A)
        val stateText = if (running) "RUNNING" else "STOPPED"
        val textColor = if (running) Color(0xFFFFFFFF) else Color(0xFF888888)
        val showLabel = LocalSize.current.width >= 110.dp
        val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF000000)))
                .cornerRadius(22.dp)
                .clickable(actionRunCallback<ShizukuRefreshAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_shizuku),
                    contentDescription = "Shizuku: $stateText",
                    modifier = GlanceModifier.size(30.dp)
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = GlanceModifier
                            .size(7.dp)
                            .background(ColorProvider(dotColor))
                            .cornerRadius(4.dp)
                    ) {}
                    if (showLabel) {
                        Spacer(modifier = GlanceModifier.width(6.dp))
                        Text(
                            text = stateText,
                            style = TextStyle(
                                color = ColorProvider(textColor),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                Text(
                    text = time,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF555555)),
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

// Widget'a dokununca: uygulamayı açmadan durumu tazele.
class ShizukuRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val running = try { Shizuku.pingBinder() } catch (_: Throwable) { false }
            ShizukuWidget().updateAll(context)
        } catch (t: Throwable) {
        }
    }
}
