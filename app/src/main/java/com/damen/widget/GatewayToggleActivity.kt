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

// pi web aç/kapa trampolini: kapalıysa Termux'a başlat komutu,
// açıksa durdur komutu gönderir (RUN_COMMAND → ~/bin/gw-*.sh).
class GatewayToggleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread {
            try {
                if (GatewayWidget.probe() == 0) {
                    runTermuxScript("gw-start.sh")
                    stamp("Gateway: başlatma komutu gönderildi")
                    toast("Gateway başlatılıyor")
                } else {
                    runTermuxScript("gw-stop.sh")
                    stamp("Gateway: durdurma komutu gönderildi")
                    toast("Gateway durduruluyor")
                }
            } catch (t: Throwable) {
                stamp("Gateway HATA: ${t.message}")
                toast("Hata: ${t.message}")
            }
            try {
                runBlocking { GatewayWidget().updateAll(this@GatewayToggleActivity) }
            } catch (_: Throwable) {
            }
            runOnUiThread { finish() }
        }.start()
    }

    private fun runTermuxScript(name: String) {
        val intent = Intent("com.termux.service_execute").apply {
            setClassName("com.termux", "com.termux.app.TermuxService")
            putExtra("com.termux.execute.execute_background", true)
            putExtra(
                "com.termux.execute.executable",
                "/data/data/com.termux/files/home/bin/$name"
            )
        }
        startService(intent)
    }

    private fun toast(msg: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this@GatewayToggleActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun stamp(msg: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            .edit().putString("last_action", "$msg • $time").apply()
    }
}
