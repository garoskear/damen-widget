package com.damen.widget

import android.content.Context
import android.media.AudioManager
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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
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

// Zil durum widget'ı: siyah kare, beyaz ikon, LED + yazı.
// Dokunmak yalnızca durumu tazeler (mod değiştirmez).
class RingerWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(BoxS, BoxM, BoxL))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent(context)
        }
    }

    @Composable
    private fun WidgetContent(context: Context) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Read from SharedPrefs first (instant), fallback to AudioManager
        val storedMode = prefs.getInt("ringer_mode", -1)
        val currentMode = if (storedMode != -1) storedMode else am.ringerMode
        val isVibrate = currentMode == AudioManager.RINGER_MODE_VIBRATE

        val iconRes = if (isVibrate) R.drawable.ic_vibrate else R.drawable.ic_bell
        val label = if (isVibrate) "VIB" else "RING"
        val dotColor = if (isVibrate) Color(0xFFFF0000) else Color(0xFF3A3A3A)
        val textColor = if (isVibrate) Color(0xFFFFFFFF) else Color(0xFF888888)
        val showLabel = LocalSize.current.width >= 110.dp

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF000000)))
                .cornerRadius(22.dp)
                .clickable(actionRunCallback<RingerRefreshAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(iconRes),
                    contentDescription = "Zil: $label",
                    modifier = GlanceModifier.size(
                        if (LocalSize.current.width < 100.dp) 26.dp else 32.dp
                    )
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
}

class RingerRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            RingerWidget().update(context, glanceId)
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val label = if (am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) "VIB" else "RING"
            widgetToast(context, "Zil: $label")
        } catch (t: Throwable) {
            widgetToast(context, "Zil hata: ${t.message}")
        }
    }
}
