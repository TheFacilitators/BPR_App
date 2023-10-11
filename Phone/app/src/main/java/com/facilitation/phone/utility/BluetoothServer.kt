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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale
import java.util.UUID

class BluetoothServer(private val appContext: Application, private val activity : Activity) {

        private var btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        private var serverSocket: BluetoothServerSocket? = null
        private val socketHandler: SocketHandler = SocketHandler(appContext)
     fun startServer(): Boolean {
        // Ensure Bluetooth is enabled
        if (!btAdapter.isEnabled) {
            Log.e("Bluetooth", "Bluetooth not enabled")
            return false
        }
        try {
            //Checking for required permissions
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS), 2)
            }
            // Create a BluetoothServerSocket with a unique UUID
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("BPRPhone", uuid)
            startListeningForConnections()
            Toast.makeText(appContext, "The bluetooth server is up", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
    private fun startListeningForConnections() {
        // Start listening for incoming connections in a separate thread
        Thread {
            var socket: BluetoothSocket?
            while (true) {
                try {
                    socket = serverSocket?.accept()
                } catch (e: IOException) {
                    break
                }
                if (socket != null) {
                    listenOnThisSocket(socket)
                }
            }
        }.start()
    }
    private fun listenOnThisSocket(socket : BluetoothSocket) {
        Thread {
            Looper.prepare()
            val clientInput = BufferedReader(InputStreamReader(socket.inputStream))
            Log.i("BluetoothServer", "Connection established")
            while (true) {
                val command = clientInput.readLine().lowercase(Locale.ROOT)
                Log.i("Command received", "Bluetooth server received $command command")
                if(command == "exit" || command == "quit")
                    break
                socketHandler.handleClientCommand(command)
            }
            clientInput.close()
            socket.close()
            Log.i("BluetoothServer", "Connection closed")
            Looper.loop()
            Looper.myLooper()?.quit()
        }.start()
    }
}