package com.facilitation.phone.utility

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.vuzix.connectivity.sdk.Connectivity
import com.vuzix.connectivity.sdk.Device

class CallReceiver : BroadcastReceiver() {
    //private lateinit var telephonyCallback: MyTelephonyCallback
    private val ACTION_CALL = "com.facilitation.CALL"


    private lateinit var c: Connectivity

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            c = Connectivity.get(context)
            val phoneNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED && phoneNumber != null) {
                val phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                when (phoneState) {
                    TelephonyManager.EXTRA_STATE_RINGING -> {
                        // Incoming call
                        Log.e("CallReceiver", "*********************************Incoming call from: $phoneNumber")

                        // Check if the number is in contacts
                        val contactName = getContactName(context, phoneNumber)

                        // Create an Intent with the action for the remote app
                        val myRemoteBroadcast = Intent("com.facilitation.view.CALL")

                        // Add extras to the intent
                        myRemoteBroadcast.putExtra("phoneNumber", phoneNumber)
                        myRemoteBroadcast.putExtra("contactName", contactName)

                        // Send the ordered broadcast
                        c.sendBroadcast(myRemoteBroadcast)
                    }

                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        // Call ended
                        Log.e("CallReceiver", "**************************************Call ended")

                        // Check if the number is in contacts
                        val contactName = getContactName(context, phoneNumber)

                        // Create an Intent with the action for the remote app
                        val myRemoteBroadcast = Intent("com.facilitation.view.CALL")

                        // Add extras to the intent
                        myRemoteBroadcast.putExtra("phoneNumber", phoneNumber)
                        myRemoteBroadcast.putExtra("contactName", contactName)

                        // Send the ordered broadcast
                        c.sendBroadcast(myRemoteBroadcast)
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getContactName(context: Context, phoneNumber: String): String {
        // Query the contacts database
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

//    inner class MyTelephonyCallback :
//        TelephonyCallback(), TelephonyCallback.CallStateListener {
//
//        override fun onCallStateChanged(state: Int) {
//            handleCallStateChange(state)
//        }
//    }

    private fun handleCallStateChange(state: Int) {
        when (state) {
//            TelephonyManager.CALL_STATE_IDLE -> {
//                Log.e(TAG, "************************Not in call")
//                // Create an Intent with the action for the remote app
//                val myRemoteBroadcast = Intent("com.facilitation.view.CALL")
//
//
//                // Send the ordered broadcast
//                c.sendBroadcast(myRemoteBroadcast)
//            }
//
//            TelephonyManager.CALL_STATE_RINGING -> {
//                Log.e(TAG, "*************************Ringing")
//                // Create an Intent with the action for the remote app
//                val myRemoteBroadcast = Intent("com.facilitation.view.CALL")
//
//
//                // Send the ordered broadcast
//                c.sendBroadcast(myRemoteBroadcast)            }
//
//            TelephonyManager.CALL_STATE_OFFHOOK -> {
//                Log.e(TAG, "****************************Call is dialing, active, or on hold")
            //}
        }
    }

    private fun handleCall() {
//        // Create an Intent with the action for the remote app
//        val myRemoteBroadcast = Intent()
//
//        // Set the remote app package (replace with your actual package name)
//        myRemoteBroadcast.setPackage("com.facilitation.view")
//
//        // Add extras to the intent (if needed)
//        myRemoteBroadcast.putExtra("my_string_extra", "hello")
//        myRemoteBroadcast.putExtra("", 2)
//
//        // Send the ordered broadcast
//        //c.sendOrderedBroadcast(c.device, Intent.)

//        if (c.device.name.equals("BPR Blade")) {
//            val getIntent = Intent(this.ACTION_GET)
//            c.sendOrderedBroadcast(c.device, getIntent)
//            Toast.makeText(appContext, "Hello to Vuzix sent", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(appContext, "Vuzix not found. Cannot send message.", Toast.LENGTH_SHORT)
//                .show()
//        }
    }
}
