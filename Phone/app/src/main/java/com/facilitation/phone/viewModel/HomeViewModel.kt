package com.facilitation.phone.viewModel

import android.app.Application
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.facilitation.phone.utility.BluetoothServer
import com.vuzix.connectivity.sdk.Connectivity
import com.vuzix.connectivity.sdk.Device
import java.io.File


class HomeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {

    private val ACTION_GET = "com.facilitation.view.GET"
    private val connectivity: Connectivity = Connectivity.get(appContext)
    private val device: Device = connectivity.device

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
    fun sendMusicToVuzix() {
        if (device.name.equals("BPR Blade")) {
            val mp3File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BPRAnthem.mp3")
            if (mp3File.exists() && mp3File.isFile) {
                BluetoothServer(appContext, mp3File)
            } else {
                Toast.makeText(appContext, "MP3 file not found", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(appContext, "Vuzix not found. Cannot send music.", Toast.LENGTH_SHORT).show()
        }
    }

}