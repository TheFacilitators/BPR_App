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
    private val gson = Gson()
    private lateinit var binding : ActivitySpotifySongBinding
    private lateinit var PlayPauseMenuItem: MenuItem
    private lateinit var NextSongMenuItem: MenuItem
    private lateinit var PrevSongMenuItem: MenuItem
    private lateinit var ShuffleMenuItem: MenuItem
    private lateinit var SongDetailsMenuItem: MenuItem
    private lateinit var FavoriteSongMenuItem: MenuItem
    private lateinit var receiver: TapReceiver
    private var isPaused = false
    private var isShuffled = false
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
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_previousSong)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_togglePlay)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_nextSong)
        ShuffleMenuItem = menu.findItem(R.id.menu_spotify_toggleShuffle)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_songDetails)
        FavoriteSongMenuItem = menu.findItem(R.id.menu_spotify_toggleFavorite)

        setLocalTrackFromCache()
        return true
    }

    override fun onResume() {
        super.onResume()
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        try {
            getBluetooth()
            updateCurrentTrack()
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

    private fun updateCurrentTrack() {
        val response = sendReturnableBluetoothCommand("currentTrack")
        try {
            val track = gson.fromJson(response, TrackDTO::class.java)
            track.isPlaying = true
            currentTrackDTO = track
            cacheHelper.setCachedTrackDTO(this, currentTrackDTO)
        } catch (e: Exception) {
            Log.e("ERROR", e.stackTraceToString())
        }
    }

    private fun updateFavorite() {
        try {
            if (currentTrackDTO.isFavorite) {
                FavoriteSongMenuItem.setIcon(R.drawable.ic_remove_favorite)
            } else {
                FavoriteSongMenuItem.setIcon(R.drawable.ic_add_favorite)
            }
            cacheHelper.setCachedTrackDTO(this, currentTrackDTO)
        } catch (e: Exception) {
            Log.e("ERROR", e.stackTraceToString())
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
        showSongDetails(item)
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
        showSongDetails(item)
    }

    fun showSongDetails(item: MenuItem?) {
        updateCurrentTrack()
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
        cacheHelper.setCachedTrackDTO(this, currentTrackDTO)
    }

    fun toggleShuffle(item: MenuItem?) {
        if (isShuffled) {
            ShuffleMenuItem.setIcon(R.drawable.ic_shuffle_off)
            sendBluetoothCommand("shuffleOff")
        } else {
            ShuffleMenuItem.setIcon(R.drawable.ic_shuffle_on)
            sendBluetoothCommand("shuffleOn")
        }
        isShuffled = !isShuffled
    }

    private fun getBluetooth() {
        val app = application as ViewApplication
        bluetoothHandler = app.bluetoothHandler
        bluetoothAdapter = app.bluetoothAdapter
    }

    private fun sendBluetoothCommand(command: String) {
        bluetoothHandler!!.sendCommand(command)
    }

    private fun sendReturnableBluetoothCommand(command: String):String {
        try {
            return bluetoothHandler!!.sendReturnableCommand(command)
        } catch(e: Exception) {
            Log.e(ContentValues.TAG, e.stackTraceToString())
        }
        return ""
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