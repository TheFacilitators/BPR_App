package com.facilitation.view

import android.bluetooth.BluetoothAdapter
import com.facilitation.view.utility.BluetoothHandler
import com.vuzix.hud.resources.DynamicThemeApplication


class ViewApplication : DynamicThemeApplication() {
    var bluetoothHandler: BluetoothHandler? = null
    var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate() {
        super.onCreate()

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