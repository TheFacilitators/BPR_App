package com.facilitation.view

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facilitation.view.utility.ConnectThread
import com.vuzix.hud.actionmenu.ActionMenuActivity


class SpotifyActivity : ActionMenuActivity() {
    var PlayPauseMenuItem: MenuItem? = null
    var NextSongMenuItem: MenuItem? = null
    var PrevSongMenuItem: MenuItem? = null
    var SongDetailsMenuItem: MenuItem? = null
    private var isPlaying = false
    val mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify)
        mediaPlayer.setDataSource("/storage/self/primary/Music/test.mp3")
        mediaPlayer.prepare()
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
        return true
    }

    override fun setCurrentMenuItem(item: MenuItem?, animate: Boolean) {
        super.setCurrentMenuItem(item, animate)
    }

    fun previousSong(item: MenuItem?) {
        showToast("Previous Song!")
    }

    fun togglePlayPause(item: MenuItem?) {
        if (isPlaying) {
            //showToast("Pause")
            pauseSong()
            item?.setIcon(R.drawable.ic_play)
        }
        else
        {
            //showToast("Play")
            playSong()
            item?.setIcon(R.drawable.ic_pause)
        }
        isPlaying = !isPlaying
    }

    private fun pauseSong() {
        mediaPlayer.pause()
    }

    private fun playSong() {
        mediaPlayer.start()
    }


    fun nextSong(item: MenuItem?) {
        showToast("Next Song")
    }

    fun showSongDetails(item: MenuItem?) {
        //showToast("Total Eclipse of The Heart - Bonnie Tyler")
        initBluetooth()
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    private fun initBluetooth() {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
        val serverDevice = bluetoothAdapter.bondedDevices.find {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
            it.name == "Galaxy S21 5G"}
        if (serverDevice != null) {
            val connectThread: ConnectThread =
                serverDevice.let { ConnectThread(it, bluetoothAdapter) }
            connectThread.start()
            showToast("Connection Started")
        } else {
            showToast("Connection Failed")
        }
    }
}