package com.facilitation.view.utility

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectThread(device: BluetoothDevice, private val bluetoothAdapter: BluetoothAdapter) : Thread() {

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
    }
    private val bufferSize = 4096
    private val receivedFile = File("/storage/self/primary/Music/test.mp3")

    @SuppressLint("MissingPermission")
    override fun run() {
        bluetoothAdapter?.cancelDiscovery()

        mmSocket?.let { socket ->
            socket.connect()
            Log.d(TAG, "Socket connected")

            receiveFile(socket)
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
