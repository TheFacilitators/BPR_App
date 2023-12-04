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

/** A class for handling the logic of Bluetooth connection with the server.
 * @constructor
 * @param context the context the class operates in.
 * @property deviceNameToConnect hardcoded string defining which device to connect to.
 * @property bluetoothString hardcoded UUID string of the device to connect to.
 * @property bluetoothAdapter the contexts Bluetooth system service's adapter property.
 * @property mainHandler a Handler created with a message Looper.
 * @property connectedSocket the connected BluetoothSocket.*/
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

    /** Attempts to create a PrintWriter for the 'connectedSocket's outputStream, printing the
     * argument string and flushing it.
     * In case of an IOException being thrown a local error is logged with details.
     * @param command the string command to be sent to the server.*/
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

    /** Attempts to call sendCommand() with the argument, create a BufferedReader from the
     * 'connectedSocket's inputStream to read and assign the string to a local variable.
     * @see sendCommand for further details.
     * @return an empty string if any exception other than IOException is thrown, otherwise the string response read from the server.*/
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

    /** Calls sendCommand() with 'exit' string argument and calls close() on connectedSocket.*/
    fun exitServer() {
        sendCommand("exit")
        connectedSocket.close()
    }

    /** Checks the state of the bluetoothAdapter, logging an error and throwing an Exception if
     * it is null or not enabled.
     * Initiates a local variable for the server by checking the bluetoothAdapter bondedDevices
     * for an object with a 'name' property of deviceNameToConnect.
     * If this local variable is not null a ConnectThread is created with it and the
     * bluetoothAdapter and then start() is called on it. Otherwise a local error is logged.
     * @see ConnectThread for further details on the thread.*/
    @SuppressLint("MissingPermission")
    fun initiateConnection() {
        if (bluetoothAdapter == null || !bluetoothAdapter?.isEnabled!!) {
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

    /** Model class for a custom Thread.
     * @constructor
     * @param device the BluetoothDevice this thread runs for.
     * @param bluetoothAdapter the adapter the thread uses.
     * @property btSocket a BluetoothSocket created on the 'device' with bluetoothString
     * @see bluetoothString*/
    @SuppressLint("MissingPermission")
    inner class ConnectThread(device: BluetoothDevice, private val bluetoothAdapter: BluetoothAdapter) : Thread() {

        private val btSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.fromString(bluetoothString))
        }

        /** Calls cancelDiscovery() on bluetoothAdapter, attempts to connect a socket and set
         * connectedSocket to it.
         * @see connectedSocket*/
        override fun run() {
            bluetoothAdapter.cancelDiscovery()

            btSocket?.let { socket ->
                socket.connect()
                connectedSocket = socket
                showToast("Bluetooth Connected")
            }
        }
    }

    /** Posts a short Toast to the mainHandler.
     * @param toast the text to display in the Toast.*/
    private fun showToast(toast: String) {
        mainHandler.post {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }
}
