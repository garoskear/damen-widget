package com.damen.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

// 15 dakikalık periyodik tazeleme: tüm durum widget'ları (tek plan, KEEP).
// WorkManager aralığı en az 15dk olabilir; aralarda dokunarak tazele.
class RefreshWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        try { ShizukuWidget().updateAll(applicationContext) } catch (_: Throwable) {}
        try { GatewayWidget().updateAll(applicationContext) } catch (_: Throwable) {}
        try { ProcWidget().updateAll(applicationContext) } catch (_: Throwable) {}
        return Result.success()
    }
}
