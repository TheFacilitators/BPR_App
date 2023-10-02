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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class BluetoothHandler(
    private val context: Context
) {

    private val deviceNameToConnect = "Galaxy S21 5G"
    private val bluetoothString = "00001101-0000-1000-8000-00805F9B34FB"
    private val receivedFile = File("/storage/self/primary/Music/test.mp3")
    private val bufferSize = 4096

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val mainHandler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingPermission")
    fun initiateConnection(): String {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Log.e(TAG, "Bluetooth is not available or not enabled")
            return ""
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
        return receivedFile.absolutePath
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
                receiveFile(socket)
                showToast("Finished download")
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }

        private fun receiveFile(socket: BluetoothSocket) {
            try {
                val inputStream = socket.inputStream
                val outputStream = FileOutputStream(receivedFile)
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int

                while (true) {
                    bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) break
                    outputStream.write(buffer, 0, bytesRead)
                    Log.d(TAG, "File data: ${bytesRead}")
                }

                outputStream.close()
                inputStream.close()
                socket.close()

                Log.d(TAG, "File received and saved at: ${receivedFile.absolutePath}")
            } catch (e: IOException) {
                Log.e(TAG, "Error receiving file", e)
            }
        }
    }
    private fun showToast(toast: String){
        mainHandler.post {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
        }
    }
}
