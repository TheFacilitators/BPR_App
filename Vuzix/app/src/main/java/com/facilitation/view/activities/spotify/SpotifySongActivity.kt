package com.facilitation.view.activities.spotify

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.facilitation.view.R
import com.facilitation.view.ViewApplication
import com.facilitation.view.databinding.ActivitySpotifySongBinding
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.CacheHelper
import com.facilitation.view.utility.interfaces.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.facilitation.view.utility.interfaces.ICache
import com.google.gson.Gson
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifySongActivity : ActionMenuActivity(), ITapInput {
    private lateinit var binding : ActivitySpotifySongBinding
    private lateinit var PlayPauseMenuItem: MenuItem
    private lateinit var NextSongMenuItem: MenuItem
    private lateinit var PrevSongMenuItem: MenuItem
    private lateinit var SongDetailsMenuItem: MenuItem
    private lateinit var FavoriteSongMenuItem: MenuItem
    private lateinit var receiver: TapReceiver
    private lateinit var currentTrackDTO: TrackDTO
    private var bluetoothHandler: BluetoothHandler? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val cacheHelper: CacheHelper = CacheHelper()
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        initCallback()
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        receiver = TapReceiver(this, activityLifecycleCallbacks)

        getBluetooth()
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu_spotify, menu)
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_item1)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_item2)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_item3)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_item4)
        FavoriteSongMenuItem = menu.findItem(R.id.menu_spotify_item5)

        setLocalTrackFromCache()
        return true
    }

    override fun onResume() {
        super.onResume()
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        try {
            getBluetooth()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        }
    }

    override fun getDefaultAction(): Int {
        return 2
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    private fun initCallback() {
        var callback = cacheHelper.getCachedActivityLifecycleCallback(this)
        if (callback == null) {
            callback = MyActivityLifecycleCallbacks(this)
            cacheHelper.setCachedActivityLifecycleCallback(this, callback)
        }

        activityLifecycleCallbacks = callback
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    private fun updateFavorite() {
        try {
            if (currentTrackDTO.isFavorite) {
                FavoriteSongMenuItem.setIcon(R.drawable.ic_remove_favorite)
            } else {
                FavoriteSongMenuItem.setIcon(R.drawable.ic_add_favorite)
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }

    private fun setLocalTrackFromCache() {
        val sharedTrack = cacheHelper.getCachedTrackDTO(this)

        if (sharedTrack == null) {
            Log.e("ERROR", "Song activity opened with no cached song in shared preferences")
            toggleMusicControlsEnabled(false)
            return
        }

        toggleMusicControlsEnabled(true)
        currentTrackDTO = sharedTrack
        updateFavorite()
    }

    private fun toggleMusicControlsEnabled(isEnabled: Boolean) {
        PlayPauseMenuItem.isEnabled = isEnabled
        NextSongMenuItem.isEnabled = isEnabled
        PrevSongMenuItem.isEnabled = isEnabled
        FavoriteSongMenuItem.isVisible = isEnabled
        SongDetailsMenuItem.isVisible = isEnabled
    }

    fun previousSong(item: MenuItem?) {
        sendBluetoothCommand("previous")
        cacheHelper.setCachedTrackDTO(this, currentTrackDTO)
    }

    fun togglePlayPause(item: MenuItem?) {
        if (currentTrackDTO.isPlaying) {
            item?.setIcon(R.drawable.ic_play)
            sendBluetoothCommand("pause")
        } else {
            item?.setIcon(R.drawable.ic_pause)
            sendBluetoothCommand("resume")
        }
        currentTrackDTO.isPlaying = !currentTrackDTO.isPlaying
        cacheHelper.setCachedTrackDTO(this, currentTrackDTO)
    }

    fun nextSong(item: MenuItem?) {
        sendBluetoothCommand("next")
        cacheHelper.setCachedTrackDTO(this, currentTrackDTO)
    }

    fun showSongDetails(item: MenuItem?) {
        showToast("${currentTrackDTO.title} - ${currentTrackDTO.artist}")
    }

    fun toggleFavorite(item: MenuItem?) {
        if (currentTrackDTO.isFavorite) {
            item?.setIcon(R.drawable.ic_add_favorite)
            sendBluetoothCommand("removeFavorite:${currentTrackDTO.uri}")
            showToast("Song removed from favorites")
        } else {
            item?.setIcon(R.drawable.ic_remove_favorite)
            sendBluetoothCommand("addFavorite:${currentTrackDTO.uri}")
            showToast("Song added to favorites")
        }
        currentTrackDTO.isFavorite = !currentTrackDTO.isFavorite
    }

    private fun getBluetooth() {
        val app = application as ViewApplication
        bluetoothHandler = app.bluetoothHandler
        bluetoothAdapter = app.bluetoothAdapter
    }

    private fun sendBluetoothCommand(command: String) {
        bluetoothHandler!!.sendCommand(command)
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(PlayPauseMenuItem.actionView, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(PlayPauseMenuItem.actionView, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }


}