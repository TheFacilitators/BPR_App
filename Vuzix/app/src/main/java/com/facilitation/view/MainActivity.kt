package com.facilitation.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.vuzix.hud.actionmenu.ActionMenuActivity


class MainActivity : ActionMenuActivity() {
    var HelloMenuItem: MenuItem? = null
    var SpotifyMenuItem: MenuItem? = null
    var mainText: TextView? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeBroadcastReceiver()
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        HelloMenuItem = menu.findItem(R.id.item1)
        SpotifyMenuItem = menu.findItem(R.id.item2)
        mainText = findViewById(R.id.mainTextView)
        updateMenuItems()
        return true
    }
    override fun alwaysShowActionMenu(): Boolean {
        return false
    }

    private fun updateMenuItems() {
        if (HelloMenuItem == null) {
            return
        }
        SpotifyMenuItem?.isEnabled = false
    }

    //Action Menu Click events
    //This events where register via the XML for the menu definitions.
    fun showHello(item: MenuItem?) {
        val broadcastIntent = Intent("com.facilitation.view.GET")
        broadcastIntent.putExtra("broadcastMessage", "Goodbye World!")
        sendBroadcast(broadcastIntent)
        mainText?.let {
            it.text = "Hello World!"
        }
        SpotifyMenuItem?.isEnabled = true
    }

    fun showSpotify(item: MenuItem?) {
        showToast("Spotify!")
        mainText?.let {
            it.text = "Spotify!"
        }
        HelloMenuItem?.isEnabled = true
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    override fun onResume() {
        super.onResume()

        // Register the BroadcastReceiver when the activity is in the foreground
        registerBroadcastReceiver()
    }

    override fun onPause() {
        super.onPause()

        // Unregister the BroadcastReceiver when the activity is in the background
        unregisterBroadcastReceiver()
    }

    private fun initializeBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    val message = intent.action
                    showToast("Received broadcast: $message")
                }
            }
        }
        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter("com.facilitation.view.GET")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver)
    }
}