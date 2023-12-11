package com.facilitation.phone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facilitation.phone.databinding.ActivityMainBinding
import com.facilitation.phone.model.SpotifyPlaylist
import com.facilitation.phone.model.TrackDTO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        authorizationSpotify()
    }


    private fun authorizationSpotify() {
        val builder: AuthorizationRequest.Builder =
            AuthorizationRequest.Builder(getString(R.string.client_id), AuthorizationResponse.Type.TOKEN, getString(R.string.redirect_uri))
        builder.setScopes(arrayOf("streaming"))
        val request: AuthorizationRequest = builder.build()
        AuthorizationClient.openLoginActivity(this, 9485, request)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // Check if result comes from the correct activity
        if (requestCode == 9485) {
            val response = AuthorizationClient.getResponse(resultCode, intent)

            when (response.type) {
                // Response was successful and contains auth token
                AuthorizationResponse.Type.TOKEN -> {
                    val editor = getSharedPreferences("SPOTIFY", 0).edit()
                    editor.putString("token", response.accessToken)
                    editor.apply()
                    Log.d("VuzixSidekick", "GOT AUTH TOKEN")
                    //Retrieving the playlist only when the new token is acquired
                    retrievePlaylistTracks()
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("VuzixSidekick", "Error: ${response.error}")
                }
                else -> {
                    Log.e("VuzixSidekick", "Error: ${response.error}")
                }
            }
        }
    }

    private fun retrievePlaylistTracks() {
        Thread {
        val sharedPreferencesSpotify = getSharedPreferences("SPOTIFY", 0)
        val token = sharedPreferencesSpotify.getString("token", null)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(getString(R.string.playlist_uri))
            .header("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        val responseData = response.body?.string()
        deserializeIntoTracks(responseData)
        }.start()
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
        val sharedPreferences = getSharedPreferences("SPOTIFY", 0)
        val editor = sharedPreferences.edit()
        editor.putString("tracksDTOJson", tracksDTOJson)
        editor.apply()
        Log.i("VuzixSidekick", "Playlist with ${tracksDTO.size} tracks successfully retrieved and saved in shared preferences")
    }
}