package com.facilitation.view.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/** BroadcastReceiver for handling incoming call events. */
class CallReceiver : BroadcastReceiver() {

    /** Handles the incoming call broadcast.
     * @param context The context in which the receiver is running.
     * @param intent The intent containing information about the incoming call. */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
            val contactName = intent.getStringExtra("contactName") ?: ""


            val notification = Notification.Builder(context, "CallChannel")
                .setContentTitle("Incoming Call from $contactName")
                .setContentText("Phone number: $phoneNumber")
                .build()

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                "CallChannel",
                "Call Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(1, notification)
            Log.d(
                "CallReceiver",
                "Incoming call notification: Phone number: $phoneNumber, Name: $contactName"
            )
        }
    }
}
