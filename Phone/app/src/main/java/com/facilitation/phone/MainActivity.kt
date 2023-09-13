package com.facilitation.phone

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facilitation.phone.databinding.ActivityMainBinding
import com.vuzix.connectivity.sdk.Connectivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var ACTION_GET = "com.facilitation.vuzix.GET"

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

    private fun getRemoteDeviceModelClicked() {
        try {
            val connectivity = Connectivity.get(this)
            val device = connectivity.device
            if (device != null) {
                val getIntent = Intent(this.ACTION_GET)
                connectivity.sendBroadcast(getIntent)
            }
        }
        catch (e:Exception) {
            print(e.stackTrace)
        }
    }
}