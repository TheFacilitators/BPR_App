package com.facilitation.view.utility

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.UUID

class BluetoothHandler(
    private val context: Context
) {
    private val deviceNameToConnect = "Galaxy S21 5G"
    private val bluetoothString = "00001101-0000-1000-8000-00805F9B34FB"
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var connectedSocket: BluetoothSocket

    fun sendCommand(command: String) {
        try {
            val writer = PrintWriter(OutputStreamWriter(connectedSocket.outputStream))
            writer.println(command)
            writer.flush()
            Log.d(TAG, "Sent command to server: $command")

        } catch (e: IOException) {
            Log.e(TAG, "Error sending command to server: $command", e)
        }
    }

    fun sendReturnableCommand(command: String): String {
        var response = ""
        try{
            sendCommand(command)
            val reader = BufferedReader(InputStreamReader(connectedSocket.inputStream))
            response = reader.readLine()
            Log.d(TAG, "Received response from server: $response")
        } catch (e: IOException) {
            Log.e(TAG, "Error receiving from server: $command", e)
        }
        return response
    }

    fun exitServer() {
        sendCommand("exit")
        connectedSocket.close()
    }

    @SuppressLint("MissingPermission")
    fun initiateConnection() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled ) {
            Log.e(TAG, "Bluetooth is not available or not enabled")
            throw Exception("Bluetooth is not available or not enabled")
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

        private val btSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString(bluetoothString))
        }

        override fun run() {
            bluetoothAdapter.cancelDiscovery()

            btSocket?.let { socket ->
                socket.connect()
                connectedSocket = socket
                showToast("Bluetooth Connected")
            }
        }
    }
    private fun showToast(toast: String) {
        mainHandler.post {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }
}
