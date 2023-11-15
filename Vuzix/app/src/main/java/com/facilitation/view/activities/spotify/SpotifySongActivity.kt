package com.facilitation.view.activities.spotify

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    private val gson = Gson()
    private lateinit var binding : ActivitySpotifySongBinding
    private lateinit var PlayPauseMenuItem: MenuItem
    private lateinit var NextSongMenuItem: MenuItem
    private lateinit var PrevSongMenuItem: MenuItem
    private lateinit var SongDetailsMenuItem: MenuItem //Not currently used but left for future functionality - Aldís 08.11.23
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

        return true
    }

    override fun getDefaultAction(): Int {
        return 2
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    fun previousSong(item: MenuItem?) {
        sendBluetoothCommand("previous")
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
        sendBluetoothCommand("next")
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

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(SongDetailsMenuItem.actionView, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(SongDetailsMenuItem.actionView, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }
}