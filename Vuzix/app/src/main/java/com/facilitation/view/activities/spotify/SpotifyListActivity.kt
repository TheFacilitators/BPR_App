package com.facilitation.view.activities.spotify

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facilitation.view.R
import com.facilitation.view.ViewApplication
import com.facilitation.view.databinding.ActivitySpotifyListBinding
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.BluetoothHandler
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.SpotifyListAdapter
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class SpotifyListActivity : AppCompatActivity(), ITapInput {
    private var playlistPosition: Int = 0
    private val gson = Gson()
    private var trackDTOList: List<TrackDTO> =  listOf(
        TrackDTO("Song 1", "Artist 1", "Uri 1", true),
        TrackDTO("Song 2", "Artist 2", "Uri 2", false),
        TrackDTO("Song 3", "Artist 3", "Uri 3", false),
        TrackDTO("Song 4", "Artist 4", "Uri 4", false),
        TrackDTO("Song 5", "Artist 5", "Uri 5", false),
        TrackDTO("Song 6", "Artist 6", "Uri 6", false),
        TrackDTO("Song 7", "Artist 7", "Uri 7", false),
        TrackDTO("Song 8", "Artist 8", "Uri 8", false),
        TrackDTO("Song 9", "Artist 9", "Uri 9", false),
        TrackDTO("Song 10", "Artist 10", "Uri 10", false)
    )
    private lateinit var spotifyListAdapter: SpotifyListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding : ActivitySpotifyListBinding
    private lateinit var receiver: TapReceiver
    private lateinit var bluetoothHandler: BluetoothHandler
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager
    override fun onCreate(savedInstanceState: Bundle?) {
        activityLifecycleCallbacks = intent.getSerializableExtra("callback") as MyActivityLifecycleCallbacks
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        receiver = TapReceiver(this, activityLifecycleCallbacks)

        initSpotifyListView()
    }

    override fun onResume() {
        try{
            getBluetooth()
            requestPlaylist()
        } catch (e: Exception) {
            showToast("Error reading from Bluetooth.\nTry to re-connect.")
        }
        super.onResume()
    }

    fun playSongFromList(view: View) {
        playlistPosition = recyclerView.getChildLayoutPosition(view)
        if (playlistPosition != RecyclerView.NO_POSITION) {
            sendBluetoothCommand("track:$playlistPosition")
            showToast("Playing ${trackDTOList[playlistPosition].title}")
            showSpotifySongActivity(trackDTOList[playlistPosition])
        }
    }

    private fun getBluetooth() {
        val app = application as ViewApplication
        bluetoothHandler = app.bluetoothHandler!!
        bluetoothAdapter = app.bluetoothAdapter!!
    }

    private fun initSpotifyListView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spotifyListAdapter = SpotifyListAdapter(trackDTOList)
        recyclerView.adapter = spotifyListAdapter
    }

    private fun sendBluetoothCommand(command: String) {
        try {
            bluetoothHandler.sendCommand(command)
        } catch(e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        }
    }

    private fun sendReturnableBluetoothCommand(command: String):String {
        try {
            return bluetoothHandler.sendReturnableCommand(command)
        } catch(e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        }
        return ""
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

    private fun showSpotifySongActivity(selectedTrack: TrackDTO) {
        val intent = Intent(this, SpotifySongActivity::class.java)
        intent.putExtra("callback", activityLifecycleCallbacks)
        intent.putExtra("track", selectedTrack)
        startActivity(intent)
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }
}