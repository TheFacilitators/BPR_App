package com.facilitation.view

import android.bluetooth.BluetoothAdapter
import com.facilitation.view.utility.BluetoothHandler
import com.vuzix.hud.resources.DynamicThemeApplication


class ViewApplication : DynamicThemeApplication() {
    lateinit var bluetoothHandler: BluetoothHandler
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate() {
        super.onCreate()

        connectToBluetooth()
    }

    fun connectToBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothHandler = BluetoothHandler(this)
        bluetoothHandler.initiateConnection()
    }

    override fun getNormalThemeResId(): Int {
        return R.style.AppTheme
    }

    override fun getLightThemeResId(): Int {
        return R.style.AppTheme_Light
    }
}