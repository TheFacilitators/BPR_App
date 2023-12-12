package com.facilitation.view.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/** BroadcastReceiver for handling incoming call events. */
class CallReceiver : BroadcastReceiver() {

    /** Handles the incoming call broadcast.
     * @param context The context in which the receiver is running.
     * @param intent The intent containing information about the incoming call. */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
            val contactName = intent.getStringExtra("contactName") ?: ""


            val message = "Incoming Call from $contactName\nPhone number: $phoneNumber"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            
            Log.d(
                "CallReceiver",
                "Incoming call notification: Phone number: $phoneNumber, Name: $contactName"
            )
        }
    }
}
