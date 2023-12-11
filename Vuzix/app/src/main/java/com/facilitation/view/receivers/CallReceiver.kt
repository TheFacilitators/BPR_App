package com.facilitation.view.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class CallReceiver : BroadcastReceiver() {
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
            Log.e(
                "CallReceiver",
                "Incoming call notification: Phone number: $phoneNumber, Name: $contactName"
            )
        }
    }
}
