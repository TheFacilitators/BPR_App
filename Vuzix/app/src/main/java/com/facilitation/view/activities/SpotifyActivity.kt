package com.facilitation.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facilitation.view.R
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.SpotifyListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vuzix.hud.actionmenu.ActionMenuActivity

class SpotifyActivity : ActionMenuActivity() {

    private val gson = Gson()
    private var trackDTOList: List<TrackDTO> = mutableListOf()

    private var PlayPauseMenuItem: MenuItem? = null
    private var NextSongMenuItem: MenuItem? = null
    private var PrevSongMenuItem: MenuItem? = null
    private var SongDetailsMenuItem: MenuItem? = null
    private var isPaused = true
    private var bluetoothHandler: BluetoothHandler? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var spotifyListAdapter: SpotifyListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not available on this device")
        }
        initBluetooth()
        initSpotifyListView()
        requestPlaylist()
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
        return 3
    }

    override fun onActionMenuClosed() {
        sendBluetoothCommand("exit")
    }

    fun returnToList(item: MenuItem) {
        this.closeActionMenu(true)
    }

    fun previousSong(item: MenuItem?) {
        showToast("Previous Song!")
        sendBluetoothCommand("previous")
    }

    fun togglePlayPause(item: MenuItem?) {
        if(isPaused)
        {
            item?.setIcon(R.drawable.ic_pause)
            sendBluetoothCommand("resume")
            showToast("resume")
        }else{
            item?.setIcon(R.drawable.ic_play)
            sendBluetoothCommand("pause")
            showToast("pause")
        }
        isPaused = !isPaused
    }

    fun nextSong(item: MenuItem?) {
        showToast("Next Song!")
        sendBluetoothCommand("next")
    }

    fun showSongDetails(item: MenuItem?) {
        // Show song details here
        showToast("Total Eclipse of The Heart - Bonnie Tyler")
    }

    fun playSongFromList(view: View) {
        val position = recyclerView.getChildLayoutPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            val selectedTrack = trackDTOList.get(position)
            sendBluetoothCommand("play")
            showToast("play ${selectedTrack.title}")
            this.openActionMenu(true)
        }
    }

    private fun initBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothHandler = BluetoothHandler(this)
            bluetoothHandler?.initiateConnection()
        }
    }

    private fun initSpotifyListView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spotifyListAdapter = SpotifyListAdapter(trackDTOList)
        recyclerView.adapter = spotifyListAdapter
    }

    private fun sendBluetoothCommand(command: String): String {
        return bluetoothHandler!!.sendCommandToServer(command)
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun requestPlaylist() {
        val tmpList: String = sendBluetoothCommand("playlist")
        trackDTOList = gson.fromJson(tmpList,  object : TypeToken<List<TrackDTO>>() {}.type)
        spotifyListAdapter.trackDisplayList = trackDTOList
        recyclerView.adapter?.notifyDataSetChanged()
    }
}
