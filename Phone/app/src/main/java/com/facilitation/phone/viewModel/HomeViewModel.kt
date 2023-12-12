package com.facilitation.phone.viewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.facilitation.phone.utility.communication.BluetoothServer


class HomeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {
    private var serverRunning = false

    fun initializeBluetoothServer(activity: Activity) {
        if(!serverRunning) {
            serverRunning = BluetoothServer(appContext, activity).startServer()
        }
        else {
            Log.i("VuzixSidekick", "Bluetooth server is already running")
        }
    }
}