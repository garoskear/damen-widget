package com.damen.widget

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.LargeBox
import androidx.glance.appwidget.MediumBox
import androidx.glance.appwidget.SmallBox
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

// Nothing OS style: black bar, white segments, red tip marks the live level.
class VolumeWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(SmallBox, MediumBox, LargeBox))

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
                .background(ColorProvider(Color(0xFF000000)))
                .cornerRadius(22.dp)
                .padding(12.dp)
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
                        isTip = level == currentLevel && currentLevel > 0,
                        modifier = GlanceModifier.defaultWeight()
                    )
                    if (level < 10) {
                        Spacer(modifier = GlanceModifier.width(3.dp))
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
        isTip: Boolean,
        modifier: GlanceModifier
    ) {
        val bgColor = when {
            isTip -> Color(0xFFFF0000)
            isActive -> Color(0xFFFFFFFF)
            else -> Color(0xFF232323)
        }
        val intent = Intent(context, SetVolumeActivity::class.java).apply {
            putExtra("volume_level", level)
        }

        Box(
            modifier = modifier
                .fillMaxHeight()
                .background(ColorProvider(bgColor))
                .cornerRadius(5.dp)
                .clickable(actionStartActivity(intent))
        ) {
            // Empty — this is just a segment representing a volume level
        }
    }
}
