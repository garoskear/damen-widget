package com.damen.widget

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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

        setContent {
            MaterialTheme(colorScheme = NothingScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    MainScreen(
                        context = this,
                        onRequestPermission = { requestDndPermission() }
                    )
                }
            }
        }
    }

    private fun requestDndPermission() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!nm.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
    }
}

@Composable
fun MainScreen(context: Context, onRequestPermission: () -> Unit) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val hasPermission = nm.isNotificationPolicyAccessGranted
    val lastAction = remember {
        context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            .getString("last_action", "henüz işlem yok") ?: "henüz işlem yok"
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
            text = "Ringer Toggle (1×1) — Ring/Vibrate\nVolume Slider (4×1) — media volume\nShizuku Toggle (1×1) — Shizuku aç/kapa",
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

        // ---------- permission card ----------
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = NothingCard),
            border = BorderStroke(1.dp, if (hasPermission) Color(0xFF333333) else NothingRed)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (hasPermission) "● READY" else "● PERMISSION NEEDED",
                    fontFamily = Mono,
                    fontSize = 16.sp,
                    color = if (hasPermission) Color.White else NothingRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (hasPermission)
                        "Everything is set. Add the widget to your home screen."
                    else
                        "Grant \"Do Not Disturb\" access so the widget can change the ringer mode.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCCCCCC)
                )
            }
        }

        if (!hasPermission) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("GRANT PERMISSION", fontFamily = Mono)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- son işlem kartı (widget teşhisi) ----------
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(containerColor = NothingCard),
            border = BorderStroke(1.dp, Color(0xFF333333))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SON İŞLEM",
                    fontFamily = Mono,
                    fontSize = 14.sp,
                    color = NothingRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(lastAction, color = Color(0xFFCCCCCC))
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
