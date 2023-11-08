package com.facilitation.view.activities.spotify

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
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
import com.facilitation.view.activities.MainActivity
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
        TrackDTO("Song 1", "Artist 1", "Uri 1"),
        TrackDTO("Song 2", "Artist 2", "Uri 2"),
        TrackDTO("Song 3", "Artist 3", "Uri 3"),
        TrackDTO("Song 4", "Artist 4", "Uri 4"),
        TrackDTO("Song 5", "Artist 5", "Uri 5"),
        TrackDTO("Song 6", "Artist 6", "Uri 6"),
        TrackDTO("Song 7", "Artist 7", "Uri 7"),
        TrackDTO("Song 8", "Artist 8", "Uri 8"),
        TrackDTO("Song 9", "Artist 9", "Uri 9"),
        TrackDTO("Song 10", "Artist 10", "Uri 10")
    )
    private lateinit var spotifyListAdapter: SpotifyListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding : ActivitySpotifyListBinding
    private lateinit var receiver: TapReceiver
    private var bluetoothHandler: BluetoothHandler? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
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

        getBluetooth()
        initSpotifyListView()
        requestPlaylist()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        spotifyListAdapter.notifyDataSetChanged()
        super.onResume()
    }

    fun playSongFromList(view: View) {
        playlistPosition = recyclerView.getChildLayoutPosition(view)
        if (playlistPosition != RecyclerView.NO_POSITION) {
            sendBluetoothCommand("track:$playlistPosition")
            showToast("Playing ${trackDTOList[playlistPosition].title}")
            showSpotifySongActivity()
        }
    }

    private fun getBluetooth() {
        val app = application as ViewApplication
        bluetoothHandler = app.bluetoothHandler
        bluetoothAdapter = app.bluetoothAdapter
    }

    private fun initSpotifyListView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) //TODO: Check this for focus - Aldís 08.11.23
        spotifyListAdapter = SpotifyListAdapter(trackDTOList)
        recyclerView.adapter = spotifyListAdapter
    }

    private fun sendBluetoothCommand(command: String) {
        bluetoothHandler!!.sendCommand(command)
    }

    private fun sendReturnableBluetoothCommand(command: String):String {
        return bluetoothHandler!!.sendReturnableCommand(command)
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

    private fun showSpotifySongActivity() {
        //TODO: Pass Spotify state to SongActivity - Jody 08.11.23
        val intent = Intent(this, SpotifySongActivity::class.java)
        intent.putExtra("callback", activityLifecycleCallbacks)
        startActivity(intent)
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(binding.root, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }

    override fun select() {
        //TODO: Refactor interface when input is solely handled by the framework - Aldís 08.11.23
    }

    override fun goUp() {
        //TODO: Refactor interface when input is solely handled by the framework - Aldís 08.11.23
    }

    override fun goDown() {
        //TODO: Refactor interface when input is solely handled by the framework - Aldís 08.11.23
    }

    override fun goLeft() {
        //TODO: Refactor interface when input is solely handled by the framework - Aldís 08.11.23
    }

    override fun goRight() {
        //TODO: Refactor interface when input is solely handled by the framework - Aldís 08.11.23
    }

    override fun goBack() {
        //TODO: Need to put a view item to go back and call this method - Aldís 08.11.23
        try {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        catch (e:Exception) {
            Log.e("Spotify menu ERROR", "Error going back in Spotify menu:\n ${e.message}")
        }
    }
}