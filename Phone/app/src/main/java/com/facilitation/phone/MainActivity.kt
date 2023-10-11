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
import com.facilitation.phone.model.Track
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
    private lateinit var trackList: List<Track>

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
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_login
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        authorizationSpotify()
        retrievePlaylistTracks()

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
                    Log.d("STARTING", "GOT AUTH TOKEN")
                    editor.apply()
                    //waitForUserInfo()
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("Spotify ERROR", "Error: ${response.error}")
                }
                else -> {
                    Log.e("Unknown error", "Error: ${response.error}")
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
            .url("https://api.spotify.com/v1/playlists/2zr0PfJnuEvTnuXSzUSnr7?si=18dcf113959a422c/tracks")
            .header("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        val responseData = response.body?.string()
        deserializeIntoTracks(responseData)
        }.start()
    }

    private fun deserializeIntoTracks(response: String?) {
        val gson = Gson()
        val playlist = gson.fromJson(response, SpotifyPlaylist::class.java)
        val tracks: List<Track> = playlist.tracks.items.map { it.track }
    }
}