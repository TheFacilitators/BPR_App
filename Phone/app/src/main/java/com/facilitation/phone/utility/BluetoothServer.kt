package com.facilitation.phone.utility

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facilitation.phone.R
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.io.File
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
        try {
            //Checking for required permissions
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
                return
            }
            // Create a BluetoothServerSocket with a unique UUID
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            btAdapter.bondedDevices
            serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("BPRPhone", uuid)
            startListeningForConnections()
            Toast.makeText(appContext, "The bluetooth server is up", Toast.LENGTH_SHORT).show()
            playSongFromSpotify(appContext)
        } catch (e: IOException) {
            e.printStackTrace()
            return
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
                    delegateSocketHandling(socket)
                }
            }
        }.start()
    }
    private fun delegateSocketHandling(socket : BluetoothSocket) {
        Thread {
            val socketHandler = SocketHandler(socket)
            mainHandler.post {
                Toast.makeText(appContext, "Started sending the mp3 file", Toast.LENGTH_SHORT).show()
            }
            socketHandler.sendMP3File(mp3File)
            mainHandler.post {
                Toast.makeText(appContext, "Finished sending the mp3 file", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun playSongFromSpotify(context: Context) {
        Thread {
            Looper.prepare()
            val sharedPreferencesSpotify = context.getSharedPreferences("SPOTIFY", 0)
            val token = sharedPreferencesSpotify.getString("token", null)
            if (token != null) {
                val connectionParams = ConnectionParams.Builder(context.getString(R.string.client_id))
                    .setRedirectUri(context.getString(R.string.redirect_uri))
                    .showAuthView(false)
                    .build()

                SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        // Connection successful
                        spotifyAppRemote.playerApi.play("spotify:track:1qAuIPMALdFtGv2Ymjy5l0")
                    }

                    override fun onFailure(throwable: Throwable) {
                        // Connection failed
                        Log.e(
                            "MainActivity",
                            "SpotifyAppRemote connection failed: ${throwable.message}"
                        )
                    }
                })
            }
            Looper.loop()
            Looper.myLooper()?.quit()
        }.start()
    }
}