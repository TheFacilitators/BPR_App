package com.facilitation.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.facilitation.view.R
import com.facilitation.view.ViewApplication
import com.facilitation.view.activities.spotify.SpotifyListActivity
import com.facilitation.view.databinding.ActivityMainBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.vuzix.connectivity.sdk.Connectivity
import com.vuzix.hud.actionmenu.ActionMenuActivity


/** Activity class to handle logic of routing based on user input.
 * @property SpotifyMenuItem the MenuItem for navigating to Spotify.
 * @property SnakeMenuItem the MenuItem for navigating to Snake.
 * @property binding the binding to the view XML.
 * @property receiver custom receiver for Tap device input.
 * @property activityLifecycleCallbacks custom implementation of activity lifecycle callbacks.
 * @property inputMethodManager manager to translate & manage the user input to the application.
 * @property app the ViewApplication.*/
class MainActivity : ActionMenuActivity(), ITapInput {
    private var getReceiver: BroadcastReceiver? = null
    private lateinit var SpotifyMenuItem: MenuItem
    private lateinit var SnakeMenuItem: MenuItem
    private lateinit var BluetoothMenuItem: MenuItem
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: TapReceiver
    private val activityLifecycleCallbacks = MyActivityLifecycleCallbacks(this)
    private lateinit var inputMethodManager : InputMethodManager
    private lateinit var app : ViewApplication
    private lateinit var c: Connectivity

    /** Initializes receiver, app & inputMethodManager.
     * Registers activityLifecycleCallbacks with the app.
     * @param savedInstanceState a Bundle containing the state the Activity was in last.*/
    override fun onCreate(savedInstanceState: Bundle?) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        registerBroadcastReceivers()
        initializeGetBroadcastReceiver()
        receiver = TapReceiver(this, activityLifecycleCallbacks)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        app = application as ViewApplication
        c = Connectivity.get(context)

    }
    override fun onResume() {
        super.onResume()

        // Register the BroadcastReceiver when the activity is in the foreground
        registerBroadcastReceivers()
    }

    /** Initializing menu items by ID.
     * @param menu the Menu to create.
     * @return 'true' on completion.*/
    override fun onPause() {
        super.onPause()

        // Unregister the BroadcastReceiver when the activity is in the background
        unregisterBroadcastReceivers()
    }
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadcastReceivers() {
        val intentFilter = IntentFilter("com.facilitation.view.CALL")
        registerReceiver(getReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceivers() {
        unregisterReceiver(getReceiver)
    }
    private fun initializeGetBroadcastReceiver() {
        getReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    val message = intent.action
                    val phoneNumber = intent.getStringExtra("phoneNumber")
                    val contactName = intent.getStringExtra("contactName")

                    showToast("Received broadcast: $message, " +
                            "Phone number: $phoneNumber, " +
                            "Name: $contactName")
                    Log.e("test", "*********************************Received broadcast: $message, " +
                            "Phone number: $phoneNumber, " +
                            "Name: $contactName")
                }

                val responseIntent = Intent("com.facilitation.phone.CALL_RESPONSE")
                responseIntent.putExtra("CALL_RESPONSE", 1)
                c.sendBroadcast(responseIntent)
                Log.e("test", "*********************************Sent broadcast: $responseIntent")            }
        }
    }
    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        SpotifyMenuItem = menu.findItem(R.id.menu_openSpotify)
        SnakeMenuItem = menu.findItem(R.id.menu_openSnake)
        BluetoothMenuItem = menu.findItem(R.id.menu_connectBt)
        return true
    }

    /** Sets the default focused item to the second menu item.
     * @return the integer value of the default menu item's position in the menu.*/
    override fun getDefaultAction(): Int {
        return 1
    }

    /** Sets the menu to always be shown.
     * @return 'true' to keep the action menu visible at all times.*/
    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    /** Creates an Intent for SpotifyListActivity, passing activityLifecycleCallbacks as an Extra
     * and starting the activity.*/
    fun showSpotify(item: MenuItem?) {
        val intent = Intent(this, SpotifyListActivity::class.java)
        //Passing the same instance of the activity lifecycle callback to the Spotify activity
        intent.putExtra("callback", activityLifecycleCallbacks)
        startActivity(intent)
    }

    /** Creates an Intent for SnakeActivity, passing activityLifecycleCallbacks as an Extra
     * and starting the activity.
     * @param item the MenuItem this method was called from.*/
    fun showSnake(item: MenuItem?) {
        val intent = Intent(this, SnakeActivity::class.java)
        intent.putExtra("callback", activityLifecycleCallbacks)
        startActivity(intent)
    }

    /** Calls connectToBluetooth() on app. In case of failure, calls showToast() with the error.
     * @param item the MenuItem this method was called from.*/
    fun connectToBluetooth(item: MenuItem) {
        try {
            app.connectToBluetooth()
        } catch (e: Exception) {
            showToast(e.message!!)
        }
    }

    /** Delegating handling of the input from the Tap device to the inputMethodManager.
     * @param commandEnum a TapToCommandEnum containing the specific command to execute.*/
    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(SpotifyMenuItem.actionView, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(SpotifyMenuItem.actionView, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }

    /** Shows a short toast with the argument string.
     * @param text the string to display in the toast.*/
    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    inner class GetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Connectivity.get(context).verify(intent, "com.facilitation.view")) {
                if (isOrderedBroadcast) {
                    resultData = Build.MODEL
                }
            }
        }
    }
}