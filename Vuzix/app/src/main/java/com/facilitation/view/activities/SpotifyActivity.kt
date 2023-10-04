package com.facilitation.view.activities

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.core.view.get
import androidx.core.view.iterator
import com.facilitation.view.R
import com.facilitation.view.databinding.ActivitySpotifyBinding
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.TapInputHandler
import com.tapwithus.sdk.TapSdk
import com.vuzix.hud.actionmenu.ActionMenuActivity
import java.io.IOException


class SpotifyActivity(tapSDK: TapSdk) : ActionMenuActivity() {
    private lateinit var tapHandler : TapInputHandler
    private lateinit var binding : ActivitySpotifyBinding
    private val tapSDK : TapSdk = tapSDK
    var PlayPauseMenuItem: MenuItem? = null
    var NextSongMenuItem: MenuItem? = null
    var PrevSongMenuItem: MenuItem? = null
    var SongDetailsMenuItem: MenuItem? = null
    private lateinit var menuArray : Array<MenuItem>
    var currentMenuItem : MenuItem? = null
    private var isPlaying = false
    val mediaPlayer = MediaPlayer()
    private var currentDataSource: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tapHandler = TapInputHandler(this, tapSDK)
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu_spotify, menu)
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_item1)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_item2)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_item3)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_item4)

        menuArray = arrayOf(menu[0], PrevSongMenuItem!!, PlayPauseMenuItem!!, NextSongMenuItem!!, SongDetailsMenuItem!!)

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
        currentMenuItem = item
    }

    fun previousSong(item: MenuItem?) {
        showToast("Previous Song!")
        checkMenuItem(1)
        TODO("Waiting for Spotify integration")
    }

    fun nextSong(item: MenuItem?) {
        showToast("Next Song!")
        checkMenuItem(3)
        TODO("Waiting for Spotify integration")
    }

    fun showSongDetails(item: MenuItem?) {
        //showToast("Total Eclipse of The Heart - Bonnie Tyler")
        initBluetooth()
        checkMenuItem(4)
    }

    private fun updateDataSource() {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(currentDataSource)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun initBluetooth() {
        val bluetoothHandler = BluetoothHandler(this)
        currentDataSource = bluetoothHandler.initiateConnection()
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    fun executeSelectedMenuItem() {
        when(menuArray.indexOf(currentMenuItem)) {
            0 -> navigateBack()
            1 -> previousSong(currentMenuItem)
            2 -> playPauseMusic()
            3 -> nextSong(currentMenuItem)
            4 -> initBluetooth()
            else -> Log.d("ERROR", "Invalid menu item selected for execution")
        }
    }

    fun navigateBack() {
        checkMenuItem(0)
        NavUtils.navigateUpFromSameTask(this)
    }

    fun moveLeft() {
        if (currentMenuItem == menuArray[0]) return
        currentMenuItem = menuArray[menuArray.indexOf(currentMenuItem)-1]
        setCurrentMenuItem(currentMenuItem, true)
    }

    fun moveRight() {
        if (currentMenuItem == menuArray[4]) return
        currentMenuItem = menuArray[menuArray.indexOf(currentMenuItem)+1]
        setCurrentMenuItem(currentMenuItem, true)
    }

    fun playPauseMusic() {
        if (isPlaying) {
            //showToast("Pause")
            mediaPlayer.pause()
            currentMenuItem?.setIcon(R.drawable.ic_play)
        }
        else
        {
            //showToast("Play")
            if(currentDataSource == ""){
                showToast("No Song available")
            }else {
                updateDataSource()
                mediaPlayer.start()
                currentMenuItem?.setIcon(R.drawable.ic_pause)
            }
        }
        isPlaying = !isPlaying
        checkMenuItem(2)
    }

    private fun checkMenuItem(desiredPosition : Int) {
        if (currentMenuItem != menuArray[desiredPosition])
            setCurrentMenuItem(menuArray[desiredPosition], true)
    }
}