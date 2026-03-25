package com.example.ringerwidget

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider

class VolumeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            VolumeSliderContent(context)
        }
    }

    @Composable
    private fun VolumeSliderContent(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        
        // Convert to percentage (0-10 scale for 10 boxes)
        val currentLevel = ((currentVol.toFloat() / maxVol) * 10).toInt()

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFFF5F5F5)))
                .cornerRadius(16.dp)
                .padding(8.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (level in 0..10) {
                    VolumeBox(
                        context = context,
                        level = level,
                        isActive = level <= currentLevel,
                        modifier = GlanceModifier.defaultWeight()
                    )
                    if (level < 10) {
                        Spacer(modifier = GlanceModifier.width(4.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun VolumeBox(
        context: Context,
        level: Int,
        isActive: Boolean,
        modifier: GlanceModifier
    ) {
        val bgColor = if (isActive) Color(0xFF1E88E5) else Color(0xFFE0E0E0)
        val intent = Intent(context, SetVolumeActivity::class.java).apply {
            putExtra("volume_level", level)
        }

        Box(
            modifier = modifier
                .fillMaxHeight()
                .background(ColorProvider(bgColor))
                .cornerRadius(8.dp)
                .clickable(actionStartActivity(intent))
        ) {
            // Empty — this is just a colored box representing a volume level
        }
    }
}
