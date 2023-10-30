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
import java.io.OutputStreamWriter
import java.io.PrintWriter

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
                            Log.i("VuzixSidekick", "SpotifyAppRemote connection established")
                        }
                        override fun onFailure(throwable: Throwable) {
                            Log.e("VuzixSidekick", "SpotifyAppRemote connection failed: ${throwable.message}")
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
                if(command.contains("song")) {
                    val uri = command.replace("song", "", ignoreCase = true)
                    spotifyRemote.playerApi.play(uri)
                }
                when (command) {
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
                        Log.e("VuzixSidekick", "I got command \"$command\" and I don't know what to do with it")
                    }
                }
                Looper.loop()
                Looper.myLooper()?.quit()
            }.start()
        }
    private fun sendTracksDTO(socket: BluetoothSocket) {
        val sharedPreferencesSpotify = context.getSharedPreferences("SPOTIFY", 0)
        val tracksDTOJson = sharedPreferencesSpotify.getString("tracksDTOJson", null)
        val writer = PrintWriter(OutputStreamWriter(socket.outputStream))

        try {
            writer.println(tracksDTOJson)
            writer.flush()

        } catch (e: IOException) {
            e.printStackTrace()
        }
//        finally {
//            writer.close()
//        }

    }
}