package com.facilitation.view.utility

import android.content.Context
import android.util.Log
import com.facilitation.view.activities.SpotifyActivity
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.tapwithus.sdk.TapListener
import com.tapwithus.sdk.TapSdk
import com.tapwithus.sdk.TapSdkFactory
import com.tapwithus.sdk.airmouse.AirMousePacket
import com.tapwithus.sdk.mode.Point3
import com.tapwithus.sdk.mode.RawSensorData
import com.tapwithus.sdk.mouse.MousePacket

class TapInputHandler(private val activity: SpotifyActivity, tapSDK: TapSdk) : ITapInput {

    fun handleInput( commandEnum : TapToCommandEnum) {
        when (commandEnum) {
            TapToCommandEnum.XXOOO -> select()
            TapToCommandEnum.XOXXO -> goHome()
            TapToCommandEnum.OXXXX -> goBack()
            TapToCommandEnum.XOXOO -> goLeft()
            TapToCommandEnum.XOOXO -> goRight()
            TapToCommandEnum.OXXOO -> goUp()
            TapToCommandEnum.OOOXX -> goDown()
            TapToCommandEnum.XXXOO -> toggleMusic()
        }
    }

    init {
        val receiver = TapReceiver(this, tapSDK)
    }

    override fun select() {
        activity.executeSelectedMenuItem()
    }

    override fun goUp() {
        TODO("Not implemented yet")
    }

    override fun goDown() {
        TODO("Not yet implemented")
    }

    override fun goLeft() {
        activity.moveLeft()
    }

    override fun goRight() {
        activity.moveRight()
    }

    override fun goBack() {
        activity.navigateBack()
    }

    override fun goHome() {
        TODO("Not yet implemented")
    }

    override fun toggleMusic() {
        activity.playPauseMusic()
    }

}