package com.facilitation.phone

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
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
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val _requestCode = 9485
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

    private fun playSongFromSpotify() {
            val sharedPreferencesSpotify = getSharedPreferences("SPOTIFY", 0)
            val token = sharedPreferencesSpotify.getString("token", null)
            if (token != null) {
                val connectionParams = ConnectionParams.Builder(getString(R.string.client_id))
                    .setRedirectUri(getString(R.string.redirect_uri))
                    .showAuthView(true)
                    .build()

                SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
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
    }
}