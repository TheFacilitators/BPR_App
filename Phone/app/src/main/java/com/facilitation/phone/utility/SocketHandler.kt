package com.facilitation.phone.utility

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.facilitation.phone.R
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SocketHandler(private val socket: BluetoothSocket) {
    fun playSongFromSpotify(context: Context) {
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
            closeSocket()
            Looper.myLooper()?.quit()
        }.start()
    }

    private fun closeSocket() {
        // Close the server socket when done
        try {
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}