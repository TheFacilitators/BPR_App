package com.facilitation.phone.utility

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.facilitation.phone.R
import com.facilitation.phone.model.PlayerStateDTO
import com.facilitation.phone.model.TrackDTO
import com.google.gson.reflect.TypeToken
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.types.LibraryState
import com.spotify.protocol.types.PlayerState
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.concurrent.CompletableFuture

class SocketHandler(private val context: Context, socket : BluetoothSocket) {
    private lateinit var spotifyRemote: SpotifyAppRemote
    private val gson = Gson()
    private val writer = PrintWriter(OutputStreamWriter(socket.outputStream))
    private var playerStateDTO: PlayerStateDTO = PlayerStateDTO(TrackDTO(title = "Dummy", artist = "1", uri = "1", isFavorite = false), isPlaying = false, isShuffled = false)
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
                            subscribeToPlayerState()
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
        fun handleClientCommand(command: String) {
            Thread {
                Looper.prepare()
                when {
                    "addFavorite" in command -> spotifyRemote.userApi.addToLibrary(command.replace("addFavorite:", ""))
                    "removeFavorite" in command -> spotifyRemote.userApi.removeFromLibrary(command.replace("removeFavorite:", ""))
                    "pause" in command -> spotifyRemote.playerApi.pause()
                    "resume" in command -> spotifyRemote.playerApi.resume()
                    "previous" in command -> spotifyRemote.playerApi.skipPrevious()
                    "next" in command -> spotifyRemote.playerApi.skipNext()
                    "shuffleOn" in command -> spotifyRemote.playerApi.setShuffle(true)
                    "shuffleOff" in command -> spotifyRemote.playerApi.setShuffle(false)
                    "playlist" in command -> sendTracksDTO()
                    "track" in command -> playTrackInPlaylist(command)
                    else -> Log.e("VuzixSidekick", "I got command \"$command\" and I don't know what to do with it")
                }
                Looper.loop()
                Looper.myLooper()?.quit()
            }.start()
        }
    private fun subscribeToPlayerState() {
        spotifyRemote.playerApi.subscribeToPlayerState()
            ?.setEventCallback { playerState: PlayerState ->
                val callbackTrack = playerState.track
                if (callbackTrack != null) {
                    playerStateDTO.currentTrack.title = callbackTrack.name
                    playerStateDTO.currentTrack.uri = callbackTrack.uri
                    playerStateDTO.currentTrack.artist = callbackTrack.artists.joinToString(", ") { it.name }
                    val callResult: CallResult<LibraryState> = spotifyRemote.userApi.getLibraryState(playerStateDTO.currentTrack.uri)
                    callResult.setResultCallback { libraryState -> playerStateDTO.currentTrack.isFavorite = libraryState.isAdded}

                    playerStateDTO.isPlaying = !playerState.isPaused
                    playerStateDTO.isShuffled = playerState.playbackOptions.isShuffling

                    Log.d("VuzixSidekick", "${playerStateDTO.currentTrack.title} is set as current track")
                    sendCurrentTrack()
                }
            }
            ?.setErrorCallback {
                Log.e("VuzixSidekick", "Something went wrong when subscribing to the player state")
            }
    }
    private fun sendTracksDTO() {
        val sharedPreferencesSpotify = context.getSharedPreferences("SPOTIFY", 0)
        val tracksDTOJson = sharedPreferencesSpotify.getString("tracksDTOJson", null)
        val tracksDTOJsonUpdated = updateTrackLibraryStatus(tracksDTOJson)

        try {
            writer.println(tracksDTOJsonUpdated)
            writer.flush()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun sendCurrentTrack() {
        val currentTrackDTOJson = gson.toJson(playerStateDTO)

        try {
            writer.println(currentTrackDTOJson)
            writer.flush()

            Log.d("VuzixSidekick", "${playerStateDTO.currentTrack.title} state is transmitted to the client")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun updateTrackLibraryStatus(tracksDTOJson: String?): String? {
        val tracksDTO: List<TrackDTO> = gson.fromJson(tracksDTOJson, object : TypeToken<List<TrackDTO>>() {}.type)
        val completableFutures = mutableListOf<CompletableFuture<LibraryState>>()

        tracksDTO.forEach { track ->
            val completableFuture = CompletableFuture<LibraryState>()

            val callResult: CallResult<LibraryState> =
                spotifyRemote.userApi.getLibraryState(track.uri)
            callResult.setResultCallback { libraryState ->
                track.isFavorite = libraryState.isAdded
                completableFuture.complete(libraryState)
            }
            completableFutures.add(completableFuture)
        }
        CompletableFuture.allOf(*completableFutures.toTypedArray()).join()
        return gson.toJson(tracksDTO)
    }
    private fun playTrackInPlaylist(position: String) {
        val playlist = context.getString(R.string.playlistID)
        val finalCommand = "spotify:playlist:$playlist"
        val songPosition = position.replace("track:", "")
        spotifyRemote.playerApi.skipToIndex(finalCommand, songPosition.toInt())
    }
}