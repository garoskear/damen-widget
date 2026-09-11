package com.damen.widget

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
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
import rikka.shizuku.Shizuku

// Çalışan process'ler: Shizuku (root/shell) üzerinden `ps` çeker,
// belleğe göre ilk 6 grubu gösterir. Dokun = tazele,
// ayrıca her 15 dakikada arka planda tazelenir (ProcWorker).
class ProcWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val alive = try { Shizuku.pingBinder() } catch (_: Throwable) { false }
        val granted = try {
            alive && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (_: Throwable) { false }

        val procs: List<Pair<String, String>>? = when {
            !alive -> null
            !granted -> null
            else -> try { probeProcs() } catch (_: Throwable) { emptyList() }
        }
        val note = when {
            !alive -> "SHIZUKU KAPALI"
            !granted -> "İZİN YOK"
            procs!!.isEmpty() -> "VERİ YOK"
            else -> null
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
                        text = "TAP",
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
                    procs!!.forEach { (name, mem) ->
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

// `ps` çıktısını (COMM, RSS kB) isim bazında topla, ilk 6.
fun probeProcs(): List<Pair<String, String>> {
    val p = Shizuku.newProcess(arrayOf("sh", "-c", "ps -A -o COMM,RSS"), null, null)
    try {
        val out = p.inputStream.bufferedReader().readText()
        p.waitFor()
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
            .take(6)
            .map { (name, rss) ->
                val short = if (name.length > 14) name.take(13) + "…" else name
                short to "${rss / 1024} MB"
            }
    } finally {
        try { p.destroy() } catch (_: Throwable) {}
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
