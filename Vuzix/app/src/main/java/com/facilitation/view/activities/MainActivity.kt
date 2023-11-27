package com.facilitation.view.activities
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.facilitation.view.model.TrackDTO
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.CacheHelper
import com.facilitation.view.utility.interfaces.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.facilitation.view.utility.interfaces.ICache
import com.google.gson.Gson
import com.vuzix.hud.actionmenu.ActionMenuActivity

class MainActivity : ActionMenuActivity(), ITapInput {
    private val cacheHelper: CacheHelper = CacheHelper()
    private lateinit var SpotifyMenuItem: MenuItem
    private lateinit var SnakeMenuItem: MenuItem
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: TapReceiver
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var inputMethodManager : InputMethodManager
    private lateinit var app : ViewApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        initCallback()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        receiver = TapReceiver(this, activityLifecycleCallbacks)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        app = application as ViewApplication
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        SpotifyMenuItem = menu.findItem(R.id.menu_item1)
        SnakeMenuItem = menu.findItem(R.id.menu_item2)
        return true
    }

    override fun getDefaultAction(): Int {
        return 1
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    private fun initCallback() {
        var callback = cacheHelper.getCachedActivityLifecycleCallback(this)
        if (callback == null) {
            callback = MyActivityLifecycleCallbacks(this)
            cacheHelper.setCachedActivityLifecycleCallback(this, callback)
        }

        activityLifecycleCallbacks = callback
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun showSpotify(item: MenuItem?) {
        val intent = Intent(this, SpotifyListActivity::class.java)
        startActivity(intent)
    }

    fun showSnake(item: MenuItem?) {
        val intent = Intent(this, SnakeActivity::class.java)
        startActivity(intent)
    }

    fun connectToBluetooth(item: MenuItem) {
        try {
            app.connectToBluetooth()
        } catch (e: Exception) {
            showToast(e.message!!)
        }
    }

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(SpotifyMenuItem.actionView, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(SpotifyMenuItem.actionView, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }
}