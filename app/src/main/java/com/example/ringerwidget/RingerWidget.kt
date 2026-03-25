package com.example.ringerwidget

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
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider

class RingerWidget : GlanceAppWidget() {

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
        val bg      = if (isVibrate) Color(0xFFFB8C00)      else Color(0xFF1E88E5)
        val label   = if (isVibrate) "Vibrate"               else "Ring"

        val toggleIntent = Intent(context, ToggleActivity::class.java)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(bg))
                .cornerRadius(18.dp)
                .clickable(actionStartActivity(toggleIntent))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = label,
                modifier = GlanceModifier.size(34.dp)
            )
        }
    }
}
