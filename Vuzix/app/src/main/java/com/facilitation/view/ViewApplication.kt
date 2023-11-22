package com.facilitation.view

import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.util.Log
import com.facilitation.view.utility.BluetoothHandler
import com.vuzix.hud.resources.DynamicThemeApplication


class ViewApplication : DynamicThemeApplication() {
    var bluetoothHandler: BluetoothHandler? = null
    var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate() {
        super.onCreate()
        try {
            connectToBluetooth()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    fun connectToBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            bluetoothHandler = BluetoothHandler(this)
            bluetoothHandler?.initiateConnection()
        }
    }

    override fun getNormalThemeResId(): Int {
        return R.style.AppTheme
    }

    override fun getLightThemeResId(): Int {
        return R.style.AppTheme_Light
    }
}