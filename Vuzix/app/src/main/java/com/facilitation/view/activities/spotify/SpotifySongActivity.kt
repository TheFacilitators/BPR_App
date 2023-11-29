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
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.google.gson.Gson
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifySongActivity : ActionMenuActivity(), ITapInput {
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
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager

    /** Initializing activityLifecycleCallbacks and currentTrackDTO from Intent Extras.
     * Creating inputMethodManager & tapReceiver, as well as calling getBluetooth().*/
    override fun onCreate(savedInstanceState: Bundle?) {
        activityLifecycleCallbacks = intent.getSerializableExtra("callback") as MyActivityLifecycleCallbacks
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        receiver = TapReceiver(this, activityLifecycleCallbacks)

        getBluetooth()

        currentTrackDTO = intent.getSerializableExtra("track") as TrackDTO
    }

    /** Initializing menu items by ID and calling updateFavorite().*/
    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu_spotify, menu)
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_item1)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_item2)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_item3)
        ShuffleMenuItem = menu.findItem(R.id.menu_spotify_item4)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_item5)
        FavoriteSongMenuItem = menu.findItem(R.id.menu_spotify_item6)

        updateFavorite()
        return true
    }

    /** Trying to sync UI button icon with currentTrackDTO.isFavorite boolean.*/
    private fun updateFavorite() {
        try {
            if (currentTrackDTO.isFavorite) {
                FavoriteSongMenuItem.setIcon(R.drawable.ic_remove_favorite)
            } else {
                FavoriteSongMenuItem.setIcon(R.drawable.ic_add_favorite)
            }
        } catch (e: UninitializedPropertyAccessException) {
            Log.e("ERROR", e.toString())
        }
    }

    /** Calls updateFavorite() & getBluetooth().
     * Registers activityLifecycleCallbacks with the app.*/
    override fun onResume() {
        super.onResume()
        updateFavorite()
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        try {
            getBluetooth()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        }
    }

    /** Sets the default focused item to the second menu item.*/
    override fun getDefaultAction(): Int {
        return 2
    }

    /** Sets the menu to always be shown.*/
    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    /** Calls sendBluetoothCommand() with the string "previous".*/
    fun previousSong(item: MenuItem?) {
        sendBluetoothCommand("previous")
    }

    /** Sets the menu item icon in the UI and calls sendBluetoothCommand() with a string argument.
     * If true: Sets 'Play' icon in UI and uses "resume"
     * Else: Sets 'Pause' icon in UI and uses "pause".
     * Inverts isPaused boolean.*/
    fun togglePlayPause(item: MenuItem?) {
        if (isPaused) {
            item?.setIcon(R.drawable.ic_pause)
            sendBluetoothCommand("resume")
        } else {
            item?.setIcon(R.drawable.ic_play)
            sendBluetoothCommand("pause")
        }
        isPaused = !isPaused
    }

    /** Calls sendBluetoothCommand() with the string "next".*/
    fun nextSong(item: MenuItem?) {
        sendBluetoothCommand("next")
    }

    /** Calls showToast() with the currentTrackDTO title and artist in the format of:
     * "<title> - <artist>"*/
    fun showSongDetails(item: MenuItem?) {
        showToast("${currentTrackDTO.title} - ${currentTrackDTO.artist}")
    }

    /** Calls sendBluetoothCommand() with the string "previous".*/
    fun toggleFavorite(item: MenuItem?) {
        if (currentTrackDTO.isFavorite) {
            item?.setIcon(R.drawable.ic_add_favorite)
            sendBluetoothCommand("removeFavorite:${currentTrackDTO.uri}")
        } else {
            item?.setIcon(R.drawable.ic_remove_favorite)
            sendBluetoothCommand("addFavorite:${currentTrackDTO.uri}")
        }
        currentTrackDTO.isFavorite = !currentTrackDTO.isFavorite
    }

    /** Sets the menu item icon in the UI and calls sendBluetoothCommand() with a string argument.
     * If true: Sets 'Shuffle off' icon in UI and uses "shuffleOff"
     * Else: Sets 'Shuffle on' icon in UI and uses "shuffleOn".
     * Inverts isShuffled boolean.*/
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

    /** Initializing the bluetoothHandler & bluetoothAdapter from the ViewApplication class.*/
    private fun getBluetooth() {
        val app = application as ViewApplication
        bluetoothHandler = app.bluetoothHandler
        bluetoothAdapter = app.bluetoothAdapter
    }

    /** Tries to call sendCommand() on bluetoothHandler with the argument string.
     * @param command a standardized, case-sensitive string of a command for the server.*/
    private fun sendBluetoothCommand(command: String) {
        bluetoothHandler!!.sendCommand(command)
    }

    /** Shows a short toast with the argument string.
     * @param text the string to display in the toast.*/
    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    /** Delegating handling of the input from the Tap device to the inputMethodManager.
     * @param commandEnum a TapToCommandEnum containing the specific command to execute.*/
    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(PlayPauseMenuItem.actionView, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(PlayPauseMenuItem.actionView, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }
}