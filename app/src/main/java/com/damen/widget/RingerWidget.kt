package com.damen.widget

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider

// Nothing OS style: pure black tile, white glyph, LED status dot.
// Red dot = vibrate, dim grey dot = ring.
class RingerWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(SizeMode.Small, SizeMode.Medium, SizeMode.Large))

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
        val label = if (isVibrate) "Vibrate" else "Ring"
        val dotColor = if (isVibrate) Color(0xFFFF0000) else Color(0xFF3A3A3A)

        val toggleIntent = Intent(context, ToggleActivity::class.java)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF000000)))
                .cornerRadius(22.dp)
                .clickable(actionStartActivity(toggleIntent)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(iconRes),
                    contentDescription = label,
                    modifier = GlanceModifier.size(
                        if (LocalSize.current.width < 100.dp) 26.dp else 32.dp
                    )
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Box(
                    modifier = GlanceModifier
                        .size(8.dp)
                        .background(ColorProvider(dotColor))
                        .cornerRadius(4.dp)
                ) {}
            }
        }
    }
}
