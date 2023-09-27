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
        private val mainHandler = Handler(Looper.getMainLooper())
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
                if (socket != null) {
                    mainHandler.post {
                        Toast.makeText(appContext, "Starting the action", Toast.LENGTH_SHORT).show()
                    }
                    delegateSocketHandling(socket)
                    mainHandler.post {
                        Toast.makeText(appContext, "Finishing the action", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.start()
    }
    private fun delegateSocketHandling(socket : BluetoothSocket) {
        Thread {
            val socketHandler = SocketHandler(socket)
            socketHandler.sendMP3File(mp3File)
        }.start()
    }
}