package com.facilitation.view.utility

import android.app.Activity
import android.util.Log
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.enums.TapToCommandEnum

//TODO: Remove this class if not used in the end - Aldís 23.10.23
class TapCommandHandler(private val activity: Activity) : ITapInput {
    private val tapReceiver: TapReceiver = TapReceiver(activity)
    private var listener: ITapInput? = null

    fun registerTapReceiver(listener: ITapInput) {
        this.listener = listener
        tapReceiver.registerListener(listener)
    }

    fun unregisterTapReceiver() {
        tapReceiver.unregisterListener()
        listener = null
    }
    override fun onInputReceived(commandEnum : TapToCommandEnum) {
        when (commandEnum) {
            TapToCommandEnum.XXOOO -> select()
//            TapToCommandEnum.XOXXO -> goHome()
//            TapToCommandEnum.OXXXX -> goBack()
            TapToCommandEnum.XOXOO -> goLeft()
            TapToCommandEnum.XOOXO -> goRight()
            TapToCommandEnum.OXXOO -> goUp()
            TapToCommandEnum.OOOXX -> goDown()
//            TapToCommandEnum.XXXOO -> toggleMusic()
            else -> {}
        }
    }

    override fun select() {
        Log.d("Tap INFO", "Select")
        listener?.select()
    }

    override fun goUp() {
        Log.d("Tap INFO", "Going up")
        listener?.goUp()
    }

    override fun goDown() {
        Log.d("Tap INFO", "Going down")
        listener?.goDown()
    }

    override fun goLeft() {
        Log.d("Tap INFO", "Going left")
        listener?.goLeft()
    }

    override fun goRight() {
        Log.d("Tap INFO", "Going right")
        listener?.goLeft()
    }
}