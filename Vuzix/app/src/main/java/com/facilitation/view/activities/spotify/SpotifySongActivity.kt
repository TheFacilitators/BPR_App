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
import com.facilitation.view.model.PlayerStateDTO
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
    private var currentStateDTO: PlayerStateDTO? = null
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

        if (getPlayerState()) {
            updateUI()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        try {
            getBluetooth()
            if (getPlayerState()) {
                updateUI()
            }
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

    /** Calls sendReturnableBluetoothCommand() to retrieve the player state from the server.
     * On success: Parses the response & updates the local & cached PlayerStateDTO in shared preferences.
     * On failure: Sets currentStateDTO to null and calls toggleMusicControlsEnabled()
     * @return When state is successfully retrieved, parsed and assigned: 'true' else 'false'.*/
    private fun getPlayerState(): Boolean {
        val response = sendReturnableBluetoothCommand("currentPlayerState")
        return try {
            val state = gson.fromJson(response, PlayerStateDTO::class.java)
            cacheHelper.setCachedPlayerState(this, state)
            currentStateDTO = state
            true
        } catch (e: Exception) {
            Log.e("ERROR", e.stackTraceToString())
            currentStateDTO = null
            toggleMusicControlsEnabled(false)
            false
        }
    }

    /** Enables and updates the state of the UI menu items based on currentStateDTO.*/
    private fun updateUI() {
        toggleMusicControlsEnabled(true)
        try {
            updatePlayPauseMenuItem(currentStateDTO!!.isPlaying)
            updateShuffleMenuItem(currentStateDTO!!.isShuffled)
            updateFavoriteMenuItem(currentStateDTO!!.currentTrack.isFavorite)
        } catch (e: NullPointerException) {
            Log.e("SongActivity", "Error updating music control UI:\n" + e.stackTraceToString())
        }
    }

    private fun updatePlayPauseMenuItem(isPlaying: Boolean) {
        if (isPlaying) {
            PlayPauseMenuItem.setIcon(R.drawable.ic_pause)
        } else {
            PlayPauseMenuItem.setIcon(R.drawable.ic_play)
        }
    }

    private fun updateShuffleMenuItem(isShuffled: Boolean) {
        if (isShuffled) {
            ShuffleMenuItem.setIcon(R.drawable.ic_shuffle_on)
        } else {
            ShuffleMenuItem.setIcon(R.drawable.ic_shuffle_off)
        }
    }

    private fun updateFavoriteMenuItem(isFavorite: Boolean) {
        if (isFavorite) {
            FavoriteSongMenuItem.setIcon(R.drawable.ic_remove_favorite)
        } else {
            FavoriteSongMenuItem.setIcon(R.drawable.ic_add_favorite)
        }
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
        if (getPlayerState()) {
            updateUI()
        }
    }

    fun togglePlayPause(item: MenuItem?) {
        if (getPlayerState()) {
            if (currentStateDTO!!.isPlaying) {
                sendBluetoothCommand("pause")
            } else {
                sendBluetoothCommand("resume")
            }
            updateUI()
        }
    }

    fun nextSong(item: MenuItem?) {
        sendBluetoothCommand("next")
        if (getPlayerState()) {
            updateUI()
        }
    }

    fun showSongDetails(item: MenuItem?) {
        if (getPlayerState()) {
            showToast("${currentStateDTO!!.currentTrack.title} - ${currentStateDTO!!.currentTrack.artist}")
        }
    }

    fun toggleFavorite(item: MenuItem?) {
        if (getPlayerState()) {
            if (currentStateDTO!!.currentTrack.isFavorite) {
                sendBluetoothCommand("removeFavorite:${currentStateDTO!!.currentTrack.uri}")
                showToast("Song removed from favorites")
            } else {
                item?.setIcon(R.drawable.ic_remove_favorite)
                sendBluetoothCommand("addFavorite:${currentStateDTO!!.currentTrack.uri}")
                showToast("Song added to favorites")
            }
            updateUI()
        }
    }

    fun toggleShuffle(item: MenuItem?) {
        if (getPlayerState()) {
            if (currentStateDTO!!.isShuffled) {
                sendBluetoothCommand("shuffleOff")
            } else {
                sendBluetoothCommand("shuffleOn")
            }
            updateUI()
        }
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