package com.facilitation.view.utility

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.UUID

class BluetoothHandler(
    private val context: Context
) {

    private val deviceNameToConnect = "Galaxy S21 5G"
    private val bluetoothString = "00001101-0000-1000-8000-00805F9B34FB"
    private val bufferSize = 4096

    private val bluetoothOutput: OutputStream? by lazy(LazyThreadSafetyMode.NONE) {
        connectedSocket?.outputStream
    }

    private var connectedSocket: BluetoothSocket? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val mainHandler = Handler(Looper.getMainLooper())

    fun sendCommandToServer(command: String) {
        if (connectedSocket != null) {
            try {
                val writer = PrintWriter(OutputStreamWriter(connectedSocket!!.outputStream))
                writer.println(command) // Send the string
                writer.flush()
                Log.d(TAG, "Sent command to server: $command")
            } catch (e: IOException) {
                Log.e(TAG, "Error sending command to server: $command", e)
            }
        } else {
            Log.e(TAG, "Socket is not connected to the server")
        }
    }

    @SuppressLint("MissingPermission")
    fun initiateConnection() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Log.e(TAG, "Bluetooth is not available or not enabled")
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
        }

        val serverDevice = bluetoothAdapter!!.bondedDevices.find { it.name == deviceNameToConnect }

        if (serverDevice != null) {
            val connectThread = ConnectThread(serverDevice, bluetoothAdapter!!)
            connectThread.start()
            Log.d(TAG, "Connection Started")
        } else {
            Log.e(TAG, "Connection Failed: Device not found")
        }
    }

    @SuppressLint("MissingPermission")
    inner class ConnectThread(device: BluetoothDevice, private val bluetoothAdapter: BluetoothAdapter) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString(bluetoothString))
        }

        override fun run() {
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.let { socket ->
                socket.connect()
                showToast("Connection Started")
                connectedSocket = socket // Store the connected socket
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
    private fun showToast(toast: String) {
        mainHandler.post {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }
}
