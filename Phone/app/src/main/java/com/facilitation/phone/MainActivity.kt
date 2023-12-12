package com.facilitation.phone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facilitation.phone.databinding.ActivityMainBinding
import com.facilitation.phone.utility.authorization.AuthorizationHandler
import com.facilitation.phone.utility.authorization.IAuthorization
import com.facilitation.phone.utility.data.DataHandler
import com.facilitation.phone.utility.data.IData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var dataHandler: IData
    private var authorizationHandler: IAuthorization = AuthorizationHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataHandler = DataHandler(this)

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

        authorizationHandler.authorizationSpotify(this)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
                ),
                1
            )
        }
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
            dataHandler.retrievePlaylist()
        }.start()
    }
}