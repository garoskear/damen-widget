package com.damen.widget

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider

// Ses seviyesi widget'ı: siyah bar, beyaz segmentler, kırmızı uç.
// Dokunmak yalnızca seviyeyi tazeler (ses değiştirmez).
class VolumeWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(BarS, BarM, BarL))

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
                .clickable(actionRunCallback<VolumeRefreshAction>()),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (level in 0..10) {
                    val bgColor = when {
                        level == currentLevel && currentLevel > 0 -> Color(0xFFFF0000)
                        level <= currentLevel -> Color(0xFFFFFFFF)
                        else -> Color(0xFF232323)
                    }
                    Box(
                        modifier = GlanceModifier.defaultWeight()
                            .fillMaxHeight()
                            .background(ColorProvider(bgColor))
                            .cornerRadius(5.dp)
                    ) {}
                    if (level < 10) {
                        Spacer(modifier = GlanceModifier.width(3.dp))
                    }
                }
            }
        }
    }
}

class VolumeRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        VolumeWidget().update(context, glanceId)
    }
}
