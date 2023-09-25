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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private var token:String? = ""

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
    }

    override fun onStart() {
        super.onStart()
        setupSpotifyConnection()
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote.let {
            SpotifyAppRemote.disconnect(it)
        }
    }

    private fun setupSpotifyConnection() {
        val connectionParams = ConnectionParams.Builder(getString(R.string.client_id))
            .setRedirectUri(getString(R.string.redirect_uri))
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
        spotifyAppRemote.playerApi.subscribeToPlayerState().setEventCallback {
            val song: Track = it.track
            Log.d("Spotify INFO", "Currently playing ${song.name} - ${song.artist.name}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === requestCode) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    token = response.accessToken
                    navController.navigate(R.id.navigation_home)
                }
                AuthorizationResponse.Type.ERROR -> {
                    //TODO: Display failure toast - Aldís 20.09.23
                }
                else -> {
                    //TODO: Display connectivity failure - Aldís 20.09.23
                }
            }
        }
    }
}