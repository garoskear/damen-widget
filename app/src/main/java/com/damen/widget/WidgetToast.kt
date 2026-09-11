package com.damen.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

// Widget içi tazeleme kanıtı: dokunma ulaştı mı, sonuç ne — hepsi toast'ta.
internal fun widgetToast(context: Context, msg: String) {
    try {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    } catch (_: Throwable) {
    }
}
