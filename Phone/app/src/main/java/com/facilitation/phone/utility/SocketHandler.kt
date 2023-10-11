package com.facilitation.phone.utility

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Looper
import android.util.Log
import com.facilitation.phone.R
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.io.IOException

class SocketHandler(private val context: Context) {
    private lateinit var spotifyRemote: SpotifyAppRemote

    init {
        Thread {
            Looper.prepare()
            val sharedPreferencesSpotify = context.getSharedPreferences("SPOTIFY", 0)
            val token = sharedPreferencesSpotify.getString("token", null)
            if (token != null) {
                val connectionParams =
                    ConnectionParams.Builder(context.getString(R.string.client_id))
                        .setRedirectUri(context.getString(R.string.redirect_uri))
                        .showAuthView(false)
                        .build()
                SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
                        override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                            spotifyRemote = spotifyAppRemote
                            Log.i("SpotifyRemote", "SpotifyAppRemote connection established")
                        }
                        override fun onFailure(throwable: Throwable) {
                            Log.e("SpotifyRemoteFailed", "SpotifyAppRemote connection failed: ${throwable.message}")
                        }
                    })
            }
            Looper.loop()
            Looper.myLooper()?.quit()
        }.start()
    }
        fun handleClientCommand(command: String, socket: BluetoothSocket) {
            Thread {
                Looper.prepare()
                when (command) {
                    "play" -> {
                        spotifyRemote.playerApi.play("spotify:track:1qAuIPMALdFtGv2Ymjy5l0")
                    }
                    "pause" -> {
                        spotifyRemote.playerApi.pause()
                    }
                    "resume" -> {
                        spotifyRemote.playerApi.resume()
                    }
                    "playlist" -> {
                        sendTracksDTO(socket)
                    }
                    else -> {
                        Log.e("UnknownCommand", "I got command $command and I don't know what to do with it")
                    }
                }
                Looper.loop()
                Looper.myLooper()?.quit()
            }.start()
        }
    private fun sendTracksDTO(socket: BluetoothSocket) {
        val sharedPreferencesSpotify = context.getSharedPreferences("SPOTIFY", 0)
        val tracksDTOJson = sharedPreferencesSpotify.getString("tracksDTOJson", null)

        try {
            val outputStream = socket.outputStream

            val byteArray = tracksDTOJson?.toByteArray()

            outputStream.write(byteArray)
            outputStream.flush()

            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}