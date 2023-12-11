package com.facilitation.phone.utility

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
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
        if (!btAdapter.isEnabled) {
            Log.e("VuzixSidekick", "Bluetooth not enabled")
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
            Log.i("VuzixSidekick", "The bluetooth server is up")
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
    private fun startListeningForConnections() {
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
        try {
            Looper.prepare()
            val clientInput = BufferedReader(InputStreamReader(socket.inputStream))
            Log.i("VuzixSidekick", "Socket connection established")
            while (true) {
                val command = clientInput.readLine()
                Log.i("VuzixSidekick", "Bluetooth server received \"$command\" command")
                if(command == "exit" || command == "quit")
                    break
                socketHandler.handleClientCommand(command, socket)
                Log.i("VuzixSidekick", "\"$command\" was executed successfully")
            }
            clientInput.close()
            socket.close()
            Log.i("VuzixSidekick", "Socket connection closed")
        }
        catch (e : IOException) {
            Log.e("VuzixSidekick", "Socket crashed. \nThe reason:\n${e.stackTrace}")
            e.printStackTrace()
        }
            Looper.loop()
            Looper.myLooper()?.quit()
        }.start()
    }
}