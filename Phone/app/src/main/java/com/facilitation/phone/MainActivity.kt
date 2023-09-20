package com.facilitation.phone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facilitation.phone.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val clientID = "f02608b7c5c84adb873b8c93c7262f40"
    private val redirectURI = "google.com"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        setupSpotifyConnection()
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    private fun setupSpotifyConnection() {
        val connectionParams = ConnectionParams.Builder(clientID)
            .setRedirectUri(redirectURI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("Spotify INFO", "Spotify connection successful.")
                subscribeToSpotifyState()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("Spotify ERROR", throwable.message, throwable)
            }
        })
    }

    private fun subscribeToSpotifyState() {
        spotifyAppRemote!!.playerApi.subscribeToPlayerState().setEventCallback {
            val song: Track = it.track
            Log.d("Spotify INFO", "Currently playing ${song.name} - ${song.artist.name}")
        }
    }
}