package com.facilitation.view.activities.spotify

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.get
import com.facilitation.view.R
import com.facilitation.view.ViewApplication
import com.facilitation.view.databinding.ActivitySpotifySongBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.google.gson.Gson
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifySongActivity : ActionMenuActivity(), ITapInput {
    private var playlistPosition: Int = 0
    private val gson = Gson()
    private lateinit var binding : ActivitySpotifySongBinding
    private lateinit var PlayPauseMenuItem: MenuItem
    private lateinit var NextSongMenuItem: MenuItem
    private lateinit var PrevSongMenuItem: MenuItem
    private lateinit var SongDetailsMenuItem: MenuItem //Not currently used but left for future functionality - Aldís 08.11.23
    private lateinit var BackMenuItem: MenuItem
    private lateinit var menu: Menu
    private lateinit var currentMenuItem : MenuItem
    private lateinit var receiver: TapReceiver
    private var isPaused = false
    private var bluetoothHandler: BluetoothHandler? = null //Not currently used but left for future functionality - Aldís 08.11.23
    private var bluetoothAdapter: BluetoothAdapter? = null //Not currently used but left for future functionality - Aldís 08.11.23
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        activityLifecycleCallbacks = intent.getSerializableExtra("callback") as MyActivityLifecycleCallbacks
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
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
        BackMenuItem = menu[0]

        this.menu = menu
        setCurrentMenuItem(menu[defaultAction], false)

        return true
    }

    override fun getDefaultAction(): Int {
        return 2
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    override fun setCurrentMenuItem(item: MenuItem?, animate: Boolean) {
        val activity: Activity = this
        activity.runOnUiThread {
            super.setCurrentMenuItem(item, animate)
            if (item != null)
                currentMenuItem = item
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Will be refactored with the focus - Aldís 08.11.23
        select()
        return super.onOptionsItemSelected(item)
    }

    fun previousSong(item: MenuItem?) {
        //TODO: Is functional, just needs the playlist from the List Activity - Jody 08.11.23
//        playlistPosition--
//        playlistPosition = (playlistPosition + trackDTOList.size) % trackDTOList.size
//        playSelectedSong()
    }

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

    fun nextSong(item: MenuItem?) {
        //TODO: Is functional, just needs the playlist from the List Activity - Jody 08.11.23
//        playlistPosition++
//        playlistPosition = playlistPosition % trackDTOList.size
//        playSelectedSong()
    }

    fun showSongDetails(item: MenuItem?) {
        // Show song details here
        showToast("Total Eclipse of The Heart - Bonnie Tyler")
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

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        //TODO: Filter here on action int in the KeyEvent constructor to differentiate between different view mappings - Aldís 11.10.23
        when (event.keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                select()
                return true
            }
            KeyEvent.KEYCODE_HOME -> {
//                goHome()
                return true
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                goBack()
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                goLeft()
                return true
            }
            KeyEvent.KEYCODE_FORWARD -> {
                goRight()
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                goUp()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                goDown()
                return true
            }
            KeyEvent.KEYCODE_SPACE -> {
                this.togglePlayPause(currentMenuItem)
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }

    override fun select() {
        when(currentMenuItem.itemId) {
            R.id.menu_spotify_item1 -> previousSong(currentMenuItem)
            R.id.menu_spotify_item2 -> togglePlayPause(currentMenuItem)
            R.id.menu_spotify_item3 -> nextSong(currentMenuItem)
            R.id.menu_spotify_item4 -> showSongDetails(currentMenuItem)
            BackMenuItem.itemId -> goBack()
            else -> Log.d("ERROR", "Invalid menu item selected for execution")
            }
    }

    override fun goUp() {
        Log.i("SpotifySongActivity INFO", "Going up is not valid in here")
    }

    override fun goDown() {
        Log.i("SpotifySongActivity INFO", "Going down is not valid in here")
    }

    override fun goLeft() {
        try {
            setCurrentMenuItem(menu[getMenuItemIndex(currentMenuItem, false) - 1], false)
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going left in Spotify menu:\n${e.stackTrace}")
            throw e
        }
    }

    override fun goRight() {
        try {
            setCurrentMenuItem(menu[getMenuItemIndex(currentMenuItem, false) + 1], false)
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going right in Spotify menu:\n${e.message}")
        }
    }

    override fun goBack() {
        try {
            val intent = Intent(this, SpotifyListActivity::class.java)
            startActivity(intent)
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going back in Spotify menu:\n ${e.message}")
        }
    }
}