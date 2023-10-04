package com.facilitation.view.receivers

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class DynamicThemeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val updateIntent = Intent()
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        context.sendBroadcast(updateIntent)
    }
}