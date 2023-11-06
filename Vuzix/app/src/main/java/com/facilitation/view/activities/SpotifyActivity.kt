package com.facilitation.view.activities

import BluetoothConnectionListener
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.get
import com.facilitation.view.R
import com.facilitation.view.ViewApplication
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.databinding.ActivitySpotifyBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.SpotifyListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifyActivity : ActionMenuActivity(), BluetoothConnectionListener, ITapInput {

    private val gson = Gson()
    private var playlistPosition: Int = 0
    private var trackDTOList: List<TrackDTO> = mutableListOf(TrackDTO("No Songs Available", "", ""))
    private lateinit var spotifyListAdapter: SpotifyListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding : ActivitySpotifyBinding
    private lateinit var PlayPauseMenuItem: MenuItem
    private lateinit var NextSongMenuItem: MenuItem
    private lateinit var PrevSongMenuItem: MenuItem
    private lateinit var SongDetailsMenuItem: MenuItem
    private lateinit var BackMenuItem: MenuItem
    private lateinit var menu: Menu
    private lateinit var currentMenuItem : MenuItem
    private lateinit var receiver: TapReceiver
    private var isPaused = false
    private var bluetoothHandler: BluetoothHandler? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = context as ViewApplication
        app.spotifyActivity = this
        activityLifecycleCallbacks = intent.getSerializableExtra("callback") as MyActivityLifecycleCallbacks
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        activityLifecycleCallbacks.onActivityCreated(this, savedInstanceState)
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiver = intent.getSerializableExtra("receiver") as TapReceiver
        initBluetooth()
        initSpotifyListView()
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

    override fun onStart() {
        activityLifecycleCallbacks.onActivityStarted(this)
        super.onStart()
    }

    override fun getDefaultAction(): Int {
        return 3
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
        select()
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        bluetoothHandler!!.exitServer()
    }

    override fun onActionMenuClosed() {
        sendBluetoothCommand("exit")
    }

    fun returnToList(item: MenuItem) {
        this.closeActionMenu(true)
    }

    fun previousSong(item: MenuItem?) {
        playlistPosition--
        playlistPosition = (playlistPosition + trackDTOList.size) % trackDTOList.size
        playSelectedSong()
    }

    fun togglePlayPause(item: MenuItem?) {
        if (isPaused) {
            item?.setIcon(R.drawable.ic_pause)
            sendBluetoothCommand("resume")
            showToast("resume")
        } else {
            item?.setIcon(R.drawable.ic_play)
            sendBluetoothCommand("pause")
            showToast("pause")
        }
        isPaused = !isPaused
    }

    fun nextSong(item: MenuItem?) {
        playlistPosition++
        playlistPosition %= trackDTOList.size
        playSelectedSong()
    }

    fun showSongDetails(item: MenuItem?) {
        // Show song details here
        showToast("Total Eclipse of The Heart - Bonnie Tyler")
    }

    fun playSongFromList(view: View) {
        playlistPosition = recyclerView.getChildLayoutPosition(view)
        if (playlistPosition != RecyclerView.NO_POSITION) {
            playSelectedSong()
            this.openActionMenu(true)
        }
    }

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            bluetoothHandler = BluetoothHandler(this)
            bluetoothHandler?.initiateConnection()
        }else{
            showToast("Bluetooth is not available on this device")
        }
    }

    private fun initSpotifyListView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spotifyListAdapter = SpotifyListAdapter(trackDTOList)
        recyclerView.adapter = spotifyListAdapter
    }

    private fun sendBluetoothCommand(command: String) {
        bluetoothHandler!!.sendCommand(command)
    }

    private fun sendReturnableBluetoothCommand(command: String): String {
        return bluetoothHandler!!.sendReturnableCommand(command)
    }

    private fun playSelectedSong() {
        val selectedTrack = trackDTOList[playlistPosition]
        sendBluetoothCommand(selectedTrack.uri)
        showToast("Now playing: ${selectedTrack.title}")
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    @SuppressLint("NotifyDataSetChanged")

    private fun requestPlaylist() {
        val tmpList: String = sendReturnableBluetoothCommand("playlist")
        trackDTOList = gson.fromJson(tmpList, object : TypeToken<List<TrackDTO>>() {}.type)
        spotifyListAdapter.trackDisplayList = trackDTOList
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onBluetoothConnected() {
        requestPlaylist()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        //TODO: Filter here on action int in the KeyEvent constructor to differentiate between different view mappings - Aldís 11.10.23
        when (event.keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                Log.d("Key Event INFO", "Selecting ${currentMenuItem.title}\n------> ${TapToCommandEnum.XXOOO.keyCode()}")
                select()
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
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        dispatchKeyEvent(KeyEvent(currentMenuItem.itemId, commandEnum.keyCode()))
    }

    override fun select() {
        when(currentMenuItem.itemId) {
            R.id.menu_spotify_item1 -> returnToList(currentMenuItem)
            R.id.menu_spotify_item2 -> previousSong(currentMenuItem)
            R.id.menu_spotify_item3 -> this.togglePlayPause(currentMenuItem)
            R.id.menu_spotify_item4 -> nextSong(currentMenuItem)
            R.id.menu_spotify_item5 -> showSongDetails(currentMenuItem)
            BackMenuItem.itemId -> goBack()
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going back in Spotify menu:\n ${e.message}")
        }
    }
}