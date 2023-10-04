package com.facilitation.phone.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.vuzix.connectivity.sdk.Connectivity
import com.vuzix.connectivity.sdk.Device
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import com.facilitation.phone.utility.BluetoothServer
import java.io.File


class HomeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {

    private val ACTION_GET = "com.facilitation.view.GET"
    private val connectivity: Connectivity = Connectivity.get(appContext)
    private val device: Device = connectivity.device
    private lateinit var mp3File : File
    private var serverRunning = false


    fun initializeBluetoothServer(activity: Activity) {
        if(!serverRunning) {
            findMusicPath()
            BluetoothServer(appContext, mp3File, activity)
            serverRunning = true
        }
        else{
            Toast.makeText(appContext, "Bluetooth server is already running", Toast.LENGTH_SHORT).show()
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
    private fun findMusicPath() {
        val mp3File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Medina_-_Kun_For_Mig.mp3")
        if (mp3File.exists() && mp3File.isFile) {
            this.mp3File = mp3File
        } else {
            Toast.makeText(appContext, "MP3 file not found", Toast.LENGTH_SHORT).show()
        }

    }
}