package com.facilitation.view.receivers

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** A class for handling dynamic changes to the UI theme.
 * Used for automatically switching between light and dark mode based on external conditions.*/
class DynamicThemeReceiver : BroadcastReceiver() {
    /** Creates an Intent with the 'action' property set to the official update action for widgets.
     * Then sendBroadcast() is called with the created Intent.
     * @param context the context the changes should apply to.
     * @param intent unused as a new Intent is created locally.*/
    override fun onReceive(context: Context, intent: Intent) {
        val updateIntent = Intent()
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        context.sendBroadcast(updateIntent)
    }
}