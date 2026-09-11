package com.damen.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

// 15 dakikalık periyodik tazeleme işçisi (MainActivity'de planlanır).
class ProcWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            ProcWidget().updateAll(applicationContext)
            Result.success()
        } catch (_: Throwable) {
            Result.failure()
        }
    }
}
