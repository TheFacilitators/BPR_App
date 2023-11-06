package com.facilitation.view

import android.content.Intent
import android.util.Log
import com.facilitation.view.activities.MainActivity
import com.facilitation.view.activities.SpotifyActivity
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.IGlobalListener
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.ActivityEnum
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.vuzix.hud.resources.*
import java.io.Serializable
import kotlin.system.exitProcess


class ViewApplication : DynamicThemeApplication(), IGlobalListener, Serializable {
    private lateinit var activityLifecycleCallbacks: MyActivityLifecycleCallbacks
    private lateinit var receiver: TapReceiver
    var spotifyActivity: SpotifyActivity? = null
    var mainActivity: MainActivity? = null

    /**
     * Initializes the activityLifecycleCallbacks and the TapReceiver.
     * Registers for activity lifecycle callbacks.
     * Calls showActivity() with 0 to open the main activity.*/
    override fun onCreate() {
        activityLifecycleCallbacks = MyActivityLifecycleCallbacks()
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        super.onCreate()
        receiver = TapReceiver(this, activityLifecycleCallbacks)
        showActivity(0)
    }

    /**
     * Method to route the input received from the TapReceiver to the appropriate functionality.
     * If the received command doesn't have a method mapped, it will log an error.
     * @param command The command received from the TapReceiver*/
    override fun onGlobalInputReceived(command: TapToCommandEnum) {
        when(command) {
            TapToCommandEnum.XOXXO -> onGlobalHomePressed()
            TapToCommandEnum.XXXOO -> onGlobalTogglePlayPause()
            TapToCommandEnum.OXXXX -> onGlobalBackPressed()
            else -> Log.e("Application ERROR", "Global command not registered: $command")
        }
    }

    /**
    * Creates an intent and starts the activity requested based on the ActivityEnum.
    * @param activityNumber The integer number() value from the ActivityEnum*/
    private fun showActivity(activityNumber: Int) {
        val intent = Intent(this, ActivityEnum.values()[activityNumber].activityName()::class.java)
        //TODO: This doesn't work, need to create methods for each activity - Aldís 06.11.23
        intent.putExtra("callback", activityLifecycleCallbacks)
        intent.putExtra("receiver", receiver)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //TODO: This doesn't work either, doesn't like you creating activities from here - Aldís 06.11.23
        startActivity(intent)
    }

    /**
    * If the currentActivity property of the custom ActivityLifecycleCallbacks is null then it exits the application.
    * Otherwise, it will look for it in the ActivityEnum class by the name of the class and use the enum number property to call showActivity() with.*/
    override fun onGlobalBackPressed() {
        val currentActivity = ActivityEnum.values().find { it.activityName() == activityLifecycleCallbacks.currentActivity!!.localClassName }
        if (currentActivity!!.number() != 0) {
            showActivity(currentActivity.number())
            return
        }

        exitProcess(0)
    }

    /**
     * Calls the showActivity() with 0 as the argument to display the main activity.*/
    override fun onGlobalHomePressed() {
        showActivity(0)
    }

    /**
     * If the spotifyActivity property has been initialized it will call the togglePlayPause() in the SpotifyActivity class.
     * Otherwise, it will log that the activity hasn't been initialized.*/
    override fun onGlobalTogglePlayPause() {
        if (spotifyActivity != null) {
            spotifyActivity!!.togglePlayPause(null)
            return
        }
        Log.i("Application INFO", "Spotify activity not initialized")
    }

    override fun getNormalThemeResId(): Int {
        return R.style.AppTheme
    }

    override fun getLightThemeResId(): Int {
        return R.style.AppTheme_Light
    }
}