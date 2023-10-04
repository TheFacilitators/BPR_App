package com.facilitation.phone

import android.content.Intent
import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facilitation.phone.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    }

    override fun onStart() {
        super.onStart()
        initSpotifyAuth()
    }

    private fun initSpotifyAuth() {
        Log.d("Spotify INFO", "Starting Spotify authentication")
        val builder = AuthorizationRequest.Builder(getString(R.string.client_id),
            AuthorizationResponse.Type.TOKEN,
            getString(R.string.redirect_uri))

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, _requestCode, request)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navController.navigate(R.id.navigation_home)

        if (requestCode == _requestCode) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    token = response.accessToken
                    Log.d("Spotify INFO", "Token received: ${token.toString()}")
                    Toast.makeText(applicationContext, "Spotify token successfully obtained", Toast.LENGTH_SHORT).show()
                }
                AuthorizationResponse.Type.ERROR -> {
                    Toast.makeText(applicationContext, "Invalid Spotify credentials", Toast.LENGTH_LONG).show()
                    Log.d("Spotify ERROR", "Error: ${response.error}")

                }
                else -> {
                    Toast.makeText(applicationContext, "Unable to verify Spotify credentials", Toast.LENGTH_LONG).show()
                    Log.e("Spotify ERROR", "Returned authorization unknown: ${response.type}\nError: ${response.error}")
                }
            }
        }
    }
}