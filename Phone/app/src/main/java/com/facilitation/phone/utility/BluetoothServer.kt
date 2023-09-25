package com.facilitation.phone.utility

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.UUID

class BluetoothServer(private val appContext: Application, private val mp3File: File, private val activity : Activity) {

        private var btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        private var serverSocket: BluetoothServerSocket? = null
        val mainHandler = Handler(Looper.getMainLooper())
    init {
        startServer()
    }
    private fun startServer() {
        // Ensure Bluetooth is enabled
        if (!btAdapter.isEnabled) {
            return
        }

        // Create a BluetoothServerSocket with a unique UUID
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        try {
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
                return
            }
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
                return
            }
            btAdapter.bondedDevices
            serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("BPRPhone", uuid)
            Toast.makeText(appContext, "The server is up", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        // Start listening for incoming connections in a separate thread
        Thread {
            var socket: BluetoothSocket?
            mainHandler.post {
                Toast.makeText(appContext, "I am in a thread!", Toast.LENGTH_SHORT).show()
            }
            while (true) {
                try {
                    socket = serverSocket?.accept()
                } catch (e: IOException) {
                    break
                }

                // If a connection is accepted, send the MP3 file
                if (socket != null) {
                    sendMP3File(socket)
                    // Close the socket when done
                    try {
                        socket.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }
    private fun sendMP3File(socket: BluetoothSocket) {
        try {
            val outputStream = socket.outputStream
            val fileInputStream = FileInputStream(mp3File)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (true) {
                bytesRead = fileInputStream.read(buffer)
                if (bytesRead == -1) break
                outputStream.write(buffer, 0, bytesRead)
                outputStream.flush()
            }
            fileInputStream.close()
            outputStream.close()
            stopServer()
            return

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun stopServer() {
        // Close the server socket when done
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}