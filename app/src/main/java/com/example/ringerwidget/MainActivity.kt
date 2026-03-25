package com.example.ringerwidget

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ringer Widget",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Two widgets to control your phone's audio:\n• Ringer Toggle (1×1) — switch Ring/Vibrate\n• Volume Slider (4×1) — adjust media volume",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- permission card ----------
        if (!hasPermission) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚠️  Permission Required",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Grant \"Do Not Disturb\" access so the widget can change the ringer mode.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestPermission, modifier = Modifier.fillMaxWidth()) {
                Text("Grant Permission")
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✓  Permission Granted",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Everything is set. Add the widget to your home screen now.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- how-to card ----------
        Text(
            text = "How to add the widget",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1.  Long-press an empty spot on your home screen.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("2.  Tap  Widgets.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("3.  Find  Ringer Widget  and choose:")
                Spacer(modifier = Modifier.height(4.dp))
                Text("    • Ringer Toggle (1×1) — tap to cycle Ring/Vibrate")
                Spacer(modifier = Modifier.height(4.dp))
                Text("    • Volume Slider (4×1) — tap bars to set volume")
                Spacer(modifier = Modifier.height(8.dp))
                Text("4.  Drag it onto your home screen and release.")
            }
        }
    }
}
