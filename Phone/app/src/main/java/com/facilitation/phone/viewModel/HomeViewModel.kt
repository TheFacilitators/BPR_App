package com.facilitation.phone.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.vuzix.connectivity.sdk.Connectivity
import com.vuzix.connectivity.sdk.Device
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.facilitation.phone.utility.BluetoothServer


class HomeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {

    private val ACTION_GET = "com.facilitation.view.GET"
    private val connectivity: Connectivity = Connectivity.get(appContext)
    private val device: Device = connectivity.device
    private var serverRunning = false


    fun initializeBluetoothServer(activity: Activity) {
        if(!serverRunning) {
            serverRunning = BluetoothServer(appContext, activity).startServer()
        }
        else{
            Log.i("VuzixSidekick", "Bluetooth server is already running")
        }
    }
    fun sayHelloToVuzix() {
        if (device.name.equals("BPR Blade")) {
            val getIntent = Intent(this.ACTION_GET)
            connectivity.sendBroadcast(getIntent)
            Toast.makeText(appContext, "Hello to Vuzix sent", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(appContext, "Vuzix not found. Cannot send message.", Toast.LENGTH_SHORT).show()
        }
    }
}