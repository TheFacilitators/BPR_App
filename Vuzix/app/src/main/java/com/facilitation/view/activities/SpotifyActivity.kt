package com.facilitation.view.activities

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facilitation.view.R
import com.facilitation.view.utility.BluetoothHandler
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifyActivity : ActionMenuActivity() {
    private var PlayPauseMenuItem: MenuItem? = null
    private var NextSongMenuItem: MenuItem? = null
    private var PrevSongMenuItem: MenuItem? = null
    private var SongDetailsMenuItem: MenuItem? = null
    private var isPlaying = false
    private var isPaused = false
    private var bluetoothHandler: BluetoothHandler? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not available on this device")
        }
        initBluetooth()
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu_spotify, menu)
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_item1)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_item2)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_item3)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_item4)
        return true
    }

    override fun getDefaultAction(): Int {
        return 2
    }

    override fun alwaysShowActionMenu(): Boolean {
        return false
    }

    override fun setCurrentMenuItem(item: MenuItem?, animate: Boolean) {
        super.setCurrentMenuItem(item, animate)
    }

    override fun onActionMenuClosed() {
        sendBluetoothCommand("exit")
    }


    fun previousSong(item: MenuItem?) {
        showToast("Previous Song!")
        sendBluetoothCommand("previous")
    }

    fun togglePlayPause(item: MenuItem?) {
        if (isPlaying) {
            if(isPaused)
            {
                item?.setIcon(R.drawable.ic_pause)
                sendBluetoothCommand("resume")
                showToast("resume")
            }else{
                item?.setIcon(R.drawable.ic_play)
                sendBluetoothCommand("pause")
                showToast("pause")
            }
            isPaused = !isPaused
        }else{
            //TODO: move "play" to song selection when ready
            item?.setIcon(R.drawable.ic_pause)
            sendBluetoothCommand("play")
            showToast("play")
            isPlaying = !isPlaying
        }
    }

    fun nextSong(item: MenuItem?) {
        showToast("Next Song!")
        sendBluetoothCommand("next")
    }

    fun showSongDetails(item: MenuItem?) {
        // Show song details here
        showToast("Total Eclipse of The Heart - Bonnie Tyler")
    }

    private fun initBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothHandler = BluetoothHandler(this)
            bluetoothHandler?.initiateConnection()
        }
    }

    private fun sendBluetoothCommand(command: String) {
        bluetoothHandler?.sendCommandToServer(command)
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }
}
