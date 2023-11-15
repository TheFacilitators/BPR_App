package com.facilitation.view.activities
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.facilitation.view.R
import com.facilitation.view.activities.spotify.SpotifyListActivity
import com.facilitation.view.databinding.ActivityMainBinding
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.vuzix.hud.actionmenu.ActionMenuActivity

class MainActivity : ActionMenuActivity(), ITapInput {
    private lateinit var SpotifyMenuItem: MenuItem
    private lateinit var SnakeMenuItem: MenuItem
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: TapReceiver
    private val activityLifecycleCallbacks = MyActivityLifecycleCallbacks(this)
    private lateinit var inputMethodManager : InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        receiver = TapReceiver(this, activityLifecycleCallbacks)
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    override fun onInputReceived(commandEnum: TapToCommandEnum) {
        inputMethodManager.dispatchKeyEventFromInputMethod(SpotifyMenuItem?.actionView, KeyEvent(KeyEvent.ACTION_DOWN, commandEnum.keyCode()))
        inputMethodManager.dispatchKeyEventFromInputMethod(SpotifyMenuItem?.actionView, KeyEvent(KeyEvent.ACTION_UP, commandEnum.keyCode()))
    }
}