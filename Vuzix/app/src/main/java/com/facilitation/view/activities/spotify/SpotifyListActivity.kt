package com.facilitation.view.activities.spotify

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

/** An activity class to handle logic related to a list of TrackDTOs.
 * @property playlistPosition an integer value of the currently highlighted playlist item.
 * @property gson an instance of Gson to use for serialization.
 * @property trackDTOList a pre-populated list used for testing when disconnected from server.
 * @property spotifyListAdapter custom adapter for RecyclerView.
 * @property recyclerView the RecyclerView used to display the list of TrackDTOs.
 * @property binding the binding to the view XML.
 * @property receiver custom receiver for Tap device input.
 * @property bluetoothHandler custom handler for Bluetooth communication.
 * @property bluetoothAdapter adapter for the Bluetooth handler.
 * @property activityLifecycleCallbacks custom implementation of activity lifecycle callbacks.
 * @property inputMethodManager manager to translate & manage the user input to the application.*/
class SpotifyListActivity : AppCompatActivity(), ITapInput {
    private var playlistPosition: Int = 0
    private val gson = Gson()
    private var trackDTOList: List<TrackDTO> = listOf(
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
    private lateinit var binding: ActivitySpotifyListBinding
    private lateinit var receiver: TapReceiver
    private lateinit var bluetoothHandler: BluetoothHandler
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager: InputMethodManager

    /** Initializing activityLifecycleCallbacks from Intent Extra.
     * Creating inputMethodManager & tapReceiver.*/
    override fun onCreate(savedInstanceState: Bundle?) {
        activityLifecycleCallbacks =
            intent.getSerializableExtra("callback") as MyActivityLifecycleCallbacks
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate(savedInstanceState)
        binding = ActivitySpotifyListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        receiver = TapReceiver(this, activityLifecycleCallbacks)

        initSpotifyListView()
    }

    /** Trying to call getBluetooth() & fetch a playlist.*/
    override fun onResume() {
        try {
            getBluetooth()
            requestPlaylist()
        } catch (e: Exception) {
            showToast("Error reading from Bluetooth.\nTry to re-connect.")
        }
        super.onResume()
    }

    /** Triggered on selection of a list item.
     * Determines the position of the recyclerView, sending a Bluetooth command, displaying a toast
     * of the selected track and opening SpotifySongActivity if the list position is a valid item.*/
    fun playSongFromList(view: View) {
        playlistPosition = recyclerView.getChildLayoutPosition(view)
        if (playlistPosition != RecyclerView.NO_POSITION) {
            sendBluetoothCommand("track:$playlistPosition")
            showToast("Playing ${trackDTOList[playlistPosition].title}")
            showSpotifySongActivity(trackDTOList[playlistPosition])
        }
    }

    /** Initializing the bluetoothHandler & bluetoothAdapter from the ViewApplication class.*/
    private fun getBluetooth() {
        val app = application as ViewApplication
        bluetoothHandler = app.bluetoothHandler!!
        bluetoothAdapter = app.bluetoothAdapter!!
    }

    /** Initializing the recyclerView by ID & setting its layoutManager.
     * Initializing spotifyListAdapter with the trackDTOList variable.
     * Setting recyclerView adapter to spotifyListAdapter.*/
    private fun initSpotifyListView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        spotifyListAdapter = SpotifyListAdapter(trackDTOList)
        recyclerView.adapter = spotifyListAdapter
    }

    /** Tries to call sendCommand() on bluetoothHandler with the argument string.
     * @param command a standardized, case-sensitive string of a command for the server.*/
    private fun sendBluetoothCommand(command: String) {
        try {
            bluetoothHandler.sendCommand(command)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        }
    }

    /** Tries to call sendCommand() on bluetoothHandler with the argument string.
     * @param command a standardized, case-sensitive string of a command for the server.
     * @return null or a string response from the server.*/
    private fun sendReturnableBluetoothCommand(command: String): String {
        try {
            return bluetoothHandler.sendReturnableCommand(command)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, e.message.toString())
        }
        return ""
    }

    /** Shows a short toast with the argument string.
     * @param text the string to display in the toast.*/
    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    /** Requesting a playlist, parsing, setting the trackDTOList and posting the results to the UI
     * in a sub-thread. Lint for NotifyDataSetChanged is suppressed to reduce noise.*/
    @SuppressLint("NotifyDataSetChanged")
    private fun requestPlaylist() {
        val handler = Handler(Looper.getMainLooper())
        Thread {
            try {
                val tmpList: String = sendReturnableBluetoothCommand("playlist")
                trackDTOList = gson.fromJson(tmpList, object : TypeToken<List<TrackDTO>>() {}.type)
                // Post the UI update to the main thread using a Handler
                handler.post {
                    spotifyListAdapter.trackDisplayList = trackDTOList
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                //swallow exception if connection fails
            }
        }.start()
    }

    /** Creates an Intent for SpotifySongActivity, passing activityLifecycleCallbacks and the
     * track selected from the list as Extras and starting the activity.
     * @param selectedTrack the TrackDTO that was selected from the list.*/
    private fun showSpotifySongActivity(selectedTrack: TrackDTO) {
        val intent = Intent(this, SpotifySongActivity::class.java)
        intent.putExtra("callback", activityLifecycleCallbacks)
        intent.putExtra("track", selectedTrack)
        startActivity(intent)
    }

    /** Delegating handling of the input from the Tap device to the inputMethodManager.
     * @param commandEnum a TapToCommandEnum containing the specific command to execute.*/
    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(
            binding.root,
            KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode())
        )
        inputMethodManager.dispatchKeyEventFromInputMethod(
            binding.root,
            KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode())
        )
    }
}