package com.facilitation.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.vuzix.hud.actionmenu.ActionMenuActivity

class MainActivity : ActionMenuActivity() {

    var SpotifyMenuItem: MenuItem? = null
    var SnakeMenuItem: MenuItem? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeBroadcastReceiver()
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        SpotifyMenuItem = menu.findItem(R.id.item1)
        SnakeMenuItem = menu.findItem(R.id.item2)
        return true
    }

    override fun getDefaultAction(): Int {
        return 1
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    override fun setCurrentMenuItem(item: MenuItem?, animate: Boolean) {
        super.setCurrentMenuItem(item, animate)
    }

    fun showSpotify(item: MenuItem?) {
        showToast("Spotify!")
        val intent = Intent(this, SpotifyActivity::class.java)
        startActivity(intent)
    }

    fun showSnake(item: MenuItem?) {
        showToast("Snake II: Cold blooded revenge!")
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