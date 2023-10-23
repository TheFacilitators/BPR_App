package com.facilitation.view.activities

import android.app.Activity
import android.content.Intent
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.core.view.get
import com.facilitation.view.R
import com.facilitation.view.databinding.ActivitySpotifyBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifyActivity : ActionMenuActivity(), ITapInput {
    private lateinit var binding : ActivitySpotifyBinding
    private lateinit var PlayPauseMenuItem: MenuItem
    private lateinit var NextSongMenuItem: MenuItem
    private lateinit var PrevSongMenuItem: MenuItem
    private lateinit var SongDetailsMenuItem: MenuItem
    private lateinit var menu: Menu
    private lateinit var currentMenuItem : MenuItem
    private lateinit var receiver: TapReceiver
    private var isPlaying = false
    private var isPaused = false
    private var bluetoothHandler: BluetoothHandler? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        receiver = TapReceiver(this)
        receiver.registerListener(this)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not available on this device")
        }
        initBluetooth()
//        checkIntentExtraCommand()
    }

//    private fun checkIntentExtraCommand() {
//        try {
//            val extra: String = intent.extras?.getString("command").toString()
//            if (extra.isNotBlank()) {
//                onInputReceived(TapToCommandEnum.valueOf(extra))
//            }
//        }
//        catch (e:Exception) {
//            Log.d("Activity INFO", "Found no command in Spotify intent: $e")
//        }
//    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu_spotify, menu)
        PrevSongMenuItem = menu.findItem(R.id.menu_spotify_item1)
        PlayPauseMenuItem = menu.findItem(R.id.menu_spotify_item2)
        NextSongMenuItem = menu.findItem(R.id.menu_spotify_item3)
        SongDetailsMenuItem = menu.findItem(R.id.menu_spotify_item4)

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

    override fun onResume() {
        receiver.registerListener(this)

        super.onResume()

        // Register the receiver when activity enters foreground
        receiver.registerListener(this)
    }

    override fun onPause() {
        super.onPause()

        // Unregister receiver when activity is in background
        receiver.unregisterListener()
    }

    override fun onStop() {
        receiver.unregisterListener()
        super.onStop()
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
        select()
        return super.onOptionsItemSelected(item)
    }

    override fun onActionMenuClosed() {
        sendBluetoothCommand("exit")
    }


    fun previousSong(item: MenuItem?) {
        if (dispatchKeyEvent(KeyEvent(getMenuItemIndex(PrevSongMenuItem, false), TapToCommandEnum.XXOOO.keyCode()))) {
            showToast("Previous Song!")
            sendBluetoothCommand("previous")
        }
    }

    fun nextSong(item: MenuItem?) {
        if (dispatchKeyEvent(KeyEvent(getMenuItemIndex(NextSongMenuItem, false), TapToCommandEnum.XXOOO.keyCode()))) {
            showToast("Next Song!")
            sendBluetoothCommand("next")
        }
    }

    fun showSongDetails(item: MenuItem?) {
        // Show song details here
        showToast("Total Eclipse of The Heart - Bonnie Tyler")
    }

    private fun initBluetooth() {
        if (!dispatchKeyEvent(KeyEvent(getMenuItemIndex(SongDetailsMenuItem, false), TapToCommandEnum.XXOOO.keyCode())))
            return
        showToast("Initiating Bluetooth connection...")
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

    fun togglePlayPause(item: MenuItem?) {
        if (isPlaying) {
            if(isPaused)
            {
                item?.setIcon(R.drawable.ic_pause)
                sendBluetoothCommand("resume")
                showToast("Resume")
            } else {
                item?.setIcon(R.drawable.ic_play)
                sendBluetoothCommand("pause")
                showToast("Pause")
            }
            isPaused = !isPaused
        } else {
            //TODO: move "play" to song selection when ready
            item?.setIcon(R.drawable.ic_pause)
            sendBluetoothCommand("play")
            showToast("Play")
            isPlaying = !isPlaying
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        //TODO: Filter here on action int in the KeyEvent constructor to differentiate between different view mappings - Aldís 11.10.23
        when (event.keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                Log.d("Key Event INFO", "Selecting ${currentMenuItem.title}\n------> ${TapToCommandEnum.XXOOO.keyCode()}")
                select()
                return true
            }
            KeyEvent.KEYCODE_HOME -> {
                Log.d("Key Event INFO", "Going home\n------> ${TapToCommandEnum.XOXXO.keyCode()}")
                goHome()
                return true
            }
            KeyEvent.KEYCODE_ESCAPE -> {
                Log.d("Key Event INFO", "Going back\n------> ${TapToCommandEnum.OXXXX.keyCode()}")
                goBack()
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                Log.d("Key Event INFO", "Going left\n------> ${TapToCommandEnum.XOXOO.keyCode()}")
                goLeft()
                return true
            }
            KeyEvent.KEYCODE_FORWARD -> {
                Log.d("Key Event INFO", "Going right\n------> ${TapToCommandEnum.XOOXO.keyCode()}")
                goRight()
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                Log.d("Key Event INFO", "Going up\n------> ${TapToCommandEnum.OOOXX.keyCode()}")
                goUp()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                Log.d("Key Event INFO", "Going down\n------> ${TapToCommandEnum.OXXOO.keyCode()}")
                goDown()
                return true
            }
            KeyEvent.KEYCODE_SPACE -> {
                Log.d("Key Event INFO", "Toggling music\n------> ${TapToCommandEnum.XXXOO.keyCode()}")
                this.togglePlayPause(currentMenuItem)
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        dispatchKeyEvent(KeyEvent(currentMenuItem.itemId, commandEnum.keyCode()))
    }

    override fun select() {
        showToast("Selecting ${currentMenuItem.title}")
        when(currentMenuItem.itemId) {
            R.id.menu_spotify_item1 -> previousSong(currentMenuItem)
            R.id.menu_spotify_item2 -> this.togglePlayPause(currentMenuItem)
            R.id.menu_spotify_item3 -> nextSong(currentMenuItem)
            R.id.menu_spotify_item4 -> showSongDetails(currentMenuItem)
            else -> Log.d("ERROR", "Invalid menu item selected for execution")
        }
    }

    override fun goUp() {
        showToast("Going up")
    }

    override fun goDown() {
        showToast("Going down")
    }

    override fun goLeft() {
        try {
            setCurrentMenuItem(menu[getMenuItemIndex(currentMenuItem, false) - 1], false)
            showToast("Going left")
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going left in Spotify menu:\n${e.stackTrace}")
            throw e
        }
    }

    override fun goRight() {
        try {
            setCurrentMenuItem(menu[getMenuItemIndex(currentMenuItem, false) + 1], false)
            showToast("Going right")
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going right in Spotify menu:\n${e.message}")
        }
    }

    private fun goBack() {
        try {
            setCurrentMenuItem(menu[1], true)
            showToast("Going back")
            NavUtils.navigateUpFromSameTask(this)
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going back in Spotify menu:\n ${e.message}")
        }
    }

    private fun goHome() {
        try {
            showToast("Going back")
            NavUtils.navigateUpTo(this.parent, Intent("spotify-back"))
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going back in Spotify menu:\n ${e.message}")
        }
    }
}