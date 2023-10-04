package com.facilitation.view.utility

import com.facilitation.view.activities.SpotifyActivity
import com.facilitation.view.receivers.TapReceiver
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.tapwithus.sdk.TapSdk

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
        activity.finishAffinity()
    }

    override fun toggleMusic() {
        activity.playPauseMusic(null)
    }

}