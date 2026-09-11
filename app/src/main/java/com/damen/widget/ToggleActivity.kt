package com.damen.widget

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking

class ToggleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Without DnD access setRingerMode throws SecurityException.
            // Redirect to the app so the user can grant it.
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!nm.isNotificationPolicyAccessGranted) {
                Toast.makeText(this, "Izin gerekli", Toast.LENGTH_SHORT).show()
                stamp("Ringer: izin yok, uygulamaya yönlendirildi")
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                return
            }

            val am = getSystemService(AUDIO_SERVICE) as AudioManager

            // Toggle: NORMAL ↔ VIBRATE. Only two states.
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

            Toast.makeText(
                this,
                if (newMode == AudioManager.RINGER_MODE_VIBRATE) "Titresim" else "Sesli",
                Toast.LENGTH_SHORT
            ).show()

            // Kick the Glance re-render, then exit.
            stamp("Ringer → " + if (newMode == AudioManager.RINGER_MODE_VIBRATE) "Titresim" else "Sesli")
            runBlocking { RingerWidget().updateAll(this@ToggleActivity) }
        } catch (t: Throwable) {
            Toast.makeText(this, "Hata: ${t.message}", Toast.LENGTH_LONG).show()
            stamp("Ringer HATA: ${t.message}")
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
