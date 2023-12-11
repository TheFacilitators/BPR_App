package com.facilitation.phone.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import com.facilitation.phone.utility.BluetoothServer


class HomeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {

    private var serverRunning = false


    fun initializeBluetoothServer(activity: Activity) {
        if(!serverRunning) {
            serverRunning = BluetoothServer(appContext, activity).startServer()
        }
        else{
            Log.i("VuzixSidekick", "Bluetooth server is already running")
        }
    }
}