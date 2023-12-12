package com.facilitation.view

import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.util.Log
import com.facilitation.view.utility.BluetoothHandler
import com.vuzix.hud.resources.DynamicThemeApplication

/** The application class itself.
 * @property bluetoothHandler custom handler class for handling Bluetooth communication.
 * @property bluetoothAdapter adapter for the handler.
 * @see BluetoothHandler*/
class ViewApplication : DynamicThemeApplication() {
    var bluetoothHandler: BluetoothHandler? = null
    var bluetoothAdapter: BluetoothAdapter? = null

    /** Calls super.onCreate() and attempts to call connectToBluetooth().*/
    override fun onCreate() {
        super.onCreate()
        try {
            connectToBluetooth()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    /** Initializes bluetoothAdapter as the default adapter. If it is null the bluetoothHandler
     * is initialized with this class as the context argument. Then initiateConnection() is
     * called on bluetoothHandler.*/
    fun connectToBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            bluetoothHandler = BluetoothHandler(this)
            bluetoothHandler?.initiateConnection()
        }
    }

    /** Getter for the resource ID of the default theme.
     * @return an integer ID value from style folder.*/
    override fun getNormalThemeResId(): Int {
        return R.style.AppTheme
    }

    /** Getter for the resource ID of the light theme.
     * @return an integer ID value from style folder.*/
    override fun getLightThemeResId(): Int {
        return R.style.AppTheme_Light
    }
}