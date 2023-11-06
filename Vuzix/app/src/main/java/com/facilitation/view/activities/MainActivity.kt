package com.facilitation.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.get
import com.facilitation.view.R
import com.facilitation.view.activities.spotify.SpotifyListActivity
import com.facilitation.view.databinding.ActivityMainBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.vuzix.hud.actionmenu.ActionMenuActivity

class MainActivity : ActionMenuActivity(), ITapInput {
    var SpotifyMenuItem: MenuItem? = null
    var SnakeMenuItem: MenuItem? = null
    lateinit var BackMenuItem: MenuItem
    private lateinit var binding: ActivityMainBinding
    private lateinit var menu: Menu
    private lateinit var currentMenuItem: MenuItem
    private lateinit var receiver: TapReceiver
    private val activityLifecycleCallbacks = MyActivityLifecycleCallbacks(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        receiver = TapReceiver(this, activityLifecycleCallbacks)
    }

    override fun onCreateActionMenu(menu: Menu): Boolean {
        super.onCreateActionMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        SpotifyMenuItem = menu.findItem(R.id.menu_item1)
        SnakeMenuItem = menu.findItem(R.id.menu_item2)
        BackMenuItem = menu[0]
        this.menu = menu
        setCurrentMenuItem(menu[defaultAction], false)
        return true
    }

    override fun getDefaultAction(): Int {
        return 1
    }

    override fun alwaysShowActionMenu(): Boolean {
        return true
    }

    fun showSpotify(item: MenuItem?) {
        val intent = Intent(this, SpotifyListActivity::class.java)
        //Passing the same instance of the activity lifecycle callback to the Spotify activity
        intent.putExtra("callback", activityLifecycleCallbacks)
        startActivity(intent)
    }

    fun showSnake(item: MenuItem?) {
        val intent = Intent(this, SnakeActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(text: String) {
        val activity: Activity = this
        activity.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    override fun setCurrentMenuItem(item: MenuItem?, animate: Boolean) {
        val activity: Activity = this
        activity.runOnUiThread {
            super.setCurrentMenuItem(item, animate)
            currentMenuItem = item ?: menu[defaultAction]
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        select()
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        //TODO: Filter here on action int in the KeyEvent constructor to differentiate between different view mappings - Aldís 11.10.23
        when (event.keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                Log.d("Key Event INFO", "Selecting ${currentMenuItem.title}\n------> ${TapToCommandEnum.XXOOO.keyCode()}")
                select()
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
                goUp()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
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
            R.id.menu_item1 -> {
                setCurrentMenuItem(currentMenuItem, true)
                showSpotify(currentMenuItem)
            }
            R.id.menu_item2 -> {
                setCurrentMenuItem(currentMenuItem, true)
                showSnake(currentMenuItem)
            }
            BackMenuItem.itemId -> {
                setCurrentMenuItem(currentMenuItem, true)
                goBack()
            }
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
            Log.e("Main menu ERROR", "Error going left: ${e.message}")
        }
    }

    override fun goRight() {
        try {
            setCurrentMenuItem(menu[getMenuItemIndex(currentMenuItem, false) + 1], false)
        }
        catch (e:Exception) {
            Log.e("Main menu ERROR", "Error going right: ${e.message}")
        }
    }

    override fun goBack() {
        finishAffinity()
    }
}