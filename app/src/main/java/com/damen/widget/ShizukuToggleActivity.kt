package com.damen.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking
import rikka.shizuku.Shizuku

// Shizuku aç/kapa trampolini.
// AÇIKSA → STOP yayını gönderir. KAPALIYSA → START yayını dener,
// tutmazsa Shizuku uygulamasını açar (oradan tek dokunuşla başlar).
class ShizukuToggleActivity : ComponentActivity() {

    companion object {
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        const val ACTION_START = "moe.shizuku.privileged.api.START"
        const val ACTION_STOP = "moe.shizuku.privileged.api.STOP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread {
            try {
                if (Shizuku.pingBinder()) {
                    sendBroadcast(Intent(ACTION_STOP).setPackage(SHIZUKU_PACKAGE))
                    stamp("Shizuku: STOP gönderildi")
                    toast("Shizuku durduruluyor")
                } else {
                    sendBroadcast(Intent(ACTION_START).setPackage(SHIZUKU_PACKAGE))
                    // START yayını sessizce düşebilir; gerçekten açıldı mı diye bekle.
                    Thread.sleep(3000)
                    if (Shizuku.pingBinder()) {
                        stamp("Shizuku: açıldı")
                        toast("Shizuku açıldı")
                    } else {
                        stamp("Shizuku: kapalıydı, uygulaması açıldı")
                        toast("Shizuku uygulaması açılıyor")
                        runOnUiThread { openShizukuApp() }
                        Thread.sleep(1000)
                    }
                }
            } catch (t: Throwable) {
                stamp("Shizuku HATA: ${t.message}")
                toast("Hata: ${t.message}")
            }
            try {
                runBlocking { ShizukuWidget().updateAll(this@ShizukuToggleActivity) }
            } catch (_: Throwable) {
            }
            runOnUiThread { finish() }
        }.start()
    }

    private fun openShizukuApp() {
        val launch = packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
        if (launch != null) {
            startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else {
            Toast.makeText(this, "Shizuku uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toast(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@ShizukuToggleActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun stamp(msg: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            .edit().putString("last_action", "$msg • $time").apply()
    }
}
