package com.damen.widget

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import rikka.shizuku.Shizuku

// Gateway (damen-gateway) ile aynı his: sistem monospace yığını.
private val Mono = FontFamily.Monospace
private val NothingRed = Color(0xFFFF0000)
private val NothingCard = Color(0xFF101010)

private val NothingScheme = darkColorScheme(
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = NothingCard,
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = NothingRed,
    onBackground = Color.White,
    onSurface = Color.White,
    error = NothingRed
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 15 dakikalık periyodik tazeleme: tüm durum widget'ları (tek seferlik plan).
        try {
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "refresh-all",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<RefreshWorker>(15, TimeUnit.MINUTES).build()
            )
        } catch (_: Throwable) {
        }

        setContent {
            MaterialTheme(colorScheme = NothingScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    MainScreen(
                        context = this,
                        onRequestShizuku = { requestShizukuPermission() }
                    )
                }
            }
        }
    }

    private fun requestShizukuPermission() {
        try {
            if (Shizuku.pingBinder()) Shizuku.requestPermission(1001)
        } catch (_: Throwable) {
        }
    }
}

@Composable
fun MainScreen(context: Context, onRequestShizuku: () -> Unit) {
    // ---------- shizuku durumu ----------
    var shAlive by remember {
        mutableStateOf(try { Shizuku.pingBinder() } catch (_: Throwable) { false })
    }
    var shGranted by remember {
        mutableStateOf(
            try { Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED }
            catch (_: Throwable) { false }
        )
    }

    DisposableEffect(Unit) {
        val onReceived = Shizuku.OnBinderReceivedListener {
            shAlive = true
            shGranted = try {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } catch (_: Throwable) { false }
        }
        val onDead = Shizuku.OnBinderDeadListener {
            shAlive = false
            shGranted = false
        }
        val onPerm = Shizuku.OnRequestPermissionResultListener { _, grant ->
            shGranted = grant == PackageManager.PERMISSION_GRANTED
        }
        Shizuku.addBinderReceivedListener(onReceived)
        Shizuku.addBinderDeadListener(onDead)
        Shizuku.addRequestPermissionResultListener(onPerm)
        onDispose {
            Shizuku.removeBinderReceivedListener(onReceived)
            Shizuku.removeBinderDeadListener(onDead)
            Shizuku.removeRequestPermissionResultListener(onPerm)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "● DAMEN WIDGET",
            fontFamily = Mono,
            fontSize = 26.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ringer Durum (1×1) — zil hâli\nSes (4×1) — medya seviyesi\nShizuku Durum (1×1) — server açık/kapalı\nGateway (1×1) — pi web durumu\nProcs (4×2) — çalışan process'ler\n\nHepsi durum gösterir, dokun=tazele.",
            fontFamily = Mono,
            fontSize = 14.sp,
            color = Color(0xFFAAAAAA),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            fontFamily = Mono,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- shizuku card ----------
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = NothingCard),
            border = BorderStroke(1.dp, if (shAlive) Color(0xFF333333) else NothingRed)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SHIZUKU",
                    fontFamily = Mono,
                    fontSize = 16.sp,
                    color = if (shAlive) Color.White else NothingRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "server: ${if (shAlive) "açık" else "kapalı"} • izin: ${if (shGranted) "var" else "yok"}",
                    fontFamily = Mono,
                    fontSize = 14.sp,
                    color = Color(0xFFCCCCCC)
                )
                if (shAlive && !shGranted) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRequestShizuku,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("İZNİ VER", fontFamily = Mono)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- how-to card ----------
        Text(
            text = "SETUP",
            fontFamily = Mono,
            fontSize = 16.sp,
            color = NothingRed
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = NothingCard),
            border = BorderStroke(1.dp, Color(0xFF333333))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. Long-press an empty spot on your home screen.",
                    color = Color(0xFFCCCCCC))
                Spacer(modifier = Modifier.height(8.dp))
                Text("2. Tap Widgets.", color = Color(0xFFCCCCCC))
                Spacer(modifier = Modifier.height(8.dp))
                Text("3. Find Damen Widget and pick a widget.",
                    color = Color(0xFFCCCCCC))
                Spacer(modifier = Modifier.height(8.dp))
                Text("4. Drag it onto your home screen.",
                    color = Color(0xFFCCCCCC))
            }
        }
    }
}
