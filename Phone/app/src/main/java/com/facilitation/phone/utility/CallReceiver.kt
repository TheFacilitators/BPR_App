package com.facilitation.phone.utility

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import com.vuzix.connectivity.sdk.Connectivity

class CallReceiver : BroadcastReceiver() {
    private lateinit var c: Connectivity

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            c = Connectivity.get(context)
            val phoneNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED && phoneNumber != null) {
                val phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                when (phoneState) {
                    TelephonyManager.EXTRA_STATE_RINGING -> {
                        Log.e("CallReceiver",
                            "*********************************Incoming call from: $phoneNumber")

                        val contactName = getContactName(context, phoneNumber)

                        val myRemoteBroadcast = Intent("com.facilitation.view.CALL")
                        myRemoteBroadcast.putExtra("phoneNumber", phoneNumber)
                        myRemoteBroadcast.putExtra("contactName", contactName)

                        c.sendBroadcast(myRemoteBroadcast)
                    }

                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        // Call ended
                        Log.e(
                            "CallReceiver",
                            "**************************************Call ended: $phoneNumber"
                        )

//                        // Check if the number is in contacts
//                        val contactName = getContactName(context, phoneNumber)
//
//                        // Create an Intent with the action for the remote app
//                        val myRemoteBroadcast = Intent("com.facilitation.view.CALL")
//
//                        // Add extras to the intent
//                        myRemoteBroadcast.putExtra("phoneNumber", phoneNumber)
//                        myRemoteBroadcast.putExtra("contactName", contactName)
//
//                        // Send the ordered broadcast
//                        c.sendBroadcast(myRemoteBroadcast)
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getContactName(context: Context, phoneNumber: String): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val contactName =
                    it.getString(it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                return contactName ?: ""
            }
        }

        return ""
    }
}
