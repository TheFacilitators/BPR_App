package com.facilitation.view.utility

import com.facilitation.view.utility.enums.TapToCommandEnum

interface IGlobalListener {
    fun onGlobalInputReceived(command: TapToCommandEnum)
    fun onGlobalBackPressed()
    fun onGlobalHomePressed()
    fun onGlobalTogglePlayPause()
}