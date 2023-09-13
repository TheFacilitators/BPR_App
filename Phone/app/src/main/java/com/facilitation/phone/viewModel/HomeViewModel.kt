package com.facilitation.phone.viewModel

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vuzix.connectivity.sdk.Connectivity

class HomeViewModel(private val appContext: Application) : AndroidViewModel(appContext) {

    private val ACTION_GET = "com.facilitation.view.GET"
    fun SayHelloToVuzix() {
        getRemoteDeviceModelClicked()
    }
    private fun getRemoteDeviceModelClicked() {
        val connectivity = Connectivity.get(appContext)
        val device = connectivity.device
        if (device != null) {
            val getIntent = Intent(this.ACTION_GET)
            connectivity.sendBroadcast(getIntent)
            Toast.makeText(appContext, "Hello to Vuzix sent", Toast.LENGTH_SHORT).show()
        }
    }
}