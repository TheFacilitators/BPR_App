package com.facilitation.phone.utility.data

import android.content.Context
import android.util.Log
import com.facilitation.phone.R
import com.facilitation.phone.model.SpotifyPlaylist
import com.facilitation.phone.model.TrackDTO
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class DataHandler(private var context: Context) : IData {
    private val gson = Gson()

    override fun retrievePlaylist() {
        val sharedPreferencesSpotify = context.getSharedPreferences("SPOTIFY", 0)
        val token = sharedPreferencesSpotify.getString("token", null)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(context.getString(R.string.playlist_uri))
            .header("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        val responseData = response.body?.string()
        deserializeIntoTracks(responseData)
    }

    private fun deserializeIntoTracks(response: String?) {
        val playlist = gson.fromJson(response, SpotifyPlaylist::class.java)

        //Constructs tracksDTO list based on the playlist retrieved from Spotify
        val tracksDTO: List<TrackDTO> = playlist.tracks.items.map { item ->
            //In case a song has multiple artists, this will take care of it
            val concatenatedArtists = item.track.artists.joinToString(", ") { it.name }
            TrackDTO(item.track.name, concatenatedArtists, item.track.uri, false)
        }
        storeTracksDTO(tracksDTO)
    }

    private fun storeTracksDTO(tracksDTO: List<TrackDTO>) {
        val tracksDTOJson = gson.toJson(tracksDTO)
        val sharedPreferences = context.getSharedPreferences("SPOTIFY", 0)
        val editor = sharedPreferences.edit()
        editor.putString("tracksDTOJson", tracksDTOJson)
        editor.apply()
        Log.i("VuzixSidekick", "Playlist with ${tracksDTO.size} tracks successfully retrieved and saved in shared preferences")
    }
}