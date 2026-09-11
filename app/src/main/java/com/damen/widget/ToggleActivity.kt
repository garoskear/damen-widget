package com.damen.widget

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking

class ToggleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val am = getSystemService(AUDIO_SERVICE) as AudioManager

        // Toggle: NORMAL ↔ VIBRATE.  Only two states.
        val newMode = if (am.ringerMode == AudioManager.RINGER_MODE_VIBRATE)
            AudioManager.RINGER_MODE_NORMAL
        else
            AudioManager.RINGER_MODE_VIBRATE

        am.ringerMode = newMode

        // Write to SharedPrefs BEFORE updateAll so provideGlance sees it instantly
        getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("ringer_mode", newMode)
            .apply()

        // Haptic feedback
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 29) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }

        // Kick the Glance re-render, then exit.
        runBlocking { RingerWidget().updateAll(this@ToggleActivity) }

        finish()
    }
}
