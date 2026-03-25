package com.example.ringerwidget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class RingerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = RingerWidget()
}
