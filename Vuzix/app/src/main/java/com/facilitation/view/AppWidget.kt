package com.facilitation.view

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */

class AppWidget : AppWidgetProvider() {
    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager?, appWidgetId: Int) {
        val isLightMode = isLightMode(context)

        val finalAppWidgetManager = appWidgetManager ?: AppWidgetManager.getInstance(context)
        val widgetIds = finalAppWidgetManager.getAppWidgetIds(ComponentName(context, AppWidget::class.java))

        if (widgetIds.isNotEmpty()) {
            val views = RemoteViews(context.packageName, if (isLightMode) R.layout.widget_light else R.layout.widget_dark)
            finalAppWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateAppWidget(context, null, 0)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    private fun isLightMode(context: Context): Boolean {
        return (context.applicationContext as ViewApplication).isLightMode()
    }

}