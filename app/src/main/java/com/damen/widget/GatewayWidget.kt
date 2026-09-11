package com.damen.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
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
import java.net.HttpURLConnection
import java.net.URL

// pi web (damen-gateway) durum + toggle: siyah kare, beyaz W.
// ON = hazır, BOOT = açılıyor, OFF = kapalı. Dokun: başlat/durdur.
class GatewayWidget : GlanceAppWidget() {

    companion object {
        const val HEALTH_URL = "http://127.0.0.1:8787/api/health"

        // 2 = hazır, 1 = açılıyor/cevap veriyor, 0 = kapalı
        fun probe(): Int {
            return try {
                val c = (URL(HEALTH_URL).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 1500
                    readTimeout = 1500
                }
                val code = c.responseCode
                val body = try {
                    c.inputStream.bufferedReader().readText()
                } catch (_: Throwable) { "" }
                c.disconnect()
                if (code == 200 && body.contains("\"ready\":true")) 2 else 1
            } catch (_: Throwable) {
                0
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent(context)
        }
    }

    @Composable
    private fun WidgetContent(context: Context) {
        val state = probe()
        val dotColor = when (state) {
            2 -> Color(0xFFFF0000)
            1 -> Color(0xFF888888)
            else -> Color(0xFF3A3A3A)
        }
        val label = when (state) {
            2 -> "ON"
            1 -> "BOOT"
            else -> "OFF"
        }
        val textColor = if (state == 0) Color(0xFF888888) else Color(0xFFFFFFFF)
        val intent = Intent(context, GatewayToggleActivity::class.java)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF000000)))
                .cornerRadius(22.dp)
                .clickable(actionStartActivity(intent)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_gateway),
                    contentDescription = "pi web: $label",
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
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = label,
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
