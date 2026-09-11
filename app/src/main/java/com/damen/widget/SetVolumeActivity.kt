package com.damen.widget

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking

class SetVolumeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val level = intent.getIntExtra("volume_level", 5) // 0-10 scale
            val am = getSystemService(AUDIO_SERVICE) as AudioManager
            val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            // Map 0-10 to actual volume range
            val targetVol = ((level.toFloat() / 10) * maxVol).toInt()
            am.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)

            // Haptic feedback
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 29) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }

            Toast.makeText(this, "Ses: $level/10", Toast.LENGTH_SHORT).show()

            // Trigger widget update
            stamp("Ses: $level/10")
            runBlocking { VolumeWidget().updateAll(this@SetVolumeActivity) }
        } catch (t: Throwable) {
            Toast.makeText(this, "Hata: ${t.message}", Toast.LENGTH_LONG).show()
        } finally {
            finish()
        }
    }

    private fun stamp(msg: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            .edit().putString("last_action", "$msg • $time").apply()
    }
}
