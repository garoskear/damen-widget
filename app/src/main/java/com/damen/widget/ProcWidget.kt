package com.damen.widget

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.LargeBox
import androidx.glance.appwidget.MediumBox
import androidx.glance.appwidget.SmallBox
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import rikka.shizuku.Shizuku

// Çalışan process'ler: Shizuku UserService (root/shell) içinde `ps` çeker,
// belleğe göre ilk 6 grubu gösterir. Dokun = tazele,
// ayrıca her 15 dakikada arka planda tazelenir (ProcWorker).
class ProcWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode =
        SizeMode.Responsive(setOf(SmallBox, MediumBox, LargeBox))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val alive = try { Shizuku.pingBinder() } catch (_: Throwable) { false }
        val granted = try {
            alive && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (_: Throwable) { false }
        val procs: List<Pair<String, String>>? = if (!alive || !granted) {
            null
        } else {
            try { parseProcs(fetchPs(context)) } catch (_: Throwable) { emptyList() }
        }
        provideContent {
            WidgetContent(alive, granted, procs)
        }
    }

    @Composable
    private fun WidgetContent(
        alive: Boolean,
        granted: Boolean,
        procs: List<Pair<String, String>>?
    ) {
        val note = when {
            !alive -> "SHIZUKU KAPALI"
            !granted -> "İZİN YOK"
            procs!!.isEmpty() -> "VERİ YOK"
            else -> null
        }
        val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        val rowCount = when {
            LocalSize.current.height < 140.dp -> 3
            LocalSize.current.height < 200.dp -> 5
            else -> 7
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF000000)))
                .cornerRadius(22.dp)
                .padding(12.dp)
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth()
                        .clickable(actionRunCallback<ProcRefreshAction>()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .size(7.dp)
                            .background(ColorProvider(Color(0xFFFF0000)))
                            .cornerRadius(4.dp)
                    ) {}
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "PROCS",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFFFFFFF)),
                            fontSize = 12.sp
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    Text(
                        text = "TAP $time",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF666666)),
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.height(6.dp))
                if (note != null) {
                    Text(
                        text = note,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF888888)),
                            fontSize = 11.sp
                        )
                    )
                } else {
                    procs!!.take(rowCount).forEach { (name, mem) ->
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFFFFFFFF)),
                                    fontSize = 11.sp
                                ),
                                modifier = GlanceModifier.defaultWeight(),
                                maxLines = 1
                            )
                            Spacer(modifier = GlanceModifier.width(8.dp))
                            Text(
                                text = mem,
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFF888888)),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// UserService'e bağlan, `ps` çıktısını al, çöz.
suspend fun fetchPs(context: Context): String = withTimeout(8000) {
    val deferred = CompletableDeferred<String>()
    val args = Shizuku.UserServiceArgs(ComponentName(context, ProcUserService::class.java))
        .processNameSuffix("procs")
    val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            try {
                val svc = IProcService.Stub.asInterface(binder)
                deferred.complete(svc.topProcesses())
            } catch (t: Throwable) {
                deferred.completeExceptionally(t)
            } finally {
                try { Shizuku.unbindUserService(args, this, true) } catch (_: Throwable) {}
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            deferred.completeExceptionally(IllegalStateException("shizuku disconnected"))
        }
    }
    Shizuku.bindUserService(args, conn)
    try {
        deferred.await()
    } finally {
        try { Shizuku.unbindUserService(args, conn, true) } catch (_: Throwable) {}
    }
}

// `ps -A -o COMM,RSS` çıktısını isim bazında topla, ilk 6.
fun parseProcs(out: String): List<Pair<String, String>> {
    return out.lineSequence()
        .mapNotNull { line ->
            val parts = line.trim().split(Regex("\\s+"))
            if (parts.size < 2) return@mapNotNull null
            val rss = parts.last().toIntOrNull() ?: return@mapNotNull null
            if (rss <= 0) return@mapNotNull null
            parts.dropLast(1).joinToString(" ") to rss
        }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.sum() }
        .toList()
        .sortedByDescending { it.second }
        .take(8)
        .map { (name, rss) ->
            val short = if (name.length > 14) name.take(13) + "…" else name
            short to "${rss / 1024} MB"
        }
}

class ProcRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        ProcWidget().update(context, glanceId)
    }
}
