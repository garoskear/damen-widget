package com.example.ringerwidget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class VolumeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = VolumeWidget()
}
