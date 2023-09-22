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
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val clientID = "f02608b7c5c84adb873b8c93c7262f40"
    private val redirectURI = "google.com"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val REQUEST_CODE = 1337

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
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_login
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

    private fun initSpotifyAuth()
    {
        val builder =
            AuthorizationRequest.Builder(getString(R.string.client_id), AuthorizationResponse.Type.TOKEN, getString(R.string.redirect_uri))

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
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